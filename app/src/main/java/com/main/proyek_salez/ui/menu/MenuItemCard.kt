package com.main.proyek_salez.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.ui.theme.*

@Composable
fun MenuItemCard(
    modifier: Modifier = Modifier,
    foodItem: FoodItemEntity,
    quantity: Int,
    onAddToCart: (FoodItemEntity) -> Unit,
    onRemoveFromCart: (FoodItemEntity) -> Unit,
    onDeleteFromCart: (FoodItemEntity) -> Unit
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Putih),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (foodItem.imagePath != null) {
                AsyncImage(
                    model = foodItem.imagePath,
                    contentDescription = foodItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = foodItem.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = UnguTua,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                ),
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Rp ${foodItem.price.toLong()}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AbuAbuGelap,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 1.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (quantity > 0) {
                    IconButton(
                        onClick = {
                            if (quantity == 1) onDeleteFromCart(foodItem) else onRemoveFromCart(foodItem)
                        },
                        modifier = Modifier
                            .size(20.dp)
                            .background(Oranye, shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = if (quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                            contentDescription = if (quantity == 1) "Delete item" else "Remove item",
                            tint = UnguTua,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(20.dp))
                }
                if (quantity > 0) {
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                } else {
                    Spacer(modifier = Modifier.width(12.dp))
                }
                IconButton(
                    onClick = { onAddToCart(foodItem) },
                    modifier = Modifier
                        .size(20.dp)
                        .background(Oranye, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to cart",
                        tint = UnguTua,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}