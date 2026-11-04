package ar.edu.uade.axelhiga.ejercicios.ej10.model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

// 1. REPOSITORIO DE TEXTO (FORMATO JSON)
class ListaBorradorRepository(context: Context) {
    private val archivo = File(context.filesDir, "notas_persistentes.json")

    suspend fun obtenerTodas(): List<NotaEj10> = withContext(Dispatchers.IO) {
        if (!archivo.exists()) return@withContext emptyList()

        try {
            val jsonString = archivo.readText(Charsets.UTF_8)
            val jsonArray = JSONArray(jsonString)
            val lista = mutableListOf<NotaEj10>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                lista.add(
                    NotaEj10(
                        id = obj.getString("id"),
                        titulo = obj.getString("titulo"),
                        contenido = obj.getString("contenido")
                    )
                )
            }
            lista
        } catch (e: Exception) {
            // Si el JSON está incompleto, roto o mal formateado, evitamos crashear.
            // Retornamos lo que hayamos podido recuperar, o una lista vacía.
            emptyList()
        }
    }

    suspend fun guardar(nota: NotaEj10) = conTransaccionDeLista { listaActual ->
        val idx = listaActual.indexOfFirst { it.id == nota.id }
        if (idx >= 0) {
            listaActual[idx] = nota
        } else {
            listaActual.add(nota)
        }
    }

    suspend fun eliminar(id: String) = conTransaccionDeLista { listaActual ->
        listaActual.removeAll { it.id == id }
    }

    // Helper para mantener la integridad de la base plana (Lee -> Modifica -> Escribe)
    private suspend fun conTransaccionDeLista(
        operacion: (MutableList<NotaEj10>) -> Unit
    ) = withContext(Dispatchers.IO) {
        val listaActual = obtenerTodas().toMutableList()
        operacion(listaActual)

        // Serializamos la lista completa a formato JSON para no usar un simple split(";")
        val jsonArray = JSONArray()
        listaActual.forEach { nota ->
            val obj = JSONObject().apply {
                put("id", nota.id)
                put("titulo", nota.titulo)
                put("contenido", nota.contenido)
            }
            jsonArray.put(obj)
        }

        try {
            // Se sobrescribe el archivo completo para evitar corrupción de formato
            archivo.writeText(jsonArray.toString(4), Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
