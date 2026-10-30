package ar.edu.uade.axelhiga.ejercicios.data.repository

import ar.edu.uade.axelhiga.ejercicios.data.local.dao.NotaDao
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity
import kotlinx.coroutines.flow.Flow

class NotaRepository(private val notaDao: NotaDao) {

    fun observarNotasFiltradas(
        query: String,
        soloImportantes: Boolean,
        ascendente: Boolean
    ): Flow<List<NotaEntity>> {
        return if (ascendente) {
            notaDao.observarNotasAsc(query, soloImportantes)
        } else {
            notaDao.observarNotasDesc(query, soloImportantes)
        }
    }

    fun observarCantidadTotal(): Flow<Int> = notaDao.contarNotas()
    fun observarCantidadImportantes(): Flow<Int> = notaDao.contarNotasImportantes()

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