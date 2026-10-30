package ar.edu.uade.axelhiga.ejercicios.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ar.edu.uade.axelhiga.ejercicios.data.local.dao.CategoriaDao
import ar.edu.uade.axelhiga.ejercicios.data.local.dao.NotaDao
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.CategoriaEntity
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity

/**
 * Migración de la versión 1 a la versión 2.
 * Agrega la columna 'archivada' a la tabla 'notas' con valor inicial 0 (false) para las filas existentes.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notas ADD COLUMN archivada INTEGER NOT NULL DEFAULT 0")
    }
}

/**
 * Migración de la versión 4 a la versión 5 (para bases que arrancaron en v4).
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notas ADD COLUMN archivada INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(
    entities = [NotaEntity::class, CategoriaEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notaDao(): NotaDao
    abstract fun categoriaDao(): CategoriaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notas_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_4_5)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
