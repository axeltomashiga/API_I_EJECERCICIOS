package ar.edu.uade.axelhiga.ejercicios.data.repository

import ar.edu.uade.axelhiga.ejercicios.data.local.dao.CategoriaDao
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow

class CategoriaRepositoryImpl(private val categoriaDao: CategoriaDao) : CategoriaRepository {
    override fun observarCategorias(): Flow<List<CategoriaEntity>> {
        return categoriaDao.obtenerCategorias()
    }
}