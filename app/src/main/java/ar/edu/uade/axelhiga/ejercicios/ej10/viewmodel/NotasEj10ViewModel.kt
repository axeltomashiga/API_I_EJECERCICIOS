package ar.edu.uade.axelhiga.ejercicios.ej10.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.ej10.model.ListaBorradorRepository
import ar.edu.uade.axelhiga.ejercicios.ej10.model.NotaEj10
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class NotasEj10UiState(
    val titulo: String = "",
    val contenido: String = "",
    val idEditando: String? = null,
    val isLoading: Boolean = false,
    val listaPersistida: List<NotaEj10> = emptyList()
)

class NotasEj10ViewModel(
    private val repository: ListaBorradorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotasEj10UiState())
    val uiState: StateFlow<NotasEj10UiState> = _uiState.asStateFlow()

    init {
        cargarNotas()
    }

    private fun cargarNotas() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val notasDesdeArchivo = repository.obtenerTodas()
            _uiState.value = _uiState.value.copy(
                listaPersistida = notasDesdeArchivo,
                isLoading = false
            )
        }
    }

    fun onTituloChange(titulo: String) {
        _uiState.value = _uiState.value.copy(titulo = titulo)
    }

    fun onContenidoChange(contenido: String) {
        _uiState.value = _uiState.value.copy(contenido = contenido)
    }

    fun onEditarClick(nota: NotaEj10) {
        _uiState.value = _uiState.value.copy(
            titulo = nota.titulo,
            contenido = nota.contenido,
            idEditando = nota.id
        )
    }

    fun onEliminarClick(id: String) {
        viewModelScope.launch {
            repository.eliminar(id)
            cargarNotas() // Refrescamos
        }
    }

    fun onGuardarClick() {
        val titulo = _uiState.value.titulo
        val contenido = _uiState.value.contenido

        // Representación de nota vacía (Consigna: No permitimos guardarla)
        if (titulo.isBlank() && contenido.isBlank()) return

        val idFinal = _uiState.value.idEditando ?: UUID.randomUUID().toString()
        val notaAGuardar = NotaEj10(idFinal, titulo, contenido)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.guardar(notaAGuardar)

            // Limpiamos el formulario y refrescamos
            _uiState.value = _uiState.value.copy(
                titulo = "",
                contenido = "",
                idEditando = null
            )
            cargarNotas()
        }
    }
}
