package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.data.models.Materia
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MateriasList(Modifier.padding(innerPadding))
                }
            }
        }
    }
}


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

@Composable
fun MateriasList (modifier: Modifier = Modifier) {
    val materias : List<Materia> = listOf(
        Materia("Programacion", 1, true),
        Materia("Matematica", 1, false),
        Materia("Fisica", 1, true),
        Materia("Quimica", 1, false),
        Materia("Historia", 1, true),
        Materia("Lengua", 1, false),
        Materia("Ingles", 1, true),
        )

    var filtro by remember { mutableStateOf("Todas") }

    val materiasFiltro = when (filtro) {
        "Aprobadas" -> materias.filter { it.aprobada }
        "Reprobadas" -> materias.filter { !it.aprobada }
        else -> materias
    }


    Column(modifier) {
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


@Preview(showBackground = true)
@Composable
fun MateriasListPreview () {
    EjerciciosTheme() {
        MateriasList()
    }
}