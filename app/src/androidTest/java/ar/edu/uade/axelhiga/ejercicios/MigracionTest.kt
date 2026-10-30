package ar.edu.uade.axelhiga.ejercicios

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ar.edu.uade.axelhiga.ejercicios.data.local.AppDatabase
import ar.edu.uade.axelhiga.ejercicios.data.local.MIGRATION_1_2
import ar.edu.uade.axelhiga.ejercicios.data.local.MIGRATION_4_5
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba de migración de esquema para verificar que las filas existentes
 * conserven sus datos y reciban el valor por defecto (false) en la nueva columna 'archivada'.
 */
@RunWith(AndroidJUnit4::class)
class MigracionTest {

    @Test
    fun verificarMigracion_agregaColumnaArchivadaYConservaFilasExistentes() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "test_migracion.db"
        context.deleteDatabase(dbName)

        // 1. Crear base e insertar 5 registros
        val dbV1 = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .allowMainThreadQueries()
            .build()

        val openHelper = dbV1.openHelper
        val db: SupportSQLiteDatabase = openHelper.writableDatabase

        for (i in 1..5) {
            db.execSQL("INSERT INTO notas (titulo, contenido, prioridad, creadaEn, modificadaEn) VALUES ('Nota $i', 'Contenido $i', 'BAJA', ${System.currentTimeMillis()}, ${System.currentTimeMillis()})")
        }

        val cursorV1 = db.query("SELECT COUNT(*) FROM notas")
        cursorV1.moveToFirst()
        assertEquals(5, cursorV1.getInt(0))
        cursorV1.close()
        dbV1.close()

        // 2. Abrir la base con la nueva versión registrando las migraciones
        val dbV2 = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .addMigrations(MIGRATION_1_2, MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()

        val notaDao = dbV2.notaDao()
        val notas = notaDao.obtenerTodasDeUnaVez()

        // 3. Comprobar que los 5 registros existen y tienen archivada = false
        assertEquals(5, notas.size)
        for (nota in notas) {
            assertFalse("Las filas antiguas deben tener archivada = false tras la migración", nota.archivada)
        }

        dbV2.close()
        context.deleteDatabase(dbName)
    }
}
