package com.necibeguner.homedeco.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.bumptech.glide.Glide
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.activities.LoginRegisterActivity
import com.necibeguner.homedeco.databinding.FragmentProfileBinding
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.util.showBottomNavigationView
import com.necibeguner.homedeco.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//Bu kod parçası, bir kullanıcı profilini temsil eden bir fragment. Kullanıcı, profil bilgilerini
// görüntüleyebilir, tüm siparişlerini inceleyebilir, faturalarını görebilir ve oturumu kapatabilir.
// Kullanıcı bilgileri, ViewModel aracılığıyla yönetilir ve ilgili ekranlara geçişler için gezinme
// işlemleri gerçekleştirilir. Ayrıca, kullanıcının profil resmi, adı ve soyadı gibi bilgileri görüntülenir.

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding // Fragment'ın bağlamını tutacak değişken
    val viewModel by viewModels<ProfileViewModel>() // Profil verilerini yöneten ViewModel

    // Görünüm oluşturulduğunda çalışacak metot
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root // Bağlamın root görünümünü geri döndürme
    }

    // Görünüm oluşturulduktan sonra yapılacak işlemler
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Profil kısmına tıklama işlemi
        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment2)
        }

        // Tüm siparişlere tıklama işlemi
        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }

        // Faturalara tıklama işlemi
        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                0f, emptyArray(), false
            )
            findNavController().navigate(action)
        }

        // Çıkış yapmaya tıklama işlemi
        binding.linearLogOut.setOnClickListener {
            viewModel.logOut() // Oturumu kapatma işlemi
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent) // Yeni bir giriş ekranına yönlendirme
            requireActivity().finish() // Aktiviteyi sonlandırma
        }

        // Kullanıcı verilerinin izlenmesi için bir yaşam döngüsü
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collectLatest { userData ->
                    when (userData) {
                        is Resource.Loading -> {
                            binding.progressbarSettings.visibility = View.VISIBLE // Yüklenirken ilerleme çubuğunu gösterme
                        }

                        is Resource.Success -> {
                            binding.progressbarSettings.visibility = View.GONE // Başarılı olunca ilerleme çubuğunu gizleme
                            Glide.with(requireView()).load(userData.data!!.imagePath)
                                .error(ColorDrawable(Color.BLACK)) // Kullanıcı resmini yükleme
                            binding.tvUserName.text = "${userData.data.firstName} ${userData.data.lastName}" // Kullanıcı adını gösterme
                        }

                        is Resource.Error -> {
                            Toast.makeText(requireContext(), userData.message, Toast.LENGTH_SHORT).show() // Hata durumunda mesaj gösterme
                            binding.progressbarSettings.visibility = View.GONE // Hata durumunda ilerleme çubuğunu gizleme
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    // Fragment yeniden aktif olduğunda alt gezinti çubuğunu gösterme işlemi
    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}

