package ar.edu.uade.axelhiga.ejercicios.ej7.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

// REPOSITORY
// Responsable estricto de interactuar con el I/O del disco.
class ArchivoTextoRepository(private val context: Context) {

    // Se solicita utilizar Dispatchers.IO para alejar la lectura pesada del hilo principal
    suspend fun leerBorrador(): ResultadoLectura = withContext(Dispatchers.IO) {
        val archivo = File(context.filesDir, "borrador.txt")

        if (!archivo.exists()) {
            // DECISIÓN: Devolver un resultado explícito (Señal)
            // Hemos decidido devolver un enum/clase sellada explícita
            // en vez de devolver un simple String vacío ("").
            // Esto permite a la capa de UI saber *por qué* no hay texto
            // (ej. para poder mostrar "Aún no escribiste nada" vs "Ocurrió un error").
            return@withContext ResultadoLectura.NoExiste
        }

        try {
            val contenido = archivo.readText(Charsets.UTF_8)
            ResultadoLectura.Exito(contenido)
        } catch (e: SecurityException) {
            ResultadoLectura.ErrorFalla("Error de seguridad al acceder al archivo.")
        } catch (e: IOException) {
            ResultadoLectura.ErrorFalla("Error físico de lectura I/O.")
        } catch (e: Exception) {
            ResultadoLectura.ErrorFalla("Falla desconocida: ${e.message}")
        }
    }

    // Método auxiliar (solo para testing manual del ejercicio)
    suspend fun escribirBorrador(texto: String) = withContext(Dispatchers.IO) {
        val archivo = File(context.filesDir, "borrador.txt")
        archivo.writeText(texto, Charsets.UTF_8)
    }
}

// Representa exhaustivamente todas las posibilidades del disco
sealed class ResultadoLectura {
    data class Exito(val texto: String) : ResultadoLectura()
    object NoExiste : ResultadoLectura()
    data class ErrorFalla(val mensaje: String) : ResultadoLectura()
}
