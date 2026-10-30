package ar.edu.uade.axelhiga.ejercicios.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaConNotas
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Insert
    suspend fun insert(categoria: CategoriaEntity)

    // Consulta simple para traer las categorías y mostrarlas en el menú desplegable
    @Query("SELECT * FROM categorias")
    fun obtenerCategorias(): Flow<List<CategoriaEntity>>

    // @Transaction es obligatorio porque Room hará internamente dos consultas (SELECT a categorias y SELECT a notas)
    // Esto asegura que la base de datos no mute entre ambas lecturas.
    @Transaction
    @Query("SELECT * FROM categorias")
    fun obtenerCategoriasConNotas(): Flow<List<CategoriaConNotas>>
}