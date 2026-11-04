package ar.edu.uade.axelhiga.ejercicios.ej1.model

import ar.edu.uade.axelhiga.ejercicios.ej1.data.NotaDao
import ar.edu.uade.axelhiga.ejercicios.ej1.data.NotaEntity
import kotlinx.coroutines.flow.Flow

// 1. MODEL / REPOSITORY (Capa de Datos)
// Se encarga exclusivamente de guardar y proveer las notas desde la base de datos (Room).
class NotasEj1Repository(private val notaDao: NotaDao) {

    // Exponemos el Flow directamente desde Room (siempre actualizado)
    val notas: Flow<List<NotaEntity>> = notaDao.obtenerTodas()

    suspend fun guardar(titulo: String, contenido: String) {
        val nuevaNota = NotaEntity(
            titulo = titulo,
            contenido = contenido
        )
        notaDao.insert(nuevaNota)
    }
}
