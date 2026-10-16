package ar.edu.uade.axelhiga.ejercicios.data.repository

import android.content.Context
import ar.edu.uade.axelhiga.ejercicios.domain.model.actividad.Actividad
import ar.edu.uade.axelhiga.ejercicios.domain.model.actividad.ActividadMapper
import java.io.File
import java.io.FileNotFoundException

class ActividadRepository(private val context: Context) {

    private val fileName = "actividades_db.txt"

    /**
     * Guarda la lista completa de actividades en el archivo interno.
     */
    fun guardarActividades(actividades: List<Actividad>) {
        val textoAguardar = actividades.joinToString(separator = "\n") { actividad ->
            ActividadMapper.toString(actividad)
        }
        
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
            output.write(textoAguardar.toByteArray())
        }
    }

    /**
     * Lee y recupera la lista de actividades desde el archivo interno.
     */
    fun cargarActividades(): List<Actividad> {
        return try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) {
                return emptyList()
            }

            val lineas = file.readLines()
            
            // mapNotNull ignora las líneas que generaron null (ej. formato corrupto)
            lineas.mapNotNull { linea ->
                ActividadMapper.fromString(linea)
            }
        } catch (e: FileNotFoundException) {
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}