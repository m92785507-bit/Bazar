package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DietaryFilter
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.SortOption
import com.example.ui.components.DietaryBadge
import com.example.ui.components.OrganicProductArt
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.ForestGreenContainer
import com.example.ui.theme.ForestGreenOnContainer
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.WarmAmberTertiary
import com.example.ui.viewmodel.OrganicMarketViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductListScreen(
  viewModel: OrganicMarketViewModel,
  onProductClick: (String) -> Unit,
  onNavigateToCart: () -> Unit,
  onNavigateToOrders: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val selectedDietary by viewModel.selectedDietaryFilters.collectAsState()
  val priceRange by viewModel.priceRange.collectAsState()
  val selectedSort by viewModel.selectedSort.collectAsState()
  val filteredProducts by viewModel.filteredProducts.collectAsState()
  val cartCount by viewModel.cartCount.collectAsState()
  val cartItemsWithProducts by viewModel.cartItemsWithProducts.collectAsState()

  var showFilterPanel by remember { mutableStateOf(false) }
  var showSupportModal by remember { mutableStateOf(false) }

  val activeFilterCount = (if (selectedCategory != ProductCategory.ALL) 1 else 0) +
    selectedDietary.size +
    (if (priceRange.start > 0f || priceRange.endInclusive < 50f) 1 else 0) +
    (if (selectedSort != SortOption.FEATURED) 1 else 0)

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ForestGreenPrimary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "PureHarvest",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = ForestGreenPrimary
              )
              Text(
                text = "খাঁটি প্রিমিয়াম শাহী নাট, ঘি ও মধু",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          IconButton(
            onClick = onNavigateToOrders,
            modifier = Modifier.testTag("orders_history_button")
          ) {
            Icon(
              imageVector = Icons.Default.ReceiptLong,
              contentDescription = "My Orders",
              tint = ForestGreenPrimary
            )
          }

          IconButton(
            onClick = onNavigateToCart,
            modifier = Modifier.testTag("top_cart_button")
          ) {
            BadgedBox(
              badge = {
                if (cartCount > 0) {
                  Badge(
                    containerColor = WarmAmberTertiary,
                    contentColor = Color.White
                  ) {
                    Text(text = cartCount.toString(), modifier = Modifier.testTag("cart_badge_count"))
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
    floatingActionButton = {
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Dedicated WhatsApp Customer Support FAB
        FloatingActionButton(
          onClick = { showSupportModal = true },
          containerColor = Color(0xFF25D366),
          contentColor = Color.White,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.testTag("whatsapp_support_fab")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Chat, contentDescription = "WhatsApp Support", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "WhatsApp Support",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }

        // Cart Checkout FAB (if cart has items)
        if (cartCount > 0) {
          FloatingActionButton(
            onClick = onNavigateToCart,
            containerColor = ForestGreenPrimary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.testTag("floating_cart_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.ShoppingBag, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "$cartCount items • Checkout",
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. Search Bar Row with Advanced Filter Toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.onSearchQueryChange(it) },
          placeholder = { Text("Search nuts, ghee, honey, fruits...") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = ForestGreenPrimary
            )
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear search")
              }
            }
          },
          modifier = Modifier
            .weight(1f)
            .testTag("product_search_input"),
          singleLine = true,
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedBorderColor = ForestGreenPrimary
          )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Filter Toggle Button
        IconButton(
          onClick = { showFilterPanel = !showFilterPanel },
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (showFilterPanel || activeFilterCount > 0) ForestGreenContainer else MaterialTheme.colorScheme.surfaceVariant)
            .testTag("filter_toggle_button")
        ) {
          BadgedBox(
            badge = {
              if (activeFilterCount > 0) {
                Badge(containerColor = ForestGreenPrimary) {
                  Text(text = activeFilterCount.toString())
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.FilterList,
              contentDescription = "Filters",
              tint = if (activeFilterCount > 0) ForestGreenOnContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // 2. Category Filter Tabs Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ProductCategory.values().forEach { category ->
          val isSelected = selectedCategory == category
          FilterChip(
            selected = isSelected,
            onClick = { viewModel.onCategorySelect(category) },
            label = {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = category.displayName,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 13.sp
                )
                Text(
                  text = category.banglaLabel,
                  fontSize = 10.sp,
                  color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ForestGreenPrimary,
              selectedLabelColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("category_chip_${category.name}")
          )
        }
      }

      // 3. Expandable Advanced Filter Tray (Categories, Price range, Dietary preferences)
      AnimatedVisibility(
        visible = showFilterPanel,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("advanced_filter_panel"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Filter Organic Produce & Food",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
              if (activeFilterCount > 0) {
                TextButton(
                  onClick = { viewModel.resetFilters() },
                  modifier = Modifier.testTag("reset_filters_button")
                ) {
                  Text("Reset All", color = MaterialTheme.colorScheme.error)
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dietary Preferences Filter
            Text(
              text = "Dietary Preferences",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              DietaryFilter.values().forEach { dietary ->
                val isSelected = selectedDietary.contains(dietary)
                FilterChip(
                  selected = isSelected,
                  onClick = { viewModel.toggleDietaryFilter(dietary) },
                  label = { Text(dietary.label, fontSize = 12.sp) },
                  leadingIcon = if (isSelected) {
                    { Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                  } else null,
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.testTag("dietary_chip_${dietary.name}")
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price Range Slider
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Price Range",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "$${String.format(Locale.US, "%.2f", priceRange.start)} - $${String.format(Locale.US, "%.2f", priceRange.endInclusive)}",
                style = MaterialTheme.typography.labelMedium,
                color = ForestGreenPrimary,
                fontWeight = FontWeight.Bold
              )
            }
            RangeSlider(
              value = priceRange,
              onValueChange = { viewModel.onPriceRangeChange(it) },
              valueRange = 0f..50f,
              steps = 24,
              colors = SliderDefaults.colors(
                thumbColor = ForestGreenPrimary,
                activeTrackColor = ForestGreenPrimary
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("price_range_slider")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sort Options
            Text(
              text = "Sort By",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              SortOption.values().forEach { sort ->
                val isSelected = selectedSort == sort
                FilterChip(
                  selected = isSelected,
                  onClick = { viewModel.onSortSelect(sort) },
                  label = { Text(sort.label, fontSize = 12.sp) },
                  shape = RoundedCornerShape(16.dp),
                  modifier = Modifier.testTag("sort_chip_${sort.name}")
                )
              }
            }
          }
        }
      }

      // 4. Products Grid with Hero Banner & Trust Badges at top
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
          .fillMaxSize()
          .testTag("product_grid"),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Hero Offer Banner
        if (searchQuery.isBlank() && selectedCategory == ProductCategory.ALL) {
          item(span = { GridItemSpan(2) }) {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onProductClick("prod_royal_combo") },
              colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(
                    Brush.horizontalGradient(
                      colors = listOf(Color(0xFF1B4D3E), Color(0xFF2E7D32), Color(0xFFD97706))
                    )
                  )
                  .padding(16.dp)
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFB300))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = "ROYAL COMBO OFFER • 15% OFF",
                        color = Color(0xFF261E05),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "খাঁটি শাহী মিক্সড নাট, গাওয়া ঘি ও সুন্দরবনের মধু",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 22.sp
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "প্রাকৃতিক শক্তির সেরা সমন্বয় • ক্যাশ অন ডেলিভারি সুবিধা",
                    color = Color(0xFFE8F5E9),
                    fontSize = 12.sp
                  )
                  Spacer(modifier = Modifier.height(10.dp))
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Button(
                      onClick = { onProductClick("prod_royal_combo") },
                      colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                      shape = RoundedCornerShape(20.dp),
                      contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                      Text("অর্ডার করুন / View Deal", color = ForestGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                  }
                }
              }
            }
          }

          // Trust Badges Bar
          item(span = { GridItemSpan(2) }) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              listOf(
                Triple(Icons.Default.Verified, "১০০% খাঁটি ও বিশুদ্ধ", "Lab Tested Pure"),
                Triple(Icons.Default.Security, "ক্যাশ অন ডেলিভারি", "Pay on Delivery"),
                Triple(Icons.Default.LocalShipping, "দ্রুত হোম ডেলিভারি", "Fresh to Doorstep")
              ).forEach { (icon, bn, en) ->
                Card(
                  modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                  Column(
                    modifier = Modifier.padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Icon(icon, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = bn, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text(text = en, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                  }
                }
              }
            }
          }
        }

        // WhatsApp / Hotline Quick Order Contact Card
        item(span = { GridItemSpan(2) }) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=8801700000000&text=PureHarvest%20Order%20Inquiry"))
                try { context.startActivity(intent) } catch (_: Exception) {}
              },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "সরাসরি হোয়াটসঅ্যাপে অর্ডার করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF1B5E20)
                  )
                  Text(
                    text = "হটলাইন ও অর্ডার সহায়তা: +880 1700-000000",
                    fontSize = 10.sp,
                    color = Color(0xFF2E7D32)
                  )
                }
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFF2E7D32))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("WhatsApp", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        // Section Title: Products Count
        item(span = { GridItemSpan(2) }) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${filteredProducts.size} টি প্রিমিয়াম অর্গানিক পণ্য",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = ForestGreenPrimary
            )
            if (activeFilterCount > 0) {
              Text(
                text = "ফিল্টার চালু ($activeFilterCount)",
                fontSize = 11.sp,
                color = ForestGreenPrimary,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        // Product Cards
        items(filteredProducts, key = { it.id }) { product ->
          val cartItem = cartItemsWithProducts.find { it.product.id == product.id }
          ProductGridCard(
            product = product,
            cartQuantity = cartItem?.quantity ?: 0,
            onClick = { onProductClick(product.id) },
            onAddToCart = { viewModel.addToCart(product.id, 1) },
            modifier = Modifier.testTag("product_card_${product.id}")
          )
        }

        // Customer Support & Hotline Contact Section
        item(span = { GridItemSpan(2) }) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 16.dp, bottom = 8.dp)
              .testTag("contact_support_section"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Chat,
                  contentDescription = null,
                  tint = Color(0xFF2E7D32),
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "অর্ডার সংক্রান্ত যে কোনো তথ্যের জন্য",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = ForestGreenPrimary
              )
              Text(
                text = "আমাদের কাস্টমার কেয়ার প্রতিনিধি হোয়াটসঅ্যাপে সর্বদা প্রস্তুত",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(12.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = { showSupportModal = true },
                  modifier = Modifier.weight(1f).testTag("support_section_whatsapp_btn"),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("WhatsApp সাপোর্ট", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                  onClick = {
                    com.example.util.WhatsAppHelper.callSupportHotline(context)
                  },
                  modifier = Modifier.weight(1f).testTag("support_section_call_btn"),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Icon(Icons.Default.Call, contentDescription = null, tint = ForestGreenPrimary, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("হটলাইন কল", color = ForestGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }
        }
      }
    }
  }

  // Customer Support & WhatsApp Modal Dialog
  if (showSupportModal) {
    com.example.ui.components.CustomerSupportModal(
      onDismiss = { showSupportModal = false }
    )
  }
}

@Composable
fun ProductGridCard(
  product: Product,
  cartQuantity: Int,
  onClick: () -> Unit,
  onAddToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Art Header
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1.22f)
      ) {
        OrganicProductArt(
          iconType = product.iconType,
          modifier = Modifier.fillMaxSize(),
          badgeText = if (product.isBestSeller) "BEST SELLER" else "100% PURE"
        )
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp)
      ) {
        // Origin
        Text(
          text = product.origin,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Product Name (English)
        Text(
          text = product.name,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        // Bangla Name
        if (product.banglaName.isNotBlank()) {
          Text(
            text = product.banglaName,
            style = MaterialTheme.typography.labelSmall,
            color = ForestGreenPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Rating & Review Count
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          StarRatingBar(rating = product.rating, starSize = 13.dp)
          Text(
            text = "${String.format(Locale.US, "%.1f", product.rating)} (${product.reviewCount})",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Dietary tags preview
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          product.dietaryTags.take(2).forEach { tag ->
            DietaryBadge(tag = tag)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Price & Add to Cart button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "$${String.format(Locale.US, "%.2f", product.price)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = ForestGreenPrimary
              )
              if (product.originalPrice != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "$${String.format(Locale.US, "%.2f", product.originalPrice)}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.outline,
                  textDecoration = TextDecoration.LineThrough
                )
              }
            }
            Text(
              text = product.unit,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Prominent Order Button
          if (cartQuantity > 0) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(ForestGreenContainer)
                .clickable { onAddToCart() }
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .testTag("in_cart_badge_${product.id}"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$cartQuantity টি অর্ডারে",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreenOnContainer
              )
            }
          } else {
            Button(
              onClick = onAddToCart,
              colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
              modifier = Modifier.testTag("order_button_${product.id}")
            ) {
              Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("অর্ডার করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
