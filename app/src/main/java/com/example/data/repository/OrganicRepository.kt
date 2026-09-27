package com.example.data.repository

import com.example.data.local.CartDao
import com.example.data.local.InitialData
import com.example.data.local.OrderDao
import com.example.data.local.ProductDao
import com.example.data.local.ReviewDao
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.roundToInt

class OrganicRepository(
  private val productDao: ProductDao,
  private val reviewDao: ReviewDao,
  private val cartDao: CartDao,
  private val orderDao: OrderDao
) {

  val allProducts: Flow<List<Product>> = productDao.getAllProducts()
  val cartItems: Flow<List<CartItem>> = cartDao.getAllCartItems()
  val allOrders: Flow<List<Order>> = orderDao.getAllOrders()

  suspend fun ensureDataSeeded() = withContext(Dispatchers.IO) {
    if (productDao.getCount() == 0) {
      productDao.insertAll(InitialData.initialProducts)
      reviewDao.insertAll(InitialData.initialReviews)
      for (order in InitialData.initialOrders) {
        orderDao.insertOrder(order)
      }
    }
  }

  fun getProductById(id: String): Flow<Product?> {
    return productDao.getProductByIdFlow(id)
  }

  fun getReviewsForProduct(productId: String): Flow<List<Review>> {
    return reviewDao.getReviewsForProduct(productId)
  }

  suspend fun submitReview(
    productId: String,
    reviewerName: String,
    rating: Int,
    title: String,
    comment: String
  ): Review = withContext(Dispatchers.IO) {
    val isVerified = hasPurchasedProduct(productId)
    val review = Review(
      id = "rev_${UUID.randomUUID()}",
      productId = productId,
      reviewerName = reviewerName.ifBlank { "Organic Food Lover" },
      rating = rating.coerceIn(1, 5),
      title = title.trim(),
      comment = comment.trim(),
      timestamp = System.currentTimeMillis(),
      isVerifiedPurchase = isVerified,
      helpfulVotes = 0
    )
    reviewDao.insertReview(review)

    // Recalculate product rating & review count
    val allProductReviews = reviewDao.getReviewsListForProduct(productId)
    if (allProductReviews.isNotEmpty()) {
      val avgRating = allProductReviews.map { it.rating }.average().toFloat()
      val roundedAvg = (avgRating * 10f).roundToInt() / 10f
      val product = productDao.getProductById(productId)
      if (product != null) {
        productDao.update(
          product.copy(
            rating = roundedAvg,
            reviewCount = allProductReviews.size
          )
        )
      }
    }

    review
  }

  suspend fun upvoteReview(reviewId: String) = withContext(Dispatchers.IO) {
    reviewDao.upvoteHelpful(reviewId)
  }

  suspend fun hasPurchasedProduct(productId: String): Boolean = withContext(Dispatchers.IO) {
    val orders = orderDao.getOrdersContainingProduct(productId)
    orders.isNotEmpty()
  }

  suspend fun addToCart(productId: String, quantityDelta: Int = 1) = withContext(Dispatchers.IO) {
    val existing = cartDao.getCartItem(productId)
    val newQuantity = if (existing != null) {
      (existing.quantity + quantityDelta).coerceAtLeast(0)
    } else {
      quantityDelta.coerceAtLeast(1)
    }
    if (newQuantity <= 0) {
      cartDao.deleteCartItem(productId)
    } else {
      cartDao.insertOrUpdate(CartItem(productId, newQuantity))
    }
  }

  suspend fun setItemQuantity(productId: String, quantity: Int) = withContext(Dispatchers.IO) {
    if (quantity <= 0) {
      cartDao.deleteCartItem(productId)
    } else {
      cartDao.insertOrUpdate(CartItem(productId, quantity))
    }
  }

  suspend fun removeFromCart(productId: String) = withContext(Dispatchers.IO) {
    cartDao.deleteCartItem(productId)
  }

  suspend fun clearCart() = withContext(Dispatchers.IO) {
    cartDao.clearCart()
  }

  suspend fun placeOrder(
    deliveryAddress: String,
    deliveryOption: String,
    paymentMethod: String,
    promoCode: String
  ): Order = withContext(Dispatchers.IO) {
    val currentCart = cartDao.getAllCartItems().first()
    val products = productDao.getAllProducts().first().associateBy { it.id }

    val itemsList = currentCart.mapNotNull { item ->
      val prod = products[item.productId]
      if (prod != null) {
        "${prod.id}:${item.quantity}:${prod.price}:${prod.name}"
      } else null
    }

    val subtotal = currentCart.sumOf { item ->
      val prod = products[item.productId]
      (prod?.price ?: 0.0) * item.quantity
    }

    val discount = if (promoCode.trim().equals("ORGANIC15", ignoreCase = true)) {
      (subtotal * 0.15 * 100.0).roundToInt() / 100.0
    } else if (promoCode.trim().equals("FRESHFIRST", ignoreCase = true)) {
      5.00
    } else 0.0

    val deliveryFee = when {
      deliveryOption.contains("Pickup", ignoreCase = true) -> 0.0
      subtotal >= 35.0 -> 0.0
      deliveryOption.contains("Express", ignoreCase = true) -> 4.99
      else -> 2.99
    }

    val total = ((subtotal - discount + deliveryFee) * 100.0).roundToInt() / 100.0

    val order = Order(
      id = "ord_${UUID.randomUUID()}",
      orderNumber = "PH-${(10000..99999).random()}",
      timestamp = System.currentTimeMillis(),
      itemsJson = itemsList.joinToString(","),
      subtotal = subtotal,
      deliveryFee = deliveryFee,
      discount = discount,
      total = total,
      deliveryAddress = deliveryAddress,
      deliveryOption = deliveryOption,
      paymentMethod = paymentMethod,
      status = "Harvesting & Packing"
    )

    orderDao.insertOrder(order)
    cartDao.clearCart()

    order
  }
}
