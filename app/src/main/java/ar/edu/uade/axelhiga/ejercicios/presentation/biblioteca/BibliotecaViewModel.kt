package ar.edu.uade.axelhiga.ejercicios.presentation.biblioteca

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.uade.axelhiga.ejercicios.domain.model.EstadoLectura
import ar.edu.uade.axelhiga.ejercicios.domain.model.Libro
import ar.edu.uade.axelhiga.ejercicios.domain.repository.BibliotecaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BibliotecaViewModel(
    private val repository: BibliotecaRepository
) : ViewModel() {

    // Filtro visual: true = solo libros no terminados (consigna), false = todos
    private val _soloNoTerminados = MutableStateFlow(true)
    val soloNoTerminados: StateFlow<Boolean> = _soloNoTerminados.asStateFlow()

    // Libros no terminados ordenados por actualización (Punto 2 y 3)
    val librosNoTerminados: StateFlow<List<Libro>> = repository.librosNoTerminados
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Todos los libros para poder ver también los terminados
    val todosLosLibros: StateFlow<List<Libro>> = repository.todosLosLibros
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun alternarFiltro() {
        _soloNoTerminados.value = !_soloNoTerminados.value
    }

    fun agregarLibro(titulo: String, autor: String) {
        if (titulo.isBlank() || autor.isBlank()) return
        viewModelScope.launch {
            repository.agregarLibro(titulo.trim(), autor.trim())
        }
    }

    fun avanzarEstado(libro: Libro) {
        val siguiente = when (libro.estado) {
            EstadoLectura.PENDIENTE -> EstadoLectura.LEYENDO
            EstadoLectura.LEYENDO -> EstadoLectura.TERMINADO
            EstadoLectura.TERMINADO -> EstadoLectura.PENDIENTE
        }
        viewModelScope.launch {
            repository.cambiarEstado(libro.id, siguiente)
        }
    }
}
