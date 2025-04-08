package com.salez.kasir.data.local

import androidx.room.*
import com.salez.kasir.data.local.daos.MemberDao
import com.salez.kasir.data.local.daos.MenuItemDao
import com.salez.kasir.data.local.daos.OrderDao
import com.salez.kasir.data.local.daos.UserDao

@Database(
    entities = [
        UserEntity::class,
        OrderEntity::class,
        MenuItemEntity::class,
        MemberEntity::class,
        OrderItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun memberDao(): MemberDao
}

// Entities
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val createdAt: Long
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val tableNumber: Int,
    val totalPrice: Double,
    val taxPercentage: Double,
    val taxAmount: Double,
    val discount: Double,
    val finalPrice: Double,
    val status: String,
    val customerId: String?,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long?
)

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val itemId: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val isAvailable: Boolean,
    val preparationTime: Int
)

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey val memberId: String,
    val name: String,
    val phone: String,
    val email: String?,
    val joinDate: Long,
    val totalSpent: Double,
    val totalOrders: Int,
    val discountPercentage: Double
)

@Entity(tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("orderId")
    ]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: String,
    val menuItemId: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val quantity: Int,
    val notes: String,
    val subtotal: Double
)
