package ar.edu.uade.axelhiga.ejercicios.ej10.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.edu.uade.axelhiga.ejercicios.ej10.model.ListaBorradorRepository

class NotasEj10ViewModelFactory(
    private val repository: ListaBorradorRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotasEj10ViewModel::class.java)) {
            return NotasEj10ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
