package com.necibeguner.homedeco.util

import android.util.Patterns

// Email'i doğrulamak için kullanılan fonksiyon. Parametre olarak bir email alır.
fun validateEmail(email: String): RegisterValidation {
    // Eğer email boş ise, başarısız bir doğrulama durumu ile birlikte hata mesajı döndürülür.
    if (email.isEmpty())
        return RegisterValidation.Failed("Email kısmı boş olamaz.")

    // Eğer email, standart bir email formatına uygun değilse, başarısız bir doğrulama durumu ile birlikte hata mesajı döndürülür.
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Email doğru formatta değil.")

    // Yukarıdaki şartları sağlamıyorsa, email doğrulaması başarılıdır ve başarılı bir doğrulama durumu döndürülür.
    return RegisterValidation.Success
}

// Şifreyi doğrulamak için kullanılan fonksiyon. Parametre olarak bir şifre alır.
fun validatePassword(password: String): RegisterValidation {
    // Eğer şifre boş ise, başarısız bir doğrulama durumu ile birlikte hata mesajı döndürülür.
    if (password.isEmpty())
        return RegisterValidation.Failed("Parola kısmı boş olamaz")

    // Eğer şifre, en az 6 karakterden oluşmuyorsa, başarısız bir doğrulama durumu ile birlikte hata mesajı döndürülür.
    if (password.length < 6)
        return RegisterValidation.Failed("Parola en az 6 karakter içermeli")

    // Yukarıdaki şartları sağlamıyorsa, şifre doğrulaması başarılıdır ve başarılı bir doğrulama durumu döndürülür.
    return RegisterValidation.Success
}
