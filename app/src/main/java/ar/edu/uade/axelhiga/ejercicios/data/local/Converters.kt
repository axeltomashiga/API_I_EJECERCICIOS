package ar.edu.uade.axelhiga.ejercicios.data.local

import androidx.room.TypeConverter
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.Prioridad

class Converters {
    @TypeConverter
    fun prioridadAString(valor: Prioridad): String = valor.name

    @TypeConverter
    fun stringAPrioridad(valor: String): Prioridad = try {
        Prioridad.valueOf(valor)
    } catch (e: IllegalArgumentException) {
        Prioridad.BAJA // Fallback por si la DB se corrompe o cambia el enum
    }
}