package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CartItem
import com.example.data.model.DietaryFilter
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.Review
import com.example.data.model.SortOption
import com.example.data.repository.OrganicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class CartItemWithProduct(
  val product: Product,
  val quantity: Int,
  val selectedWeight: String = "500g"
) {
  val subtotal: Double
    get() = ((product.price * quantity) * 100.0).roundToInt() / 100.0
}

private data class FilterState(
  val query: String,
  val category: ProductCategory,
  val dietary: Set<DietaryFilter>,
  val price: ClosedFloatingPointRange<Float>,
  val sort: SortOption
)

class OrganicMarketViewModel(
  private val repository: OrganicRepository
) : ViewModel() {

  // Search & Filter State
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
  val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

  private val _selectedDietaryFilters = MutableStateFlow<Set<DietaryFilter>>(emptySet())
  val selectedDietaryFilters: StateFlow<Set<DietaryFilter>> = _selectedDietaryFilters.asStateFlow()

  private val _priceRange = MutableStateFlow(0f..50f)
  val priceRange: StateFlow<ClosedFloatingPointRange<Float>> = _priceRange.asStateFlow()

  private val _selectedSort = MutableStateFlow(SortOption.FEATURED)
  val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

  // Selected Product Detail State
  private val _selectedProductId = MutableStateFlow<String?>("prod_shahi_nuts")
  val selectedProductId: StateFlow<String?> = _selectedProductId.asStateFlow()

  private val _reviewStarFilter = MutableStateFlow<Int?>(null)
  val reviewStarFilter: StateFlow<Int?> = _reviewStarFilter.asStateFlow()

  private val _hasPurchasedCurrent = MutableStateFlow(true)
  val hasPurchasedCurrent: StateFlow<Boolean> = _hasPurchasedCurrent.asStateFlow()

  // Selected weight on product detail
  private val _selectedWeightOption = MutableStateFlow("500g")
  val selectedWeightOption: StateFlow<String> = _selectedWeightOption.asStateFlow()

  // Checkout Form State
  private val _deliveryAddress = MutableStateFlow("House 42, Road 11, Banani / 742 Evergreen Terrace")
  val deliveryAddress: StateFlow<String> = _deliveryAddress.asStateFlow()

  private val _specialInstructions = MutableStateFlow("Please deliver in afternoon; ensure airtight jar seal.")
  val specialInstructions: StateFlow<String> = _specialInstructions.asStateFlow()

  private val _deliveryOption = MutableStateFlow("Standard Fast Eco Delivery")
  val deliveryOption: StateFlow<String> = _deliveryOption.asStateFlow()

  private val _paymentMethod = MutableStateFlow("Cash on Delivery (ক্যাশ অন ডেলিভারি)")
  val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

  private val _promoCode = MutableStateFlow("ORGANIC15")
  val promoCode: StateFlow<String> = _promoCode.asStateFlow()

  private val _promoMessage = MutableStateFlow<String?>("Coupon ORGANIC15 applied: 15% off pure organic harvest!")
  val promoMessage: StateFlow<String?> = _promoMessage.asStateFlow()

  private val _latestPlacedOrder = MutableStateFlow<Order?>(null)
  val latestPlacedOrder: StateFlow<Order?> = _latestPlacedOrder.asStateFlow()

  private val _allProducts = repository.allProducts.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  init {
    viewModelScope.launch {
      repository.ensureDataSeeded()
    }
  }

  // Combine 5 filter parameters cleanly into FilterState flow
  private val filterStateFlow = combine(
    _searchQuery,
    _selectedCategory,
    _selectedDietaryFilters,
    _priceRange,
    _selectedSort
  ) { query, category, dietary, price, sort ->
    FilterState(query, category, dietary, price, sort)
  }

  // Filtered & Sorted Product List Flow
  val filteredProducts: StateFlow<List<Product>> = combine(
    _allProducts,
    filterStateFlow
  ) { products, filters ->
    products.filter { product ->
      // Query filter (matches English name, Bangla name, description, category, origin, certifications)
      val matchesQuery = filters.query.isBlank() ||
        product.name.contains(filters.query, ignoreCase = true) ||
        product.banglaName.contains(filters.query, ignoreCase = true) ||
        product.shortDescription.contains(filters.query, ignoreCase = true) ||
        product.category.contains(filters.query, ignoreCase = true) ||
        product.origin.contains(filters.query, ignoreCase = true) ||
        product.certifications.any { it.contains(filters.query, ignoreCase = true) }

      // Category filter
      val matchesCategory = when (filters.category) {
        ProductCategory.ALL -> true
        ProductCategory.NUTS -> product.category.contains("Nuts", ignoreCase = true)
        ProductCategory.GHEE -> product.category.contains("Ghee", ignoreCase = true)
        ProductCategory.HONEY -> product.category.contains("Honey", ignoreCase = true)
        ProductCategory.COMBOS -> product.category.contains("Combos", ignoreCase = true)
        ProductCategory.PRODUCE -> product.category.contains("Fruit", ignoreCase = true) || product.category.contains("Produce", ignoreCase = true)
      }

      // Dietary filter
      val matchesDietary = filters.dietary.all { filter ->
        when (filter) {
          DietaryFilter.VEGAN -> product.isVegan
          DietaryFilter.GLUTEN_FREE -> product.isGlutenFree
          DietaryFilter.DAIRY_FREE -> product.isDairyFree
          DietaryFilter.RAW -> product.isRaw
        }
      }

      // Price filter
      val matchesPrice = product.price in (filters.price.start.toDouble()..filters.price.endInclusive.toDouble())

      matchesQuery && matchesCategory && matchesDietary && matchesPrice
    }.let { list ->
      when (filters.sort) {
        SortOption.FEATURED -> list
        SortOption.RATING_DESC -> list.sortedByDescending { it.rating }
        SortOption.PRICE_ASC -> list.sortedBy { it.price }
        SortOption.PRICE_DESC -> list.sortedByDescending { it.price }
      }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Current Selected Product
  val selectedProduct: StateFlow<Product?> = combine(_allProducts, _selectedProductId) { list, id ->
    list.firstOrNull { it.id == id }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Current Selected Product's Reviews
  private val _rawReviews = MutableStateFlow<List<Review>>(emptyList())
  val selectedProductReviews: StateFlow<List<Review>> = combine(
    _rawReviews,
    _reviewStarFilter
  ) { reviews, filter ->
    if (filter == null) reviews else reviews.filter { it.rating == filter }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Cart with full product details
  val cartItemsWithProducts: StateFlow<List<CartItemWithProduct>> = combine(
    repository.cartItems,
    _allProducts
  ) { cartList, productList ->
    val prodMap = productList.associateBy { it.id }
    cartList.mapNotNull { cartItem ->
      val prod = prodMap[cartItem.productId]
      if (prod != null) CartItemWithProduct(prod, cartItem.quantity) else null
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val cartCount: StateFlow<Int> = cartItemsWithProducts.combine(MutableStateFlow(0)) { items, _ ->
    items.sumOf { it.quantity }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val cartSubtotal: StateFlow<Double> = cartItemsWithProducts.combine(MutableStateFlow(0)) { items, _ ->
    ((items.sumOf { it.subtotal }) * 100.0).roundToInt() / 100.0
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val orders: StateFlow<List<Order>> = repository.allOrders.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  // Actions
  fun onSearchQueryChange(query: String) {
    _searchQuery.value = query
  }

  fun onCategorySelect(category: ProductCategory) {
    _selectedCategory.value = category
  }

  fun toggleDietaryFilter(filter: DietaryFilter) {
    val current = _selectedDietaryFilters.value
    _selectedDietaryFilters.value = if (current.contains(filter)) {
      current - filter
    } else {
      current + filter
    }
  }

  fun onPriceRangeChange(range: ClosedFloatingPointRange<Float>) {
    _priceRange.value = range
  }

  fun onSortSelect(sort: SortOption) {
    _selectedSort.value = sort
  }

  fun resetFilters() {
    _searchQuery.value = ""
    _selectedCategory.value = ProductCategory.ALL
    _selectedDietaryFilters.value = emptySet()
    _priceRange.value = 0f..50f
    _selectedSort.value = SortOption.FEATURED
  }

  fun selectProduct(productId: String) {
    _selectedProductId.value = productId
    _reviewStarFilter.value = null
    viewModelScope.launch {
      _hasPurchasedCurrent.value = repository.hasPurchasedProduct(productId)
      repository.getReviewsForProduct(productId).collect { reviews ->
        _rawReviews.value = reviews
      }
    }
  }

  fun selectWeightOption(weight: String) {
    _selectedWeightOption.value = weight
  }

  fun setReviewStarFilter(stars: Int?) {
    _reviewStarFilter.value = stars
  }

  fun submitReview(
    productId: String,
    reviewerName: String,
    rating: Int,
    title: String,
    comment: String
  ) {
    viewModelScope.launch {
      repository.submitReview(productId, reviewerName, rating, title, comment)
    }
  }

  fun upvoteReview(reviewId: String) {
    viewModelScope.launch {
      repository.upvoteReview(reviewId)
    }
  }

  fun addToCart(productId: String, quantity: Int = 1) {
    viewModelScope.launch {
      repository.addToCart(productId, quantity)
    }
  }

  fun updateCartQuantity(productId: String, quantity: Int) {
    viewModelScope.launch {
      repository.setItemQuantity(productId, quantity)
    }
  }

  fun removeFromCart(productId: String) {
    viewModelScope.launch {
      repository.removeFromCart(productId)
    }
  }

  fun setDeliveryAddress(address: String) {
    _deliveryAddress.value = address
  }

  fun setSpecialInstructions(instructions: String) {
    _specialInstructions.value = instructions
  }

  fun setDeliveryOption(option: String) {
    _deliveryOption.value = option
  }

  fun setPaymentMethod(method: String) {
    _paymentMethod.value = method
  }

  fun applyPromoCode(code: String) {
    _promoCode.value = code
    if (code.trim().equals("ORGANIC15", ignoreCase = true)) {
      _promoMessage.value = "Coupon applied: 15% off your organic harvest!"
    } else if (code.trim().equals("FRESHFIRST", ignoreCase = true)) {
      _promoMessage.value = "$5.00 off welcome discount applied!"
    } else if (code.isBlank()) {
      _promoMessage.value = null
    } else {
      _promoMessage.value = "Invalid or expired promo code"
    }
  }

  fun placeOrder(onSuccess: (Order) -> Unit) {
    viewModelScope.launch {
      val order = repository.placeOrder(
        deliveryAddress = _deliveryAddress.value,
        deliveryOption = _deliveryOption.value,
        paymentMethod = _paymentMethod.value,
        promoCode = _promoCode.value
      )
      _latestPlacedOrder.value = order
      onSuccess(order)
    }
  }

  fun dismissOrderSuccess() {
    _latestPlacedOrder.value = null
  }
}
