package ar.edu.uade.axelhiga.ejercicios.domain.model

data class Actividad(
    val titulo: String,
    val materia: String,
    val prioridad: Int,
    val completada: Boolean
)
