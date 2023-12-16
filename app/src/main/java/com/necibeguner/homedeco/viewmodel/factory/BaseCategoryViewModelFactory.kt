package com.necibeguner.homedeco.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.Category
import com.necibeguner.homedeco.viewmodel.CategoryViewModel

class BaseCategoryViewModelFactory(
    private val firestore: FirebaseFirestore,//firestore: FirebaseFirestore türünde bir nesne. Bu, Firestore veritabanına erişmek için kullanılır.
    private val category: Category//category: Category türünde bir nesne. Bu, ViewModel oluşturulurken belirli bir kategoriye ait bilgileri temsil eder.
) : ViewModelProvider.Factory {
    // ViewModelProvider.Factory sınıfından türetilen bu sınıf, ViewModel örneklerini oluşturmak için kullanılır.

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // ViewModelProvider.Factory'nin abstract olan create metodu, ViewModel örneklerini oluşturmak için kullanılır.

        // Verilen sınıf tipine göre bir ViewModel örneği oluşturulur.
        // Burada, CategoryViewModel sınıfı oluşturulur ve firestore ve category parametreleri ile başlatılır.
        return CategoryViewModel(firestore, category) as T
        // Oluşturulan ViewModel, çağrıldığı yerde istenilen tipe (T) uygun olarak döndürülür.
    }
}
