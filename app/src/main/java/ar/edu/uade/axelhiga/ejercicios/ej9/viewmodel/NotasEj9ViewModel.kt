package ar.edu.uade.axelhiga.ejercicios.ej9.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.ej9.model.CargaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

// 1. MODELADO DEL ESTADO (Cargando, Listo, Falló)
sealed class EstadoCarga {
    object Listo : EstadoCarga()
    object Cargando : EstadoCarga()
    data class Fallo(val mensaje: String) : EstadoCarga()
}

data class NotasEj9UiState(
    val textoFormulario: String = "",
    val estadoLectura: EstadoCarga = EstadoCarga.Listo,
    val estadoEscritura: EstadoCarga = EstadoCarga.Listo,
    val mensajeExito: String? = null
)

class NotasEj9ViewModel(
    private val repository: CargaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotasEj9UiState())
    val uiState: StateFlow<NotasEj9UiState> = _uiState.asStateFlow()

    init {
        leerArchivo()
    }

    private fun leerArchivo() {
        // Uso de viewModelScope (supervivencia al ciclo de vida y cancelación automática)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(estadoLectura = EstadoCarga.Cargando)
            try {
                val textoLeido = repository.leerTexto() // Suspende y espera (I/O)
                _uiState.value = _uiState.value.copy(
                    textoFormulario = textoLeido,
                    estadoLectura = EstadoCarga.Listo
                )
            } catch (e: CancellationException) {
                // ATENCIÓN: Exception NO conviene capturarla silenciosamente (ver justificación en Readme)
                throw e
            } catch (e: Exception) {
                // Capturamos cualquier otro error de lectura para no crashear la app
                _uiState.value = _uiState.value.copy(
                    estadoLectura = EstadoCarga.Fallo("No se pudo leer el archivo: ${e.message}")
                )
            }
        }
    }

    fun onTextoChange(nuevoTexto: String) {
        _uiState.value = _uiState.value.copy(
            textoFormulario = nuevoTexto,
            // Limpiamos mensajes previos al volver a escribir
            mensajeExito = null,
            estadoEscritura = EstadoCarga.Listo
        )
    }

    fun onGuardarClick() {
        if (_uiState.value.estadoEscritura == EstadoCarga.Cargando) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(estadoEscritura = EstadoCarga.Cargando)
            try {
                // Solo si esto NO lanza excepción, el guardado fue exitoso
                repository.escribirTexto(_uiState.value.textoFormulario)

                // Un mensaje "Guardado" SOLO corresponde DESPUÉS de completar la escritura sin errores.
                _uiState.value = _uiState.value.copy(
                    estadoEscritura = EstadoCarga.Listo,
                    mensajeExito = "¡Texto guardado exitosamente en el disco!"
                )
            } catch (e: CancellationException) {
                throw e // Permitimos que la corrutina se cancele estructuralmente
            } catch (e: Exception) {
                // Captura controlada del error de escritura
                _uiState.value = _uiState.value.copy(
                    estadoEscritura = EstadoCarga.Fallo("No se pudo guardar: ${e.message}")
                )
            }
        }
    }
}
