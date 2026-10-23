package ar.edu.uade.axelhiga.ejercicios.domain.model

data class Libro(
    val id: Long = 0,
    val titulo: String,
    val autor: String,
    val estanteriaId: Long? = null,
    val estado: EstadoLectura = EstadoLectura.PENDIENTE,
    val fechaActualizacion: Long = System.currentTimeMillis()
)
