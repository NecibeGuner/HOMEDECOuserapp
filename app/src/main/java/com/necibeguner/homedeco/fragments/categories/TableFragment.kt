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

//Bu kod, bir fragment sınıfını temsil eder. TableFragment, BaseCategoryFragment sınıfından
// türetilmiştir ve Hilt tarafından yönetilen firestore bağımlılığını içerir. viewModel değişkeni,
// CategoryViewModel'in bir örneğini alır ve ilgili veri kaynağı türünü belirlemek için
// BaseCategoryViewModelFactory kullanılır. offerProducts ve bestProducts adında iki ayrı LiveData
// kaynağını gözlemleyerek, bunların güncellenmesini bekler ve bu güncel verileri RecyclerView'lara
// yansıtır. Hata durumlarında ise Snackbar ile kullanıcıya bilgi verilir.

// AndroidEntryPoint, Hilt'in bu fragmentı otomatik olarak bağlamasını sağlar.
@AndroidEntryPoint
// BaseCategoryFragment sınıfını miras alan ve AndroidEntryPoint ile işaretlenmiş TableFragment sınıfı.
class TableFragment : BaseCategoryFragment() {

    // FirebaseFirestore bağımlılığı için enjeksiyon.
    @Inject
    lateinit var firestore: FirebaseFirestore

    // CategoryViewModel'in lazım yüklenmesi ve ilgili fabrika sınıfının kullanılması.
    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Table)
    }

    // Fragment oluşturulduktan sonra yapılacak işlemler.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // offerProducts LiveData'sını izleyerek güncellemeleri alıp RecyclerView'a yansıtan işlemler.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.offerProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            // Yükleme durumunda yapılacak işlemler buraya yazılabilir.
                        }
                        is Resource.Success -> {
                            // Başarılı durumda, veriyi adapter'a ileterek RecyclerView'a yansıtılır.
                            offerAdapter.differ.submitList(it.data)
                        }
                        is Resource.Error -> {
                            // Hata durumunda Snackbar ile kullanıcıya bilgi verilir.
                            Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        // bestProducts LiveData'sını izleyerek güncellemeleri alıp RecyclerView'a yansıtan işlemler.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bestProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            // Yükleme durumunda yapılacak işlemler buraya yazılabilir.
                        }
                        is Resource.Success -> {
                            // Başarılı durumda, veriyi adapter'a ileterek RecyclerView'a yansıtılır.
                            bestProductsAdapter.differ.submitList(it.data)
                        }
                        is Resource.Error -> {
                            // Hata durumunda Snackbar ile kullanıcıya bilgi verilir.
                            Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

    }
}
