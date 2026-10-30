package ar.edu.uade.axelhiga.ejercicios.data.repository

import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaConNotas
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
    fun observarCategorias(): Flow<List<CategoriaEntity>>
    fun observarCategoriasConNotas(): Flow<List<CategoriaConNotas>>
    suspend fun moverNotasYEliminarCategoria(origenId: Int, destinoId: Int)
}