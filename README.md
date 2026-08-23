# Avelrune Gayrimenkul

Premium gayrimenkul şirketi için uçtan uca çalışan tam yığın web uygulaması. Public site, admin paneli, RSA JWT, mikroservisler, Traefik ve PostgreSQL aynı Docker ağında ayağa kalkar.

Slogan: **Her adres, bir tercih.**

## Mimari

```text
Tarayıcı
  └── Traefik :80
        ├── /        → frontend (Next.js :3000)
        └── /api/**  → api-gateway (Spring Cloud Gateway :8080)
                         ├── /api/auth/**       → auth-service     :8081  → auth_db
                         ├── /api/properties/** → property-service :8082  → property_db
                         ├── /api/agents/**     → property-service
                         ├── /api/admin/**      → property-service
                         └── /api/requests/**   → request-service  :8083  → request_db
                               └── postgres :5432
```

Frontend, mikroservislere doğrudan gitmez. Tarayıcı `/api` istekleri Traefik üzerinden API Gateway’e gider. JWT doğrulaması yalnızca Gateway’de yapılır.

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
| `traefik` | Tek public giriş (port 80) |
| `frontend` | Public site ve admin paneli |
| `api-gateway` | `/api/**` yönlendirme, CORS, JWT |
| `auth-service` | Admin girişi, RS256 JWT üretimi |
| `property-service` | İlanlar, görseller, danışmanlar, filtreleme, CRUD |
| `request-service` | İletişim / ilan talepleri (Inquiry) |
| `postgres` | Kalıcı veri (geliştirme için 5432 açık) |

## Çalıştırma

```bash
docker compose up --build
```

Uygulama: [http://localhost](http://localhost)

Mikroservis portları dışarı açılmaz. Geliştirme için PostgreSQL `localhost:5432` üzerinden erişilebilir.

İlk açılışta seed data otomatik yüklenir. Site boş gelmez.

Önceki sürümden geliyorsanız `request_db` için volume’u yenilemek gerekebilir:

```bash
docker compose down
docker compose up --build
```

`db-init` mevcut Postgres volume’unda eksik veritabanlarını oluşturur. JWT anahtarları `jwt-keys` volume’unda tutulur; `private.pem` repoya girmez.

## Demo hesaplar

Admin:

- E-posta: `admin@avelrune.com`
- Şifre: `AvelruneAdmin2026!`
- Giriş: [http://localhost/admin/login](http://localhost/admin/login) veya [http://localhost/giris](http://localhost/giris)

Üye:

- E-posta: `user@avelrune.com`
- Şifre: `AvelruneUser2026!`
- Giriş: [http://localhost/giris](http://localhost/giris)
- Üye ol: [http://localhost/uye-ol](http://localhost/uye-ol)
- Evlerim: [http://localhost/evlerim](http://localhost/evlerim)

Üye kendi ev ilanını satılık/kiralık yayınlar. Ziyaretçi veya üye, ilan sayfasından sahibine mesaj gönderir. Mesajlar ayrı bir kutuda değil, Evlerim’de ilgili evin altında görünür.

## Environment

Değişkenler `.env.example` dosyasındadır. `docker compose` varsayılan değerlerle çalışır.

| Değişken | Açıklama |
| --- | --- |
| `POSTGRES_USER` / `POSTGRES_PASSWORD` | Veritabanı erişimi |
| `JWT_EXPIRATION_MS` | Token süresi (varsayılan 8 saat) |
| `API_INTERNAL_URL` | Frontend SSR’nin gateway adresi (`http://api-gateway:8080`) |
| `SITE_URL` | Public origin (`http://localhost`) |

Şema ve demo veri Flyway migration’larındadır (`**/db/migration`). Hibernate `ddl-auto: validate` kullanır. Eski Hibernate tabloları varsa volume’u sıfırlamak gerekir: `docker compose down -v`.

Auth servisi RSA anahtar çiftini `JWT_KEYS_DIR` altında üretir. Gateway aynı volume’dan `public.pem` okur.

## Endpoint yapısı

```text
POST /api/auth/login          public
POST /api/auth/register       public
GET  /api/auth/me             JWT
GET  /api/auth/users          JWT + ADMIN

GET  /api/properties          public
GET  /api/properties/featured public
GET  /api/properties/meta     public
GET  /api/properties/mine     JWT
GET  /api/properties/{slug}   public
GET  /api/properties/{slug}/similar public
GET  /api/agents              public

POST  /api/requests           public
GET   /api/requests           JWT + ADMIN
GET   /api/requests/stats     JWT + ADMIN
PATCH /api/requests/{id}/read JWT + ADMIN

GET    /api/admin/stats                JWT + ADMIN
GET    /api/admin/properties           JWT + ADMIN
POST   /api/admin/properties           JWT + ADMIN
GET    /api/admin/properties/{id}      JWT + ADMIN
PUT    /api/admin/properties/{id}      JWT + ADMIN
DELETE /api/admin/properties/{id}      JWT + ADMIN
PATCH  /api/admin/properties/{id}/active   JWT + ADMIN
PATCH  /api/admin/properties/{id}/featured JWT + ADMIN
```

Public ilan filtreleri query parametreleriyle çalışır:

```text
/ilanlar?listingType=SALE&city=İstanbul&rooms=4+1
```

## Klasör yapısı

```text
avelrune/
├── frontend/
├── api-gateway/
├── auth-service/
├── property-service/
├── request-service/
├── infra/postgres/init.sql
├── docker-compose.yml
├── .env.example
└── README.md
```
