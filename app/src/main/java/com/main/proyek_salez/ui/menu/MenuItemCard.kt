package com.main.proyek_salez.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.main.proyek_salez.data.model.FoodItemEntity
import com.main.proyek_salez.ui.theme.*

@Composable
fun MenuItemCard(
    foodItem: FoodItemEntity,
    onAddToCart: (FoodItemEntity) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Putih),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = foodItem.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rp ${foodItem.price.toLong()}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AbuAbuGelap
                    )
                )
            }
            Button(
                onClick = { onAddToCart(foodItem) },
                modifier = Modifier
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                shape = RoundedCornerShape(50),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "Tambah",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}