package com.main.proyek_salez.data.repository

import com.main.proyek_salez.data.local.daos.OrderDao
import com.main.proyek_salez.data.local.OrderEntity
import com.main.proyek_salez.data.local.OrderItemEntity
import com.main.proyek_salez.data.models.MenuItem
import com.main.proyek_salez.data.models.Order
import com.main.proyek_salez.data.models.OrderItem
import com.main.proyek_salez.data.models.OrderStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao,
    private val memberRepository: MemberRepository
) {
    // Create Order
    suspend fun createOrder(order: Order): Result<Order> {
        return try {
            // Generate new order ID if not provided
            val orderId = order.orderId.ifEmpty {
                UUID.randomUUID().toString()
            }

            val newOrder = order.copy(orderId = orderId)

            // Apply member discount if applicable
            val finalOrder = if (order.customerId != null) {
                val member = memberRepository.getMemberById(order.customerId).getOrNull()
                if (member != null) {
                    val discount = (order.totalPrice * member.discountPercentage) / 100
                    newOrder.copy(
                        discount = discount,
                        finalPrice = order.totalPrice - discount
                    )
                } else {
                    newOrder
                }
            } else {
                newOrder
            }

            // Convert to entity and insert
            val orderEntity = OrderEntity(
                orderId = finalOrder.orderId,
                tableNumber = finalOrder.tableNumber,
                totalPrice = finalOrder.totalPrice,
                discount = finalOrder.discount,
                finalPrice = finalOrder.finalPrice,
                status = finalOrder.status.name,
                customerId = finalOrder.customerId,
                createdBy = finalOrder.createdBy,
                createdAt = finalOrder.createdAt,
                updatedAt = finalOrder.updatedAt,
                completedAt = finalOrder.completedAt,
                taxPercentage = finalOrder.taxPercentage,
                taxAmount = finalOrder.taxAmount
            )

            // Create order item entities
            val orderItemEntities = finalOrder.items.map { item ->
                OrderItemEntity(
                    orderId = finalOrder.orderId,
                    menuItemId = item.menuItem.itemId,
                    name = item.menuItem.name,
                    description = item.menuItem.description,
                    price = item.menuItem.price,
                    category = item.menuItem.category,
                    imageUrl = item.menuItem.imageUrl,
                    quantity = item.quantity,
                    notes = item.notes,
                    subtotal = item.subtotal
                )
            }

            // Insert order and items
            orderDao.insertOrderWithItems(orderEntity, orderItemEntities)

            // Update member stats if applicable
            if (finalOrder.customerId != null) {
                memberRepository.updateMemberStats(
                    memberId = finalOrder.customerId,
                    orderAmount = finalOrder.finalPrice
                )
            }

            Result.success(finalOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get Order By ID
    suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val orderEntity = orderDao.getOrderById(orderId)
                ?: return Result.failure(Exception("Order not found"))

            // Get order items
            val orderItemEntities = orderDao.getOrderItemsForOrder(orderId)

            // Convert to model
            val orderItems = orderItemEntities.map { entity ->
                OrderItem(
                    menuItem = MenuItem(
                        itemId = entity.menuItemId,
                        name = entity.name,
                        description = entity.description,
                        price = entity.price,
                        category = entity.category,
                        imageUrl = entity.imageUrl
                    ),
                    quantity = entity.quantity,
                    notes = entity.notes,
                    subtotal = entity.subtotal
                )
            }

            val order = Order(
                orderId = orderEntity.orderId,
                tableNumber = orderEntity.tableNumber,
                items = orderItems,
                totalPrice = orderEntity.totalPrice,
                discount = orderEntity.discount,
                finalPrice = orderEntity.finalPrice,
                status = OrderStatus.valueOf(orderEntity.status),
                customerId = orderEntity.customerId,
                createdBy = orderEntity.createdBy,
                createdAt = orderEntity.createdAt,
                updatedAt = orderEntity.updatedAt,
                completedAt = orderEntity.completedAt
            )

            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get Orders by Status
    fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>> {
        return orderDao.getOrdersByStatus(status.name).map { entities ->
            entities.map { entity ->
                // For each order, we need to get its items
                val orderItemEntities = orderDao.getOrderItemsForOrder(entity.orderId)

                val orderItems = orderItemEntities.map { itemEntity ->
                    OrderItem(
                        menuItem = MenuItem(
                            itemId = itemEntity.menuItemId,
                            name = itemEntity.name,
                            description = itemEntity.description,
                            price = itemEntity.price,
                            category = itemEntity.category,
                            imageUrl = itemEntity.imageUrl
                        ),
                        quantity = itemEntity.quantity,
                        notes = itemEntity.notes,
                        subtotal = itemEntity.subtotal
                    )
                }

                Order(
                    orderId = entity.orderId,
                    tableNumber = entity.tableNumber,
                    items = orderItems,
                    totalPrice = entity.totalPrice,
                    discount = entity.discount,
                    finalPrice = entity.finalPrice,
                    status = OrderStatus.valueOf(entity.status),
                    customerId = entity.customerId,
                    createdBy = entity.createdBy,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    completedAt = entity.completedAt
                )
            }
        }
    }

    // Get Today's Orders
    fun getTodayOrders(): Flow<List<Order>> {
        val startOfDay = getStartOfDayTimestamp()
        val endOfDay = getEndOfDayTimestamp()

        return orderDao.getTodayOrders(startOfDay, endOfDay).map { entities ->
            entities.map { entity ->
                // For each order, we need to get its items
                val orderItemEntities = orderDao.getOrderItemsForOrder(entity.orderId)

                val orderItems = orderItemEntities.map { itemEntity ->
                    OrderItem(
                        menuItem = MenuItem(
                            itemId = itemEntity.menuItemId,
                            name = itemEntity.name,
                            description = itemEntity.description,
                            price = itemEntity.price,
                            category = itemEntity.category,
                            imageUrl = itemEntity.imageUrl
                        ),
                        quantity = itemEntity.quantity,
                        notes = itemEntity.notes,
                        subtotal = itemEntity.subtotal
                    )
                }

                Order(
                    orderId = entity.orderId,
                    tableNumber = entity.tableNumber,
                    items = orderItems,
                    totalPrice = entity.totalPrice,
                    discount = entity.discount,
                    finalPrice = entity.finalPrice,
                    status = OrderStatus.valueOf(entity.status),
                    customerId = entity.customerId,
                    createdBy = entity.createdBy,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    completedAt = entity.completedAt
                )
            }
        }
    }

    // Update Order Status
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus, completedAt1: Long?): Result<Order> {
        return try {
            val completedAt = if (status == OrderStatus.COMPLETED) System.currentTimeMillis() else null

            // Update the status
            orderDao.updateOrderStatus(
                orderId = orderId,
                status = status.name,
                updatedAt = System.currentTimeMillis(),
                completedAt = completedAt
            )

            // Get updated order
            val updatedOrder = getOrderById(orderId).getOrNull()
                ?: return Result.failure(Exception("Failed to retrieve updated order"))

            Result.success(updatedOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get Orders by Date Range
    suspend fun getOrdersByDateRange(startDate: Long, endDate: Long): Result<List<Order>> {
        return try {
            val orderEntities = orderDao.getOrdersByDateRange(startDate, endDate)

            val orders = orderEntities.map { entity ->
                val orderItemEntities = orderDao.getOrderItemsForOrder(entity.orderId)

                val orderItems = orderItemEntities.map { itemEntity ->
                    OrderItem(
                        menuItem = MenuItem(
                            itemId = itemEntity.menuItemId,
                            name = itemEntity.name,
                            description = itemEntity.description,
                            price = itemEntity.price,
                            category = itemEntity.category,
                            imageUrl = itemEntity.imageUrl
                        ),
                        quantity = itemEntity.quantity,
                        notes = itemEntity.notes,
                        subtotal = itemEntity.subtotal
                    )
                }

                Order(
                    orderId = entity.orderId,
                    tableNumber = entity.tableNumber,
                    items = orderItems,
                    totalPrice = entity.totalPrice,
                    discount = entity.discount,
                    finalPrice = entity.finalPrice,
                    status = OrderStatus.valueOf(entity.status),
                    customerId = entity.customerId,
                    createdBy = entity.createdBy,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    completedAt = entity.completedAt
                )
            }

            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper functions for timestamp calculation
    private fun getStartOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}