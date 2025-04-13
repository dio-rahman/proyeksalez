package com.salez.kasir.ui.manager

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salez.kasir.data.models.MenuItem
import com.salez.kasir.data.repository.MenuRepository
import com.salez.kasir.data.repository.MenuRepository1
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuManagementViewModel @Inject constructor(
    private val menuRepository: MenuRepository1,
    private val fullMenuRepository: MenuRepository
) : ViewModel() {

    private val TAG = "MenuManagementViewModel"

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadAllMenuItems()
        loadCategories()
    }

    fun loadAllMenuItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Loading menu items...")
                menuRepository.getAllMenuItems().collect { items ->
                    _menuItems.value = items
                    Log.d(TAG, "Menu items loaded: ${items.size}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading menu items", e)
                _errorMessage.value = "Gagal memuat menu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d(TAG, "Loading categories...")
                menuRepository.getAllCategories().collect { categories ->
                    _categories.value = categories
                    Log.d(TAG, "Categories loaded: ${categories.size}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading categories", e)
                _errorMessage.value = "Gagal memuat kategori: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addMenuItem(menuItem: MenuItem, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Although we are using the interface in the constructor,
                // we need to use the concrete implementation for certain operations
                // In a real app, you would adjust the interface to include all needed methods
                if (fullMenuRepository != null) {
                    Log.d(TAG, "Adding menu item: ${menuItem.name}")
                    val result = fullMenuRepository.addMenuItem(menuItem, imageUri)
                    if (result.isSuccess) {
                        Log.d(TAG, "Menu item added successfully")
                        // Refresh the menu list
                        loadAllMenuItems()
                    } else {
                        Log.e(TAG, "Error adding menu item", result.exceptionOrNull())
                        _errorMessage.value = "Gagal menambahkan menu: ${result.exceptionOrNull()?.message}"
                    }
                } else {
                    // Fallback when using the interface without the addMenuItem method
                    // In a real app with direct Firebase integration, you might add to Firestore directly
                    // For now, we'll simulate adding an item
                    simulateAddMenuItem(menuItem)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding menu item", e)
                _errorMessage.value = "Gagal menambahkan menu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMenuItem(menuItem: MenuItem, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (fullMenuRepository != null) {
                    Log.d(TAG, "Updating menu item: ${menuItem.name}")
                    val result = fullMenuRepository.updateMenuItem(menuItem, imageUri)
                    if (result.isSuccess) {
                        Log.d(TAG, "Menu item updated successfully")
                        // Refresh the menu list
                        loadAllMenuItems()
                    } else {
                        Log.e(TAG, "Error updating menu item", result.exceptionOrNull())
                        _errorMessage.value = "Gagal mengupdate menu: ${result.exceptionOrNull()?.message}"
                    }
                } else {
                    // Fallback for interface implementation
                    simulateUpdateMenuItem(menuItem)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating menu item", e)
                _errorMessage.value = "Gagal mengupdate menu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // In a real app, you would call a repository method for this
                Log.d(TAG, "Deleting menu item: $itemId")
                // fullMenuRepository.deleteMenuItem(itemId)

                // For now, simulate deletion
                simulateDeleteMenuItem(itemId)
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting menu item", e)
                _errorMessage.value = "Gagal menghapus menu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleMenuItemAvailability(itemId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (fullMenuRepository != null) {
                    Log.d(TAG, "Toggling menu item availability: $itemId to $isAvailable")
                    val result = fullMenuRepository.toggleMenuItemAvailability(itemId, isAvailable)
                    if (result.isSuccess) {
                        Log.d(TAG, "Menu item availability toggled successfully")
                        // Refresh the menu list
                        loadAllMenuItems()
                    } else {
                        Log.e(TAG, "Error toggling menu item availability", result.exceptionOrNull())
                        _errorMessage.value = "Gagal mengubah ketersediaan menu: ${result.exceptionOrNull()?.message}"
                    }
                } else {
                    // Fallback for interface implementation
                    simulateToggleAvailability(itemId, isAvailable)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error toggling menu item availability", e)
                _errorMessage.value = "Gagal mengubah ketersediaan menu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Simulation methods for cases where full implementation isn't available

    private fun simulateAddMenuItem(menuItem: MenuItem) {
        // Generate a simple ID
        val newId = "item${System.currentTimeMillis()}"
        val newMenuItem = menuItem.copy(itemId = newId)

        // Add to current list
        _menuItems.update { currentList ->
            currentList + newMenuItem
        }

        Log.d(TAG, "Menu item added (simulated): ${newMenuItem.name}")
    }

    private fun simulateUpdateMenuItem(menuItem: MenuItem) {
        _menuItems.update { currentList ->
            currentList.map {
                if (it.itemId == menuItem.itemId) menuItem else it
            }
        }

        Log.d(TAG, "Menu item updated (simulated): ${menuItem.name}")
    }

    private fun simulateDeleteMenuItem(itemId: String) {
        _menuItems.update { currentList ->
            currentList.filter { it.itemId != itemId }
        }

        Log.d(TAG, "Menu item deleted (simulated): $itemId")
    }

    private fun simulateToggleAvailability(itemId: String, isAvailable: Boolean) {
        _menuItems.update { currentList ->
            currentList.map {
                if (it.itemId == itemId) it.copy(isAvailable = isAvailable) else it
            }
        }

        Log.d(TAG, "Menu item availability toggled (simulated): $itemId to $isAvailable")
    }

    // Reset error message
    fun resetErrorMessage() {
        _errorMessage.value = null
    }
}