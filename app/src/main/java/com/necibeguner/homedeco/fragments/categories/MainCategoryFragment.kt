package com.necibeguner.homedeco.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.adapters.BestDealsAdapter
import com.necibeguner.homedeco.adapters.BestProductsAdapter
import com.necibeguner.homedeco.adapters.SpecialProductsAdapter
import com.necibeguner.homedeco.databinding.FragmentMainCategoryBinding
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.util.showBottomNavigationView
import com.necibeguner.homedeco.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//Bu kod, bir fragment içinde belirli RecyclerView'ları ve bunların adaptörlerini hazırlar,
// ViewModel'den gelen verileri bu RecyclerView'larla bağlar ve kullanıcı etkileşimlerine yanıt verir.
// Yükleme durumlarını ve hataları işler ve gerekli görüntüleri gösterir veya gizler. Ayrıca,
// fragment tekrar aktif olduğunda alttaki navigasyon çubuğunu gösterir.


// Fragment'ın içindeki TAG değişkeni, loglama işlemleri için bir etiket oluşturuyor.
private val TAG = "MainCategoryFragment"
// AndroidEntryPoint, Hilt'in bu fragmentı otomatik olarak bağlamasını sağlar.
@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_category) {
    // FragmentMainCategoryBinding sınıfından bir değişken, fragment'ın layoutunu bağlamak için kullanılıyor.
    private lateinit var binding : FragmentMainCategoryBinding
    // Özel ürünleri göstermek için kullanılan adaptör.
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    // En iyi fırsatları göstermek için kullanılan adaptör.
    private lateinit var bestDealsAdapter : BestDealsAdapter
    // En iyi ürünleri göstermek için kullanılan adaptör.
    private lateinit var bestProductsAdapter: BestProductsAdapter
    // ViewModel'in yüklenmesi.
    private val viewModel by viewModels<MainCategoryViewModel>()

    // Fragment'ın görünümünü oluşturma işlemi.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    // Görünüm oluşturulduktan sonra yapılacak işlemler.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Özel ürünler, en iyi fırsatlar ve en iyi ürünler için RecyclerView'ları hazırlayan metodlar.
        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProducts()

        // Özel ürünler, en iyi fırsatlar ve en iyi ürünler için tıklama işlemleri.
        specialProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        bestDealsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        bestProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        // Özel ürünler, en iyi fırsatlar ve en iyi ürünlerin güncellemelerini izleyen ve görsel olarak eşleştiren işlemler.
        // Her biri farklı bir LiveData'yı dinliyor.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.specialProducts.collectLatest {
                    when(it){
                        is Resource.Loading ->{
                            showLoading()
                        }
                        is Resource.Success ->{
                            specialProductsAdapter.differ.submitList(it.data)
                            hideLoading()
                        }
                        is Resource.Error ->{
                            hideLoading()
                            Log.e(TAG,it.message.toString())
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.bestDealsProducts.collectLatest {
                    when(it){
                        is Resource.Loading ->{
                            showLoading()
                        }
                        is Resource.Success ->{
                            bestDealsAdapter.differ.submitList(it.data)
                            hideLoading()
                        }
                        is Resource.Error ->{
                            hideLoading()
                            Log.e(TAG,it.message.toString())
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.bestProducts.collectLatest {
                    when(it){
                        is Resource.Loading ->{
                            binding.bestProductsProgressbar.visibility = View.VISIBLE
                        }
                        is Resource.Success ->{
                            bestProductsAdapter.differ.submitList(it.data)
                            binding.bestProductsProgressbar.visibility = View.GONE
                        }
                        is Resource.Error ->{
                            Log.e(TAG,it.message.toString())
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            binding.bestProductsProgressbar.visibility = View.GONE
                        }
                        else -> Unit
                    }
                }
            }
        }
        // Diğer LiveData'ları izleyen ve güncellenen verileri RecyclerView'lara bağlayan benzer işlemler yapılıyor.
        // Her biri farklı bir LiveData'yı dinliyor.
        // Hatalı durumları loglar ve kullanıcıya bir Toast mesajıyla bildirir.
        // Üstelik her biri farklı bir RecyclerView'ı güncelliyor.

        // nestedScrollMainCategory'nin scroll değişimlerini dinleyerek, en iyi ürünlerin daha
        // fazlasını yüklemek için ViewModel'deki bir fonksiyonu çağırıyor.

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v,_,scrollY,_,_ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.fetchBestProducts()
            }
        })
    }

    // En iyi ürünler için RecyclerView'ı hazırlayan metod.
    private fun setupBestProducts() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProduct.apply {
            layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
            adapter = bestProductsAdapter
        }
    }

    // En iyi fırsatlar için RecyclerView'ı hazırlayan metod.
    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProduct.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = bestDealsAdapter
        }
    }


    // Yükleniyor göstergesini gizleyen metod
    private fun hideLoading() {
        binding.mainCategoryProgressbar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressbar.visibility = View.VISIBLE
    }

    // Özel ürünler için RecyclerView'ı hazırlayan metod.
    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProduct.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = specialProductsAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }
}