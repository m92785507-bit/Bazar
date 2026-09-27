package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
  @PrimaryKey val id: String,
  val name: String,
  val banglaName: String = "",
  val category: String,
  val price: Double,
  val originalPrice: Double? = null,
  val unit: String,
  val origin: String,
  val shortDescription: String,
  val description: String,
  val nutritionalHighlights: String,
  val storageTips: String,
  val certificationsCsv: String,
  val dietaryTagsCsv: String,
  val weightOptionsCsv: String = "250g, 500g, 1 kg",
  val inStock: Boolean = true,
  val isBestSeller: Boolean = false,
  val rating: Float = 0f,
  val reviewCount: Int = 0,
  val iconType: String = "honey"
) {
  val certifications: List<String>
    get() = if (certificationsCsv.isBlank()) emptyList() else certificationsCsv.split(",").map { it.trim() }

  val dietaryTags: List<String>
    get() = if (dietaryTagsCsv.isBlank()) emptyList() else dietaryTagsCsv.split(",").map { it.trim() }

  val weightOptions: List<String>
    get() = if (weightOptionsCsv.isBlank()) listOf(unit) else weightOptionsCsv.split(",").map { it.trim() }

  val isVegan: Boolean
    get() = dietaryTags.any { it.equals("Vegan", ignoreCase = true) }

  val isGlutenFree: Boolean
    get() = dietaryTags.any { it.equals("Gluten-Free", ignoreCase = true) }

  val isDairyFree: Boolean
    get() = dietaryTags.any { it.equals("Dairy-Free", ignoreCase = true) }

  val isRaw: Boolean
    get() = dietaryTags.any { it.equals("100% Raw", ignoreCase = true) }
}

enum class ProductCategory(val displayName: String, val banglaLabel: String) {
  ALL("All", "সব পণ্য"),
  NUTS("Mixed Nuts", "শাহী নাটস"),
  GHEE("Pure Ghee", "খাঁটি ঘি"),
  HONEY("Raw Honey", "প্রাকৃতিক মধু"),
  COMBOS("Combos", "কম্বো বান্ডেল"),
  PRODUCE("Fresh Fruits", "অর্গানিক ফল")
}

enum class DietaryFilter(val label: String) {
  VEGAN("Vegan"),
  GLUTEN_FREE("Gluten-Free"),
  DAIRY_FREE("Dairy-Free"),
  RAW("100% Raw")
}

enum class SortOption(val label: String) {
  FEATURED("Featured"),
  RATING_DESC("Highest Rated"),
  PRICE_ASC("Price: Low to High"),
  PRICE_DESC("Price: High to Low")
}
