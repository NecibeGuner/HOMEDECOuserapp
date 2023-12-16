package com.necibeguner.homedeco.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.necibeguner.homedeco.data.Product
import com.necibeguner.homedeco.databinding.SpecialRvItemBinding

class SpecialProductsAdapter : RecyclerView.Adapter<SpecialProductsAdapter.SpecialProductsViewHolder>() {
    //ViewPager2ImagesViewHolder iç içe sınıfı, ViewPager2'de her bir görüntü öğesini temsil eden görünüm tutucularını
    //(view holder) yönetir ve görüntü verilerini bağlar.
    // Her bir özel ürünün görünüm tutucusunu (view holder) temsil eder
    inner class SpecialProductsViewHolder(private val binding: SpecialRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Özel ürün verilerini görünüme bağlama işlevi
        fun bind(product: Product) {
            binding.apply {
                // Glide kütüphanesi kullanılarak özel ürünün ilk resmi yüklenir
                Glide.with(itemView).load(product.images[0]).into(imageSpecialRvItem)

                // Özel ürünün adı ve fiyatı görünüme atanır
                tvSpecialProductName.text = product.name
                tvSpecialProductPrice.text = product.price.toString()
            }
        }
    }

    // Ürünler arasındaki farkları belirlemek için DiffUtil kullanılır
    //diffCallback, RecyclerView'daki değişiklikleri algılamak için kullanılan DiffUtil.ItemCallback'i uygular.
    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id // Ürünlerin benzersiz kimliklerini kontrol eder
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem // Ürün içeriklerini kontrol eder
        }
    }

    // Liste üzerinde asenkron farkları işlemek için AsyncListDiffer kullanılır
    val differ = AsyncListDiffer(this, diffCallback)
    //differ, asenkron olarak farkları işlemek için AsyncListDiffer nesnesini tanımlar.

    //onCreateViewHolder, bir görünüm tutucu oluşturur ve bağlama işlemi yapar.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsViewHolder {
        // Görünüm tutucusu oluşturma ve bağlama işlemi
        return SpecialProductsViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    //onBindViewHolder, özel ürün verilerini bağlar ve tıklama olaylarını işler.
    override fun onBindViewHolder(holder: SpecialProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        // Öğe tıklama işlevselliğini tetikleme
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    //getItemCount, listenin mevcut öğe sayısını döndürür.
    override fun getItemCount(): Int {
        return differ.currentList.size // Listenin mevcut öğe sayısını döndürme
    }

    // Ürün üzerinde tıklama olayını işleyen lambda ifadesi
    var onClick: ((Product) -> Unit)? = null
}
