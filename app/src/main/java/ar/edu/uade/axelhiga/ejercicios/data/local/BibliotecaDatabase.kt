package ar.edu.uade.axelhiga.ejercicios.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ar.edu.uade.axelhiga.ejercicios.data.local.dao.LibroDao
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.EstanteriaEntity
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.LibroEntity

@Database(
    entities = [EstanteriaEntity::class, LibroEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BibliotecaDatabase : RoomDatabase() {

    abstract fun libroDao(): LibroDao

    companion object {
        @Volatile
        private var INSTANCE: BibliotecaDatabase? = null

        /**
         * Migración hacia versión 2 (Punto 4 de la consigna):
         * Agrega una nueva columna obligatoria (NOT NULL) con DEFAULT 0 sin perder datos.
         */
        val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE libros ADD COLUMN paginas INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): BibliotecaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BibliotecaDatabase::class.java,
                    "biblioteca.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
