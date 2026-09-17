package ar.edu.uade.axelhiga.ejercicios.presentation.organizadorcursada

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import ar.edu.uade.axelhiga.ejercicios.presentation.components.ActividadCard
import ar.edu.uade.axelhiga.ejercicios.presentation.components.OrganizadorForm

@Composable
fun OrganizadorCursadaScreen(
    viewModel: OrganizadorCursadaViewModel = OrganizadorCursadaViewModel()
) {
    var actividadesList = viewModel.actividadesList.collectAsState()

    Scaffold() { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            OrganizadorForm( onAgregarActividad =
                { actividad ->
                    viewModel.agregarActividad(actividad)
                }
            )

            LazyColumn() {
                items(actividadesList.value.size) { index ->
                    val actividad = actividadesList.value[index]
                    ActividadCard(actividad,
                    onCompletarActividad = {
                        actividad ->
                            viewModel.completarActividad(actividad)
                    },
                    onEliminarActividad = {
                        actividad ->
                            viewModel.eliminarActividad(actividad)
                    })
                }
            }

            Text("Te faltan completar ${actividadesList.value.filter { !it.completada }.size} actividades")
        }
    }
}