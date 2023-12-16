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

//Bu fragment AccessoryFragment sınıfını temsil eder. Açıklamalar, bu sınıfın ViewModel'den gelen verileri
// alarak ve duruma göre ekranda ilgili işlemleri gerçekleştirerek, bir fragment içindeki teklif ve en
// iyi ürünleri göstermekte nasıl kullanıldığını açıklar. offerProducts ve bestProducts gibi veri kaynaklarından
// gelen durumlara göre ekranın nasıl güncellendiğini gösterir.

// AndroidEntryPoint, Hilt'in bu fragmentı otomatik olarak bağlamasını sağlar.
@AndroidEntryPoint
class AccessoryFragment : BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore // Firestore'dan veri almak ve göndermek için kullanılacak Firestore referansı

    // Kategori görünüm modelini (ViewModel) lazy initialization ile alır, accessorize kategorisini göstermek için kullanılır
    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Accessory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tekrar edilen yaşam döngüsü olaylarını işlemek için viewLifecycleOwner'ın yaşam alanı kapsamında coroutine başlatılır
        viewLifecycleOwner.lifecycleScope.launch {
            // İlgili yaşam döngüsü STATE_STARTED durumundayken işlemleri tekrar eden yaşam döngüsüne alır
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // ViewModel tarafından sunulan teklif ürünlerini akıştan alır ve işler
                viewModel.offerProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            // Yükleme durumunda ekranı güncelle
                            // Burada bir şeyler yapılabilir, örneğin: ilerleme çubuğu gösterme
                        }
                        is Resource.Success -> {
                            // Başarılı durumda, teklif ürünleri adaptörüne yeni verileri sunar
                            offerAdapter.differ.submitList(it.data)
                        }
                        is Resource.Error -> {
                            // Hata durumunda, Snackbar ile bir hata mesajı gösterir
                            Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        // Tekrar edilen yaşam döngüsü olaylarını işlemek için viewLifecycleOwner'ın yaşam alanı kapsamında coroutine başlatılır
        viewLifecycleOwner.lifecycleScope.launch {
            // İlgili yaşam döngüsü STATE_STARTED durumundayken işlemleri tekrar eden yaşam döngüsüne alır
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // ViewModel tarafından sunulan en iyi ürünleri akıştan alır ve işler
                viewModel.bestProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            // Yükleme durumunda ekranı güncelle
                            // Burada bir şeyler yapılabilir, örneğin: ilerleme çubuğu gösterme
                        }
                        is Resource.Success -> {
                            // Başarılı durumda, en iyi ürünler adaptörüne yeni verileri sunar
                            bestProductsAdapter.differ.submitList(it.data)
                        }
                        is Resource.Error -> {
                            // Hata durumunda, Snackbar ile bir hata mesajı gösterir
                            Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}
