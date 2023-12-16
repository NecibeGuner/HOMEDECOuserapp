package com.necibeguner.homedeco.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.adapters.BestProductsAdapter
import com.necibeguner.homedeco.databinding.FragmentBaseCategoryBinding
import com.necibeguner.homedeco.util.showBottomNavigationView

//Bu kod bloğu, base category fragmentini temsil eder ve içerisinde tekliflerin ve en
// iyi ürünlerin gösterildiği bir yapı sağlar. Açıklamalar, fragmentin yaşam döngüsü olaylarını
// okurken ne yapacağını ve hangi işlevleri uyguladığını açıklar. Örneğin, teklifler ve en iyi
// ürünler için kaydırma işlemleri, yükleme durumlarının gösterilmesi, sayfalama isteklerinin nasıl
// yapıldığı gibi durumları belirtir. Alt sınıflar, sayfalama isteklerini uygulamak için
// onOfferPagingRequest ve onBestProductsPagingRequest işlevlerini uyarlayabilirler.

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding // Fragment için bağlama değişkeni

    // Teklif ve en iyi ürünler için adaptörlerin tembellikle (lazy) oluşturulması
    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root // Bağlama dosyasının kök görünümünü döndürür
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRv() // Teklifler RecyclerView'ini hazırlar
        setupBestProductsRv() // En iyi ürünler RecyclerView'ini hazırlar

        // En iyi ürünler ve teklifler adaptörlerine tıklama işlevlerini ekler
        bestProductsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        offerAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        // Teklifler RecyclerView'ini kaydırma olaylarını dinler ve sayfalama isteği yapar
        binding.rvOfferProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollHorizontally(1) && dx != 0)
                    onOfferPagingRequest()
            }
        })

        // En iyi ürünler NestedScrollView'ini kaydırma olaylarını dinler ve sayfalama isteği yapar
        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                onBestProductsPagingRequest()
            }
        })
    }

    // Tekliflerin yüklenme durumunu gösterir
    fun showOfferLoading() {
        binding.offerProductsProgressBar.visibility = View.VISIBLE
    }

    // Tekliflerin yüklenme durumunu gizler
    fun hideOfferLoading() {
        binding.offerProductsProgressBar.visibility = View.GONE
    }

    // En iyi ürünlerin yüklenme durumunu gösterir
    fun showBestProductsLoading() {
        binding.bestProductsProgressBar.visibility = View.VISIBLE
    }

    // En iyi ürünlerin yüklenme durumunu gizler
    fun hideBestProductsLoading() {
        binding.bestProductsProgressBar.visibility = View.GONE
    }

    // Teklifler için sayfalama isteği işlevi, alt sınıflar tarafından uyarlanabilir
    open fun onOfferPagingRequest() {}

    // En iyi ürünler için sayfalama isteği işlevi, alt sınıflar tarafından uyarlanabilir
    open fun onBestProductsPagingRequest() {}

    // En iyi ürünler RecyclerView'i için düzenlemeleri yapılandırır
    private fun setupBestProductsRv() {
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    // Teklifler RecyclerView'i için düzenlemeleri yapılandırır
    private fun setupOfferRv() {
        binding.rvOfferProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView() // Alt gezinme çubuğunu gösterir
    }
}
