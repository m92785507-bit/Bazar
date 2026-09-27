package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForestGreenContainer
import com.example.ui.theme.ForestGreenOnContainer
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.viewmodel.OrganicMarketViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
  viewModel: OrganicMarketViewModel,
  onNavigateBack: () -> Unit,
  onProductClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val orders by viewModel.orders.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "My Organic Orders & Purchases",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("orders_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    if (orders.isEmpty()) {
      Box(
        modifier = modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.ReceiptLong,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.outline
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No Orders Yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "When you place an organic order, it will appear here so you can track deliveries and review items.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }
    } else {
      LazyColumn(
        modifier = modifier
          .fillMaxSize()
          .padding(innerPadding)
          .testTag("orders_list"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        items(orders, key = { it.id }) { order ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("order_card_${order.id}"),
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
                Column {
                  Text(
                    text = "Order #${order.orderNumber}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary
                  )
                  Text(
                    text = order.formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ForestGreenContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = order.status,
                    color = ForestGreenOnContainer,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = "Purchased Organic Items (Tap to Review):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Parse items "productId:qty:price:name"
              val itemsParsed = order.itemsJson.split(",").filter { it.isNotBlank() }
              itemsParsed.forEach { itemStr ->
                val parts = itemStr.split(":")
                val productId = parts.getOrNull(0) ?: ""
                val qty = parts.getOrNull(1) ?: "1"
                val price = parts.getOrNull(2) ?: "0.00"
                val name = parts.getOrNull(3) ?: "Organic Produce"

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "$qty × $name",
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.Medium
                    )
                    Text(
                      text = "$$price each",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  // "Review Item" button
                  OutlinedButton(
                    onClick = { onProductClick(productId) },
                    modifier = Modifier.testTag("review_item_btn_$productId"),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.RateReview,
                      contentDescription = null,
                      modifier = Modifier.size(14.dp),
                      tint = ForestGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Review", fontSize = 12.sp, color = ForestGreenPrimary)
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Delivered to: ${order.deliveryAddress}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.weight(1f)
                )
                Text(
                  text = "Total: $${String.format(Locale.US, "%.2f", order.total)}",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreenPrimary
                )
              }
            }
          }
        }
      }
    }
  }
}
