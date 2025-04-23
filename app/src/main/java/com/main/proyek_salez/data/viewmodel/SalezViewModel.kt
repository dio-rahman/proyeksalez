package com.main.proyek_salez.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.main.proyek_salez.data.repository.SalezRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SalezViewModel @Inject constructor(
    val repository: SalezRepository
) : ViewModel()