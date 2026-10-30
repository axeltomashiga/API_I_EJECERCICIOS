package ar.edu.uade.axelhiga.ejercicios.data.repository

import ar.edu.uade.axelhiga.ejercicios.data.local.dao.NotaDao
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity
import kotlinx.coroutines.flow.Flow

class NotaRepository(private val notaDao: NotaDao) {

    /**
     * Devuelve un flujo continuo con todas las notas ordenadas por fecha de creación descendente.
     */
    fun observarNotas(): Flow<List<NotaEntity>> {
        return notaDao.observarNotas()
    }

    /**
     * Inserta una nueva nota en la base de datos.
     * Esta función debe ser llamada desde una corrutina en Dispatchers.IO
     */
    suspend fun insertarNota(nota: NotaEntity) {
        notaDao.insert(nota)
    }

    suspend fun actualizarNota(nota: NotaEntity) {
        notaDao.actualizar(nota)
    }

    suspend fun eliminarNota(nota: NotaEntity) {
        notaDao.eliminar(nota)
    }
}