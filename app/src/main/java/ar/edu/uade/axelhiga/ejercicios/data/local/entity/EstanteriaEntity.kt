package ar.edu.uade.axelhiga.ejercicios.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ar.edu.uade.axelhiga.ejercicios.domain.model.Estanteria

@Entity(tableName = "estanterias")
data class EstanteriaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String
) {
    fun toDomain(): Estanteria = Estanteria(
        id = id,
        nombre = nombre
    )

    companion object {
        fun fromDomain(estanteria: Estanteria): EstanteriaEntity = EstanteriaEntity(
            id = estanteria.id,
            nombre = estanteria.nombre
        )
    }
}
