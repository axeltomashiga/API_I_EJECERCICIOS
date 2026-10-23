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
    version = 2, // Subimos a versión 2 (Punto 4 de la consigna)
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
         * Agrega la nueva columna obligatoria 'paginas' (NOT NULL) con DEFAULT 0
         * para garantizar que los datos existentes no se pierdan ni se corrompan.
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
                )
                    .addMigrations(MIGRACION_1_2) // Se registra la migración
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
