package com.necibeguner.homedeco.fragments.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.necibeguner.homedeco.data.User
import com.necibeguner.homedeco.databinding.FragmentUserAccountBinding
import com.necibeguner.homedeco.dialog.setupBottomSheetDialog
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.viewmodel.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//Bu kod parçası, kullanıcı hesap bilgilerinin güncellendiği bir fragmentı gösteriyor . ViewModel
//üzerinden kullanıcı bilgilerini alır, gösterir, günceller ve resim seçme işlemlerini gerçekleştirir.
//Ayrıca, kullanıcının bilgileri yüklenirken ve güncellenirken UI üzerinde ilerleme durumunu gösterir.

@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserAccountBinding // Bağlama değişkeni tanımlanır
    private val viewModel by viewModels<UserAccountViewModel>() // UserAccountViewModel'den bir örnek alınır
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent> // ActivityResultLauncher kullanarak resim alma işlemi için bir değişken tanımlanır

    private var imageUri: Uri? = null // Seçilen resmin URI'si tutulur

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Resim alma işlemi için ActivityResultLauncher başlatılır
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            imageUri = result.data?.data // Seçilen resmin URI'si alınır
            Glide.with(this).load(imageUri).into(binding.imageUser) // Seçilen resim ImageView'a yüklenir
        }
    }

    // Fragment'in oluşturulduğu metot, layout için bağlama oluşturulur ve geri döndürülür
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }
        // Kullanıcı bilgileri View'lar üzerinde gösterilir
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collectLatest { userData ->
                    when (userData) {
                        is Resource.Loading -> {
                            showUserLoading() // Kullanıcı bilgileri yüklenirken ilerleme gösterilir
                        }
                        is Resource.Success -> {
                            hideUserLoading() // Kullanıcı bilgileri başarıyla yüklendiyse ilerleme gizlenir
                            showUserInfo(userData.data!!) // Kullanıcı bilgileri gösterilir
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), userData.message, Toast.LENGTH_SHORT).show() // Hata durumunda kullanıcıya bilgi verilir
                        }
                        else -> Unit
                    }
                }
            }
        }

        // Kullanıcı bilgilerini güncelleme işlemi gözlemlenir
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateInfo.collectLatest { updateInfo ->
                    when (updateInfo) {
                        is Resource.Loading -> {
                            binding.buttonSave.startAnimation() // Bilgiler güncellenirken buton animasyonu başlatılır
                        }
                        is Resource.Success -> {
                            binding.buttonSave.revertAnimation() // Bilgiler başarıyla güncellendiğinde buton animasyonu sonlandırılır
                            findNavController().navigateUp() // Geri navigasyon gerçekleştirilir
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), updateInfo.message, Toast.LENGTH_SHORT).show() // Hata durumunda kullanıcıya bilgi verilir
                        }
                        else -> Unit
                    }
                }
            }
        }

        // Şifre güncelleme işlemi için alt dialog penceresi açılır
        binding.tvUpdatePassword.setOnClickListener {
            setupBottomSheetDialog {

            }
        }

        // Kullanıcı bilgilerini güncelleme butonuna tıklama işlemi
        binding.buttonSave.setOnClickListener {
            binding.apply {
                // EditText'lerden kullanıcı bilgileri alınır
                val firstName = edFirstName.text.toString().trim()
                val lastName = edLastName.text.toString().trim()
                val email = edEmail.text.toString().trim()
                val user = User(firstName, lastName, email)
                viewModel.updateUser(user, imageUri) // ViewModel üzerinden kullanıcı bilgileri güncellenir
            }
        }

        // Profil fotoğrafı değiştirme işlemi
        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent) // Resim seçme işlemi başlatılır
        }
    }

    // Kullanıcı bilgilerini gösteren metot
    private fun showUserInfo(user: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment).load(user.imagePath).error(ColorDrawable(Color.BLACK)).into(imageUser) // Profil fotoğrafı yüklenir
            edFirstName.setText(user.firstName) // Ad bilgisi gösterilir
            edLastName.setText(user.lastName) // Soyad bilgisi gösterilir
            edEmail.setText(user.email) // E-posta bilgisi gösterilir
        }
    }

    // Kullanıcı bilgilerinin yüklenirken gösterilen metot
    private fun showUserLoading() {
        binding.apply {
            // İlerleme gösterilir, View'lar gizlenir
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }

    // Kullanıcı bilgileri yüklendikten sonra gösterilen metot
    private fun hideUserLoading() {
        binding.apply {
            // İlerleme gizlenir, View'lar gösterilir
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }
}
