package ar.edu.uade.axelhiga.ejercicios.ej5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotasEj5UiState(
    val titulo: String = "",
    val contenido: String = "",
    val errorTitulo: String? = null,
    val errorContenido: String? = null,
    val guardando: Boolean = false,
    val mensajeExito: String? = null
)

sealed class NotasEj5Event {
    data class OnTituloChange(val titulo: String) : NotasEj5Event()
    data class OnContenidoChange(val contenido: String) : NotasEj5Event()
    object OnGuardarClick : NotasEj5Event()
    object OnMensajeExitoMostrado : NotasEj5Event()
}

class NotasEj5ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotasEj5UiState())
    val uiState: StateFlow<NotasEj5UiState> = _uiState.asStateFlow()

    fun onEvent(event: NotasEj5Event) {
        when (event) {
            is NotasEj5Event.OnTituloChange -> {
                // Al tipear, limpiamos el error específico del título
                _uiState.value = _uiState.value.copy(
                    titulo = event.titulo,
                    errorTitulo = null
                )
            }
            is NotasEj5Event.OnContenidoChange -> {
                // Al tipear, limpiamos el error específico del contenido
                _uiState.value = _uiState.value.copy(
                    contenido = event.contenido,
                    errorContenido = null
                )
            }
            is NotasEj5Event.OnGuardarClick -> validarYGuardar()
            is NotasEj5Event.OnMensajeExitoMostrado -> {
                _uiState.value = _uiState.value.copy(mensajeExito = null)
            }
        }
    }

    private fun validarYGuardar() {
        // Regla: Prohibición de guardar dos veces mientras se procesa la solicitud
        if (_uiState.value.guardando) return

        val titulo = _uiState.value.titulo
        val contenido = _uiState.value.contenido

        // Reglas de validación
        val tituloEsValido = titulo.isNotBlank()
        // Trim para que los "espacios en blanco" puros no sumen caracteres válidos
        val contenidoEsValido = contenido.trim().length >= 10

        if (!tituloEsValido || !contenidoEsValido) {
            _uiState.value = _uiState.value.copy(
                errorTitulo = if (!tituloEsValido) "El título no puede estar vacío" else null,
                errorContenido = if (!contenidoEsValido) "Debe tener al menos 10 caracteres" else null
            )
            return
        }

        // Si pasa validaciones, simula guardado
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                guardando = true,
                errorTitulo = null,
                errorContenido = null
            )

            delay(2000) // Simulación de carga

            _uiState.value = NotasEj5UiState(mensajeExito = "¡Nota guardada correctamente!")
        }
    }
}
