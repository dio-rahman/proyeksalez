package com.salez.kasir.ui.cashier.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.salez.kasir.data.models.Order
import com.salez.kasir.data.models.OrderStatus
import com.salez.kasir.ui.theme.CompletedColor
import com.salez.kasir.ui.theme.PendingColor
import com.salez.kasir.ui.theme.PendingText
import com.salez.kasir.ui.theme.ProcessingColor
import com.salez.kasir.utils.FormatUtils

@Composable
fun OrderList(
    orders: List<Order>,
    emptyMessage: String,
    onOrderClick: ((Order) -> Unit)? = null
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(orders) { order ->
                OrderCard(
                    order = order,
                    onClick = { if (onOrderClick != null) onOrderClick(order) }
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit = {}
) {
    val timeString = FormatUtils.formatTime(order.createdAt)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                // Order ID and Table
                Column {
                    Text(
                        text = "Meja #${order.tableNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Order #${order.orderId.takeLast(4).uppercase()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Status Badge
                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Order summary (items count, total price)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${order.items.size} item • $timeString",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = FormatUtils.formatPrice(order.finalPrice),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Show first 2 items only if there are items
            if (order.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    order.items.take(2).forEach { item ->
                        Text(
                            text = "• ${item.quantity}x ${item.menuItem.name}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Show "and X more" if there are more than 2 items
                    if (order.items.size > 2) {
                        Text(
                            text = "• dan ${order.items.size - 2} item lainnya",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        OrderStatus.PENDING -> Triple(PendingColor, PendingText, "Menunggu")
        OrderStatus.PROCESSING -> Triple(ProcessingColor, Color.White, "Dibuat")
        OrderStatus.COMPLETED -> Triple(CompletedColor, Color.White, "Selesai")
        OrderStatus.CANCELLED -> Triple(Color.Gray, Color.White, "Dibatalkan")
    }

    Surface(
        color = backgroundColor,
        contentColor = textColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}