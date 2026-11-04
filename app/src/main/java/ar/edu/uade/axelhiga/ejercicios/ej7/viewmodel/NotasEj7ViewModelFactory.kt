package ar.edu.uade.axelhiga.ejercicios.ej7.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.edu.uade.axelhiga.ejercicios.ej7.model.ArchivoTextoRepository

class NotasEj7ViewModelFactory(
    private val repository: ArchivoTextoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotasEj7ViewModel::class.java)) {
            return NotasEj7ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
