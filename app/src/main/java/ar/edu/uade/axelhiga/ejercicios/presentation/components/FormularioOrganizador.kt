package ar.edu.uade.axelhiga.ejercicios.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ar.edu.uade.axelhiga.ejercicios.domain.model.Actividad

@Composable
fun FormularioOrganizador (
    onAgregarActividad: (Actividad) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var materia by remember { mutableStateOf("") }
    var prioridad by remember { mutableIntStateOf(0) }

    Column() {
        TextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") })
        TextField(value = materia, onValueChange = { materia = it }, label = { Text("Materia") })
        TextField(value = prioridad.toString(), onValueChange = { prioridad = it.toIntOrNull() ?: 0 }, label = { Text("Prioridad") })

        Button(onClick = {
            onAgregarActividad(Actividad(titulo, materia, prioridad, false))
        }) {
            Text("Agregar actividad")
        }
    }
}