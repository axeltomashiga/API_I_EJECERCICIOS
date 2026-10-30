package ar.edu.uade.axelhiga.ejercicios.presentation.notas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaEntity
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity
import ar.edu.uade.axelhiga.ejercicios.data.repository.CategoriaRepository
import ar.edu.uade.axelhiga.ejercicios.data.repository.NotaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class NotasViewModel(
    private val repository: NotaRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    // Lista de categorías para mostrar en el dropdown
    val categoriasDisponibles: StateFlow<List<CategoriaEntity>> = categoriaRepository.observarCategorias()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Filtros ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _soloImportantesFilter = MutableStateFlow(false)
    val soloImportantesFilter = _soloImportantesFilter.asStateFlow()

    private val _ordenAscendente = MutableStateFlow(false)
    val ordenAscendente = _ordenAscendente.asStateFlow()

    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun toggleImportantesFilter() { _soloImportantesFilter.value = !_soloImportantesFilter.value }
    fun toggleOrden() { _ordenAscendente.value = !_ordenAscendente.value }

    // 1. Exposición del flujo de Room convertido en StateFlow, reaccionando a los filtros
    val notas: StateFlow<List<NotaEntity>> = combine(
        _searchQuery,
        _soloImportantesFilter,
        _ordenAscendente
    ) { query, importantes, asc ->
        Triple(query, importantes, asc)
    }.flatMapLatest { (query, importantes, asc) ->
        repository.observarNotasFiltradas(query, importantes, asc)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Estadísticas
    val totalNotas: StateFlow<Int> = repository.observarCantidadTotal().stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0
    )
    val totalImportantes: StateFlow<Int> = repository.observarCantidadImportantes().stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0
    )

    // 2. Estados del formulario (Reemplazando a los 'remember' de Compose)
    private val _titulo = MutableStateFlow("")
    val titulo = _titulo.asStateFlow()

    private val _contenido = MutableStateFlow("")
    val contenido = _contenido.asStateFlow()

    private val _importante = MutableStateFlow(false)
    val importante = _importante.asStateFlow()

    private val _categoriaId = MutableStateFlow<Int?>(null)
    val categoriaId = _categoriaId.asStateFlow()

    private val _codigoExterno = MutableStateFlow("")
    val codigoExterno = _codigoExterno.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando = _guardando.asStateFlow()

    // Estado para saber si estamos editando una nota (guardamos su ID)
    private val _notaIdEnEdicion = MutableStateFlow<Int?>(null)
    val notaIdEnEdicion = _notaIdEnEdicion.asStateFlow()

    // Estado para guardar TEMPORALMENTE la última nota eliminada (Desafío: Deshacer)
    private val _ultimaNotaEliminada = MutableStateFlow<NotaEntity?>(null)
    val ultimaNotaEliminada = _ultimaNotaEliminada.asStateFlow()

    // Funciones para actualizar los estados desde la vista
    fun onTituloChange(nuevoTitulo: String) { _titulo.value = nuevoTitulo }
    fun onContenidoChange(nuevoContenido: String) { _contenido.value = nuevoContenido }
    fun onImportanteChange(esImportante: Boolean) { _importante.value = esImportante }
    fun onCodigoExternoChange(nuevoCodigo: String) { _codigoExterno.value = nuevoCodigo }
    fun onCategoriaChange(id: Int?) { _categoriaId.value = id }
    
    // Cargar nota para edición
    fun cargarNotaParaEdicion(nota: NotaEntity) {
        _notaIdEnEdicion.value = nota.id
        _titulo.value = nota.titulo
        _contenido.value = nota.contenido
        _importante.value = nota.importante
        _codigoExterno.value = nota.codigoExterno ?: ""
        _categoriaId.value = nota.categoriaId
        _mensaje.value = ""
    }

    // Cancelar la edición y limpiar el formulario
    fun cancelarEdicion() {
        limpiarFormulario()
        _mensaje.value = ""
    }

    private fun limpiarFormulario() {
        _notaIdEnEdicion.value = null
        _titulo.value = ""
        _contenido.value = ""
        _codigoExterno.value = ""
        _importante.value = false
        _categoriaId.value = null
    }

    // 3. Lógica de negocio (Validaciones e inserción/actualización)
    fun guardarNota() {
        val tituloLimpio = _titulo.value.trim()
        val contenidoLimpio = _contenido.value.trim()
        val codigoLimpio = _codigoExterno.value.trim().takeIf { it.isNotEmpty() }

        if (tituloLimpio.isEmpty()) {
            _mensaje.value = "Error: El título no puede estar vacío"
            return
        }

        if (_guardando.value) return // Evitar doble submit

        _guardando.value = true
        _mensaje.value = "Guardando..."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val idActual = _notaIdEnEdicion.value
                val nuevaNota = NotaEntity(
                    id = idActual ?: 0,
                    titulo = tituloLimpio,
                    contenido = contenidoLimpio,
                    importante = _importante.value,
                    codigoExterno = codigoLimpio,
                    categoriaId = _categoriaId.value
                )

                if (idActual != null) {
                    repository.actualizarNota(nuevaNota)
                    _mensaje.value = "¡Nota actualizada con éxito!"
                } else {
                    repository.insertarNota(nuevaNota)
                    _mensaje.value = "¡Nota guardada con éxito!"
                }

                // Limpiamos el formulario al finalizar (pero mantenemos el mensaje)
                limpiarFormulario()
            } catch (e: Exception) {
                _mensaje.value = "Error al guardar: ${e.message}"
            } finally {
                _guardando.value = false
            }
        }
    }

    fun eliminarNota(nota: NotaEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Guardamos el objeto COMPLETO en memoria antes de borrarlo de la DB
                _ultimaNotaEliminada.value = nota
                
                repository.eliminarNota(nota)
                
                // Si la nota que estamos eliminando es la que estamos editando actualmente, limpiamos el formulario
                if (_notaIdEnEdicion.value == nota.id) {
                    cancelarEdicion()
                }
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    // Desafío: Reinsertar la nota guardada temporalmente
    fun deshacerEliminacion() {
        _ultimaNotaEliminada.value?.let { notaRestaurada ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // La reinsertamos en la base de datos tal cual era
                    repository.insertarNota(notaRestaurada)
                    // Limpiamos el estado temporal para que desaparezca el botón deshacer
                    _ultimaNotaEliminada.value = null
                    _mensaje.value = "¡Nota restaurada!"
                } catch (e: Exception) {
                    _mensaje.value = "Error al restaurar: ${e.message}"
                }
            }
        }
    }
    
    // Función para simplemente descartar la nota eliminada de la memoria si no la queremos deshacer
    fun descartarDeshacer() {
        _ultimaNotaEliminada.value = null
    }
}