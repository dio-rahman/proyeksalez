package com.main.proyek_salez.ui.cart

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.main.proyek_salez.data.model.CartItemWithFood
import com.main.proyek_salez.ui.theme.*

@Composable
fun CartItemCard(
    modifier: Modifier = Modifier,
    cartItemWithFood: CartItemWithFood,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val foodItem = cartItemWithFood.foodItem
    val quantity = cartItemWithFood.cartItem.quantity

    val stateKey by remember(cartItemWithFood.cartItem.cartItemId, quantity) {
        mutableStateOf("${cartItemWithFood.cartItem.cartItemId}-$quantity")
    }

    Log.d("CartItemCard", "Rendering ${foodItem.name} with quantity: $quantity (key: $stateKey)")

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
                IconButton(
                    onClick = {
                        Log.d("CartItemCard", "Decrement clicked for ${foodItem.name}, current quantity: $quantity")
                        onDecrement()
                    },
                    modifier = Modifier
                        .size(20.dp)
                        .background(Oranye, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = if (quantity <= 1) Icons.Default.Delete else Icons.Default.Remove,
                        contentDescription = if (quantity <= 1) "Remove item" else "Decrease quantity",
                        tint = UnguTua,
                        modifier = Modifier.size(12.dp)
                    )
                }

                key(stateKey) {
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }

                IconButton(
                    onClick = {
                        Log.d("CartItemCard", "Increment clicked for ${foodItem.name}, current quantity: $quantity")
                        onIncrement()
                    },
                    modifier = Modifier
                        .size(20.dp)
                        .background(Oranye, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        tint = UnguTua,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}