package com.necibeguner.homedeco.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.necibeguner.homedeco.data.Product
import com.necibeguner.homedeco.databinding.ProductRvItemBinding
import com.necibeguner.homedeco.helper.getProductPrice

class BestProductsAdapter : RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {
    // Her bir ürün için bir görünüm tutucusu tanımlanmıştır.
    inner class BestProductsViewHolder(private val binding: ProductRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Veri bağlama işlevi, ürünlerin görünümünü günceller.
        fun bind(product: Product) {
            binding.apply {
                // Ürünün indirim yüzdesine göre fiyatını hesaplayıp görünüme yazdırma
                val priceAfterOffer = product.offerPercentage.getProductPrice(product.price)
                tvNewPrice.text = "${String.format("%.2f", priceAfterOffer)} TL"

                // Eski fiyatı çizgili bir şekilde gösterme
                tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                // Eğer üründe indirim yoksa yeni fiyatı görünmez yapma
                if (product.offerPercentage == null)
                    tvNewPrice.visibility = View.INVISIBLE

                // Ürün resmini Glide kütüphanesiyle yükleme
                Glide.with(itemView).load(product.images[0]).into(imgProduct)

                // Ürünün fiyatını görünüme yazdırma
                tvPrice.text = "${product.price} TL"

                // Ürünün adını görünüme yazdırma
                tvName.text = product.name
            }
        }
    }

    // ürünlerin karşılaştırılması için DiffUtil kullanılır
    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            // ürünlerin benzersiz kimlikleri aynı mı diye kontrol eder
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            // ürün içerikleri aynı mı diye kontrol eder
            return oldItem == newItem
        }
    }

    // AsyncListDiffer, ürün farklılıklarını hesaplar ve günceller
    val differ = AsyncListDiffer(this, diffCallback)

    // Yeni bir görünüm tutucusu oluşturur ve döndürür
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    // Görünüm tutucusu ile veri bağlama işlevini çağırır
    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position] // Belirli bir pozisyondaki ürünü alır
        holder.bind(product) // Görünüm tutucusuna ürünü bağlar

        // ürüne tıklanıldığında tanımlı olan onClick işlevini çağırır
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    // Ürün sayısını döndürür
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Ürünlere tıklanıldığında çağrılacak işlevi tanımlar
    var onClick: ((Product) -> Unit)? = null
}
