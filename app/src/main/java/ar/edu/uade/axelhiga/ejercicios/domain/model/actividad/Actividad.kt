package ar.edu.uade.axelhiga.ejercicios.domain.model.actividad

import java.util.UUID

data class Actividad(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val materia: String,
    val prioridad: Int?,
    val completada: Boolean = false
)
