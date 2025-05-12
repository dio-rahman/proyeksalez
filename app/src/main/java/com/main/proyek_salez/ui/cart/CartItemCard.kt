package com.main.proyek_salez.ui.cart

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.ui.theme.*

@Composable
fun CartItemCard(
    foodItem: FoodItemEntity,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (foodItem.imagePath != null) {
                AsyncImage(
                    model = foodItem.imagePath,
                    contentDescription = foodItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = foodItem.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = UnguTua,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                ),
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rp ${foodItem.price.toLong()}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AbuAbuGelap,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier
                        .size(24.dp)
                        .background(Oranye, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = if (quantity <= 1) Icons.Default.Delete else Icons.Default.Remove,
                        contentDescription = if (quantity <= 1) "Remove item" else "Decrease quantity",
                        tint = UnguTua,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier
                        .size(24.dp)
                        .background(Oranye, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase quantity",
                        tint = UnguTua,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}