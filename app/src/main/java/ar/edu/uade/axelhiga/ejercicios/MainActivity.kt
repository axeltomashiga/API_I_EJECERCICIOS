package ar.edu.uade.axelhiga.ejercicios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.data.models.Tarea
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ToDoList(Modifier.padding(innerPadding))
                }
            }
        }
    }
}


fun agregarTarea(tareas: MutableList<Tarea>, tarea: String) {
    if (tarea.isNotBlank()){
        tareas.add(Tarea(tareas.size+1, tarea, false))
    }
}

fun borrarTarea (tareas: MutableList<Tarea>, id: Int) {
    tareas.removeIf { tarea -> tarea.id == id }
}
@Composable
fun TareaItem(tarea: Tarea, tareas: MutableList<Tarea>) {
    var isChecked by remember { mutableStateOf(false) }
    Card(border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color.Black),
        modifier = Modifier.padding(top = 5.dp, end = 20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()) {
            Text(tarea.nombre, modifier = Modifier.padding(start = 15.dp))
            Row(){
                IconButton (onClick = { borrarTarea(tareas, tarea.id) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar tarea"
                    )
                }
                Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
            }

        }
    }
}
@Composable
fun ToDoList(modifier: Modifier = Modifier) {
    val tareas = remember { mutableStateListOf<Tarea>() }
    var tareaString by remember { mutableStateOf("") }

    Column (modifier = modifier.fillMaxWidth().padding(start = 20.dp)) {
        Text(stringResource(id = R.string.titulo_tareas))
        TextField(value = tareaString,
            onValueChange = { tareaString = it },
            modifier = Modifier.fillMaxWidth().padding(end = 20.dp)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween){
            Button( onClick = {agregarTarea(tareas, tareaString)
                tareaString = ""
            }) {
                Text(stringResource(id = R.string.agregar))
            }
            Button( onClick = {tareas.clear()
            }) {
                Text(stringResource(id = R.string.borrar_todas))
            }
        }
        LazyColumn() {
            items(tareas) {
                tarea -> TareaItem(tarea, tareas)
            }
        }
        Text(stringResource(id = R.string.cantidad_tareas) + tareas.size.toString())
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoListPreview () {
    EjerciciosTheme() {
        ToDoList()
    }
}