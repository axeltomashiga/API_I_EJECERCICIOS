package ar.edu.uade.axelhiga.ejercicios.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notas",
    indices = [
        // 1. Índice único: Obliga a que no se repita el código externo
        Index(value = ["codigoExterno"], unique = true),
        // 2. Índice no único: Optimiza el motor de base de datos para las búsquedas frecuentes por título
        Index(value = ["titulo"])
    ]
)
data class NotaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val importante: Boolean = false,
    val codigoExterno: String? = null,
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
