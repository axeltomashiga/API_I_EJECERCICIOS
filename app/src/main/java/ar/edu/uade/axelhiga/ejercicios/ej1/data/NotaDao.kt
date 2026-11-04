package ar.edu.uade.axelhiga.ejercicios.ej1.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(nota: NotaEntity): Long

    @Query("SELECT * FROM notas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<NotaEntity>>
}
