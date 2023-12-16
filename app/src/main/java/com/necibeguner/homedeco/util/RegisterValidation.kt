package com.necibeguner.homedeco.util

// Mühürlü (sealed) bir sınıf olan RegisterValidation, kayıt doğrulama durumlarını temsil eder.
sealed class RegisterValidation() {
    // Kayıt doğrulamasının başarılı olduğunu belirten bir nesne (object).
    object Success : RegisterValidation()

    // Kayıt doğrulamasının başarısız olduğunu ve bir hata mesajı içerdiğini belirten veri sınıfı (data class).
    data class Failed(val message: String) : RegisterValidation()
}

// Kayıt alanlarının durumunu temsil eden bir veri sınıfı (data class).
data class RegisterFieldsState(
    val email: RegisterValidation, // Email doğrulama durumu
    val password: RegisterValidation // Şifre doğrulama durumu
)
