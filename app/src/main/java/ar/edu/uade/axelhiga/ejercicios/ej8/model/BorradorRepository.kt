package ar.edu.uade.axelhiga.ejercicios.ej8.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

// Capa de Datos: Manipula directamente el archivo físico "borrador_ej8.txt"
class BorradorRepository(private val context: Context) {

    private val archivo = File(context.filesDir, "borrador_ej8.txt")

    // Lectura
    suspend fun leerBorrador(): String = withContext(Dispatchers.IO) {
        if (!archivo.exists()) return@withContext ""
        try {
            archivo.readText(Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }

    // Escritura
    suspend fun escribirBorrador(texto: String) = withContext(Dispatchers.IO) {
        // DECISIÓN: Sobrescribir vs Agregar
        // Para un "Borrador" que simula un estado único de trabajo actual,
        // lo lógico es SOBRESCRIBIR completamente el contenido (writeText).
        // Si usáramos appendText(), el texto se iría acumulando al final
        // formando un historial gigante sin sentido para este caso de uso.
        try {
            archivo.writeText(texto, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
