package ar.edu.uade.axelhiga.ejercicios.ej14.model

import android.content.Context
import ar.edu.uade.axelhiga.ejercicios.ej1.data.AppDatabase

object AppDatabaseFactory {
    fun getDao(context: Context) = AppDatabase.getInstance(context).notaDao()
}
