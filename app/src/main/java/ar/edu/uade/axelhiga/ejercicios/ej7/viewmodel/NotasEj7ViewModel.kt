package ar.edu.uade.axelhiga.ejercicios.ej7.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.ej7.model.ArchivoTextoRepository
import ar.edu.uade.axelhiga.ejercicios.ej7.model.ResultadoLectura
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotasEj7UiState(
    val textoBorrador: String = "",
    val estadoLecturaInfo: String = "Esperando lectura...",
    val estaCargando: Boolean = false
)

class NotasEj7ViewModel(
    private val repository: ArchivoTextoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotasEj7UiState())
    val uiState: StateFlow<NotasEj7UiState> = _uiState.asStateFlow()

    fun cargarBorrador() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(estaCargando = true)

            // El ViewModel SOLICITA la lectura al Repositorio
            // (no importa de dónde viene o si usó Dispatchers.IO, eso lo decide el Repo)
            val resultado = repository.leerBorrador()

            // El ViewModel PUBLICA el resultado hacia la Vista
            when (resultado) {
                is ResultadoLectura.Exito -> {
                    _uiState.value = _uiState.value.copy(
                        textoBorrador = resultado.texto,
                        estadoLecturaInfo = "¡Archivo leído exitosamente!",
                        estaCargando = false
                    )
                }
                is ResultadoLectura.NoExiste -> {
                    _uiState.value = _uiState.value.copy(
                        textoBorrador = "",
                        estadoLecturaInfo = "El archivo borrador.txt aún no existe.",
                        estaCargando = false
                    )
                }
                is ResultadoLectura.ErrorFalla -> {
                    _uiState.value = _uiState.value.copy(
                        estadoLecturaInfo = "ERROR GRAVE: ${resultado.mensaje}",
                        estaCargando = false
                    )
                }
            }
        }
    }

    // Método auxiliar (solo para testing manual del ejercicio)
    fun guardarBorradorDePrueba(texto: String) {
         viewModelScope.launch {
            repository.escribirBorrador(texto)
            cargarBorrador() // Refrescamos
         }
    }
}
