package com.necibeguner.homedeco.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.necibeguner.homedeco.R

fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
) {
    // BottomSheetDialog oluşturuluyor ve görünümü belirleniyor.
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setContentView(view)

    // Bottom sheet dialogunun açılış şekli belirleniyor (burada geniş olarak açılır).
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    // Dialog içindeki bileşenlerin referansları alınıyor.
    val edEmail = view.findViewById<EditText>(R.id.edResetPassword)
    val buttonSend = view.findViewById<Button>(R.id.buttonSendRestPassword)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCanselRestPassword)

    // Gönder butonuna tıklama dinleyicisi ekleniyor.
    buttonSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        // Gönder butonuna tıklandığında, belirtilen işlev onSendClick ile çağrılıyor.
        // Bu işlev, parametre olarak e-posta adresini alır.
        onSendClick(email)
        dialog.dismiss() // Dialog kapatılıyor.
    }

    // İptal butonuna tıklama dinleyicisi ekleniyor.
    buttonCancel.setOnClickListener {
        dialog.dismiss() // Dialog kapatılıyor.
    }
}
