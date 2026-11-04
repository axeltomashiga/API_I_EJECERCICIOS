package ar.edu.uade.axelhiga.ejercicios.ej12.model

import android.content.Context
import ar.edu.uade.axelhiga.ejercicios.ej10.model.NotaEj10
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

// REPOSITORY: Única Fuente de la Verdad (SSOT).
// Todas las pantallas consumen el Flow de este repositorio en lugar de tener listas separadas.
class NotaEj12Repository(context: Context) {
    private val archivo = File(context.filesDir, "notas_ej12.json")

    // El StateFlow mantiene la lista activa en memoria RAM y emite los cambios automáticamente
    // a quien sea que se suscriba (las pantallas).
    private val _notas = MutableStateFlow<List<NotaEj10>>(emptyList())
    val notas: Flow<List<NotaEj10>> = _notas.asStateFlow()

    init {
        // Al instanciar, cargamos desde el disco a la memoria
        cargarDesdeDisco()
    }

    private fun cargarDesdeDisco() {
        if (!archivo.exists()) return
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
            _notas.value = lista
        } catch (e: Exception) {
            _notas.value = emptyList()
        }
    }

    suspend fun guardar(nota: NotaEj10) {
        _notas.update { listaActual ->
            val idx = listaActual.indexOfFirst { it.id == nota.id }
            val nuevaLista = listaActual.toMutableList()
            if (idx >= 0) nuevaLista[idx] = nota else nuevaLista.add(nota)

            // Disparamos el guardado físico de forma paralela y transparente.
            // Al retornar nuevaLista, el Flow emite y la UI se actualiza INSTANTÁNEAMENTE.
            nuevaLista
        }
        guardarEnDiscoFisico()
    }

    suspend fun obtenerPorId(id: String): NotaEj10? {
        // Buscamos directamente en nuestra caché (memoria), sin ir al disco lento.
        return _notas.value.find { it.id == id }
    }

    suspend fun eliminar(id: String) {
        _notas.update { listaActual ->
            val nuevaLista = listaActual.toMutableList().apply { removeAll { it.id == id } }
            nuevaLista
        }
        guardarEnDiscoFisico()
    }

    private suspend fun guardarEnDiscoFisico() = withContext(Dispatchers.IO) {
        val listaActual = _notas.value
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
            archivo.writeText(jsonArray.toString(4), Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
