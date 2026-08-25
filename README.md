# Avelrune Gayrimenkul

Premium gayrimenkul şirketi için uçtan uca çalışan tam yığın web uygulaması. Public site, admin paneli, RSA JWT, mikroservisler, Traefik ve PostgreSQL aynı Docker ağında ayağa kalkar.

Slogan: **Her adres, bir tercih.**

## Mimari

```text
Tarayıcı HTTPS :443
  └── Traefik (TLS sonlandırma; HTTP :80 → 301 HTTPS)
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

TLS yalnızca Traefik kenarında biter. Docker içi trafik HTTP kalır. Frontend, mikroservislere doğrudan gitmez. Tarayıcı `/api` istekleri Traefik üzerinden API Gateway’e gider. JWT doğrulaması yalnızca Gateway’de yapılır.

Diyagramlar: `docs/diagrams/` (`architecture.mmd`, `jwt-flow.mmd`, `index.html`).

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
| `traefik` | Tek public giriş (HTTPS 443, HTTP 80 → HTTPS) |
| `frontend` | Public site ve admin paneli |
| `api-gateway` | `/api/**` yönlendirme, CORS, JWT |
| `auth-service` | Admin girişi, RS256 JWT üretimi |
| `property-service` | İlanlar, görseller, danışmanlar, filtreleme, CRUD |
| `inquiry-service` | İlan sahiplerine gelen mesajlar |
| `postgres` | Kalıcı veri (geliştirme için 5432 açık) |

## Çalıştırma

```bash
docker compose up --build
```

Uygulama: [https://localhost](https://localhost)

`http://localhost` kalıcı olarak HTTPS’e yönlenir. Sertifika `infra/tls/localhost.crt` (SAN: `localhost`, `127.0.0.1`). İlk ziyarette tarayıcı uyarı verebilir; macOS’ta güvenmek için:

```bash
sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain infra/tls/localhost.crt
```

Yeniden üretmek: `sh infra/tls/generate.sh`

Mikroservis portları dışarı açılmaz. Geliştirme için PostgreSQL `localhost:5432` üzerinden erişilebilir.

İlk açılışta seed data otomatik yüklenir. Site boş gelmez.

`auth_db`, `property_db` ve `inquiry_db` ilk volume oluşturuluşunda `infra/postgres/init.sql` ile açılır. Mevcut volume’da bu veritabanları yoksa:

```bash
docker compose down -v
docker compose up --build
```

JWT anahtarları `jwt-keys` volume’unda tutulur; `private.pem` repoya girmez.

## Demo hesaplar

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

OpenAPI (springdoc) tüm servislerde vardır. Swagger UI yalnızca auth, property ve inquiry servislerindedir:

```text
https://localhost/api/docs/auth/swagger-ui/index.html
https://localhost/api/docs/property/swagger-ui/index.html
https://localhost/api/docs/inquiries/swagger-ui/index.html
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
├── infra/postgres/init.sql
├── infra/dev/flyway/
├── infra/tls/
├── infra/traefik/
├── docker-compose.yml
├── .env.example
└── README.md
```
