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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.main.proyek_salez.ui.cart.CartViewModel
import com.main.proyek_salez.ui.theme.*

@Composable
fun MenuItemCard(
    foodItem: FoodItem,
    cartViewModel: CartViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var itemCount by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
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
                if (foodItem.isPopular) {
                    Card(
                        modifier = Modifier
                            .padding(start = 4.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Oranye)
                    ) {
                        Text(
                            text = "Lagi Trend!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = UnguTua,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            Image(
                painter = painterResource(id = foodItem.imageRes),
                contentDescription = foodItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

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
                Icon(
                    painter = painterResource(id = foodItem.imageRes),
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = " ${foodItem.rating} (${foodItem.reviews})",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AbuAbuGelap,
                        fontSize = 8.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Hidangan ${foodItem.price}",
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
                            itemCount--
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
                        modifier = Modifier.fillMaxSize()
                    )
                }

                IconButton(
                    onClick = {
                        itemCount++
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
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}