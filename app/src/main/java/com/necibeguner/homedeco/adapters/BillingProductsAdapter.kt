package com.necibeguner.homedeco.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.necibeguner.homedeco.data.CartProduct
import com.necibeguner.homedeco.databinding.BillingProductsRvItemBinding
import com.necibeguner.homedeco.helper.getProductPrice

// BillingProductsAdapter sınıfı, RecyclerView için bir Adaptördür.
class BillingProductsAdapter : Adapter<BillingProductsAdapter.BillingProductsViewHolder>() {

    // Her bir ürün için bir görünüm tutucusu iç içe sınıf olarak tanımlanmıştır.
    inner class BillingProductsViewHolder(val binding: BillingProductsRvItemBinding) : ViewHolder(binding.root) {

        // Veri bağlama işlevi, listedeki her bir ürünün görünümünü günceller.
        fun bind(billingProduct: CartProduct) {
            binding.apply {
                // Glide kütüphanesiyle ürün resmini yükleme
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)

                // Ürün adını görünümde ayarlama
                tvProductCartName.text = billingProduct.product.name

                // Fatura ürününün miktarını görünümde ayarlama
                tvBillingProductQuantity.text = billingProduct.quantity.toString()

                // İndirim uygulandıktan sonra ürün fiyatını hesapla ve görünümde göster
                val priceAfterPercentage = billingProduct.product.offerPercentage.getProductPrice(billingProduct.product.price)
                tvProductCartPrice.text = "${String.format("%.2f", priceAfterPercentage)} TL"

                // Seçilen renk varsa ürün rengini, yoksa varsayılanı göster
                imageCartProductColor.setImageDrawable(ColorDrawable(billingProduct.selectedColor ?: Color.TRANSPARENT))

                // Seçilen boyut varsa ürün boyutunu, yoksa varsayılanı göster
                tvCartProductSize.text = billingProduct.selectedSize ?: ""
                imageCartProductSize.setImageDrawable(ColorDrawable(billingProduct.selectedSize?.let { Color.TRANSPARENT } ?: Color.TRANSPARENT))
            }
        }
    }

    // DiffUtil, eski ve yeni ürünler arasındaki farkları hesaplamak için kullanılır.
    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product // Öğelerin aynı olup olmadığını kontrol eder
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem // İçerik açısından öğelerin aynı olup olmadığını kontrol eder
        }
    }

    // AsyncListDiffer, RecyclerView için ürün farklılıklarını hesaplar ve günceller.
    val differ = AsyncListDiffer(this, diffUtil)

    // Yeni bir ViewHolder oluşturulduğunda çağrılır ve ViewHolder'ı döndürür.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewHolder {
        return BillingProductsViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    // ViewHolder ile veri bağlama işlevi çağrılır.
    override fun onBindViewHolder(holder: BillingProductsViewHolder, position: Int) {
        val billingProduct = differ.currentList[position] // Belirli bir pozisyondaki ürünü alır
        holder.bind(billingProduct) // Görünüm tutucusuna ürünü bağlar
    }

    // Veri kümesindeki ürün sayısını döndürür.
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
