package com.necibeguner.homedeco.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.CartProduct

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    // Kullanıcı sepetini temsil eden belirli bir koleksiyon referansı
    private val cartCollection = firestore.collection("user").document(auth.uid!!).collection("cart")

    // Bir ürünü sepete ekleme işlevi
    //addProductToCart, kullanıcının sepetine bir ürün eklemek için Firestore'a erişim sağlar.
    fun addProductToCart(cartProduct: CartProduct , onResult: (CartProduct?,Exception?) -> Unit){
        cartCollection.document().set(cartProduct).addOnSuccessListener {
            // Ekleme başarılı olduysa sonucu işleme
            onResult(cartProduct,null)
        }.addOnFailureListener {
            // Ekleme başarısız olduysa hata ile sonucu işleme
            onResult(null,it)
        }
    }

    // Ürün miktarını artırma işlevi
    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction {transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let {cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef,newProductObject)
            }
        }.addOnSuccessListener {
            // Miktar artırma işlemi başarılı ise sonucu işleme
            onResult(documentId,null)
        }.addOnFailureListener {
            // Miktar artırma işlemi başarısız ise hata ile sonucu işleme
            onResult(null,it)
        }
    }
    //increaseQuantity ve decreaseQuantity, belirli bir ürünün
    // sepetindeki miktarını artırmak veya azaltmak için Firestore transaction'larını kullanır.

    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction {transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let {cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef,newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId,null)
        }.addOnFailureListener {
            onResult(null,it)
        }
    }

    //QuantityChanging, miktarın artırılmasını veya azaltılmasını temsil eden enum yapısıdır.
    enum class QuantityChanging {
        INCREASE,DECREASE
    }

}