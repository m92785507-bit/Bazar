package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.PureHarvestDatabase
import com.example.data.repository.OrganicRepository
import com.example.ui.screens.CartCheckoutScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProductListScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.OrganicMarketViewModel

sealed interface Screen {
  data object ProductList : Screen
  data class ProductDetail(val productId: String) : Screen
  data object CartCheckout : Screen
  data object Orders : Screen
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = PureHarvestDatabase.getDatabase(applicationContext)
    val repository = OrganicRepository(
      productDao = database.productDao(),
      reviewDao = database.reviewDao(),
      cartDao = database.cartDao(),
      orderDao = database.orderDao()
    )

    val viewModelFactory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrganicMarketViewModel(repository) as T
      }
    }

    setContent {
      MyApplicationTheme {
        val viewModel: OrganicMarketViewModel = viewModel(factory = viewModelFactory)
        PureHarvestApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun PureHarvestApp(viewModel: OrganicMarketViewModel) {
  var backStack by remember { mutableStateOf<List<Screen>>(listOf(Screen.ProductList)) }
  val currentScreen = backStack.lastOrNull() ?: Screen.ProductList

  fun navigateTo(screen: Screen) {
    backStack = backStack + screen
  }

  fun navigateBack() {
    if (backStack.size > 1) {
      backStack = backStack.dropLast(1)
    }
  }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    when (val screen = currentScreen) {
      is Screen.ProductList -> {
        ProductListScreen(
          viewModel = viewModel,
          onProductClick = { productId ->
            viewModel.selectProduct(productId)
            navigateTo(Screen.ProductDetail(productId))
          },
          onNavigateToCart = { navigateTo(Screen.CartCheckout) },
          onNavigateToOrders = { navigateTo(Screen.Orders) }
        )
      }

      is Screen.ProductDetail -> {
        BackHandler { navigateBack() }
        ProductDetailScreen(
          viewModel = viewModel,
          onNavigateBack = { navigateBack() },
          onNavigateToCart = { navigateTo(Screen.CartCheckout) }
        )
      }

      is Screen.CartCheckout -> {
        BackHandler { navigateBack() }
        CartCheckoutScreen(
          viewModel = viewModel,
          onNavigateBack = { navigateBack() },
          onNavigateToOrders = { navigateTo(Screen.Orders) }
        )
      }

      is Screen.Orders -> {
        BackHandler { navigateBack() }
        OrdersScreen(
          viewModel = viewModel,
          onNavigateBack = { navigateBack() },
          onProductClick = { productId ->
            viewModel.selectProduct(productId)
            navigateTo(Screen.ProductDetail(productId))
          }
        )
      }
    }
  }
}
