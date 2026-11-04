package ar.edu.uade.axelhiga.ejercicios.ej8.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.edu.uade.axelhiga.ejercicios.ej8.model.BorradorRepository

class NotasEj8ViewModelFactory(
    private val repository: BorradorRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotasEj8ViewModel::class.java)) {
            return NotasEj8ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
