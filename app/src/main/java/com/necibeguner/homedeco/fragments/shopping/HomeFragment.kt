package com.necibeguner.homedeco.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.adapters.HomeViewpagerAdapter
import com.necibeguner.homedeco.databinding.FragmentHomeBinding
import com.necibeguner.homedeco.fragments.categories.AccessoryFragment
import com.necibeguner.homedeco.fragments.categories.ChairFragment
import com.necibeguner.homedeco.fragments.categories.CupboardFragment
import com.necibeguner.homedeco.fragments.categories.FurnitureFragment
import com.necibeguner.homedeco.fragments.categories.MainCategoryFragment
import com.necibeguner.homedeco.fragments.categories.TableFragment

//Bu kod parçası, ana ekranın görünümünü yöneten bir fragment. Bu fragment, kategori sekmesine
// sahip bir ekranın oluşturulmasını sağlar. ViewPager ve TabLayout kullanarak farklı kategorilere
// ait fragmentleri bir araya getirir ve her sekme için başlık belirler. Kullanıcı bu kategoriler
// arasında gezinebilir ve her bir kategoriye ait içeriği görebilir. ViewPager kullanıcı girişlerini
// devre dışı bırakarak, kullanıcının kaydırma işlemleriyle kategori değiştirmesini engeller.

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding // Fragment'ın bağlamını tutacak değişken

    // Görünüm oluşturulduğunda çalışacak metot
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Bağlam oluşturuluyor ve root görünümü geri döndürülüyor
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    // Görünüm oluşturulduktan sonra yapılacak işlemler
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kategorilerin fragmentlerini tutacak bir liste oluşturuluyor
        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        // ViewPager'ın kullanıcı girişlerine kapatılması
        binding.viewpagerHome.isUserInputEnabled = false

        // ViewPager için özel bir adaptör oluşturuluyor ve atanıyor
        val viewPager2Adapter = HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter

        // TabLayout ile ViewPager'ın ilişkilendirilmesi ve her sekme için başlık belirlenmesi
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "AnaSayfa"
                1 -> tab.text = "Sandalye"
                2 -> tab.text = "Dolap"
                3 -> tab.text = "Masa"
                4 -> tab.text = "Dekorasyon Malzemeleri"
                5 -> tab.text = "Mobilya"
            }
        }.attach()
    }
}
