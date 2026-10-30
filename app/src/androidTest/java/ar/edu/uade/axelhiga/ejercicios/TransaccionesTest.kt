package ar.edu.uade.axelhiga.ejercicios

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ar.edu.uade.axelhiga.ejercicios.data.local.AppDatabase
import ar.edu.uade.axelhiga.ejercicios.data.local.dao.CategoriaDao
import ar.edu.uade.axelhiga.ejercicios.data.local.dao.NotaDao
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaEntity
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.Prioridad
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
 * Pruebas unitarias/instrumentadas para el Ejercicio 13 (Transacciones en Room).
 */
@RunWith(AndroidJUnit4::class)
class TransaccionesTest {

    private lateinit var db: AppDatabase
    private lateinit var categoriaDao: CategoriaDao
    private lateinit var notaDao: NotaDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        categoriaDao = db.categoriaDao()
        notaDao = db.notaDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * PRUEBA 1: Sin Transacción.
     * Si falla el paso 2 (eliminar la categoría), las notas se movieron pero la categoría origen
     * NO se borra. La base de datos queda en un estado inconsistente (categoría vacía no deseada).
     */
    @Test
    fun pruebaSinTransaccion_fallaDejaEstadoInconsistente() = runBlocking {
        val idOrigen = categoriaDao.insert(CategoriaEntity(nombre = "Origen")).toInt()
        val idDestino = categoriaDao.insert(CategoriaEntity(nombre = "Destino")).toInt()

        val notaId = notaDao.insert(
            NotaEntity(
                titulo = "Nota Importante",
                contenido = "Contenido de prueba",
                prioridad = Prioridad.ALTA,
                categoriaId = idOrigen
            )
        ).toInt()

        try {
            categoriaDao.moverNotasYEliminarCategoriaSinTransaccion(idOrigen, idDestino, simularFallo = true)
        } catch (e: Exception) {
            // Se esperaba la excepción
        }

        // Estado inconsistente:
        // Las notas SÍ cambiaron de categoría
        val notaActualizada = notaDao.obtenerPorId(notaId)
        assertEquals(idDestino, notaActualizada.categoriaId)

        // Pero la categoría origen NO se eliminó (se interrumpió antes)
        val categoriaOrigen = categoriaDao.obtenerPorId(idOrigen)
        assertNotNull("La categoría origen no se eliminó porque la operación no fue atómica", categoriaOrigen)
    }

    /**
     * PRUEBA 2: Con Transacción (@Transaction).
     * Si la operación falla a mitad de camino, Room ejecuta un ROLLBACK.
     * Toda la operación se revierte: las notas vuelven a la categoría origen y la categoría origen se conserva.
     */
    @Test
    fun pruebaConTransaccion_fallaHaceRollbackTotal() = runBlocking {
        val idOrigen = categoriaDao.insert(CategoriaEntity(nombre = "Origen")).toInt()
        val idDestino = categoriaDao.insert(CategoriaEntity(nombre = "Destino")).toInt()

        val notaId = notaDao.insert(
            NotaEntity(
                titulo = "Nota Importante",
                contenido = "Contenido de prueba",
                prioridad = Prioridad.ALTA,
                categoriaId = idOrigen
            )
        ).toInt()

        try {
            categoriaDao.moverNotasYEliminarCategoriaConTransaccion(idOrigen, idDestino, simularFallo = true)
        } catch (e: Exception) {
            // Se esperaba la excepción
        }

        // Estado atómico (Rollback completo):
        // La nota volvió a su categoría original
        val notaRestaurada = notaDao.obtenerPorId(notaId)
        assertEquals(idOrigen, notaRestaurada.categoriaId)

        // La categoría origen sigue existiendo normalmente
        val categoriaOrigen = categoriaDao.obtenerPorId(idOrigen)
        assertNotNull("La categoría origen se conserva al revertirse la transacción", categoriaOrigen)
    }

    /**
     * PRUEBA 3: Con Transacción en ejecución exitosa.
     * Ambas operaciones completan atómicamente.
     */
    @Test
    fun pruebaConTransaccion_exitoMueveNotasYEliminaCategoria() = runBlocking {
        val idOrigen = categoriaDao.insert(CategoriaEntity(nombre = "Origen")).toInt()
        val idDestino = categoriaDao.insert(CategoriaEntity(nombre = "Destino")).toInt()

        val notaId = notaDao.insert(
            NotaEntity(
                titulo = "Nota Importante",
                contenido = "Contenido de prueba",
                prioridad = Prioridad.ALTA,
                categoriaId = idOrigen
            )
        ).toInt()

        // Ejecución exitosa con @Transaction
        categoriaDao.moverNotasYEliminarCategoriaConTransaccion(idOrigen, idDestino, simularFallo = false)

        // Verificación de éxito atómico:
        val notaMovida = notaDao.obtenerPorId(notaId)
        assertEquals(idDestino, notaMovida.categoriaId)

        val categoriaOrigen = categoriaDao.obtenerPorId(idOrigen)
        assertNull("La categoría origen fue eliminada exitosamente", categoriaOrigen)
    }

    /**
     * PRUEBA 4: Lectura relacional con @Transaction.
     * Verifica que al consultar la relación 1:N (Categoría con Notas), @Transaction garantice
     * que la consulta de categorías y la consulta de notas relacionadas se hagan sobre una captura
     * consistente de la base de datos sin mutaciones intermedias.
     */
    @Test
    fun pruebaLecturaRelacionalConTransaccion() = runBlocking {
        val idCategoria = categoriaDao.insert(CategoriaEntity(nombre = "Estudios")).toInt()
        notaDao.insert(
            NotaEntity(
                titulo = "Aprender Room",
                contenido = "Revisar @Transaction",
                prioridad = Prioridad.ALTA,
                categoriaId = idCategoria
            )
        )

        val resultado = categoriaDao.obtenerCategoriasConNotas().first()

        assertEquals(1, resultado.size)
        assertEquals("Estudios", resultado[0].categoria.nombre)
        assertEquals(1, resultado[0].notas.size)
        assertEquals("Aprender Room", resultado[0].notas[0].titulo)
    }
}
