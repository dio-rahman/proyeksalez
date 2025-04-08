package com.salez.kasir.data.repository

import android.content.Context
import android.net.Uri
import com.salez.kasir.data.local.daos.MenuItemDao
import com.salez.kasir.data.local.MenuItemEntity
import com.salez.kasir.data.models.MenuItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepository @Inject constructor(
    private val menuItemDao: MenuItemDao,
    @ApplicationContext private val context: Context
) {
    fun getAllMenuItems(): Flow<List<MenuItem>> {
        return menuItemDao.getAllAvailableMenuItems().map { entities ->
            entities.map { entity ->
                MenuItem(
                    itemId = entity.itemId,
                    name = entity.name,
                    description = entity.description,
                    price = entity.price,
                    category = entity.category,
                    imageUrl = entity.imageUrl,
                    isAvailable = entity.isAvailable,
                    preparationTime = entity.preparationTime
                )
            }
        }
    }

    fun getMenuItemsByCategory(category: String): Flow<List<MenuItem>> {
        return menuItemDao.getMenuItemsByCategory(category).map { entities ->
            entities.map { entity ->
                MenuItem(
                    itemId = entity.itemId,
                    name = entity.name,
                    description = entity.description,
                    price = entity.price,
                    category = entity.category,
                    imageUrl = entity.imageUrl,
                    isAvailable = entity.isAvailable,
                    preparationTime = entity.preparationTime
                )
            }
        }
    }

    fun getAllCategories(): Flow<List<String>> {
        return menuItemDao.getAllCategories()
    }

    suspend fun addMenuItem(menuItem: MenuItem, imageUri: Uri?): Result<MenuItem> {
        return try {
            // Generate ID if not provided
            val itemId = menuItem.itemId.ifEmpty {
                UUID.randomUUID().toString()
            }

            // Save image to local storage if provided
            val imageUrl = if (imageUri != null) {
                saveImageToLocalStorage(imageUri, itemId)
            } else {
                menuItem.imageUrl
            }

            val newMenuItem = menuItem.copy(
                itemId = itemId,
                imageUrl = imageUrl
            )

            // Convert to entity and insert
            val menuItemEntity = MenuItemEntity(
                itemId = newMenuItem.itemId,
                name = newMenuItem.name,
                description = newMenuItem.description,
                price = newMenuItem.price,
                category = newMenuItem.category,
                imageUrl = newMenuItem.imageUrl,
                isAvailable = newMenuItem.isAvailable,
                preparationTime = newMenuItem.preparationTime
            )

            menuItemDao.insertMenuItem(menuItemEntity)
            Result.success(newMenuItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMenuItem(menuItem: MenuItem, imageUri: Uri?): Result<MenuItem> {
        return try {
            // Save new image if provided
            val imageUrl = if (imageUri != null) {
                saveImageToLocalStorage(imageUri, menuItem.itemId)
            } else {
                menuItem.imageUrl
            }

            val updatedMenuItem = menuItem.copy(imageUrl = imageUrl)

            // Convert to entity and insert (replace)
            val menuItemEntity = MenuItemEntity(
                itemId = updatedMenuItem.itemId,
                name = updatedMenuItem.name,
                description = updatedMenuItem.description,
                price = updatedMenuItem.price,
                category = updatedMenuItem.category,
                imageUrl = updatedMenuItem.imageUrl,
                isAvailable = updatedMenuItem.isAvailable,
                preparationTime = updatedMenuItem.preparationTime
            )

            menuItemDao.insertMenuItem(menuItemEntity)
            Result.success(updatedMenuItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleMenuItemAvailability(itemId: String, isAvailable: Boolean): Result<Unit> {
        return try {
            menuItemDao.updateMenuItemAvailability(itemId, isAvailable)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper method to save images locally instead of Firebase Storage
    private fun saveImageToLocalStorage(uri: Uri, fileName: String): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val imagesDir = File(context.filesDir, "menu_images")
        if (!imagesDir.exists()) {
            imagesDir.mkdir()
        }

        val file = File(imagesDir, "$fileName.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return "file://${file.absolutePath}"
    }
}