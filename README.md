# Twitter API — Spring Boot & React

**Java 21, Spring Boot, Spring Security, JWT ve PostgreSQL** ile geliştirilen, kimlik doğrulama ve tweet oluşturma için küçük bir **React + Vite** frontend'i içeren Twittervari bir RESTful API.

Bu proje **Workintech Full Stack Web Geliştirme Programı - Spring 19 Challenge** kapsamında geliştirildi.

## İçerikler

### Kimlik Doğrulama & Güvenlik
- Kullanıcı kaydı ve giriş olması
- BCrypt şifreleme hashing'i
- JWT tabanlı kimlik doğrulama
- Stateless Spring Security konfigürasyonu
- Korumalı API endpoint'leri
- Sahiplik tabanlı yetkilendirme
- Global exception handling ve girdi doğrulama

### Tweet Yönetimi
- Tweet oluşturma (280 karaktere kadar)
- ID'ye göre tweet alma
- Kullanıcı ID'sine göre tweet alma
- Tweet güncelleme
- Tweet silme
- Bir tweet silindiğinde onla ilişkili yorum, beğeni ve retweetleri kaldırma

### Yorum Yönetimi
- Tweetlerde yorum oluşturma
- Kişisel yorumları güncelleme
- Yorum sahibi veya tweet sahibi olarak tweet silme
- Bir tweet'le alakalı yorumları alma

### Beğeniler & Retweetler
- Tweetleri beğenme
- Çifte beğeniyi önleme
- Retweet oluşturma ve silme
- Çifte retweeti önleme
- Retweet sahiplik kuralını uygulama

### React Entegrasyonu
- Kullanıcı kaydı
- Kullanıcı girişi ve çıkışı
- React state kullanarak geçici JWT yönetimi
- Doğrulanmış tweet oluşturma
- Form doğrulama ve API geri bildirimi
- Spring Boot ile CORS iletişimi

## Kullanılan Teknolojiler

| Kategori | Teknolojiler |
|---|---|
| Backend | Java 21, Spring Boot 4.1.2 |
| Security | Spring Security, JWT, BCrypt |
| Veritabanı | PostgreSQL, Spring Data JPA, Hibernate |
| Doğrulama | Jakarta Bean Validation |
| Frontend | React, Vite, JavaScript, CSS |
| Test | JUnit 5, Mockito, Insomnia |
| Kapsam | JaCoCo |
| Derleme Araçları | Maven Wrapper, npm |
| Versiyon Kontrolü | Git, GitHub |

## Proje Yapısı

```text
FSWEB-s19-Challenge/
├── src/
│   ├── main/
│   │   ├── java/com/workintech/twitterapi/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   │       └── impl/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/workintech/twitterapi/
│           └── service/
├── frontend/
│   ├── src/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   └── index.css
│   ├── package.json
│   └── vite.config.js
├── pom.xml
└── README.md
```

## API Endpointleri

Backend `http://localhost:3000` adresinde çalışmaktadır.

### Kimilk Doğrulama

| Metod | Endpoint | Açıklama |
|---|---|---|
| POST | `/register` | Yeni kullanıcı kaydı |
| POST | `/login` | Kimlik doğrulama ve JWT alma |

### Tweetler

| Metod | Endpoint | Açıklama |
|---|---|---|
| POST | `/tweet` | Tweet oluşturma |
| GET | `/tweet/findById?id={id}` | Tweet alma |
| GET | `/tweet/findByUserId?userId={id}` | Bir kullanıcının tweetini alma |
| PUT | `/tweet/{id}` | Tweet güncelleme |
| DELETE | `/tweet/{id}` | Tweet silme |

### Yorumlar

| Metod | Endpoint | Açıklama |
|---|---|---|
| POST | `/comment` | Yorum gönderme |
| GET | `/comment/findByTweetId?tweetId={id}` | Bir tweetin yorumlarını getirme |
| PUT | `/comment/{id}` | Yorum güncelleme |
| DELETE | `/comment/{id}` | Yorum silme |

### Beğeniler

| Method | Endpoint | Description |
|---|---|---|
| POST | `/like` | Tweet beğenme |
| POST | `/dislike` | Beğenilen tweetin beğenisini kaldırma |

### Retweets

| Method | Endpoint | Description |
|---|---|---|
| POST | `/retweet` | Mevcut bir tweeti retweetleme |
| DELETE | `/retweet/{id}` | Retweeti silme |

Kullanıcı kaydı ve girişi harici tüm endpointler kimlik doğrulama gerektirmektedir.

## Başlarken

### Ön Koşullar

- Java 21
- PostgreSQL
- Yüklenmiş Vite versiyonu ile uyumlu Node.js and npm
- Git
- Insomnia gibi bir API test aracı (opsiyonel)

### 1. Repository'i klonlama

```bash
git clone https://github.com/keremgarip/FSWEB-s19-Challenge.git
cd FSWEB-s19-Challenge
```

### 2. PostgreSQL'i Kurma

Lokal bir PostgreSQL veritabanı oluşturun:

```sql
CREATE DATABASE twitter_db;
```

PostgreSQL'in `5432` portunda çalıştığından emin olun.

### 3. Backend'i düzenleme

Bu uygulama hassas bilgiler için yerel yapılandırma kullanmaktadır.

Aşağıdaki dosyayı oluşturun:

`src/main/resources/application-local.properties`

Örnek:

```properties
spring.datasource.password=YOUR_POSTGRES_PASSWORD
jwt.secret=YOUR_BASE64_ENCODED_SECRET
```

JWT secret HS256 (en az 256 bit) için yeterli kriptografik anahtar içermelidir.

Backend'in kalan yapılandırması aşağıdakileri içermektedir:

```properties
server.port=3000
spring.datasource.url=jdbc:postgresql://localhost:5432/twitter_db
spring.datasource.username=postgres
spring.jpa.hibernate.ddl-auto=update
spring.profiles.active=local
jwt.expiration=86400000
```

Eğer `postgres`ten farklıysa kendi PostgreSQL isminizi kullanınız.

**Asla gerçek veritabanı şifreler, JWT secretları veya yerel kimlik bilgilerini GitHub'la paylaşmayın.**

### 4. Backend'i çalıştırma

Windows'ta:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend aşağıda çalışmaya başlayacak:

`http://localhost:3000`

### 5. Frontend'i yapılandırma

Frontend dizinine erişiniz:

```bash
cd frontend
npm install
```

`frontend/.env` dosyasını aşağıdaki içerikle oluşturun:

```dotenv
VITE_API_URL=http://localhost:3000
```

### 6. Frontend'i çalıştırma

```bash
npm run dev
```

Açınız:

`http://localhost:3200`

Frontend, yaılandırılan API URL aracılığıyla backend'e bağlanır.

## Örnek API İstekleri

### Kaydolma

`POST /register`

```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "SecurePassword123"
}
```

### Kullanıcı Girişi

`POST /login`

```json
{
  "username": "testuser",
  "password": "SecurePassword123"
}
```

Bir başarılı giriş korumalı API isteklerine dahil edilebilecek bir JWT döndürür:

```http
Authorization: Bearer YOUR_JWT_TOKEN
```

### Tweet Oluşturma

`POST /tweet`

```json
{
  "content": "My first tweet using Spring Boot and React!"
}
```

### Yorum Oluşturma

`POST /comment`

```json
{
  "tweetId": 1,
  "content": "This is a test comment."
}
```

### Beğeni veya Retweet

`POST /like` or `POST /retweet`

```json
{
  "tweetId": 1
}
```

Mevcut bir beğeniyi kaldırmak için, aynı istek body'sini `POST /dislike`a gönderiniz.

## Test

Bu proje JUnit 5 ve Mockito ile geliştirilen otomatik unit testleri içermektedir.

Test edilen bileşenler aşağıdaki gibidir:

- TweetServiceImpl
- CommentServiceImpl
- LikeServiceImpl
- RetweetServiceImpl

Tüm backend testlerini çalıştırma:

```powershell
.\mvnw.cmd test
```

JaCoCo kapsamını oluşturup doğrulama

```powershell
.\mvnw.cmd verify
```

Oluşan raporu açınız:

`target/site/jacoco/index.html`

### Kapsam Sonuçları

| Metrik | Sonuç |
|---|---|
| Unit Testler | 28 |
| Metod Kapsamı | 54.96% |
| İstenen Metod Kapsamı | 30% |
| Kapsam Gerekliliği | Geçti |

Kapsam sonuçları geliştirme esnasında yerel JaCoCo raporundan edinildi.

### Frontend Doğrulaması

`frontend` dizini içerisinde:

```bash
npm run lint
npm run build
```

İki komut geliştirme esnasında başarıyla doğrulandı.

## Güvenlik & Yetkilendirme

API kullanıcıları tespit etmek için JWT yetkilendirmesini kullanıyor.

Yetkilendirme frontend tarafından sağlanan kullanıcı ID'lere güvenmekten ziyade backend tarafında uygulanmaktadır.

Ana kurallar şunları içerir:

- Kullanıcılar yalnızca kendi tweetlerini günceller ve siler.
- Kullanıcılar yalnızca kendi yorumlarını günceller.
- Yorum sahipleri veya tweet sahipleri yorumları silebilir.
- Kullanıcılar başkası tarafından yapılan beğeniyi kaldıramaz.
- Kullanıcılar yalnızca kendi retweetlerini silebilir.
- Yinelenen beğeni ve retweetler uygulama düzeyinde doğrulama ve veritabanı benzersizlik kısıtlamaları aracılığıyla engellenir.

React uygulaması JWT'leri component'te geçici olarak saklar. Sayfayı yenilemek yerel kimlik doğrulama state'ini temizler.

## Geliştirme Notları

Bu proje backend mimarisi, güvenlik, RESTful API tasarımı, ilişkili veri modellemesi ve otomatik testlere odaklanmaktadır.

React frontend'i tümüyle bir Twitter klonu olmaktan ziyade küçük bir entegrasyon arayüzüdür.

Uygulama yerel geliştirme için yapılandırılmıştır. Ürünün canlıya alınması çevreye özgü uygun yapılandırma, güvenli kimlik bilgileri yönetimi, ve veritabanı geçiş uygulamalarını gerekli tutar.

## Projeyi Oluşturan - Kerem Garip

Workintech Full Stack Web Geliştirme Programı kapsamında geliştirildi.
