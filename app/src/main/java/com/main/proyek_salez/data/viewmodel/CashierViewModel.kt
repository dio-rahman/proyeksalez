package com.main.proyek_salez.data.viewmodel

import androidx.lifecycle.ViewModel
import com.main.proyek_salez.data.repository.CashierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CashierViewModel @Inject constructor(
    val repository: CashierRepository
) : ViewModel()