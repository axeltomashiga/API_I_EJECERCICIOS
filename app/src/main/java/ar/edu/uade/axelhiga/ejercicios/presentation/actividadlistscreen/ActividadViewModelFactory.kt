package ar.edu.uade.axelhiga.ejercicios.presentation.actividadlistscreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ar.edu.uade.axelhiga.ejercicios.data.repository.ActividadRepository

class ActividadViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActividadListScreenViewModel::class.java)) {
            val repository = ActividadRepository(context)
            @Suppress("UNCHECKED_CAST")
            return ActividadListScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}