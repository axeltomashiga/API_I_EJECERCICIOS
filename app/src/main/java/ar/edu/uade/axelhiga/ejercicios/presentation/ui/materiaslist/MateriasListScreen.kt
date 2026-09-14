package ar.edu.uade.axelhiga.ejercicios.presentation.ui.materiaslist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.presentation.ui.components.MateriaItem

@Composable
fun MateriasListScreen (
    viewModel: MateriasListViewModel = MateriasListViewModel(),
) {
    val materias by viewModel.materias.collectAsState()

    var filtro by remember { mutableStateOf("Todas") }

    val materiasFiltro = when (filtro) {
        "Aprobadas" -> materias.filter { it.aprobada }
        "Reprobadas" -> materias.filter { !it.aprobada }
        else -> materias
    }

    Scaffold() { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row() {
                Text("Nombre")
                Spacer(Modifier.width(15.dp))
                Text("Año")
                Spacer(Modifier.width(15.dp))
                Text("Estado")
            }
            Spacer(Modifier.height(15.dp))
            LazyColumn() {
                items(materiasFiltro) { materia ->
                    MateriaItem(materia)
                }
            }

            Spacer(Modifier.height(15.dp))

            Text("Cantidad de Materias: ${ materiasFiltro.size }")

            Spacer(Modifier.height(15.dp))

            Row () {

                Button(
                    onClick = {
                        filtro = "Aprobadas"
                    }
                ) {
                    Text("Filtrar Aprobadas")
                }

                Button(
                    onClick = {
                        filtro = "Reprobadas"
                    }
                ) {
                    Text("Filtrar Reprobadas")
                }

                Button(
                    onClick = {
                        filtro = "Todas"
                    }
                ) {
                    Text("Mostrar todas")
                }

            }
        }
    }


}