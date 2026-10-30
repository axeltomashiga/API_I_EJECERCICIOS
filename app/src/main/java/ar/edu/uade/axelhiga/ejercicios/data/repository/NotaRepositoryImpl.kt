package ar.edu.uade.axelhiga.ejercicios.data.repository

import ar.edu.uade.axelhiga.ejercicios.data.local.dao.NotaDao
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity
import kotlinx.coroutines.flow.Flow

class NotaRepositoryImpl(private val notaDao: NotaDao) : NotaRepository {

    override fun observarNotasFiltradas(
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

    override fun observarCantidadTotal(): Flow<Int> = notaDao.contarNotas()
    
    override fun observarCantidadImportantes(): Flow<Int> = notaDao.contarNotasImportantes()

    override suspend fun insertarNota(nota: NotaEntity) {
        notaDao.insert(nota)
    }

    override suspend fun actualizarNota(nota: NotaEntity) {
        notaDao.actualizar(nota)
    }

    override suspend fun eliminarNota(nota: NotaEntity) {
        notaDao.eliminar(nota)
    }
}