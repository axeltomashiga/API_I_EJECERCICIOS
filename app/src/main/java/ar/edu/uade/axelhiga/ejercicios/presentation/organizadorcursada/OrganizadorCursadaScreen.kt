package ar.edu.uade.axelhiga.ejercicios.presentation.organizadorcursada

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import ar.edu.uade.axelhiga.ejercicios.presentation.components.ActividadCard
import ar.edu.uade.axelhiga.ejercicios.presentation.components.FormularioOrganizador

@Composable
fun OrganizadorCursadaScreen(
    viewModel: OrganizadorCursadaViewModel = OrganizadorCursadaViewModel()
) {
    var actividadesList = viewModel.actividadesList.collectAsState()

    Scaffold() { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            FormularioOrganizador( onAgregarActividad = {actividad -> viewModel.agregarActividad(actividad)})

            LazyColumn() {
                items(actividadesList.value.size) { index ->
                    val actividad = actividadesList.value[index]
                    Row() {
                        ActividadCard(actividad)
                    }
                }
            }
        }
    }
}