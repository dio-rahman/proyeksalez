package com.main.proyek_salez.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.main.proyek_salez.R
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.data.viewmodel.CartViewModel
import com.main.proyek_salez.ui.theme.*

    @Composable
fun MenuItemCard(
    foodItem: FoodItemEntity,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val itemCount = cartItems.find { it.foodItem.id == foodItem.id }?.cartItem?.quantity ?: 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .height(280.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                // Kosong untuk mempertahankan tata letak asli
            }

            if (foodItem.imagePath != null) {
                AsyncImage(
                    model = foodItem.imagePath,
                    contentDescription = foodItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.salez_logo),
                    contentDescription = "Placeholder",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
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
                    fontSize = 12.sp
                ),
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = foodItem.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AbuAbuGelap,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Kosong untuk mempertahankan tata letak asli
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Rp ${foodItem.price}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AbuAbuGelap,
                    fontSize = 8.sp,
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
                    onClick = {
                        if (itemCount > 0) {
                            cartViewModel.decrementItem(foodItem)
                        }
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .background(Oranye, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Remove",
                        tint = UnguTua,
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (itemCount > 0) {
                    Text(
                        text = itemCount.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                IconButton(
                    onClick = {
                        cartViewModel.addToCart(foodItem)
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .background(Oranye, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = UnguTua,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}