package ar.edu.uade.axelhiga.ejercicios.presentation.notas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity
import ar.edu.uade.axelhiga.ejercicios.data.repository.NotaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotasViewModel(private val repository: NotaRepository) : ViewModel() {

    // 1. Exposición del flujo de Room convertido en StateFlow para la UI
    val notas: StateFlow<List<NotaEntity>> = repository.observarNotas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Estados del formulario (Reemplazando a los 'remember' de Compose)
    private val _titulo = MutableStateFlow("")
    val titulo = _titulo.asStateFlow()

    private val _contenido = MutableStateFlow("")
    val contenido = _contenido.asStateFlow()

    private val _importante = MutableStateFlow(false)
    val importante = _importante.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando = _guardando.asStateFlow()

    // Funciones para actualizar los estados desde la vista
    fun onTituloChange(nuevoTitulo: String) { _titulo.value = nuevoTitulo }
    fun onContenidoChange(nuevoContenido: String) { _contenido.value = nuevoContenido }
    fun onImportanteChange(esImportante: Boolean) { _importante.value = esImportante }
    
    // 3. Lógica de negocio (Validaciones e inserción)
    fun guardarNota() {
        val tituloLimpio = _titulo.value.trim()
        val contenidoLimpio = _contenido.value.trim()

        if (tituloLimpio.isEmpty()) {
            _mensaje.value = "Error: El título no puede estar vacío"
            return
        }

        if (_guardando.value) return // Evitar doble submit

        _guardando.value = true
        _mensaje.value = "Guardando..."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nuevaNota = NotaEntity(
                    titulo = tituloLimpio,
                    contenido = contenidoLimpio,
                    importante = _importante.value
                )

                repository.insertarNota(nuevaNota)

                // Actualizamos estados en UI al finalizar
                _mensaje.value = "¡Nota guardada con éxito!"
                _titulo.value = ""
                _contenido.value = ""
                _importante.value = false
            } catch (e: Exception) {
                _mensaje.value = "Error al guardar: ${e.message}"
            } finally {
                _guardando.value = false
            }
        }
    }
}