package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Review
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [Product::class, Review::class, CartItem::class, Order::class],
  version = 1,
  exportSchema = false
)
abstract class PureHarvestDatabase : RoomDatabase() {
  abstract fun productDao(): ProductDao
  abstract fun reviewDao(): ReviewDao
  abstract fun cartDao(): CartDao
  abstract fun orderDao(): OrderDao

  companion object {
    @Volatile
    private var INSTANCE: PureHarvestDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): PureHarvestDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          PureHarvestDatabase::class.java,
          "pure_harvest_db"
        )
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              scope.launch {
                val database = getDatabase(context, scope)
                database.productDao().insertAll(InitialData.initialProducts)
                database.reviewDao().insertAll(InitialData.initialReviews)
                for (order in InitialData.initialOrders) {
                  database.orderDao().insertOrder(order)
                }
              }
            }
          })
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
