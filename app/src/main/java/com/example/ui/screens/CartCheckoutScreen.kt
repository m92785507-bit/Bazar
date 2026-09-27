package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Order
import com.example.ui.components.OrganicProductArt
import com.example.ui.theme.ForestGreenContainer
import com.example.ui.theme.ForestGreenOnContainer
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.WarmAmberTertiary
import com.example.ui.viewmodel.OrganicMarketViewModel
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
  viewModel: OrganicMarketViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToOrders: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cartItems by viewModel.cartItemsWithProducts.collectAsState()
  val cartSubtotal by viewModel.cartSubtotal.collectAsState()
  val deliveryAddress by viewModel.deliveryAddress.collectAsState()
  val specialInstructions by viewModel.specialInstructions.collectAsState()
  val deliveryOption by viewModel.deliveryOption.collectAsState()
  val paymentMethod by viewModel.paymentMethod.collectAsState()
  val promoCode by viewModel.promoCode.collectAsState()
  val promoMessage by viewModel.promoMessage.collectAsState()

  var promoInput by remember { mutableStateOf(promoCode) }
  var orderSuccessDialogOrder by remember { mutableStateOf<Order?>(null) }

  // Calculation
  val discount = if (promoCode.trim().equals("ORGANIC15", ignoreCase = true)) {
    ((cartSubtotal * 0.15) * 100.0).roundToInt() / 100.0
  } else if (promoCode.trim().equals("FRESHFIRST", ignoreCase = true)) {
    5.00
  } else 0.0

  val deliveryFee = when {
    cartItems.isEmpty() -> 0.0
    deliveryOption.contains("Pickup", ignoreCase = true) -> 0.0
    cartSubtotal >= 35.0 -> 0.0
    deliveryOption.contains("Express", ignoreCase = true) -> 4.99
    else -> 2.99
  }

  val finalTotal = if (cartItems.isEmpty()) 0.0 else {
    ((cartSubtotal - discount + deliveryFee).coerceAtLeast(0.0) * 100.0).roundToInt() / 100.0
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Organic Cart & Checkout",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("checkout_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      if (cartItems.isNotEmpty()) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("place_order_bottom_bar"),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "Total to Pay",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "$${String.format(Locale.US, "%.2f", finalTotal)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = ForestGreenPrimary
              )
            }

            Button(
              onClick = {
                viewModel.placeOrder { order ->
                  orderSuccessDialogOrder = order
                }
              },
              modifier = Modifier
                .height(48.dp)
                .testTag("place_order_button"),
              colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
              shape = RoundedCornerShape(14.dp)
            ) {
              Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("অর্ডার কনফার্ম করুন", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
          }
        }
      }
    }
  ) { innerPadding ->
    if (cartItems.isEmpty()) {
      Box(
        modifier = modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .background(ForestGreenContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingBag,
              contentDescription = null,
              tint = ForestGreenOnContainer,
              modifier = Modifier.size(42.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "Your Organic Basket is Empty",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Explore our fresh orchard harvests, pasture-raised eggs, raw honey, and artisanal sourdough.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(20.dp))
          Button(
            onClick = onNavigateBack,
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("browse_market_empty_btn")
          ) {
            Text("Browse Fresh Harvest")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = modifier
          .fillMaxSize()
          .padding(innerPadding)
          .testTag("checkout_scroll_list"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
          start = 16.dp,
          end = 16.dp,
          top = 12.dp,
          bottom = 90.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Section 1: Cart Items Header & List
        item {
          Text(
            text = "Your Harvest Basket (${cartItems.sumOf { it.quantity }} items)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ForestGreenPrimary
          )
        }

        items(cartItems, key = { it.product.id }) { item ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("cart_item_card_${item.product.id}"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Artwork Thumbnail
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .clip(RoundedCornerShape(10.dp))
              ) {
                OrganicProductArt(
                  iconType = item.product.iconType,
                  modifier = Modifier.fillMaxSize(),
                  badgeText = null
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              // Title and Price
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = item.product.name,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1
                )
                Text(
                  text = "$${String.format(Locale.US, "%.2f", item.product.price)} / ${item.product.unit}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Subtotal: $${String.format(Locale.US, "%.2f", item.subtotal)}",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = ForestGreenPrimary
                )
              }

              // Quantity Controls
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(MaterialTheme.colorScheme.surfaceVariant)
              ) {
                IconButton(
                  onClick = { viewModel.addToCart(item.product.id, -1) },
                  modifier = Modifier.size(30.dp)
                ) {
                  Icon(
                    imageVector = if (item.quantity == 1) Icons.Default.DeleteOutline else Icons.Default.Remove,
                    contentDescription = "Decrease",
                    modifier = Modifier.size(15.dp)
                  )
                }
                Text(
                  text = item.quantity.toString(),
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  modifier = Modifier.padding(horizontal = 6.dp)
                )
                IconButton(
                  onClick = { viewModel.addToCart(item.product.id, 1) },
                  modifier = Modifier.size(30.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(15.dp))
                }
              }
            }
          }
        }

        // Section 2: Delivery Method
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = ForestGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Delivery Method",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              val deliveryOptions = listOf(
                "Standard Eco-Friendly (1-2 days)" to if (cartSubtotal >= 35.0) "FREE ($35+ order)" else "$2.99",
                "Same-Day Fresh Express" to "$4.99",
                "Local Farm Hub Pickup" to "FREE"
              )

              deliveryOptions.forEach { (optionName, priceDesc) ->
                val isSelected = deliveryOption == optionName
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { viewModel.setDeliveryOption(optionName) }
                    .padding(vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                      selected = isSelected,
                      onClick = { viewModel.setDeliveryOption(optionName) },
                      colors = RadioButtonDefaults.colors(selectedColor = ForestGreenPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = optionName, style = MaterialTheme.typography.bodyMedium)
                  }
                  Text(
                    text = priceDesc,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (priceDesc.contains("FREE")) ForestGreenPrimary else MaterialTheme.colorScheme.onSurface
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              // Eco Tote Initiative note
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(ForestGreenContainer)
                  .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Eco, contentDescription = null, tint = ForestGreenOnContainer, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Packaged in 100% biodegradable cornstarch insulated boxes.",
                  fontSize = 11.sp,
                  color = ForestGreenOnContainer
                )
              }
            }
          }
        }

        // Section 3: Delivery Address & Instructions
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = ForestGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Delivery Destination",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = deliveryAddress,
                onValueChange = { viewModel.setDeliveryAddress(it) },
                label = { Text("Delivery Address") },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("delivery_address_input"),
                shape = RoundedCornerShape(12.dp)
              )

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = specialInstructions,
                onValueChange = { viewModel.setSpecialInstructions(it) },
                label = { Text("Harvest Delivery Instructions (optional)") },
                placeholder = { Text("e.g. Leave in shady spot; gate code #1234") },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("special_instructions_input"),
                shape = RoundedCornerShape(12.dp)
              )
            }
          }
        }

        // Section 4: Promo Code
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Organic Promo Code",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                OutlinedTextField(
                  value = promoInput,
                  onValueChange = { promoInput = it },
                  placeholder = { Text("Enter code") },
                  modifier = Modifier
                    .weight(1f)
                    .testTag("promo_code_input"),
                  shape = RoundedCornerShape(12.dp),
                  singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                  onClick = { viewModel.applyPromoCode(promoInput) },
                  colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.testTag("apply_promo_button")
                ) {
                  Text("Apply")
                }
              }

              if (promoMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = promoMessage!!,
                  fontSize = 12.sp,
                  color = if (discount > 0) ForestGreenPrimary else MaterialTheme.colorScheme.error,
                  fontWeight = FontWeight.Medium
                )
              }

              Spacer(modifier = Modifier.height(6.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(
                  onClick = {
                    promoInput = "ORGANIC15"
                    viewModel.applyPromoCode("ORGANIC15")
                  },
                  label = { Text("Tap to apply ORGANIC15 (15% off)") },
                  colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = ForestGreenContainer
                  )
                )
              }
            }
          }
        }

        // Section 5: Payment Method
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payment, contentDescription = null, tint = ForestGreenPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Payment Method",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              listOf(
                "Cash on Delivery (ক্যাশ অন ডেলিভারি)",
                "bKash / Nagad / Rocket (মোবাইল ব্যাংকিং)",
                "Debit / Credit Card (ভিসা / মাস্টারকার্ড)"
              ).forEach { method ->
                val isSelected = paymentMethod == method
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.setPaymentMethod(method) }
                    .padding(vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = isSelected,
                    onClick = { viewModel.setPaymentMethod(method) },
                    colors = RadioButtonDefaults.colors(selectedColor = ForestGreenPrimary)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(text = method, style = MaterialTheme.typography.bodyMedium)
                }
              }
            }
          }
        }

        // Section 6: Summary Breakdown
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "Order Summary",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Items Subtotal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${String.format(Locale.US, "%.2f", cartSubtotal)}")
              }

              if (discount > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                  Text("Organic Discount ($promoCode)", color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
                  Text("-$${String.format(Locale.US, "%.2f", discount)}", color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
                }
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Eco Packaging & Insulated Tote", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("FREE", color = ForestGreenPrimary, fontWeight = FontWeight.Bold)
              }

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Delivery Fee", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (deliveryFee == 0.0) "FREE" else "$${String.format(Locale.US, "%.2f", deliveryFee)}")
              }

              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("Order Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                  "$${String.format(Locale.US, "%.2f", finalTotal)}",
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.ExtraBold,
                  color = ForestGreenPrimary
                )
              }
            }
          }
        }
      }
    }
  }

  // Order Success Celebration Dialog
  if (orderSuccessDialogOrder != null) {
    val order = orderSuccessDialogOrder!!
    Dialog(
      onDismissRequest = {
        orderSuccessDialogOrder = null
        viewModel.dismissOrderSuccess()
        onNavigateToOrders()
      },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth(0.92f)
          .padding(16.dp)
          .testTag("order_success_dialog"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(ForestGreenContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = ForestGreenPrimary,
              modifier = Modifier.size(42.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "Harvest Order Confirmed!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = ForestGreenPrimary,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Order #${order.orderNumber}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Your fresh organic order has been transmitted to our regional farms for hand-picking and packaging.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Detail box
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "Total Paid: $${String.format(Locale.US, "%.2f", order.total)}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = "Destination: ${order.deliveryAddress}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "Estimated Arrival: Within 24-48 hours",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ForestGreenPrimary
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = {
              orderSuccessDialogOrder = null
              viewModel.dismissOrderSuccess()
              onNavigateToOrders()
            },
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("view_orders_button")
          ) {
            Text("View Orders & Review Harvest")
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedButton(
            onClick = {
              orderSuccessDialogOrder = null
              viewModel.dismissOrderSuccess()
              onNavigateBack()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Continue Shopping")
          }
        }
      }
    }
  }
}
