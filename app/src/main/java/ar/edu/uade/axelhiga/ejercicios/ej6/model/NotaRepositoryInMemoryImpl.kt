package ar.edu.uade.axelhiga.ejercicios.ej6.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// IMPLEMENTACIÓN EN MEMORIA
class NotaRepositoryInMemoryImpl : NotaRepository {

    // Variable interna para almacenar las notas temporalmente en la RAM
    private val _notasFlow = MutableStateFlow<List<Nota>>(emptyList())

    override fun obtenerTodas(): Flow<List<Nota>> {
        return _notasFlow.asStateFlow()
    }

    override suspend fun guardar(nota: Nota) {
        _notasFlow.update { listaActual ->
            // Si la nota ya existe, la reemplaza (Update)
            // Si no existe, la agrega (Insert)
            val idx = listaActual.indexOfFirst { it.id == nota.id }
            if (idx != -1) {
                val listaModificada = listaActual.toMutableList()
                listaModificada[idx] = nota
                listaModificada
            } else {
                listaActual + nota
            }
        }
    }

    override suspend fun eliminar(id: String) {
        _notasFlow.update { listaActual ->
            listaActual.filterNot { it.id == id }
        }
    }
}
