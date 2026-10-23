package ar.edu.uade.axelhiga.ejercicios.data.repository

import ar.edu.uade.axelhiga.ejercicios.data.local.dao.LibroDao
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.EstanteriaEntity
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.LibroEntity
import ar.edu.uade.axelhiga.ejercicios.domain.model.EstadoLectura
import ar.edu.uade.axelhiga.ejercicios.domain.model.Libro
import ar.edu.uade.axelhiga.ejercicios.domain.repository.BibliotecaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación del repositorio en la capa Data.
 * Conecta la fuente de datos local (Room) con los modelos limpios de Domain.
 */
class BibliotecaRepositoryImpl(
    private val libroDao: LibroDao
) : BibliotecaRepository {

    override val librosNoTerminados: Flow<List<Libro>> =
        libroDao.observarLibrosNoTerminados().map { lista ->
            lista.map { it.toDomain() }
        }

    override val todosLosLibros: Flow<List<Libro>> =
        libroDao.observarTodosLosLibros().map { lista ->
            lista.map { it.toDomain() }
        }

    override suspend fun agregarLibro(
        titulo: String,
        autor: String,
        estanteriaId: Long?
    ): Long {
        val entity = LibroEntity(
            titulo = titulo,
            autor = autor,
            estanteriaId = estanteriaId,
            estado = EstadoLectura.PENDIENTE,
            fechaActualizacion = System.currentTimeMillis()
        )
        return libroDao.insertarLibro(entity)
    }

    override suspend fun cambiarEstado(libroId: Long, nuevoEstado: EstadoLectura) {
        libroDao.actualizarEstado(libroId, nuevoEstado, System.currentTimeMillis())
    }

    override suspend fun crearEstanteria(nombre: String): Long {
        return libroDao.insertarEstanteria(EstanteriaEntity(nombre = nombre))
    }

    override suspend fun eliminarEstanteria(estanteriaId: Long) {
        libroDao.eliminarEstanteria(estanteriaId)
    }
}
