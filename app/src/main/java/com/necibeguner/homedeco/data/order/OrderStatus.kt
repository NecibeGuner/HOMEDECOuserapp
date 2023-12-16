package com.necibeguner.homedeco.data.order

sealed class OrderStatus(val status: String) {

    object Ordered: OrderStatus("Sipariş Edildi")
    object Canceled: OrderStatus("İptal Edildi")
    object Confirmed: OrderStatus("Onaylandı")
    object Shipped: OrderStatus("Kargoda")
    object Delivered: OrderStatus("Teslim Edildi")
    object Returned: OrderStatus("İade Edildi")
}
fun getOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Sipariş Edildi" -> {
            OrderStatus.Ordered
        }
        "İptal Edildi" -> {
            OrderStatus.Canceled
        }
        "Onaylandı" -> {
            OrderStatus.Confirmed
        }
        "Kargoda" -> {
            OrderStatus.Shipped
        }
        "Teslim Edildi" -> {
            OrderStatus.Delivered
        }
        else -> OrderStatus.Returned
    }
}
