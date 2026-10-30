package ar.edu.uade.axelhiga.ejercicios.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ar.edu.uade.axelhiga.ejercicios.data.local.dao.NotaDao
import ar.edu.uade.axelhiga.ejercicios.data.local.entity.NotaEntity

@Database(
    entities = [NotaEntity::class],
    version = 1,
    exportSchema = false // Recomendado en false si no configuras una ruta de exportación en Gradle
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notaDao(): NotaDao

    // Singleton para evitar múltiples instancias de la base de datos abierta al mismo tiempo
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notas_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}