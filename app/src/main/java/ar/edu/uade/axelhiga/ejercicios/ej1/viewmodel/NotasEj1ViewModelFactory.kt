package ar.edu.uade.axelhiga.ejercicios.ej1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.edu.uade.axelhiga.ejercicios.ej1.model.NotasEj1Repository

class NotasEj1ViewModelFactory(
    private val repository: NotasEj1Repository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotasEj1ViewModel::class.java)) {
            return NotasEj1ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
