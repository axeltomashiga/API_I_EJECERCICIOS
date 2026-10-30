package ar.edu.uade.axelhiga.ejercicios.data.repository

import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
    fun observarCategorias(): Flow<List<CategoriaEntity>>
}