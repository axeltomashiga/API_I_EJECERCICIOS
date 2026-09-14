package ar.edu.uade.axelhiga.ejercicios.presentation.ui.materiaslist

import ar.edu.uade.axelhiga.ejercicios.data.repository.MateriasRepositoryImpl
import ar.edu.uade.axelhiga.ejercicios.domain.model.Materia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MateriasListViewModel (
    private val repository: MateriasRepositoryImpl = MateriasRepositoryImpl()
){
    private val _materias = MutableStateFlow<List<Materia>>(emptyList())

    val materias : StateFlow<List<Materia>> = _materias

    init {
        loadMaterias()
    }

    private fun loadMaterias() {
        _materias.value = repository.getMaterias()
    }
}