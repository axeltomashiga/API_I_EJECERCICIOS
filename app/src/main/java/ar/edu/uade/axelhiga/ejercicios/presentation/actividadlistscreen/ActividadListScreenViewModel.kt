package ar.edu.uade.axelhiga.ejercicios.presentation.actividadlistscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.data.repository.ActividadRepository
import ar.edu.uade.axelhiga.ejercicios.domain.model.actividad.Actividad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ActividadListScreenViewModel(
    private val repository: ActividadRepository
) : ViewModel() {

    // Estado privado que maneja los datos modificables internamente
    private val _actividades = MutableStateFlow<List<Actividad>>(emptyList())

    // Estado público de solo lectura expuesto a la interfaz de usuario (Requisito 5)
    val actividades: StateFlow<List<Actividad>> = _actividades.asStateFlow()

    // Estado derivado para mostrar cuántas actividades están pendientes (Requisito 8)
    val pendientes: StateFlow<Int> = _actividades.map { list ->
        list.count { !it.completada }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        // (Requisito 9) Cargar desde archivo al arrancar
        _actividades.value = repository.cargarActividades()
    }

    // (Requisito 4) Agregar actividad
    fun agregarActividad(titulo: String, materia: String, prioridad: Int?) {
        val nuevaActividad = Actividad(
            titulo = titulo,
            materia = materia,
            prioridad = prioridad
        )
        // Añadimos a la lista actual
        val listaNueva = _actividades.value + nuevaActividad
        _actividades.value = listaNueva
        
        // Guardamos en archivo
        repository.guardarActividades(listaNueva)
    }

    // (Requisito 6) Marcar como completada/incompleta
    fun cambiarEstadoCompletada(id: String, completada: Boolean) {
        val listaNueva = _actividades.value.map { actividad ->
            if (actividad.id == id) {
                actividad.copy(completada = completada)
            } else {
                actividad
            }
        }
        _actividades.value = listaNueva
        
        // Guardamos en archivo
        repository.guardarActividades(listaNueva)
    }

    // (Requisito 7) Eliminar una actividad
    fun eliminarActividad(id: String) {
        val listaNueva = _actividades.value.filter { it.id != id }
        _actividades.value = listaNueva
        
        // Guardamos en archivo
        repository.guardarActividades(listaNueva)
    }
}