package ar.edu.uade.axelhiga.ejercicios.presentation.organizadorcursada

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ar.edu.uade.axelhiga.ejercicios.domain.model.Actividad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrganizadorCursadaViewModel: ViewModel() {

    private val _actividadesList =
        MutableStateFlow<List<Actividad>>(emptyList())

    val actividadesList =
        _actividadesList.asStateFlow()

    fun agregarActividad(actividad: Actividad) {
        _actividadesList.value += actividad
    }

    fun completarActividad(actividad: Actividad) {
        _actividadesList.value =
            _actividadesList.value.map {
                if (it == actividad) {
                    it.copy(completada = !it.completada)
                } else {
                    it
                }
            }
    }
    fun eliminarActividad(actividad: Actividad) {
        _actividadesList.value =
            _actividadesList.value.filter { it != actividad }
    }

}