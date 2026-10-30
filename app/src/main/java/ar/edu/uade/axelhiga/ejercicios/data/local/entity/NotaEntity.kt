package ar.edu.uade.axelhiga.ejercicios.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notas",
    // 3. Desafío: Foreign Key
    // Si se borra la categoría a la que pertenece esta nota, el ID de la categoría en esta nota
    // pasará a ser 'null' (SET_NULL), preservando la nota como "sin categoría".
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["codigoExterno"], unique = true),
        Index(value = ["titulo"]),
        // Buena práctica: Siempre indexar las columnas que son claves foráneas para acelerar joins
        Index(value = ["categoriaId"])
    ]
)
data class NotaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val prioridad: Prioridad = Prioridad.BAJA,
    val codigoExterno: String? = null,
    val categoriaId: Int? = null, // Puede existir una nota sin categoría
    val creadaEn: Long = System.currentTimeMillis(),
    val modificadaEn: Long = System.currentTimeMillis(),
    val archivada: Boolean = false
)

/*
* la tabla seria notas con las siguientes columnas:
* id integer
* titulo text
* contenido text
* importante integer
* creadaEn integer
* modificadaEn integer
* archivada integer
* select * from notas where archivada = false
* */
