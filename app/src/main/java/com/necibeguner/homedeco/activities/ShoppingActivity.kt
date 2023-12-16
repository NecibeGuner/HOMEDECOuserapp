package com.necibeguner.homedeco.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.databinding.ActivityShoppingBinding
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    // Bağlama (binding) örneği, aktivitenin içeriğini temsil eder
    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    // ViewModel örneği, alışveriş sepeti iş mantığını yönetir
    val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // shopping layout u çağırır

        // NavController'ı elde etmek ve bottom navigation ile bağlamak
        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)

        // Alışveriş sepetindeki ürünleri dinleyerek gösterimini güncelle
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProducts.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            val count = it.data?.size ?: 0
                            // Alışveriş sepetindeki ürün sayısını alt menüde göster
                            val bottomNavigation =
                                findViewById<BottomNavigationView>(R.id.bottomNavigation)
                            bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                                number = count // Ürün sayısını göster
                                backgroundColor = resources.getColor(R.color.g_blue) // arka plan rengini ayarla
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}



