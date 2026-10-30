package ar.edu.uade.axelhiga.ejercicios.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CategoriaConNotas(
    @Embedded 
    val categoria: CategoriaEntity,
    
    @Relation(
        parentColumn = "id", // El ID de CategoriaEntity
        entityColumn = "categoriaId" // El ID foráneo dentro de NotaEntity
    )
    val notas: List<NotaEntity>
)