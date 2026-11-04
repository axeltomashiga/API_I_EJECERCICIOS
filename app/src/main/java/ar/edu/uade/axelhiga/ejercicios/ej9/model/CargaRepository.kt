package ar.edu.uade.axelhiga.ejercicios.ej9.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

interface CargaRepository {
    suspend fun leerTexto(): String
    suspend fun escribirTexto(texto: String)
}

// 1. REPOSITORIO REAL: Acceso a disco en Dispatchers.IO
class CargaRepositoryImpl(private val context: Context) : CargaRepository {
    private val archivo = File(context.filesDir, "borrador_ej9.txt")

    override suspend fun leerTexto(): String = withContext(Dispatchers.IO) {
        if (!archivo.exists()) return@withContext ""
        archivo.readText(Charsets.UTF_8)
    }

    override suspend fun escribirTexto(texto: String) = withContext(Dispatchers.IO) {
        archivo.writeText(texto, Charsets.UTF_8)
    }
}

// 2. REPOSITORIO DE PRUEBA: Para comprobar la ruta de error (Consigna)
class CargaRepositoryErrorSimulado : CargaRepository {
    override suspend fun leerTexto(): String {
        delay(1000) // Simular retardo de disco
        throw IllegalStateException("¡Falla controlada de LECTURA (simulada)!")
    }

    override suspend fun escribirTexto(texto: String) {
        delay(1000) // Simular retardo de disco
        throw IllegalStateException("¡Falla controlada de ESCRITURA (simulada)!")
    }
}
