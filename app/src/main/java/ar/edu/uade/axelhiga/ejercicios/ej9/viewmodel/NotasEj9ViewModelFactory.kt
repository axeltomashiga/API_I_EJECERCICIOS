package ar.edu.uade.axelhiga.ejercicios.ej9.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.edu.uade.axelhiga.ejercicios.ej9.model.CargaRepository

class NotasEj9ViewModelFactory(
    private val repository: CargaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotasEj9ViewModel::class.java)) {
            return NotasEj9ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
