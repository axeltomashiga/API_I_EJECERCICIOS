package ar.edu.uade.axelhiga.ejercicios.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.domain.model.Materia

@Composable
fun MateriaItem (materia: Materia, modifier: Modifier = Modifier) {
    Row (modifier = modifier)
    {
        Text(text = materia.nombre)
        Spacer(Modifier.width(15.dp))
        Text(text = materia.anio.toString())
        Spacer(Modifier.width(15.dp))
        Text(text = if (materia.aprobada) "Aprobada" else "Reprobada")
    }
}