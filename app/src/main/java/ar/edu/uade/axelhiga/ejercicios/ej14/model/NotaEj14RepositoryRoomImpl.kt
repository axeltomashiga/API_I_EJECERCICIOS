package ar.edu.uade.axelhiga.ejercicios.ej14.model

import ar.edu.uade.axelhiga.ejercicios.ej1.data.NotaDao
import ar.edu.uade.axelhiga.ejercicios.ej1.data.NotaEntity
import ar.edu.uade.axelhiga.ejercicios.ej6.model.Nota
import ar.edu.uade.axelhiga.ejercicios.ej6.model.NotaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ADAPTADOR (Repository de Room)
// Esta clase implementa la MISMA interfaz (NotaRepository) que creamos en el Ejercicio 6.
// Reutiliza el viejo `NotaDao` del Ejercicio 1.
class NotaEj14RepositoryRoomImpl(
    private val dao: NotaDao
) : NotaRepository {

    // MAPEO (De Entity a Modelo de Dominio puro)
    override fun obtenerTodas(): Flow<List<Nota>> {
        // En Room, el DAO devuelve un Flow, por lo que Room se encarga de reemitir
        // automáticamente si la tabla de notas sufre un INSERT/UPDATE/DELETE.
        return dao.obtenerTodas().map { entidades ->
            entidades.map { entity ->
                // ¿Quién transforma Entity en modelo de dominio? El Repository.
                Nota(
                    id = entity.id.toString(), // Entity usa Int autogenerado, Dominio espera String
                    titulo = entity.titulo,
                    contenido = entity.contenido
                )
            }
        }
    }

    override suspend fun guardar(nota: Nota) {
        // MAPEO INVERSO (De Dominio puro a Entity de Base de Datos)
        // Intentamos parsear el ID. Si falla (UUID por ej), forzamos id=0 para que Room
        // lo autogenere en la base SQLite.
        val idInt = nota.id.toIntOrNull() ?: 0

        val entity = NotaEntity(
            id = idInt,
            titulo = nota.titulo,
            contenido = nota.contenido
        )
        // Usamos INSERT con OnConflictStrategy.REPLACE
        dao.insert(entity)
    }

    override suspend fun eliminar(id: String) {
        // En este dao simplificado de ej1 no tenemos delete,
        // pero simularemos dejando este método vacío o puedes agregar un Query de Delete al Dao original.
    }
}
