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
    suspend fun insert(categoria: CategoriaEntity): Long

    // Consulta simple para traer las categorías y mostrarlas en el menú desplegable
    @Query("SELECT * FROM categorias")
    fun obtenerCategorias(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun obtenerPorId(id: Int): CategoriaEntity?

    // @Transaction es obligatorio porque Room hará internamente dos consultas (SELECT a categorias y SELECT a notas)
    // Esto asegura que la base de datos no mute entre ambas lecturas.
    @Transaction
    @Query("SELECT * FROM categorias")
    fun obtenerCategoriasConNotas(): Flow<List<CategoriaConNotas>>

    @Query("UPDATE notas SET categoriaId = :destinoId WHERE categoriaId = :origenId")
    suspend fun reasignarNotasDeCategoria(origenId: Int, destinoId: Int)

    @Query("DELETE FROM categorias WHERE id = :idCategoria")
    suspend fun eliminarCategoriaPorId(idCategoria: Int)

    // --- EJERCICIO 13 ---
    // 1. VARIANTE SIN TRANSACCIÓN:
    // Se ejecutan ambos pasos por separado.
    // Si falla entre ambos (ej. simularFallo = true), las notas quedan movidas pero la categoría origen se mantiene vacía sin eliminarse (estado inconsistente para la app).
    suspend fun moverNotasYEliminarCategoriaSinTransaccion(origenId: Int, destinoId: Int, simularFallo: Boolean = false) {
        reasignarNotasDeCategoria(origenId, destinoId)
        if (simularFallo) {
            throw RuntimeException("Fallo simulado entre la reasignación de notas y la eliminación de categoría")
        }
        eliminarCategoriaPorId(origenId)
    }

    // 2. VARIANTE CON TRANSACCIÓN (@Transaction):
    // Garantiza que la operación sea atómica (ambos pasos o ninguno).
    // Si falla a mitad de camino, SQLite realiza un ROLLBACK revirtiendo el paso 1.
    @Transaction
    suspend fun moverNotasYEliminarCategoriaConTransaccion(origenId: Int, destinoId: Int, simularFallo: Boolean = false) {
        reasignarNotasDeCategoria(origenId, destinoId)
        if (simularFallo) {
            throw RuntimeException("Fallo simulado dentro de la transacción de Room")
        }
        eliminarCategoriaPorId(origenId)
    }
}