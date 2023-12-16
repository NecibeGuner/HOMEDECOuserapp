package com.necibeguner.homedeco.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.necibeguner.homedeco.databinding.ViewpagerImageItemBinding

class ViewPager2Images : RecyclerView.Adapter<ViewPager2Images.ViewPager2ImagesViewHolder>() {
    //ViewPager2ImagesViewHolder iç içe sınıfı, ViewPager2'de her bir görüntü öğesini temsil eden
    // görünüm tutucularını (view holder) yönetir ve görüntü verilerini bağlar.
    // Her bir ViewPager2 görüntü öğesinin görünüm tutucusunu (view holder) temsil eder
    inner class ViewPager2ImagesViewHolder(val binding: ViewpagerImageItemBinding) : RecyclerView.ViewHolder(binding.root) {

        // Görüntüyü Glide kütüphanesi ile bağlama işlevi
        fun bind(imagePath: String) {
            Glide.with(itemView).load(imagePath).into(binding.imageProductDetails)
        }
    }

    // Görüntüler arasındaki farkları belirlemek için DiffUtil kullanılır
    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem // Öğeler aynı mı?
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem // Öğe içerikleri aynı mı?
        }
    }

    // Liste üzerinde asenkron farkları işlemek için AsyncListDiffer kullanılır
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ImagesViewHolder {
        // Görünüm tutucusu oluşturma ve bağlama işlemi
        return ViewPager2ImagesViewHolder(
            ViewpagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPager2ImagesViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image) // Görüntüyü görünüm tutucusuna bağlama
    }

    override fun getItemCount(): Int {
        return differ.currentList.size // Listenin mevcut öğe sayısını döndürme
    }
}
