package ar.edu.uade.axelhiga.ejercicios.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.domain.model.Actividad

@Composable
fun ActividadCard(actividad: Actividad,
                  onCompletarActividad: (Actividad) -> Unit,
                  onEliminarActividad: (Actividad) -> Unit) {
    Card(border = BorderStroke(1.dp, Color.Black), modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)) {
        Row() {
            Column(Modifier.fillMaxSize().padding(start = 15.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)) {
                Text("Titulo: ${actividad.titulo}")
                Text("Materia: ${actividad.materia}")
                Text("Prioridad: ${actividad.prioridad.toString()}")
                Text("Completada: ${actividad.completada.toString()}")
                Button(onClick = {
                    onCompletarActividad(actividad)
                }) {
                    Text("Completar")
                }

                Button(onClick = {
                    onEliminarActividad(actividad)
                }) {
                    Text("Eliminar")
                }
            }

        }
    }
}