package com.example

import com.example.data.local.InitialData
import com.example.data.model.Review
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testInitialProductsNotEmpty() {
    val products = InitialData.initialProducts
    assertTrue(products.isNotEmpty())
    val shahiNuts = products.find { it.id == "prod_shahi_nuts" }
    assertNotNull(shahiNuts)
    assertTrue(shahiNuts!!.isBestSeller)
    assertTrue(shahiNuts.weightOptions.contains("500g"))
  }

  @Test
  fun testDietaryFlags() {
    val apples = InitialData.initialProducts.find { it.id == "prod_honeycrisp" }!!
    assertTrue(apples.isVegan)
    assertTrue(apples.isGlutenFree)
    assertTrue(apples.isRaw)
  }

  @Test
  fun testReviewFormattedDate() {
    val review = Review(
      id = "test_rev",
      productId = "prod_shahi_nuts",
      reviewerName = "Tariqul",
      rating = 5,
      title = "Great",
      comment = "Excellent pure product",
      timestamp = 1700000000000L
    )
    assertNotNull(review.formattedDate)
    assertFalse(review.formattedDate.isEmpty())
  }
}
