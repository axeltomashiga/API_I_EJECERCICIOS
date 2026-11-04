package ar.edu.uade.axelhiga.ejercicios.ej12

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.edu.uade.axelhiga.ejercicios.ej12.model.NotaEj12Repository
import ar.edu.uade.axelhiga.ejercicios.ej12.viewmodel.EditorEj12ViewModel
import ar.edu.uade.axelhiga.ejercicios.ej12.viewmodel.ListaEj12ViewModel

// Fábrica para el ViewModel de la Lista
class ListaEj12ViewModelFactory(
    private val repository: NotaEj12Repository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ListaEj12ViewModel(repository) as T
    }
}

// Fábrica para el ViewModel del Editor (inyecta el repo Y el id de navegación)
class EditorEj12ViewModelFactory(
    private val repository: NotaEj12Repository,
    private val notaId: String?
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditorEj12ViewModel(repository, notaId) as T
    }
}
