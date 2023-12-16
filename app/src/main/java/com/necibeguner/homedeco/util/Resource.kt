package com.necibeguner.homedeco.util

// 'Resource' adında bir sealed class (mühürlü sınıf) tanımlanıyor.
// Bu sınıf, farklı veri işleme durumlarını temsil etmek için kullanılır.
sealed class Resource<T>(
    val data: T? = null,          // Veriyi içerecek olan değişken
    val message: String? = null    // Durumla ilişkili mesajı içerecek olan değişken
) {
    // 'Success' sınıfı, başarılı bir durumu temsil eder.
    // Generic tip T'yi alır ve veri içerebilir.
    class Success<T>(data: T) : Resource<T>(data)

    // 'Error' sınıfı, bir hata durumunu temsil eder.
    // Hata durumunda bir mesaj içerir.
    class Error<T>(message: String) : Resource<T>(message = message)

    // 'Loading' sınıfı, bir yükleme durumunu temsil eder.
    // Veri yüklenirken kullanılır ve veri içermez.
    class Loading<T> : Resource<T>()

    class Unspecifed<T>: Resource<T>()
}
