package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "reviews")
data class Review(
  @PrimaryKey val id: String,
  val productId: String,
  val reviewerName: String,
  val rating: Int, // 1 to 5
  val title: String,
  val comment: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isVerifiedPurchase: Boolean = true,
  val helpfulVotes: Int = 0
) {
  val formattedDate: String
    get() {
      val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
      return sdf.format(Date(timestamp))
    }
}
