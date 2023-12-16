package com.necibeguner.homedeco.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.adapters.CartProductAdapter
import com.necibeguner.homedeco.databinding.FragmentCartBinding
import com.necibeguner.homedeco.firebase.FirebaseCommon
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.util.VerticalItemDecoration
import com.necibeguner.homedeco.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//Bu kod, alışveriş sepeti ekranını yöneten bir fragment. Sepet içeriğini göstermek, ürünlerin
// miktarını değiştirmek, ürünleri silmek ve ödeme işlemine gitmek gibi işlevleri barındırıyor.
// Sepetin boş olup olmadığını kontrol ederek, gerektiğinde ilgili görünümleri gösterip gizleyebiliyor.
// Ayrıca, kullanıcıya ürünü silme işlemi hakkında onay aldıktan sonra silme işlemini gerçekleştirme imkanı da sunuyor.

class CartFragment : Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding // Fragment'ın bağlamını tutacak değişken
    private val cartAdapter by lazy { CartProductAdapter() } // Alışveriş sepetindeki ürünleri göstermek için adaptör
    private val viewModel by activityViewModels<CartViewModel>() // Alışveriş sepeti veri modeli

    // Görünüm oluşturulduğunda çalışacak metot
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Bağlam oluşturuluyor ve root görünümü geri döndürülüyor
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    // Görünüm oluşturulduktan sonra yapılacak işlemler
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Alışveriş sepeti RecyclerView'unun hazırlanması
        setupCartRv()

        // Alışveriş sepetinden çıkış yapılmak istendiğinde geri gitme işlemi
        binding.imageCloseCart.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_homeFragment)
        }

        var totalPrice = 0f // Toplam fiyatı tutacak değişken

        // Ürünlerin fiyatlarının güncellenmesini izleyen bir yaşam döngüsü
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productsPrice.collectLatest { price ->
                    price?.let {
                        totalPrice = it
                        binding.tvTotalPrice.text = "$price TL"
                    }
                }
            }
        }

        // Ürüne tıklama işlemi
        cartAdapter.onProductClick = { product ->
            val bundle = Bundle().apply { putParcelable("product", product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, bundle)
        }

        // Artı butonuna tıklama işlemi
        cartAdapter.onPlusClick = { product ->
            viewModel.changeQuantity(product, FirebaseCommon.QuantityChanging.INCREASE)
        }

        // Eksi butonuna tıklama işlemi
        cartAdapter.onMinusClick = { product ->
            viewModel.changeQuantity(product, FirebaseCommon.QuantityChanging.DECREASE)
        }

        // Ödeme ekranına gitme işlemi
        binding.buttonCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice, cartAdapter.differ.currentList.toTypedArray(), true)
            findNavController().navigate(action)
        }

        // Ürün silme işlemi için izlenecek yaşam döngüsü
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteDialog.collectLatest { product ->
                    val alertDialog = AlertDialog.Builder(requireContext()).apply {
                        setTitle("Sepetinizden ürün silinecek")
                        setMessage("Bu ürünü sepetinizden çıkartmak istediğinizden emin misiniz?")
                        setNegativeButton("Hayır") { dialog, _ ->
                            dialog.dismiss()
                        }
                        setPositiveButton("Evet") { dialog, _ ->
                            viewModel.deleteCartProduct(product)
                            dialog.dismiss()
                        }
                    }
                    alertDialog.create()
                    alertDialog.show()
                }
            }
        }

        // Alışveriş sepetinin güncellenmesini izleyen bir yaşam döngüsü
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProducts.collectLatest { cartProducts ->
                    when (cartProducts) {
                        is Resource.Loading -> {
                            binding.progressbarCart.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressbarCart.visibility = View.INVISIBLE
                            if (cartProducts.data!!.isEmpty()) {
                                showEmptyCart()
                                hideOtherViews()
                            } else {
                                hideEmptyCart()
                                showOtherViews()
                                cartAdapter.differ.submitList(cartProducts.data)
                            }
                        }
                        is Resource.Error -> {
                            binding.progressbarCart.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), cartProducts.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    // Diğer görünümleri gösterme işlemi
    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    // Diğer görünümleri gizleme işlemi
    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    // Boş sepet durumunu gizleme işlemi
    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    // Boş sepet durumunu gösterme işlemi
    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    // Alışveriş sepeti RecyclerView'unu hazırlama işlemi
    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}
