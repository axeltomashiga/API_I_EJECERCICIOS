package ar.edu.uade.axelhiga.ejercicios.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity

@Dao
interface NotaDao {
    @Insert
    suspend fun insert(nota: NotaEntity) : Long

    @Query("SELECT * FROM notas ORDER BY CreadaEn DESC")
    suspend fun obtenerTodasDeUnaVez() : List<NotaEntity>

    @Query("DELETE FROM notas")
    suspend fun borrarTodas()

    @Query("SELECT * FROM notas WHERE id = :id")
    suspend fun obtenerPorId(id: Int) : NotaEntity

    @Query("SELECT * FROM notas WHERE importante = 1")
    suspend fun obtenerImportantes() : List<NotaEntity>
}