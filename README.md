# HOMEDECO User App

Bu depo, Kocaeli Üniversitesi Bilişim Sistemleri Mühendisliği öğrencileri tarafından geliştirilen **HOMEDECO Mobilya Satış Uygulaması**nın son kullanıcı (customer) Android uygulamasının kaynak kodlarını içerir. Uygulama geliştirme sürecinde **Kotlin**, **Android Studio** ve **Firebase** teknolojileri kullanılmıştır.

## Yazar

* **Necibe GÜNER** – [221307049@kocaeli.edu.tr](mailto:221307049@kocaeli.edu.tr)

## Proje Hakkında

HOMEDECO User App, müşterilerin Android cihazları üzerinden mobilya ürünlerini inceleyebilecekleri, sepet oluşturarak sipariş verebilecekleri ve profil bilgilerini yönetebilecekleri bir mobil uygulamadır. Firebase altyapısı ile ürün verileri ve siparişler gerçek zamanlı olarak senkronize edilmektedir.

### Temel Özellikler

* Ürün katalogunu kategori bazlı listeleme ve detay görüntüleme
* Arama ve filtreleme
* Ürünleri sepete ekleyip düzenleme
* Sipariş oluşturma ve geçmiş siparişleri görüntüleme
* Kullanıcı kaydı, giriş (Firebase Authentication) ve profil düzenleme
* Push bildirimler ile sipariş durumu takibi (Firebase Cloud Messaging)
* Uygulama içi destek ve geri bildirim formu

### Teknoloji Yığını

* Kotlin (Android uygulama dili)
* Android Studio (IDE)
* Firebase Authentication
* Firebase Firestore (veritabanı)
* Firebase Cloud Storage (ürün görselleri)
* Firebase Cloud Messaging (push bildirimleri)
* Material Components for Android

## Kurulum

1. Depoyu klonlayın:

   ```bash
   git clone https://github.com/NecibeGuner/HOMEDECOuserapp.git
   cd HOMEDECOuserapp
   ```
2. Android Studio ile projeyi açın:

   * **Open an existing Android Studio project** seçeneği ile proje klasörünü seçin.
3. Firebase yapılandırma dosyasını ekleyin:

   * Firebase Console üzerinden Android uygulama ekleyin.
   * İndirdiğiniz `google-services.json` dosyasını `app/` klasörüne yerleştirin.
4. Gradle projeyi senkronize edin:

   * Android Studio içinde **Sync Project with Gradle Files** butonuna tıklayın.
5. Uygulamayı çalıştırın:

   * Emulator veya USB ile bağlı cihazı seçip **Run** butonuna basın.

## Proje Yapısı

```
HOMEDECOuserapp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/homedeco/user/   # Kotlin kaynak kodları
│   │   │   ├── res/                     # XML layout, drawable, values
│   │   │   └── AndroidManifest.xml     # Uygulama tanımı
│   └── build.gradle                    # Modül yapılandırması
├── build.gradle                        # Proje yapılandırması
├── settings.gradle
├── google-services.json                # Firebase yapılandırma dosyası
└── README.md                           # Proje dokümantasyonu
```

## Kullanım

1. Uygulamayı açtıktan sonra kayıt veya giriş ekranından işlemi tamamlayın.
2. Ana ekranda ürün kataloğunu gezin, beğendiğiniz ürünleri sepete ekleyin.
3. Sepet ekranından sipariş detaylarını kontrol edip onaylayın.
4. Profil menüsünden hesap bilgilerinizi ve adreslerinizi güncelleyebilir, sipariş geçmişinizi görüntüleyebilirsiniz.
5. Sipariş durumu güncellemeleri push bildirimlerle iletilecektir.

## Lisans

Bu proje akademik amaçlı geliştirilmiştir ve ticari bir lisansa tabi değildir.

## İletişim

Her türlü soru ve geri bildirim için lütfen e-posta yoluyla ulaşın:

* Necibe GÜNER – [221307049@kocaeli.edu.tr](mailto:221307049@kocaeli.edu.tr)
