package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DietaryBadge
import com.example.ui.components.OrganicProductArt
import com.example.ui.components.ReviewCard
import com.example.ui.components.StarRatingBar
import com.example.ui.components.WriteReviewDialog
import com.example.ui.theme.AmberStar
import com.example.ui.theme.ForestGreenContainer
import com.example.ui.theme.ForestGreenOnContainer
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.WarmAmberTertiary
import com.example.ui.viewmodel.OrganicMarketViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(
  viewModel: OrganicMarketViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val product by viewModel.selectedProduct.collectAsState()
  val reviews by viewModel.selectedProductReviews.collectAsState()
  val starFilter by viewModel.reviewStarFilter.collectAsState()
  val hasPurchased by viewModel.hasPurchasedCurrent.collectAsState()
  val selectedWeight by viewModel.selectedWeightOption.collectAsState()
  val cartCount by viewModel.cartCount.collectAsState()
  val cartItemsWithProducts by viewModel.cartItemsWithProducts.collectAsState()

  var quantity by remember { mutableIntStateOf(1) }
  var showWriteReviewDialog by remember { mutableStateOf(false) }
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  if (product == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("Product not found")
    }
    return
  }

  val currentProduct = product!!

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Harvest Details",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("detail_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = onNavigateToCart,
            modifier = Modifier.testTag("detail_cart_button")
          ) {
            BadgedBox(
              badge = {
                if (cartCount > 0) {
                  Badge(
                    containerColor = WarmAmberTertiary,
                    contentColor = Color.White
                  ) {
                    Text(text = cartCount.toString())
                  }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = "Shopping Cart",
                tint = ForestGreenPrimary
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      // Bottom sticky bar: Quantity selector and Add to Cart / One-Click Buy
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("add_to_cart_bottom_bar"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            // Quantity Selector
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              IconButton(
                onClick = { if (quantity > 1) quantity-- },
                modifier = Modifier
                  .size(34.dp)
                  .testTag("decrease_qty_button")
              ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease quantity", modifier = Modifier.size(16.dp))
              }

              Text(
                text = quantity.toString(),
                modifier = Modifier
                  .padding(horizontal = 10.dp)
                  .testTag("selected_qty_text"),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )

              IconButton(
                onClick = { quantity++ },
                modifier = Modifier
                  .size(34.dp)
                  .testTag("increase_qty_button")
              ) {
                Icon(Icons.Default.Add, contentDescription = "Increase quantity", modifier = Modifier.size(16.dp))
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Add to Cart Action
            val totalPrice = currentProduct.price * quantity
            OutlinedButton(
              onClick = {
                viewModel.addToCart(currentProduct.id, quantity)
                scope.launch {
                  snackbarHostState.showSnackbar("Added $quantity × ${currentProduct.name} to cart")
                }
              },
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("add_to_cart_primary_button"),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "কার্ট-এ যোগ",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Big Conspicuous "অর্ডার করুন / Buy Now" Primary Button
          val totalPrice = currentProduct.price * quantity
          Button(
            onClick = {
              viewModel.addToCart(currentProduct.id, quantity)
              onNavigateToCart()
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("direct_order_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = ForestGreenPrimary
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFFFFD54F))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "সরাসরি অর্ডার করুন • $${String.format(Locale.US, "%.2f", totalPrice)}",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 14.sp
            )
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .testTag("product_detail_scroll"),
      contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
    ) {
      // 1. Hero Artwork Banner
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.35f)
        ) {
          OrganicProductArt(
            iconType = currentProduct.iconType,
            modifier = Modifier.fillMaxSize(),
            badgeText = if (currentProduct.isBestSeller) "BEST SELLER • 100% PURE" else "100% CERTIFIED ORGANIC"
          )
        }
      }

      // 2. Product Headline, Bangla Name & Farm Origin
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Place,
                  contentDescription = null,
                  tint = ForestGreenPrimary,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = currentProduct.origin,
                  style = MaterialTheme.typography.labelMedium,
                  color = ForestGreenPrimary,
                  fontWeight = FontWeight.SemiBold
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = currentProduct.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )

              if (currentProduct.banglaName.isNotBlank()) {
                Text(
                  text = currentProduct.banglaName,
                  style = MaterialTheme.typography.titleMedium,
                  color = ForestGreenPrimary,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            // In-stock badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(ForestGreenContainer)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "In Stock",
                color = ForestGreenOnContainer,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Price & Unit
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "$${String.format(Locale.US, "%.2f", currentProduct.price)}",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = ForestGreenPrimary
            )
            if (currentProduct.originalPrice != null) {
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "$${String.format(Locale.US, "%.2f", currentProduct.originalPrice)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline,
                textDecoration = TextDecoration.LineThrough
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "per ${currentProduct.unit}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Rating summary anchor
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            StarRatingBar(rating = currentProduct.rating, starSize = 18.dp)
            Text(
              text = String.format(Locale.US, "%.1f", currentProduct.rating),
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = "(${reviews.size} verified customer reviews)",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Dietary & Quality Tags
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            currentProduct.dietaryTags.forEach { tag ->
              DietaryBadge(tag = tag)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Weight / Size Variant Selector
          Text(
            text = "Select Quantity / Weight (ওজন ও পরিমাণের ধরণ):",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            currentProduct.weightOptions.forEach { weight ->
              val isSelected = selectedWeight == weight
              FilterChip(
                selected = isSelected,
                onClick = { viewModel.selectWeightOption(weight) },
                label = { Text(weight, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = ForestGreenPrimary,
                  selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Quick WhatsApp Order Direct Contact
          OutlinedButton(
            onClick = {
              val msg = "Hello PureHarvest, I would like to order ${currentProduct.name} ($selectedWeight). Please confirm delivery."
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=8801700000000&text=${Uri.encode(msg)}"))
              try { context.startActivity(intent) } catch (_: Exception) {}
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("হোয়াটসঅ্যাপে অর্ডার করুন / WhatsApp Order", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
          }
        }
      }

      // 3. Certifications and Eco Guarantee
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = ForestGreenPrimary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Organic Certifications & Standards",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = currentProduct.certificationsCsv,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // 4. Description and Health Benefits
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Text(
            text = "Product Details & Purity (উপাদান ও স্বাস্থ্য উপকারিতা)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = currentProduct.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Storage & Nutrition card
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Storage & Freshness Tips (সংরক্ষণ পদ্ধতি)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = currentProduct.storageTips,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "Health & Nutritional Highlights (পুষ্টিগুণ)",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = currentProduct.nutritionalHighlights,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // 5. Dedicated Customer Reviews & Ratings Section (Core User Requirement)
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("reviews_section")
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Customer Reviews & Ratings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "ক্রেতাদের আসল রিভিউ ও রেটিং",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // Write a Review Button
            Button(
              onClick = { showWriteReviewDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("write_review_button")
            ) {
              Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Write Review")
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Rating Breakdown Card
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("rating_breakdown_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Left: Big Score
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp)
              ) {
                Text(
                  text = String.format(Locale.US, "%.1f", currentProduct.rating),
                  style = MaterialTheme.typography.displaySmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = ForestGreenPrimary
                )
                StarRatingBar(rating = currentProduct.rating, starSize = 16.dp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "${reviews.size} reviews",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              // Right: Distribution Bars (5 down to 1 star)
              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                for (star in 5 downTo 1) {
                  val starCount = reviews.count { it.rating == star }
                  val progress = if (reviews.isNotEmpty()) starCount.toFloat() / reviews.size.toFloat() else 0f

                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(4.dp))
                      .clickable {
                        viewModel.setReviewStarFilter(if (starFilter == star) null else star)
                      }
                      .padding(vertical = 2.dp)
                  ) {
                    Text(
                      text = "$star★",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.width(22.dp)
                    )
                    LinearProgressIndicator(
                      progress = { progress },
                      modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                      color = AmberStar,
                      trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "$starCount",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.width(18.dp)
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Star Filter Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            FilterChip(
              selected = starFilter == null,
              onClick = { viewModel.setReviewStarFilter(null) },
              label = { Text("All (${reviews.size})", fontSize = 12.sp) },
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.testTag("filter_reviews_all")
            )
            listOf(5, 4, 3, 2, 1).forEach { stars ->
              val count = reviews.count { it.rating == stars }
              FilterChip(
                selected = starFilter == stars,
                onClick = {
                  viewModel.setReviewStarFilter(if (starFilter == stars) null else stars)
                },
                label = { Text("$stars★ ($count)", fontSize = 12.sp) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("filter_reviews_${stars}_star")
              )
            }
          }
        }
      }

      // 6. Review Cards List
      if (reviews.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "No reviews yet for this rating",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(8.dp))
              OutlinedButton(
                onClick = { viewModel.setReviewStarFilter(null) }
              ) {
                Text("Show All Reviews")
              }
            }
          }
        }
      } else {
        items(reviews, key = { it.id }) { review ->
          Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            ReviewCard(
              review = review,
              onUpvoteHelpful = { viewModel.upvoteReview(review.id) }
            )
          }
        }
      }
    }
  }

  // Write Review Modal Dialog
  if (showWriteReviewDialog) {
    WriteReviewDialog(
      productName = currentProduct.name,
      isPurchasedByUser = hasPurchased,
      onDismiss = { showWriteReviewDialog = false },
      onSubmit = { name, rating, title, comment ->
        viewModel.submitReview(
          productId = currentProduct.id,
          reviewerName = name,
          rating = rating,
          title = title,
          comment = comment
        )
        showWriteReviewDialog = false
        scope.launch {
          snackbarHostState.showSnackbar("ধন্যবাদ! Your verified organic review has been posted.")
        }
      }
    )
  }
}
