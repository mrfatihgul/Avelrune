# Avelrune Gayrimenkul

Premium gayrimenkul şirketi için uçtan uca çalışan tam yığın web uygulaması. Public site, admin paneli, RSA JWT, mikroservisler, Traefik ve PostgreSQL aynı Docker ağında ayağa kalkar.

Slogan: **Her adres, bir tercih.**

## Mimari

```text
Tarayıcı HTTPS :443
  └── Traefik
        HTTP :80 site  → 308 HTTPS
        HTTP :80 /api  → api-gateway (Postman / OpenAPI, TLS yok)
        ├── /        → frontend (Next.js :3000)          HTTP, Docker ağı
        └── /api/**  → api-gateway (Spring Cloud Gateway :8080)
                         ├── /api/auth/**          → auth-service     :8081  → auth_db
                         ├── /api/docs/auth/**     → auth-service Swagger
                         ├── /api/properties/**    → property-service :8082  → property_db
                         ├── /api/docs/property/** → property-service Swagger
                         ├── /api/agents/**        → property-service
                         ├── /api/admin/**         → property-service
                         ├── /api/inquiries/**      → inquiry-service  :8083  → inquiry_db
                         ├── /api/docs/inquiries/** → inquiry-service Swagger
                         └── /api/docs/gateway/**  → api-gateway OpenAPI (Swagger yok)
```

TLS yalnızca Traefik kenarında, site için biter. `/api` hem HTTP hem HTTPS’ten açılır; Postman speçi HTTP’den çeker. Docker içi trafik HTTP kalır. Frontend, mikroservislere doğrudan gitmez. JWT doğrulaması yalnızca Gateway’de yapılır.

Diyagramlar: `docs/diagrams/` (`architecture.mmd`, `jwt-flow.mmd`).

## Teknolojiler

- Frontend: Next.js 15, TypeScript, App Router
- Backend: Java 21, Spring Boot 3.3, Spring Security, Spring Data JPA
- Gateway: Spring Cloud Gateway (RS256 JWT doğrulama)
- Edge: Traefik 3.6
- Veritabanı: PostgreSQL 16
- Altyapı: Docker Compose

## Servisler

| Servis | Görev |
| --- | --- |
| `traefik` | Site HTTPS 443; HTTP 80 site → HTTPS, `/api` HTTP kalır |
| `frontend` | Public site ve admin paneli |
| `api-gateway` | `/api/**` yönlendirme, CORS, JWT |
| `auth-service` | Admin girişi, RS256 JWT üretimi |
| `property-service` | İlanlar, görseller, danışmanlar, filtreleme, CRUD |
| `inquiry-service` | İlan sahiplerine gelen mesajlar |
| `postgres` | Kalıcı veri (geliştirme için 5432 açık) |

## Çalıştırma

Bu komut **geliştirme** ortamını açar; `dev` profili demo verileri yükler.

```bash
docker compose up --build --remove-orphans
```

Uygulama: [https://localhost](https://localhost)

`http://localhost` kalıcı olarak HTTPS’e yönlenir. Sertifika `infra/tls/localhost.crt` (SAN: `localhost`, `127.0.0.1`). İlk ziyarette tarayıcı uyarı verebilir; macOS’ta güvenmek için:

```bash
sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain infra/tls/localhost.crt
```

Yeniden üretmek: `sh infra/tls/generate.sh`

## Geliştirme ve production ayrımı

`docker-compose.yml` ve servislerin `local` profilleri geliştirme içindir. IntelliJ'deki
`local` profil ve geliştirme Compose'u aynı PostgreSQL volume'unu kullanır.
Demo kullanıcılar ve ilanlar yalnızca bu ortamda Flyway seed dosyalarından
yüklenir. Geçiş yaparken aynı veritabanını production için kullanmayın.

`docker-compose.prod.yml` ayrı Compose projesi (`avelrune-production`) kurar.
Kendine ait PostgreSQL, JWT anahtar ve görsel volume'ları ile Docker ağı vardır.
PostgreSQL ve Java servis portları bilgisayara açılmaz; site yalnızca HTTPS
üzerinden açılır. Production servislerinde yalnızca şema migration'ları çalışır;
`infra/dev/flyway` mount edilmez ve demo seed yüklenmez. Bu ayrım, mevcut
geliştirme verilerini silmez veya production ortamına taşımaz.
Geliştirme Traefik container'ı eski yapılandırmayla zaten çalışıyorsa,
production ortamını ilk kez açmadan önce yeni ortam filtresini yüklemek için
geliştirme Traefik'ini bir kez yeniden oluşturun:

```bash
docker compose up -d --no-deps --force-recreate traefik
```

Production ayarları için örneği kopyalayıp **boş `PROD_POSTGRES_PASSWORD`
alanını doldurun**:

```bash
cp .env.production.example .env.production
```

Ardından production ortamını başlatın:

```bash
docker compose --env-file .env.production -f docker-compose.prod.yml up -d --build
```

Örnek dosyanın varsayılan ayarlarıyla yerel production kontrolü
`https://localhost:18443` adresindedir; böylece geliştirme ortamı aynı anda
çalışabilir. Gerçek sunucuda `PROD_SITE_URL` değerini gerçek HTTPS alan adına,
`PROD_HTTPS_PORT` değerini `443`e ve sertifika yollarını o alan adının
dosyalarına ayarlayın. `.env.production` Git'e girmez. Production ortamını
volume'ları koruyarak durdurmak için:

```bash
docker compose --env-file .env.production -f docker-compose.prod.yml down
```

Production veritabanı ilk açılışta boştur. Demo hesaplar orada bulunmaz;
admin hesabı gerekiyorsa ayrıca güvenli biçimde oluşturulmalıdır. Bu Compose
ayrımı tek başına gerçek sunucu güvenliği, yedekleme veya admin hesabı
oluşturma sürecinin tamamlandığı anlamına gelmez.

## Hızlı yerel geliştirme (Docker imajı derlemeden)

PostgreSQL Docker'da, uygulamalar bilgisayarında çalışır. Java 21, Maven 3.9+
ve Node.js 22/npm kurulu olmalı; `java`, `mvn`, `node`, `npm` PATH üzerinde olmalı.
İlk çalıştırmada bağımlılıklar indirilir; sonraki değişikliklerde Docker build gerekmez.

Proje kökünde önce veritabanını başlat:

```bash
docker compose up -d postgres
```

Bu komut yalnızca PostgreSQL'i başlatır ve mevcut `postgres_data` volume'unu
korur. İlk kurulumda veritabanları `infra/postgres/init.sql` ile oluşturulur. Bağlantı
`localhost:5432`; veritabanları `auth_db`, `property_db`, `inquiry_db`.
Varsayılan kullanıcı/şifre `avelrune` / `avelrune`; kökteki `.env` ile
`POSTGRES_USER` ve `POSTGRES_PASSWORD` değiştirilebilir. Var olan volume'un
şifresi `.env` değiştirmekle otomatik değişmez.

Ardından **her komutu ayrı terminalde**, proje kökünden çalıştır:

```bash
(cd auth-service && mvn spring-boot:run -Dspring-boot.run.profiles=local)
(cd property-service && mvn spring-boot:run -Dspring-boot.run.profiles=local)
(cd inquiry-service && mvn spring-boot:run -Dspring-boot.run.profiles=local)
(cd api-gateway && mvn spring-boot:run -Dspring-boot.run.profiles=local)
(cd frontend && npm install && NEXT_PUBLIC_SITE_URL=http://localhost:3000 npm run dev)
```

Auth hazır olduktan sonra gateway'i başlat; gateway auth'un oluşturduğu public
key'i okur. Inquiry'nin ilan işlemleri için property servisi gerekir.
Frontend varsayılan olarak `http://localhost:8080` gateway'ine bağlanır.
Farklı bir gateway kullanıyorsan frontend komutunda `API_INTERNAL_URL` değerini ayarla.
Yerel Java süreçleri kökteki `.env` dosyasını otomatik okumaz; varsayılan dışı
veritabanı bilgilerini `POSTGRES_USER` ve `POSTGRES_PASSWORD` olarak süreçlere aktar.

| Bileşen | Yerel adres |
| --- | --- |
| Frontend | http://localhost:3000 |
| API gateway / Postman baseUrl | http://localhost:8080 |
| Auth | http://localhost:8081 |
| Property | http://localhost:8082 |
| Inquiry | http://localhost:8083 |
| PostgreSQL | localhost:5432 |

Frontend `npm run dev` ile çalışır; kaydedilen değişiklikler otomatik görünür.
Java değişikliklerinde yalnızca ilgili servisi Ctrl+C ile durdurup aynı komutla
başlat veya IDE'den yeniden Run/Debug yap. Diğer servisler ve PostgreSQL açık kalır.
Servis sağlık adresleri `/actuator/health`tir.

Yerel profil demo kullanıcıları/ilanları Flyway ile yükler. JWT anahtarları
`.local/keys`, yüklenen görseller `.local/media` altında saklanır ve Git'e girmez.
Yerel ve **geliştirme** Docker modları aynı veritabanını, fakat ayrı anahtar ve görsel depolarını
kullanır: mod değiştirince yeniden giriş yap; diğer modda yüklenmiş görseller
orada kalır. Verileri korumak için `docker compose down -v` kullanma.

### IntelliJ IDEA ile ayrı Run/Debug

Her servisin `pom.xml` dosyasını Maven projesi olarak içe aktar. Spring Boot
çalıştırma yapılandırmasında **Active profiles: `local`**, **Working directory:
ilgili servisin klasörü** seç. Ana sınıflar `AuthApplication`,
`PropertyApplication`, `InquiryApplication`, `GatewayApplication`.
Terminal alternatifi, ilgili servis klasöründe:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

IDE kökteki `.env` dosyasını otomatik okumaz. Özelleştirilmiş veritabanı
kullanıcı/şifresini Run Configuration ortamına `POSTGRES_USER` ve
`POSTGRES_PASSWORD` olarak ekle. Standart değerlerde ek ayar gerekmez.

Doğrudan Swagger UI adresleri `http://localhost:8081/swagger-ui.html`,
`http://localhost:8082/swagger-ui.html`, `http://localhost:8083/swagger-ui.html`.
Gerçek JWT/rol doğrulaması gateway'de olduğu için kullanıcı yetkisi gerektiren
Postman ve uygulama testlerini `http://localhost:8080/api/...` üzerinden yap.
Servis portları bağımsız sağlık, public API ve servis içi testler içindir.

### Docker ve yerel mod arasında geçiş

Tam Docker çalıştırma komutu değişmedi:

```bash
docker compose up --build --remove-orphans
```

Docker servisleri de aynı `3000`, `8080`, `8081`, `8082`, `8083` portlarını
**yalnızca localhost'a** açar. Aynı portu kullanan yerel süreci ve container'ı
aynı anda başlatma. Tam Docker'dan yerel moda geçerken:

```bash
docker compose stop frontend api-gateway inquiry-service property-service auth-service traefik
docker compose up -d postgres
```

Sonra yukarıdaki yerel servis komutlarını başlat. Docker'a dönmeden önce yerel
süreçleri Ctrl+C ile kapat. Yalnızca bir Docker imajını güncellemek için örnek:

```bash
docker compose up -d --build --no-deps property-service
```

Sadece arayüz geliştiriyorsan backend'leri Docker'da bırakabilirsin:

```bash
docker compose up -d postgres auth-service property-service inquiry-service api-gateway
docker compose stop frontend
(cd frontend && npm install && NEXT_PUBLIC_SITE_URL=http://localhost:3000 npm run dev)
```

Bu durumda tarayıcıda `http://localhost:3000` kullan. Yerel frontend her iki
modda da `http://localhost:8080` gateway'ine bağlanır. Farklı adresler için
`API_INTERNAL_URL` ve `NEXT_PUBLIC_SITE_URL` ortam değişkenlerini kullan.
Backend'leri yerelde birlikte çalıştırmak en basit düzendir; Docker içindeki
bir gateway'in localhost adresi bilgisayarın değildir, bu yüzden tek bir
backend'i yerelle değiştirmek ayrıca container yönlendirmesi gerektirir.

Yerel ortamda E2E testi:

```bash
cd e2e
npm install
npx playwright install chromium
E2E_BASE_URL=http://localhost:3000 npm test
```

## E2E

Yığın ayaktayken tarayıcıdan üye olup ilan yayınlar:

```bash
cd e2e
npm install
npx playwright install chromium
npm test
```

Mikroservis portları yalnızca localhost üzerinde açılır. Geliştirme için PostgreSQL `localhost:5432` üzerinden erişilebilir.

İlk açılışta seed data otomatik yüklenir. Site boş gelmez.

`auth_db`, `property_db` ve `inquiry_db` ilk volume oluşturuluşunda `infra/postgres/init.sql` ile açılır. Eski bir geliştirme volume'unda bunlardan biri eksikse, verileri silmeden oluşturmak için PostgreSQL'i başlatıp şu komutu çalıştır:

```bash
docker compose up -d postgres
docker compose exec -T postgres sh -c 'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d postgres' <<'SQL'
SELECT format('CREATE DATABASE %I', name)
FROM (VALUES ('auth_db'), ('property_db'), ('inquiry_db')) AS databases(name)
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = name)
\gexec
SQL
```

JWT anahtarları `jwt-keys` volume’unda tutulur; `private.pem` repoya girmez.

## Demo hesaplar (yalnızca geliştirme)

Admin:

- E-posta: `admin@avelrune.com`
- Şifre: `AvelruneAdmin2026!`
- Giriş: [https://localhost/admin/login](https://localhost/admin/login) veya [https://localhost/login](https://localhost/login)

Üye:

- E-posta: `user@avelrune.com`
- Şifre: `AvelruneUser2026!`
- Giriş: [https://localhost/login](https://localhost/login)
- Üye ol: [https://localhost/register](https://localhost/register)
- Evlerim: [https://localhost/my-listings](https://localhost/my-listings)

Üye kendi ev ilanını satılık/kiralık yayınlar. Ziyaretçi veya üye, ilan sayfasından sahibine mesaj gönderir. Mesajlar ayrı bir kutuda değil, Evlerim’de ilgili evin altında görünür.

## Environment

Değişkenler `.env.example` dosyasındadır. `docker compose` varsayılan değerlerle çalışır.

| Değişken | Açıklama |
| --- | --- |
| `POSTGRES_USER` / `POSTGRES_PASSWORD` | Veritabanı erişimi |
| `JWT_EXPIRATION_MS` | Token süresi (varsayılan 8 saat) |
| `API_INTERNAL_URL` | Frontend SSR’nin gateway adresi (`http://api-gateway:8080`) |
| `SITE_URL` | Public origin (`https://localhost`) |

Şema Flyway ile `**/db/migration` altındadır (yayına giden kısım). Demo seed ayrıdır: `infra/dev/flyway`. Yerel `docker compose` `dev` profilini açıp o klasörü mount eder; production imajında seed SQL yoktur. Hibernate `ddl-auto: validate` kullanır.

Auth servisi RSA anahtar çiftini `JWT_KEYS_DIR` altında üretir. Gateway aynı volume’dan `public.pem` okur.

RSA anahtar üretimi Bouncy Castle sağlayıcısıyla (2048 bit), PEM okuma/yazma ise
`PEMParser`, `JcaPEMKeyConverter` ve `JcaPEMWriter` ile yapılır. JJWT, tokenları
RS256 ile imzalamaya ve doğrulamaya devam eder. OpenSSL kurulumu veya ayrı bir
anahtar hazırlama komutu gerekmez.

Mevcut `private.pem` (PKCS#8) ve `public.pem` (SubjectPublicKeyInfo) dosyaları
değiştirilmeden okunur. Yeni dosyalar da aynı biçimlerde oluşturulur. İki dosya
da yoksa yeni çift üretilir; yalnızca biri varsa veya çift uyumsuzsa servis
mevcut dosyaların üzerine yazmadan hata verir. Geçiş sırasında `jwt-keys`
volume’unu koruyun; `docker compose down -v` anahtarları ve veritabanını siler.
Anahtarlar korunduğunda süresi dolmamış mevcut tokenlar geçerli kalır.

Postman akışı değişmez: login yanıtındaki `accessToken` değerini ortamın `token`
değişkeninin VALUE alanına yapıştırın. İçe aktarılan koleksiyonun
`X-Access-Token: {{token}}` header’ı çalışmaya devam eder; elle ayarlanan
Bearer Token alanında `{{token}}` kullanımı da desteklenir. `baseUrl`,
endpointler, yanıt alanları ve token süresi değişmez.

## Endpoint yapısı

```text
POST /api/auth/login          public
POST /api/auth/register       public
GET  /api/auth/me             JWT
GET  /api/auth/users          JWT + ADMIN

GET    /api/properties                public
GET    /api/properties/featured       public
GET    /api/properties/meta           public
GET    /api/properties/mine           JWT
GET    /api/properties/owned/{id}     JWT (sahip veya ADMIN)
POST   /api/properties                JWT
PUT    /api/properties/{id}           JWT (sahip veya ADMIN)
DELETE /api/properties/{id}           JWT (sahip veya ADMIN)
GET    /api/properties/{slug}         public
GET    /api/properties/{slug}/similar public
GET    /api/properties/id/{id}        public (yalnızca aktif)
GET    /api/agents                    public

POST  /api/inquiries                   public (JWT isteğe bağlı)
GET   /api/inquiries                   JWT + ADMIN
GET   /api/inquiries/stats             JWT + ADMIN
GET   /api/inquiries/summary           JWT (ilan sahibi)
GET   /api/inquiries/property/{id}     JWT (sahip veya ADMIN)
PATCH /api/inquiries/{id}/read         JWT (sahip veya ADMIN)

GET    /api/admin/stats                JWT + ADMIN
GET    /api/admin/properties           JWT + ADMIN
POST   /api/admin/properties           JWT + ADMIN
GET    /api/admin/properties/{id}      JWT + ADMIN
PUT    /api/admin/properties/{id}      JWT + ADMIN
DELETE /api/admin/properties/{id}      JWT + ADMIN
PATCH  /api/admin/properties/{id}/active   JWT + ADMIN
PATCH  /api/admin/properties/{id}/featured JWT + ADMIN
```

OpenAPI (springdoc) tüm servislerde vardır. Swagger UI yalnızca auth, property ve inquiry servislerindedir.

Postman Import’a `v3/api-docs` yapıştır (bu adres Collection JSON döner):

```text
http://localhost/api/docs/auth/v3/api-docs
http://localhost/api/docs/property/v3/api-docs
http://localhost/api/docs/inquiries/v3/api-docs
```

Ortamda yalnızca `baseUrl` (`http://localhost`) ve `token` olsun. `login` yanıtındaki `accessToken` değerini `token` VALUE kutusuna sen yapıştır. Koleksiyon bu değeri kendiliğinden yazmaz. Token kutusunda `{{token}}` dursun.

Swagger UI OpenAPI spec’i `openapi.json` üzerinden okur.

Swagger UI:

```text
http://localhost/api/docs/auth/swagger-ui/index.html
http://localhost/api/docs/property/swagger-ui/index.html
http://localhost/api/docs/inquiries/swagger-ui/index.html
```

Gateway yalnızca spec üretir (`/api/docs/gateway/v3/api-docs`), Swagger UI açmaz.

Swagger UI `dev` profilinde açıktır (yerel compose). Production profilinde kapalıdır.

İlan görselleri `http(s)` veya site içi yol (`/...`) olmalıdır. `/admin` yalnızca `ADMIN` rolü içindir; üye hesabı `/my-listings`e yönlenir.

Public ilan filtreleri query parametreleriyle çalışır:

```text
/listings?listingType=SALE&city=İstanbul&rooms=4+1
```

## Klasör yapısı

```text
avelrune/
├── frontend/
├── api-gateway/
├── auth-service/
├── property-service/
├── inquiry-service/
├── e2e/
├── docs/diagrams/
├── infra/postgres/init.sql
├── infra/dev/flyway/
├── infra/tls/
├── infra/traefik/
├── docker-compose.yml
├── .env.example
└── README.md
```
