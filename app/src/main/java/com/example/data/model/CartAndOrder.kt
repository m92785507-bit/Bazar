package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "cart_items")
data class CartItem(
  @PrimaryKey val productId: String,
  val quantity: Int
)

@Entity(tableName = "orders")
data class Order(
  @PrimaryKey val id: String,
  val orderNumber: String,
  val timestamp: Long = System.currentTimeMillis(),
  val itemsJson: String, // comma-separated or formatted list of "productId:qty:price:name"
  val subtotal: Double,
  val deliveryFee: Double,
  val discount: Double,
  val total: Double,
  val deliveryAddress: String,
  val deliveryOption: String,
  val paymentMethod: String,
  val status: String = "Processing Harvest"
) {
  val formattedDate: String
    get() {
      val sdf = SimpleDateFormat("MMM d, yyyy - h:mm a", Locale.getDefault())
      return sdf.format(Date(timestamp))
    }

  // Parse purchased product IDs to verify if a user has purchased a product
  fun containsProduct(productId: String): Boolean {
    return itemsJson.contains(productId)
  }
}
