package com.necibeguner.homedeco.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.necibeguner.homedeco.databinding.ColorRvItemBinding

class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {
    //ColorsViewHolder iç içe sınıfı, RecyclerView'da her bir renk öğesini temsil eden görünüm
    //tutucularını (view holder) yönetir ve renk verilerini bağlar.

    // Seçili rengin pozisyonunu izlemek için kullanılır, başlangıçta seçili bir pozisyon yok (-1).
    private var selectedPosition = -1

    inner class ColorsViewHolder(private val binding: ColorRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Renkleri bağlama işlemi
        fun bind(color: Int, position: Int) {
            val imageDrawable = ColorDrawable(color)
            binding.imageColor.setImageDrawable(imageDrawable)

            // Eğer bu pozisyon seçili pozisyona eşitse
            if (position == selectedPosition) {
                binding.apply {
                    // Görünürlüğü ayarla: Renk seçildiyse, gölge ve işaretleri görünür yap
                    imageShadow.visibility = View.VISIBLE
                    imagePicked.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    // Renk seçilmediyse, gölge ve işaretleri gizle
                    imageShadow.visibility = View.INVISIBLE
                    imagePicked.visibility = View.INVISIBLE
                }
            }
        }
    }

    // Renk öğeleri arasındaki farkları belirlemek için DiffUtil kullanılır
    //differCallback, RecyclerView'daki değişiklikleri algılamak için kullanılan DiffUtil.ItemCallback'i uygular.
    private val differCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem // Öğeler aynı mı?
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem // Öğe içerikleri aynı mı?
        }
    }

    // Liste üzerinde asenkron farkları işlemek için AsyncListDiffer
    val differ = AsyncListDiffer(this, differCallback)
    //differ, asenkron olarak farkları işlemek için AsyncListDiffer nesnesini tanımlar.

    //onCreateViewHolder, bir görünüm tutucu oluşturur ve bağlama işlemi yapar.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder(
            ColorRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    //onBindViewHolder, renk verilerini bağlar ve tıklama olaylarını işler.
    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color, position)

        // Her bir renk öğesine tıklama olayını ekleme
        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0)
                notifyItemChanged(selectedPosition) // Önceki seçili rengin durumunu güncelle

            selectedPosition = holder.adapterPosition // Yeni seçilen rengin pozisyonunu güncelle
            notifyItemChanged(selectedPosition) // Yeni seçilen rengin durumunu güncelle

            onItemClick?.invoke(color) // Renk üzerinde tıklama işlevselliğini tetikleme
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size // Listenin mevcut öğe sayısını döndürme
    }

    // Renk öğesine tıklama olayını işleyen lambda ifadesi
    var onItemClick: ((Int) -> Unit)? = null
}
