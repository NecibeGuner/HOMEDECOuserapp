package com.necibeguner.homedeco.di
//di dependency injections ifadesinin kısaltması.Bağımlılık enjeksiyonu (dependency injection),
// bir yazılım mühendisliği tasarım desenidir ve bir bileşenin dış bağımlılıklarını, onlara doğrudan
// referanslar yerine, dışarıdan verilerek veya başka bir bileşen tarafından sağlanarak almasını sağlar.
// Bu, bir bileşenin, gereksinim duyduğu diğer bileşenleri doğrudan oluşturmak veya yönetmek zorunda kalmadan kullanabilmesini sağlar.


import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.necibeguner.homedeco.firebase.FirebaseCommon
import com.necibeguner.homedeco.util.Constants.INTRODUCTION_SP
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module // Bu kod bir Hilt/Dagger modülü tanımlar.
@InstallIn(SingletonComponent::class) // Bu modül içindeki bağımlılıkların yaşam süresini belirler; SingletonComponent, bağımlılıkların uygulama yaşam döngüsü boyunca tek bir örnek olarak saklanmasını sağlar.
object AppModule {

    @Provides
    @Singleton
    // FirebaseAuth nesnesini getiriyor
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    // FirebaseFirestore nesnesini getiriyor
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    @Provides
    // Uygulama genelinde kullanılacak bir SharedPreferences nesnesini getiriyor
    fun provideIntroductionsSP(
        application: Application
    ) = application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)

    @Provides
    @Singleton
    // FirebaseCommon nesnesini sağlayan bir yöntem; Firebase AuthService ve Firestore Service'lerini içerir.
    fun provideFirebaseCommon(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = FirebaseCommon(firestore, firebaseAuth)

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference
}
