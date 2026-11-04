package ar.edu.uade.axelhiga.ejercicios.ej3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. DATA CLASS DE ESTADO
data class NotasEj3UiState(
    val titulo: String = "",
    val contenido: String = "",
    val errorValidacion: String? = null,
    val guardando: Boolean = false,
    val mensajeExito: String? = null,
    val intentosGuardado: Int = 0 // NUEVO: Desafío (contador de vida del ViewModel)
)

// 2. EVENTOS (Intents / Actions)
sealed class NotasEj3Event {
    data class OnTituloChange(val nuevoTitulo: String) : NotasEj3Event()
    data class OnContenidoChange(val nuevoContenido: String) : NotasEj3Event()
    object OnGuardarClick : NotasEj3Event()
    object OnMensajeExitoMostrado : NotasEj3Event()
}

// 3. VIEWMODEL
// Extiende de ViewModel. Conserva su estado ante cambios de configuración (como rotar pantalla).
class NotasEj3ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotasEj3UiState())
    val uiState: StateFlow<NotasEj3UiState> = _uiState.asStateFlow()

    init {
        // Imprimimos para probar visualmente cuándo nace el ViewModel
        println("NotasEj3ViewModel: ¡Instancia Creada!")
    }

    override fun onCleared() {
        super.onCleared()
        // Imprimimos para probar visualmente cuándo muere el ViewModel
        println("NotasEj3ViewModel: ¡Instancia Destruida (onCleared)!")
    }

    fun onEvent(event: NotasEj3Event) {
        when (event) {
            is NotasEj3Event.OnTituloChange -> {
                _uiState.value = _uiState.value.copy(
                    titulo = event.nuevoTitulo,
                    errorValidacion = null
                )
            }
            is NotasEj3Event.OnContenidoChange -> {
                _uiState.value = _uiState.value.copy(
                    contenido = event.nuevoContenido,
                    errorValidacion = null
                )
            }
            is NotasEj3Event.OnGuardarClick -> {
                guardarNota()
            }
            is NotasEj3Event.OnMensajeExitoMostrado -> {
                _uiState.value = _uiState.value.copy(mensajeExito = null)
            }
        }
    }

    private fun guardarNota() {
        // DESAFÍO: Incrementar el contador de intentos en el estado.
        // Al rotar la pantalla (reiniciar la Activity), este número NO debe volver a cero
        // porque el ViewModel sobrevive a la rotación.
        val contadorActualizado = _uiState.value.intentosGuardado + 1
        _uiState.value = _uiState.value.copy(intentosGuardado = contadorActualizado)

        val tituloActual = _uiState.value.titulo
        val contenidoActual = _uiState.value.contenido

        if (tituloActual.isBlank()) {
            _uiState.value = _uiState.value.copy(errorValidacion = "El título es obligatorio")
            return
        }
        if (contenidoActual.isBlank()) {
            _uiState.value = _uiState.value.copy(errorValidacion = "El contenido es obligatorio")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(guardando = true, errorValidacion = null)

            // Simulamos guardado
            delay(1500)

            _uiState.value = NotasEj3UiState(
                mensajeExito = "Nota guardada (Intento #$contadorActualizado)",
                intentosGuardado = contadorActualizado // Mantenemos el contador vivo
            )
        }
    }
}
