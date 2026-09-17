package ar.edu.uade.axelhiga.ejercicios.presentation.organizadorcursada

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ar.edu.uade.axelhiga.ejercicios.domain.model.Actividad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrganizadorCursadaViewModel: ViewModel() {

    private val _actividadesList = MutableStateFlow<MutableList<Actividad>>(mutableStateListOf())

    val actividadesList = _actividadesList.asStateFlow()

    fun agregarActividad(actividad: Actividad) {
        _actividadesList.value.add(actividad)
    }
}