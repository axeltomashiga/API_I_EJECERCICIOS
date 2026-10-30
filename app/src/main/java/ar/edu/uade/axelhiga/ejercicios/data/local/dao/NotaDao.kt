package ar.edu.uade.axelhiga.ejercicios.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {
    // Si intentamos insertar una nota y choca con el índice único de otra
    // nota que ya existe, REPLACE borrará la vieja y guardará esta nueva.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(nota: NotaEntity) : Long

    @Update
    suspend fun actualizar(nota: NotaEntity)

    @Delete
    suspend fun eliminar(nota: NotaEntity)

    // 1, 2 y 3. Búsqueda y filtrado (Descendente)
    @Query("""
        SELECT * FROM notas 
        WHERE titulo LIKE '%' || :query || '%' 
        AND (:soloImportantes = 0 OR importante = 1)
        ORDER BY creadaEn DESC
    """)
    fun observarNotasDesc(query: String, soloImportantes: Boolean): Flow<List<NotaEntity>>

    // 1, 2 y 3. Búsqueda y filtrado (Ascendente)
    @Query("""
        SELECT * FROM notas 
        WHERE titulo LIKE '%' || :query || '%' 
        AND (:soloImportantes = 0 OR importante = 1)
        ORDER BY creadaEn ASC
    """)
    fun observarNotasAsc(query: String, soloImportantes: Boolean): Flow<List<NotaEntity>>

    // 4. Cantidad total de notas
    @Query("SELECT COUNT(*) FROM notas")
    fun contarNotas(): Flow<Int>

    // 5. Cantidad de notas importantes
    @Query("SELECT COUNT(*) FROM notas WHERE importante = 1")
    fun contarNotasImportantes(): Flow<Int>

    @Query("SELECT * FROM notas ORDER BY CreadaEn DESC")
    suspend fun obtenerTodasDeUnaVez() : List<NotaEntity>

    @Query("DELETE FROM notas")
    suspend fun borrarTodas()

    @Query("SELECT * FROM notas WHERE id = :id")
    suspend fun obtenerPorId(id: Int) : NotaEntity

    @Query("SELECT * FROM notas WHERE importante = 1")
    suspend fun obtenerImportantes() : List<NotaEntity>
}