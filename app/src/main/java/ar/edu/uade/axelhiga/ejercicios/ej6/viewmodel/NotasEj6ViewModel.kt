package ar.edu.uade.axelhiga.ejercicios.ej6.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.ej6.model.Nota
import ar.edu.uade.axelhiga.ejercicios.ej6.model.NotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class NotasEj6UiState(
    val titulo: String = "",
    val contenido: String = "",
    val error: String? = null
)

class NotasEj6ViewModel(
    // El ViewModel solo conoce EL CONTRATO (Interfaz).
    // No sabe (ni le importa) si es Room, Memoria o Internet.
    private val repository: NotaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotasEj6UiState())
    val uiState: StateFlow<NotasEj6UiState> = _uiState.asStateFlow()

    // Observamos directamente el repositorio reactivo
    val notas: StateFlow<List<Nota>> = repository.obtenerTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onTituloChange(titulo: String) {
        _uiState.value = _uiState.value.copy(titulo = titulo, error = null)
    }

    fun onContenidoChange(contenido: String) {
        _uiState.value = _uiState.value.copy(contenido = contenido, error = null)
    }

    fun onGuardarClick() {
        val titulo = _uiState.value.titulo
        val contenido = _uiState.value.contenido

        if (titulo.isBlank() || contenido.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Ningún campo puede estar vacío")
            return
        }

        val nuevaNota = Nota(
            id = UUID.randomUUID().toString(),
            titulo = titulo,
            contenido = contenido
        )

        viewModelScope.launch {
            repository.guardar(nuevaNota)
            _uiState.value = NotasEj6UiState() // Limpiamos formulario
        }
    }

    fun onEliminarClick(id: String) {
        viewModelScope.launch {
            repository.eliminar(id)
        }
    }
}
