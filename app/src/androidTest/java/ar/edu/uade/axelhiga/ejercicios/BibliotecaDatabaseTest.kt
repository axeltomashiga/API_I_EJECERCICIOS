package ar.edu.uade.axelhiga.ejercicios

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ar.edu.uade.axelhiga.ejercicios.data.local.BibliotecaDatabase
import ar.edu.uade.axelhiga.ejercicios.data.local.dao.LibroDao
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.EstanteriaEntity
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.LibroEntity
import ar.edu.uade.axelhiga.ejercicios.domain.model.EstadoLectura
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Pruebas críticas solicitadas en el Punto 5 de la consigna.
 * Se ejecutan sobre una base de datos en memoria para no alterar datos reales.
 */
@RunWith(AndroidJUnit4::class)
class BibliotecaDatabaseTest {

    private lateinit var db: BibliotecaDatabase
    private lateinit var dao: LibroDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, BibliotecaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.libroDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * PRUEBA CRÍTICA 1:
     * Verificar que la política de eliminación onDelete = SET_NULL funcione correctamente.
     * Al borrar una estantería, los libros que contenía no deben destruirse,
     * sino que su campo estanteriaId debe quedar en null.
     */
    @Test
    fun pruebaCritica1_politicaEliminacionSetNullPreservaLibros() = runBlocking {
        // 1. Insertamos una estantería
        val estanteriaId = dao.insertarEstanteria(EstanteriaEntity(nombre = "Favoritos"))

        // 2. Insertamos un libro asociado a esa estantería
        val libroId = dao.insertarLibro(
            LibroEntity(
                titulo = "El Principito",
                autor = "Antoine de Saint-Exupéry",
                estanteriaId = estanteriaId,
                estado = EstadoLectura.LEYENDO
            )
        )

        // 3. Eliminamos la estantería
        dao.eliminarEstanteria(estanteriaId)

        // 4. Verificamos que el libro sigue existiendo pero su estanteriaId ahora es null
        val libroRecuperado = dao.obtenerLibroPorId(libroId)
        assertNotNull("El libro no debe ser eliminado", libroRecuperado)
        assertNull("La clave foránea debe ser seteada a null por la política SET_NULL", libroRecuperado?.estanteriaId)
    }

    /**
     * PRUEBA CRÍTICA 2:
     * Verificar la consulta de libros no terminados y el orden por fecha de actualización (Punto 2).
     * Los libros en estado TERMINADO deben ser excluidos y el resultado debe ordenarse
     * de más reciente a más antiguo (fechaActualizacion DESC).
     */
    @Test
    fun pruebaCritica2_consultaNoTerminadosOrdenadosPorActualizacion() = runBlocking {
        // Insertamos un libro terminado
        dao.insertarLibro(
            LibroEntity(
                titulo = "Libro Leído",
                autor = "Autor A",
                estado = EstadoLectura.TERMINADO,
                fechaActualizacion = 1000L
            )
        )

        // Insertamos un libro pendiente modificado hace poco
        val idLibroPendiente = dao.insertarLibro(
            LibroEntity(
                titulo = "Libro Pendiente",
                autor = "Autor B",
                estado = EstadoLectura.PENDIENTE,
                fechaActualizacion = 2000L
            )
        )

        // Insertamos un libro leyendo modificado más recientemente
        val idLibroLeyendo = dao.insertarLibro(
            LibroEntity(
                titulo = "Libro Leyendo Actualmente",
                autor = "Autor C",
                estado = EstadoLectura.LEYENDO,
                fechaActualizacion = 3000L
            )
        )

        // Ejecutamos la consulta reactiva
        val resultado = dao.observarLibrosNoTerminados().first()

        // Verificamos que solo hayan 2 libros (se descartó el TERMINADO)
        assertEquals(2, resultado.size)

        // Verificamos el orden: el primero debe ser el de mayor timestamp (3000L)
        assertEquals("El primer libro debe ser el más recientemente actualizado", idLibroLeyendo, resultado[0].id)
        assertEquals("El segundo libro debe ser el de actualización anterior", idLibroPendiente, resultado[1].id)
    }
}
