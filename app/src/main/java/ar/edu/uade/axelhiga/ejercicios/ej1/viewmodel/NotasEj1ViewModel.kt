package ar.edu.uade.axelhiga.ejercicios.ej1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.ej1.data.NotaEntity
import ar.edu.uade.axelhiga.ejercicios.ej1.model.NotasEj1Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Clase para agrupar el estado temporal de la pantalla (formulario)
data class NotasFormUiState(
    val titulo: String = "",
    val contenido: String = "",
    val mensajeError: String = ""
)

// 2. VIEWMODEL (Capa de Presentación)
// Maneja el estado temporal, valida las reglas de negocio y coordina con el Repository.
class NotasEj1ViewModel(
    private val repository: NotasEj1Repository
) : ViewModel() {

    private val _formState = MutableStateFlow(NotasFormUiState())
    val formState: StateFlow<NotasFormUiState> = _formState.asStateFlow()

    // Convertimos el Flow de Room a un StateFlow para que Compose lo observe
    val notas: StateFlow<List<NotaEntity>> = repository.notas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onTituloChange(nuevoTitulo: String) {
        _formState.value = _formState.value.copy(titulo = nuevoTitulo, mensajeError = "")
    }

    fun onContenidoChange(nuevoContenido: String) {
        _formState.value = _formState.value.copy(contenido = nuevoContenido, mensajeError = "")
    }

    fun onGuardarClick() {
        val currentTitulo = _formState.value.titulo
        val currentContenido = _formState.value.contenido

        // DÓNDE SE VALIDAN LOS DATOS (Lógica de Negocio)
        if (currentTitulo.isBlank()) {
            _formState.value = _formState.value.copy(mensajeError = "El título no puede estar vacío")
            return
        }
        if (currentContenido.isBlank()) {
            _formState.value = _formState.value.copy(mensajeError = "El contenido no puede estar vacío")
            return
        }

        // DÓNDE SE MODIFICA LA LISTA / SE GUARDA (Delegado al Repository con Corrutinas)
        viewModelScope.launch {
            repository.guardar(currentTitulo, currentContenido)
            // Limpiamos el formulario y los errores tras un guardado exitoso
            _formState.value = NotasFormUiState()
        }
    }
}
