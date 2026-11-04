package ar.edu.uade.axelhiga.ejercicios.ej4

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 1. DATA CLASS (Estado Inmutable)
data class NotaUiState(
    val titulo: String = "",
    val contenido: String = ""
)

// 2. VIEWMODEL
class NotasEj4ViewModel : ViewModel() {

    // Privado y Mutable: Solo el ViewModel puede cambiar los datos
    private val _uiState = MutableStateFlow(NotaUiState())

    // Público y de Solo Lectura: La Pantalla solo puede observar
    val uiState: StateFlow<NotaUiState> = _uiState.asStateFlow()

    fun actualizarTitulo(nuevoTitulo: String) {
        // DESAFÍO: Cambio puntual usando .copy()
        // Esto crea un nuevo objeto cambiando SOLO el título, conservando el contenido intacto.
        _uiState.value = _uiState.value.copy(titulo = nuevoTitulo)
    }

    fun actualizarContenido(nuevoContenido: String) {
        // DESAFÍO: Cambio puntual usando .copy()
        // Conserva el título escrito y actualiza solo el contenido.
        _uiState.value = _uiState.value.copy(contenido = nuevoContenido)
    }
}
