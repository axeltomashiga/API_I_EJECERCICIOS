package ar.edu.uade.axelhiga.ejercicios.ej6.model

import kotlinx.coroutines.flow.Flow

// CONTRATO
// Interfaz agnóstica a la implementación física de datos.
interface NotaRepository {

    // Se decide utilizar Flow porque la UI (Compose) en arquitecturas modernas
    // es Reactiva. Si otra pantalla (u otro ViewModel) agrega, modifica o borra
    // una nota de este repositorio, queremos que la lista se actualice automáticamente
    // en pantalla sin necesidad de invocar un método "recargarNotas()" manualmente.
    fun obtenerTodas(): Flow<List<Nota>>

    suspend fun guardar(nota: Nota)

    suspend fun eliminar(id: String)
}
