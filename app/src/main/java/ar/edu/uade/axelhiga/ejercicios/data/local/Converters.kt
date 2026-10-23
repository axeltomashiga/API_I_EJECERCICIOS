package ar.edu.uade.axelhiga.ejercicios.data.local

import androidx.room.TypeConverter
import ar.edu.uade.axelhiga.ejercicios.domain.model.EstadoLectura

class Converters {
    @TypeConverter
    fun fromEstadoLectura(estado: EstadoLectura?): String? = estado?.name

    @TypeConverter
    fun toEstadoLectura(valor: String?): EstadoLectura? =
        valor?.let { EstadoLectura.valueOf(it) }
}
