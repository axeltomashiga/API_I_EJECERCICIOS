package ar.edu.uade.axelhiga.ejercicios.presentation.notas

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.edu.uade.axelhiga.ejercicios.data.local.AppDatabase
import ar.edu.uade.axelhiga.ejercicios.data.repository.CategoriaRepositoryImpl
import ar.edu.uade.axelhiga.ejercicios.data.repository.NotaRepositoryImpl

class NotasViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotasViewModel::class.java)) {
            val db = AppDatabase.getInstance(context)
            // Instanciamos la implementación concreta
            val repository = NotaRepositoryImpl(db.notaDao())
            val categoriaRepository = CategoriaRepositoryImpl(db.categoriaDao())
            @Suppress("UNCHECKED_CAST")
            return NotasViewModel(repository, categoriaRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}