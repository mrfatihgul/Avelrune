# Bouncy Castle entegrasyon raporu

Tarih: 25 Eylül 2026

## Kapsam

Auth ve API gateway servislerinde RSA anahtar yönetimi Bouncy Castle 1.86'ya
geçirildi. `bcprov-jdk18on` ve `bcpkix-jdk18on` Maven bağımlılıkları eklendi.
Test çalışma sınıf yolunda `bcprov`, `bcpkix` ve geçişli `bcutil` sürümlerinin
üçünün de 1.86 olduğu doğrulandı. Sürüm için [resmî dağıtım sayfası](https://www.bouncycastle.org/download/bouncy-castle-java/) kontrol edildi.

## Kod değişiklikleri

- `RsaKeyServiceImpl`: 2048 bit RSA anahtar üretimi açıkça Bouncy Castle
  sağlayıcısını kullanıyor. Sağlayıcı nesnesi ilgili işlemlere veriliyor;
  JVM'nin genel sağlayıcı sıralaması değiştirilmiyor.
- PEM okuma için `PEMParser` ve `JcaPEMKeyConverter`, yazma için
  `JcaPEMWriter` kullanılıyor. Elle yapılan Base64 kodlama/çözme ve
  `BEGIN/END` metni oluşturma/temizleme kaldırıldı.
- Private key yazımı `JcaPKCS8Generator` ile açıkça PKCS#8 seçiyor.
  Public key yazımı `JcaMiscPEMGenerator` ile SubjectPublicKeyInfo biçimini koruyor.
  Böylece yeni dosyalar önceki JDK tabanlı okuyucularla da okunabiliyor.
- Mevcut iki anahtar dosyası varsa yalnızca okunuyor; tekrar yazılmıyor.
  İkisi de yoksa bir çift üretiliyor. Bir dosya eksikse diğerinin üzerine
  yazılmadan hata veriliyor. RSA türü ve çiftin modulus eşleşmesi kontrol ediliyor.
- Gateway `JwtPublicKeyServiceImpl`, public key'i Bouncy Castle ile okuyor
  ve RSA anahtarı olmasını kontrol ediyor. Yeniden yükleme davranışı korunuyor;
  bozuk dosyadan yeniden yükleme başarısız olursa bellekteki eski anahtar kalıyor.

JJWT 0.12.6 token üretme ve doğrulama görevini sürdürür. RS256 algoritması,
`sub`, `uid`, `role`, `iat`, `exp` alanları ve varsayılan 8 saatlik süre değişmedi.
Login/register yanıtları, endpointler, Bearer normalizasyonu, alternatif
`X-Access-Token` desteği ve Postman koleksiyon dönüştürücüsü değiştirilmedi.
Frontend, veritabanı şeması, Docker Compose ve anahtar volume yolları değiştirilmedi.
OpenSSL kurulumu veya anahtar üretim scripti eklenmedi.

## Otomatik doğrulama

Makinede Java/Maven bulunmadığından testler `maven:3.9.9-eclipse-temurin-21`
Docker imajı içinde, her servisin dizininde `mvn -B test` ile çalıştırıldı.

| Servis | Toplam test | Başarısız | Hata |
| --- | ---: | ---: | ---: |
| Auth | 7 | 0 | 0 |
| API gateway | 13 | 0 | 0 |

Eklenen 8 test şunları kapsıyor:

1. Yeni anahtarların 2048 bit RSA ve mevcut PEM biçiminde olması; önceki JDK
   okuyucusuyla okunması; gerçek JWT alanları/süresi; yeniden başlatma uyumu.
2. Önceki JDK ile üretilmiş dosyaları değiştirmeden okuma ve eski tokenı doğrulama.
3. Eksik anahtar çiftinde mevcut dosyanın üzerine yazılmaması.
4. Farklı çiftlerden alınan anahtarların reddedilmesi.
5. Gerçek imzalı tokenın Bearer ve `X-Access-Token` ile gateway'den geçmesi.
6. Gateway'in eski PEM dosyası ve tokenı kabul etmesi.
7. Public key yeniden yükleme ve bozuk dosyada önceki anahtarın korunması.
8. Yanlış imzalı, süresi geçmiş ve çözülmemiş `{{token}}` değerlerinin 401;
   admin yoluna üye tokenıyla erişimin 403 alması.

Mevcut login, Bearer kopyalama/normalizasyon, gateway erişim ve Postman
koleksiyon testleri de bu toplamın içindedir. İlk test çalıştırması Bouncy Castle
varsayılan yazıcısının PKCS#1 seçtiğini yakaladı; PKCS#8 açıkça seçilerek düzeltildi
ve auth testlerinin tamamı yeniden başarıyla çalıştırıldı.

## Postman kullanımı

1. Her zamanki `baseUrl` ile `POST /api/auth/login` isteğini gönder.
2. Yanıttaki `accessToken` değerini kopyala.
3. Ortamın `token` değişkeninin VALUE alanına yapıştır.
4. Mevcut koleksiyondaki `X-Access-Token: {{token}}` ile istek gönder.
   Elle Bearer Token ayarlanmış isteklerde de `{{token}}` kullanılabilir.

Yeni bir değişken, token dönüştürme işlemi veya otomatik token kaydetme scripti yok.
Anahtar volume'u korunmalıdır; `docker compose down -v` anahtarları ve veritabanını
siler. Anahtar değişikliği ve token süresinin dolması mevcut kurallara göre oturumu
etkiler.

## Çalışan Docker servislerinde doğrulama

Auth ve gateway imajları yeniden derlendi. Mevcut volume'lar korunarak
`docker compose up -d --no-build --wait` ile uygulama başlatıldı.

- Eski imajdan geçiş öncesi admin tokenı alındı. Yeni auth imajının çalıştığı
  doğrulandıktan sonra aynı token `/api/auth/me` üzerinde HTTP 200 aldı.
- Auth container'ındaki `private.pem` ve `public.pem` SHA-256 özetleri geçiş
  öncesi ve sonrası eşleşti. Özel anahtar içerikleri dışarı aktarılmadı.
- Yeni admin ve üye girişlerinin yanıt alanları `accessToken`, `tokenType`,
  `expiresIn`, `user` olarak kaldı; `Bearer` ve 28800 saniye doğrulandı.
- Her iki header yöntemiyle admin ve üye kimlikleri doğrulandı; admin istatistik
  isteği 200, aynı isteğin üye tokenıyla yapılması 403 aldı. Üyenin kendi
  ilanlarına erişimi 200 aldı. Eksik/geçersiz tokenlar 401 aldı.
- Auth, property ve inquiries Postman koleksiyonlarının JSON içerik özetleri
  geçiş öncesi/sonrası aynı çıktı. Koleksiyon şeması ve `{{token}}` header'ları
  ayrıca kontrol edildi.
- Kullanıcının Postman'da eriştiği `http://localhost` adresinden Traefik üzerinden
  login yapıldı; yanıttaki tokenla Bearer ve `X-Access-Token` korumalı istekleri
  başarılı oldu. Üç koleksiyon import adresi de bu dış adresten başarılı oldu.
- Compose'daki sağlık kontrolü tanımlı altı servis healthy; Traefik çalışır
  durumda. Uygulama test sonrasında çalışır bırakıldı.
- `git diff --check` başarılı.

## Doğrulamanın sınırı

Postman masaüstü arayüzü otomasyonla kullanılmadı; aynı HTTP adresleri,
login yanıtından alınan gerçek tokenlar, header'lar ve import JSON'ları sınandı.
Bu değişiklik için kullanıcı akışının uyumluluğu doğrulandı. Yanlış ortam
seçimi, süresi dolmuş/eksik kopyalanmış token veya daha sonra silinen anahtar
volume'u dahil her koşul için hatasızlık garantisi verilmez. Bunlar yeni bir
Postman adımı gerektiren değişiklikler değildir.
