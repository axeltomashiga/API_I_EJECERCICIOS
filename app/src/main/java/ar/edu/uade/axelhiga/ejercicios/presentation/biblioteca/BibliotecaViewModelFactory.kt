package ar.edu.uade.axelhiga.ejercicios.presentation.biblioteca

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.edu.uade.axelhiga.ejercicios.domain.repository.BibliotecaRepository

class BibliotecaViewModelFactory(
    private val repository: BibliotecaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BibliotecaViewModel::class.java)) {
            return BibliotecaViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}
