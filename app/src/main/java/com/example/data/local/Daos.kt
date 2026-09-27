package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
  @Query("SELECT * FROM products")
  fun getAllProducts(): Flow<List<Product>>

  @Query("SELECT * FROM products WHERE id = :id")
  suspend fun getProductById(id: String): Product?

  @Query("SELECT * FROM products WHERE id = :id")
  fun getProductByIdFlow(id: String): Flow<Product?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(products: List<Product>)

  @Update
  suspend fun update(product: Product)

  @Query("SELECT COUNT(*) FROM products")
  suspend fun getCount(): Int
}

@Dao
interface ReviewDao {
  @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY timestamp DESC")
  fun getReviewsForProduct(productId: String): Flow<List<Review>>

  @Query("SELECT * FROM reviews WHERE productId = :productId")
  suspend fun getReviewsListForProduct(productId: String): List<Review>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReview(review: Review)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(reviews: List<Review>)

  @Query("UPDATE reviews SET helpfulVotes = helpfulVotes + 1 WHERE id = :reviewId")
  suspend fun upvoteHelpful(reviewId: String)
}

@Dao
interface CartDao {
  @Query("SELECT * FROM cart_items")
  fun getAllCartItems(): Flow<List<CartItem>>

  @Query("SELECT * FROM cart_items WHERE productId = :productId")
  suspend fun getCartItem(productId: String): CartItem?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(item: CartItem)

  @Query("DELETE FROM cart_items WHERE productId = :productId")
  suspend fun deleteCartItem(productId: String)

  @Query("DELETE FROM cart_items")
  suspend fun clearCart()
}

@Dao
interface OrderDao {
  @Query("SELECT * FROM orders ORDER BY timestamp DESC")
  fun getAllOrders(): Flow<List<Order>>

  @Query("SELECT * FROM orders WHERE itemsJson LIKE '%' || :productId || '%'")
  suspend fun getOrdersContainingProduct(productId: String): List<Order>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: Order)
}
