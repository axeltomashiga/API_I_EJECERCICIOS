package ar.edu.uade.axelhiga.ejercicios.data.repository

import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity
import kotlinx.coroutines.flow.Flow

interface NotaRepository {
    fun observarNotasFiltradas(query: String, soloImportantes: Boolean, ascendente: Boolean): Flow<List<NotaEntity>>
    fun observarCantidadTotal(): Flow<Int>
    fun observarCantidadImportantes(): Flow<Int>
    suspend fun insertarNota(nota: NotaEntity)
    suspend fun actualizarNota(nota: NotaEntity)
    suspend fun eliminarNota(nota: NotaEntity)
}