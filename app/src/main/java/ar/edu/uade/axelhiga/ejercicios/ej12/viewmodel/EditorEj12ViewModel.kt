package ar.edu.uade.axelhiga.ejercicios.ej12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.ej10.model.NotaEj10
import ar.edu.uade.axelhiga.ejercicios.ej12.model.NotaEj12Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class EditorUiState(
    val id: String? = null,
    val titulo: String = "",
    val contenido: String = "",
    val guardadoConExito: Boolean = false // Bandera para avisarle a la UI que navegue hacia atrás
)

// VIEWMODEL DE LA PANTALLA 2 (EL DETALLE/EDITOR)
class EditorEj12ViewModel(
    private val repository: NotaEj12Repository,
    private val notaId: String? // El identificador pasado por la navegación
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        // Consigna: Usar identificadores simples en lugar de objetos gigantes.
        // Si recibimos un ID, le pedimos al Repo que nos devuelva la nota original para editar.
        if (notaId != null) {
            viewModelScope.launch {
                val notaOriginal = repository.obtenerPorId(notaId)
                if (notaOriginal != null) {
                    _uiState.value = _uiState.value.copy(
                        id = notaOriginal.id,
                        titulo = notaOriginal.titulo,
                        contenido = notaOriginal.contenido
                    )
                }
            }
        }
    }

    fun onTituloChange(titulo: String) {
        _uiState.value = _uiState.value.copy(titulo = titulo)
    }

    fun onContenidoChange(contenido: String) {
        _uiState.value = _uiState.value.copy(contenido = contenido)
    }

    fun onGuardarClick() {
        val titulo = _uiState.value.titulo
        val contenido = _uiState.value.contenido
        if (titulo.isBlank() && contenido.isBlank()) return

        val notaAGuardar = NotaEj10(
            id = _uiState.value.id ?: UUID.randomUUID().toString(),
            titulo = titulo,
            contenido = contenido
        )

        viewModelScope.launch {
            // Guardamos a través del Repository compartido
            repository.guardar(notaAGuardar)
            // Avisamos a la Vista que ya terminamos para que cierre la pantalla (popBackStack)
            _uiState.value = _uiState.value.copy(guardadoConExito = true)
        }
    }
}
