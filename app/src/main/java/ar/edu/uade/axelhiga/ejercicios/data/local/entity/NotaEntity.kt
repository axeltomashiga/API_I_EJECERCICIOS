package ar.edu.uade.axelhiga.ejercicios.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class NotaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val importante: Boolean = false,
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
