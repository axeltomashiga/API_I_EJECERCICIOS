package ar.edu.uade.axelhiga.ejercicios.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.EstanteriaEntity
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.LibroEntity
import ar.edu.uade.axelhiga.ejercicios.domain.model.EstadoLectura
import kotlinx.coroutines.flow.Flow

@Dao
interface LibroDao {

    // 1. Alta de libros
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLibro(libro: LibroEntity): Long

    // Alta de estanterías
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEstanteria(estanteria: EstanteriaEntity): Long

    // 2. Cambio de estado y fecha de actualización
    @Query("""
        UPDATE libros 
        SET estado = :nuevoEstado, fechaActualizacion = :timestamp 
        WHERE id = :libroId
    """)
    suspend fun actualizarEstado(
        libroId: Long, 
        nuevoEstado: EstadoLectura, 
        timestamp: Long = System.currentTimeMillis()
    )

    // 3. Consulta reactiva de libros no terminados ordenados por actualización (Punto 2)
    @Query("""
        SELECT * FROM libros 
        WHERE estado != 'TERMINADO' 
        ORDER BY fechaActualizacion DESC
    """)
    fun observarLibrosNoTerminados(): Flow<List<LibroEntity>>

    // Consulta reactiva de todos los libros
    @Query("SELECT * FROM libros ORDER BY fechaActualizacion DESC")
    fun observarTodosLosLibros(): Flow<List<LibroEntity>>

    // Buscar libro por ID
    @Query("SELECT * FROM libros WHERE id = :id")
    suspend fun obtenerLibroPorId(id: Long): LibroEntity?

    // Eliminar estantería (para verificar política onDelete = SET_NULL)
    @Query("DELETE FROM estanterias WHERE id = :estanteriaId")
    suspend fun eliminarEstanteria(estanteriaId: Long)
}
