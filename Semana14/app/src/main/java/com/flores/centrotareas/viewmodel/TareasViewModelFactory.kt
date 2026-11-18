package com.flores.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flores.centrotareas.data.TareasRepository

// Commit 1: fábrica del ViewModel (proyecto base)
class TareasViewModelFactory(private val repo: TareasRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = TareasViewModel(repo) as T
}
