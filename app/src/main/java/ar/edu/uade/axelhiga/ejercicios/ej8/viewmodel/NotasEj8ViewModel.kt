package ar.edu.uade.axelhiga.ejercicios.ej8.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.ej8.model.BorradorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotasEj8UiState(
    val textoFormulario: String = "",
    val estaGuardando: Boolean = false,
    val mensajeStatus: String = "Escribe y guarda."
)

class NotasEj8ViewModel(
    private val repository: BorradorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotasEj8UiState())
    val uiState: StateFlow<NotasEj8UiState> = _uiState.asStateFlow()

    init {
        // Al arrancar (o volver de matar la app), leemos desde el disco
        cargarDesdeDisco()
    }

    private fun cargarDesdeDisco() {
        viewModelScope.launch {
            val textoEnDisco = repository.leerBorrador()
            _uiState.value = _uiState.value.copy(
                textoFormulario = textoEnDisco,
                mensajeStatus = "Recuperado desde el disco."
            )
        }
    }

    fun onTextoChange(nuevoTexto: String) {
        // El texto vive SOLO en la RAM del ViewModel (Estado efímero)
        _uiState.value = _uiState.value.copy(
            textoFormulario = nuevoTexto,
            mensajeStatus = "Editando sin guardar..."
        )
    }

    fun onGuardarClick() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(estaGuardando = true)

            // Persiste los datos físicamente (Disco)
            repository.escribirBorrador(_uiState.value.textoFormulario)

            _uiState.value = _uiState.value.copy(
                estaGuardando = false,
                mensajeStatus = "¡Guardado en disco permanentemente!"
            )
        }
    }

    // DESAFÍO: Descartar cambios
    // Simplemente re-lee el archivo de texto y sobreescribe lo que estaba en memoria.
    fun onDescartarClick() {
        _uiState.value = _uiState.value.copy(mensajeStatus = "Descartando...")
        cargarDesdeDisco()
    }
}
