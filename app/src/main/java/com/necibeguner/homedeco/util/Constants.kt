package com.necibeguner.homedeco.util

//sabit değerleri saklamak için kullanılan bir Constants (Sabitler) nesnesini içerir.

object Constants {
    const val USER_COLLECTION = "user"//USER_COLLECTION: Firebase Firestore'da kullanıcılar için oluşturulmuş koleksiyonun adı
    //user Kullanıcı koleksiyon adı için sabit bir değer
    const val INTRODUCTION_SP = "IntroductionSp"//INTRODUCTION_SP: Uygulama içindeki tanıtım sürecini kontrol etmek için
    // kullanılan SharedPreferences anahtar adı. Bu, uygulamanın tanıtım ekranlarını göstermek veya göstermemek için
    // kullanılabilir. Kullanıcının uygulamayı ilk kez açtığında veya belirli bir süre boyunca
    // ekranları göstermek için kullanılabilir.
    const val INTRODUCTION_KEY = "IntroductionKey"//INTRODUCTION_KEY: Tanıtım işlemi sırasında kullanılan anahtar değeri.
    // Bu, tanıtımın durumunu izlemek veya saklamak için kullanılabilir, böylece kullanıcıya tekrar tekrar tanıtım ekranlarını
    // göstermekten kaçınılabilir.
}