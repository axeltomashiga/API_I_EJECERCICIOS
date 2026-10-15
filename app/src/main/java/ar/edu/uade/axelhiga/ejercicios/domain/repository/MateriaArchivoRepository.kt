package ar.edu.uade.axelhiga.ejercicios.domain.repository

import android.content.Context
import ar.edu.uade.axelhiga.ejercicios.domain.model.Materia
import java.io.File

class MateriaArchivoRepository(
    private val context: Context
) {
    // Estas dependencias quedan encapsuladas dentro del repositorio
    private val fileName = "materias.txt"
    private val archivo = File(context.filesDir, fileName)

    init {
        // Aseguramos que el archivo exista al instanciar el repositorio
        if (!archivo.exists()) {
            archivo.createNewFile()
        }
    }

    private fun materiaATexto(materia: Materia): String {
        return "${materia.nombre}|${materia.anio ?: ""}|${if (materia.aprobada) "Aprobada" else "No aprobada"}"
    }

    private fun textoAMateria(linea: String): Materia? {
        val partes = linea.split("|")
        if (partes.size == 3) {
            val nombre = partes[0].trim()
            val anio = partes[1].trim().toIntOrNull()
            val aprobada = partes[2].trim() == "Aprobada"
            if (nombre.isNotEmpty()) {
                return Materia(nombre, anio, aprobada)
            }
        }
        return null
    }

    fun guardar(materias: List<Materia?>) {
        val contenido = materias.filterNotNull().joinToString("\n") { materiaATexto(it) }
        archivo.writeText(contenido)
    }

    fun cargar(): List<Materia?> {
        if (!archivo.exists()) return emptyList()
        return archivo.readLines().map { textoAMateria(it) }
    }
    
    // Desafíos opcionales implementados:
    fun existeArchivo(): Boolean {
        return archivo.exists()
    }

    fun borrarTodo() {
        if (archivo.exists()) {
            archivo.delete()
        }
    }
}