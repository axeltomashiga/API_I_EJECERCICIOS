package ar.edu.uade.axelhiga.ejercicios.data.repository

import ar.edu.uade.axelhiga.ejercicios.data.local.dao.CategoriaDao
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaConNotas
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow

class CategoriaRepositoryImpl(private val categoriaDao: CategoriaDao) : CategoriaRepository {
    override fun observarCategorias(): Flow<List<CategoriaEntity>> {
        return categoriaDao.obtenerCategorias()
    }

    override fun observarCategoriasConNotas(): Flow<List<CategoriaConNotas>> {
        return categoriaDao.obtenerCategoriasConNotas()
    }

    override suspend fun moverNotasYEliminarCategoria(origenId: Int, destinoId: Int) {
        categoriaDao.moverNotasYEliminarCategoriaConTransaccion(origenId, destinoId)
    }
}