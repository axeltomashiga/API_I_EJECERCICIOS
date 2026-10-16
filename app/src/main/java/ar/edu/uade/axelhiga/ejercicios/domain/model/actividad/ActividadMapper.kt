package ar.edu.uade.axelhiga.ejercicios.domain.model.actividad

object ActividadMapper {
    private const val SEPARATOR = "|"

    /**
     * Convierte un objeto [Actividad] a un String separado por un delimitador.
     */
    fun toString(actividad: Actividad): String {
        return buildString {
            append(actividad.id).append(SEPARATOR)
            append(actividad.titulo).append(SEPARATOR)
            append(actividad.materia).append(SEPARATOR)
            append(actividad.prioridad?.toString() ?: "null").append(SEPARATOR)
            append(actividad.completada)
        }
    }

    /**
     * Convierte un String formateado a un objeto [Actividad].
     * @return [Actividad] si el formato es correcto, de lo contrario null.
     */
    fun fromString(text: String): Actividad? {
        // Ignoramos líneas vacías por seguridad
        if (text.isBlank()) return null
        
        val partes = text.split(SEPARATOR)
        
        // Verificamos que el texto tenga exactamente los 5 atributos de la clase Actividad
        if (partes.size != 5) return null

        return try {
            Actividad(
                id = partes[0],
                titulo = partes[1],
                materia = partes[2],
                prioridad = if (partes[3] == "null") null else partes[3].toIntOrNull(),
                completada = partes[4].toBooleanStrictOrNull() ?: false
            )
        } catch (e: Exception) {
            // Si la conversión falla, devolvemos null en lugar de hacer crashear la app
            null 
        }
    }
}