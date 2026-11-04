package ar.edu.uade.axelhiga.ejercicios.ej12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.ej10.model.NotaEj10
import ar.edu.uade.axelhiga.ejercicios.ej12.model.NotaEj12Repository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// VIEWMODEL DE LA PANTALLA 1 (LA LISTA)
class ListaEj12ViewModel(
    private val repository: NotaEj12Repository
) : ViewModel() {

    // Simplemente se suscribe al Flow global del Repositorio.
    // Si la pantalla de Editor guarda algo, el Repositorio emite aquí y la lista se auto-refresca.
    val notas: StateFlow<List<NotaEj10>> = repository.notas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onEliminarClick(id: String) {
        viewModelScope.launch {
            repository.eliminar(id)
        }
    }
}
