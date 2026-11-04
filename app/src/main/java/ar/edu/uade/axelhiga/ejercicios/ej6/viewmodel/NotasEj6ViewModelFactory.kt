package ar.edu.uade.axelhiga.ejercicios.ej6.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.edu.uade.axelhiga.ejercicios.ej6.model.NotaRepository

class NotasEj6ViewModelFactory(
    private val repository: NotaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotasEj6ViewModel::class.java)) {
            return NotasEj6ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
