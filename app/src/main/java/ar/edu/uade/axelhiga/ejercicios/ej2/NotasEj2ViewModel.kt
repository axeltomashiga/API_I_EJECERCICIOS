package ar.edu.uade.axelhiga.ejercicios.ej2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. DATA CLASS DE ESTADO
// Representa exactamente lo que la pantalla debe mostrar en un instante dado.
data class NotasEj2UiState(
    val titulo: String = "",
    val contenido: String = "",
    val errorValidacion: String? = null,
    val guardando: Boolean = false,
    val mensajeExito: String? = null
)

// 2. EVENTOS (Intents / Actions)
// Todo lo que el usuario puede "hacer" en la pantalla.
sealed class NotasEj2Event {
    data class OnTituloChange(val nuevoTitulo: String) : NotasEj2Event()
    data class OnContenidoChange(val nuevoContenido: String) : NotasEj2Event()
    object OnGuardarClick : NotasEj2Event()
    object OnMensajeExitoMostrado : NotasEj2Event()
}

class NotasEj2ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotasEj2UiState())
    val uiState: StateFlow<NotasEj2UiState> = _uiState.asStateFlow()

    fun onEvent(event: NotasEj2Event) {
        when (event) {
            is NotasEj2Event.OnTituloChange -> {
                _uiState.value = _uiState.value.copy(
                    titulo = event.nuevoTitulo,
                    errorValidacion = null // Limpiamos el error al escribir
                )
            }
            is NotasEj2Event.OnContenidoChange -> {
                _uiState.value = _uiState.value.copy(
                    contenido = event.nuevoContenido,
                    errorValidacion = null // Limpiamos el error al escribir
                )
            }
            is NotasEj2Event.OnGuardarClick -> {
                guardarNota()
            }
            is NotasEj2Event.OnMensajeExitoMostrado -> {
                _uiState.value = _uiState.value.copy(mensajeExito = null)
            }
        }
    }

    private fun guardarNota() {
        val tituloActual = _uiState.value.titulo
        val contenidoActual = _uiState.value.contenido

        // Validación
        if (tituloActual.isBlank()) {
            _uiState.value = _uiState.value.copy(errorValidacion = "El título es obligatorio")
            return
        }
        if (contenidoActual.isBlank()) {
            _uiState.value = _uiState.value.copy(errorValidacion = "El contenido es obligatorio")
            return
        }

        // Simulación de guardado (operación asíncrona en curso)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(guardando = true, errorValidacion = null)

            delay(1500) // Simulamos delay de red o base de datos

            // Estado post-guardado exitoso
            _uiState.value = NotasEj2UiState(
                mensajeExito = "Nota '$tituloActual' guardada correctamente"
            )
            // Se reseteó a NotasEj2UiState() limpio, por lo que titulo y contenido vuelven a "".
            // El guardando vuelve a ser false.
        }
    }
}
