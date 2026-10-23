package ar.edu.uade.axelhiga.ejercicios.domain.repository

import ar.edu.uade.axelhiga.ejercicios.domain.model.EstadoLectura
import ar.edu.uade.axelhiga.ejercicios.domain.model.Libro
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio en la capa de Domain.
 * No depende de Room, SQLite ni de ninguna librería de Android.
 */
interface BibliotecaRepository {
    val librosNoTerminados: Flow<List<Libro>>
    val todosLosLibros: Flow<List<Libro>>

    suspend fun agregarLibro(
        titulo: String,
        autor: String,
        paginas: Int = 0,
        estanteriaId: Long? = null
    ): Long

    suspend fun cambiarEstado(libroId: Long, nuevoEstado: EstadoLectura)
    suspend fun crearEstanteria(nombre: String): Long
    suspend fun eliminarEstanteria(estanteriaId: Long)
}
