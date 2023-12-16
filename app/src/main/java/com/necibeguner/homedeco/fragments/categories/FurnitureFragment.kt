package com.necibeguner.homedeco.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.Category
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.viewmodel.CategoryViewModel
import com.necibeguner.homedeco.viewmodel.factory.BaseCategoryViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

//FurnitureFragment sınıfı, BaseCategoryFragment'ten türetilmiş ve 'mobilya' kategorisi için teklifler
//ve en iyi ürünleri gösteren bir yapı sağlar. Açıklamalar, ViewModel kullanarak teklifler ve en
//iyi ürünlerin nasıl alındığını ve bu verilerin kullanıcı arayüzüne nasıl aktarıldığını anlatır.
//Teklifler ve en iyi ürünler için verilerin yüklenme durumunu, başarı durumunu veya hata durumunu
//işler ve kullanıcıya uygun geribildirimler sağlar.

// AndroidEntryPoint, Hilt'in bu fragmentı otomatik olarak bağlamasını sağlar.
@AndroidEntryPoint
class FurnitureFragment : BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore // Firestore bağlantısını enjekte eder

    // Kategori ViewModel'ini belirli bir kategori türüyle oluşturur
    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Furniture)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Teklifler ve en iyi ürünler verilerini toplamak için yaşam döngüsü kapsamında bir iş akışı başlatır
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.offerProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            // Yükleme durumunu göstermek için gerekli işlemler
                        }
                        is Resource.Success -> {
                            offerAdapter.differ.submitList(it.data) // Teklifler listesini günceller
                        }
                        is Resource.Error -> {
                            Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                            // Hata durumunda Snackbar ile kullanıcıya bilgi verme
                        }
                        else -> Unit
                    }
                }
            }
        }

        // Teklifler ve en iyi ürünler verilerini toplamak için yaşam döngüsü kapsamında bir iş akışı daha başlatır
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bestProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            // Yükleme durumunu göstermek için gerekli işlemler
                        }
                        is Resource.Success -> {
                            bestProductsAdapter.differ.submitList(it.data) // En iyi ürünler listesini günceller
                        }
                        is Resource.Error -> {
                            Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                            // Hata durumunda Snackbar ile kullanıcıya bilgi verme
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}
