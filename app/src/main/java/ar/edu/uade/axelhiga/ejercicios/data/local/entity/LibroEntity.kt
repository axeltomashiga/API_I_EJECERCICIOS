package ar.edu.uade.axelhiga.ejercicios.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ar.edu.uade.axelhiga.ejercicios.domain.model.EstadoLectura
import ar.edu.uade.axelhiga.ejercicios.domain.model.Libro

@Entity(
    tableName = "libros",
    foreignKeys = [
        ForeignKey(
            entity = EstanteriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["estanteriaId"],
            onDelete = ForeignKey.SET_NULL // Política de eliminación: SET_NULL
        )
    ],
    indices = [
        Index(value = ["estanteriaId"]), // Índice en clave foránea
        Index(value = ["estado", "fechaActualizacion"]) // Índice compuesto para la consulta frecuente
    ]
)
data class LibroEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val autor: String,
    @ColumnInfo(name = "paginas", defaultValue = "0")
    val paginas: Int = 0, // Nueva columna obligatoria incorporada en la versión 2
    val estanteriaId: Long? = null,
    val estado: EstadoLectura = EstadoLectura.PENDIENTE,
    val fechaActualizacion: Long = System.currentTimeMillis()
) {
    fun toDomain(): Libro = Libro(
        id = id,
        titulo = titulo,
        autor = autor,
        paginas = paginas,
        estanteriaId = estanteriaId,
        estado = estado,
        fechaActualizacion = fechaActualizacion
    )

    companion object {
        fun fromDomain(libro: Libro): LibroEntity = LibroEntity(
            id = libro.id,
            titulo = libro.titulo,
            autor = libro.autor,
            paginas = libro.paginas,
            estanteriaId = libro.estanteriaId,
            estado = libro.estado,
            fechaActualizacion = libro.fechaActualizacion
        )
    }
}
