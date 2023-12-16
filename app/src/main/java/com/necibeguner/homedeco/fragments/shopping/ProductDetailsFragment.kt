package com.necibeguner.homedeco.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.necibeguner.homedeco.adapters.ColorsAdapter
import com.necibeguner.homedeco.adapters.SizesAdapter
import com.necibeguner.homedeco.adapters.ViewPager2Images
import com.necibeguner.homedeco.data.CartProduct
import com.necibeguner.homedeco.databinding.FragmentProductDetailsBinding
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.util.hideBottomNavigationView
import com.necibeguner.homedeco.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//Bu kod parçası, ürün detaylarının gösterildiği bir fragment. Kullanıcıya ürünün adı, fiyatı, açıklaması,
// renkleri, bedenleri ve resimleri gibi detayları sunar. Kullanıcı seçtiği renk ve bedeni seçerek
// ürünü sepete ekleyebilir. Bu işlemleri gerçekleştirirken, ViewModel tarafından yönetilen işlemleri
// takip eder ve uygun geri bildirimleri gösterir.

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>() // Fragment'a geçirilen argümanları almak için kullanılan değişken
    private lateinit var binding: FragmentProductDetailsBinding // Fragment'ın bağlamını tutacak değişken
    private val viewPagerAdapter by lazy { ViewPager2Images() } // Ürün resimlerini göstermek için ViewPager adaptörü
    private val sizesAdapter by lazy { SizesAdapter() } // Ürün bedenlerini göstermek için adaptör
    private val colorsAdapter by lazy { ColorsAdapter() } // Ürün renklerini göstermek için adaptör
    private var selectedColor: Int? = null // Seçili renk bilgisini tutacak değişken
    private var selectedSize: String? = null // Seçili beden bilgisini tutacak değişken
    private val viewModel by viewModels<DetailsViewModel>() // Ürün detayları veri modeli

    // Görünüm oluşturulduğunda çalışacak metot
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigationView() // Alt gezinti çubuğunu gizleyen metot
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    // Görünüm oluşturulduktan sonra yapılacak işlemler
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = args.product // Argümandan gelen ürün bilgisi

        setupSizesRv() // Bedenleri gösteren RecyclerView'un hazırlanması
        setupColorsRv() // Renkleri gösteren RecyclerView'un hazırlanması
        setupViewpager() // Resimleri gösteren ViewPager'ın hazırlanması

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp() // Geri tuşuna basıldığında fragment'ten çıkış yapılması
        }

        sizesAdapter.onItemClick = {
            selectedSize = it // Seçilen bedenin belirlenmesi
        }

        colorsAdapter.onItemClick = {
            selectedColor = it // Seçilen rengin belirlenmesi
        }

        binding.buttonAddToCart.setOnClickListener {
            // Sepete ürün ekleme işlemi
            viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize))
        }

        // Sepete ürün ekleme işleminin takibini yapacak yaşam döngüsü
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addToCart.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.buttonAddToCart.startAnimation() // Sepete ekleme animasyonu başlatma
                        }
                        is Resource.Success -> {
                            binding.buttonAddToCart.revertAnimation() // Sepete ekleme animasyonunu geri alma
                            Toast.makeText(requireContext(), "Sepete eklendi", Toast.LENGTH_SHORT).show() // Başarılı ekleme mesajı gösterme
                        }
                        is Resource.Error -> {
                            binding.buttonAddToCart.stopAnimation() // Sepete ekleme animasyonunu durdurma
                            Toast.makeText(requireContext(), "Sepete ekleme başarısız", Toast.LENGTH_SHORT).show() // Hata mesajı gösterme
                        }
                        else -> Unit
                    }
                }
            }
        }

        // Ürün detaylarını gösterme işlemleri
        binding.apply {
            tvProductName.text = product.name // Ürün adını gösterme
            tvProductPrice.text = "${product.price} TL" // Ürün fiyatını gösterme
            tvProductDetails.text = product.description // Ürün açıklamasını gösterme
            if (product.colors.isNullOrEmpty())
                tvProductColors.visibility = View.INVISIBLE // Renkler boşsa, renkleri gösterme alanını gizleme
            if (product.sizes.isNullOrEmpty())
                tvProductSize.visibility = View.INVISIBLE // Bedenler boşsa, bedenleri gösterme alanını gizleme
        }

        viewPagerAdapter.differ.submitList(product.images) // Resimleri ViewPager'a yükleme
        product.colors?.let { colorsAdapter.differ.submitList(it) } // Renkleri RecyclerView'a yükleme
        product.sizes?.let { sizesAdapter.differ.submitList(it) } // Bedenleri RecyclerView'a yükleme
    }

    private fun setupViewpager() {
        binding
            .apply {
                viewPagerProductImages.adapter = viewPagerAdapter // ViewPager'ın adaptörünü ayarlama
            }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter = colorsAdapter // Renkleri gösteren RecyclerView'ın adaptörünü ayarlama
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) // Yatay yönlü LinearLayoutManager kullanarak düzen ayarlama
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter // Bedenleri gösteren RecyclerView'ın adaptörünü ayarlama
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) // Yatay yönlü LinearLayoutManager kullanarak düzen ayarlama
        }
    }
}
