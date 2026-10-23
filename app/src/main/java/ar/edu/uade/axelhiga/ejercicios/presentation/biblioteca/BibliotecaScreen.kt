package ar.edu.uade.axelhiga.ejercicios.presentation.biblioteca

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.uade.axelhiga.ejercicios.domain.model.EstadoLectura
import ar.edu.uade.axelhiga.ejercicios.domain.model.Libro
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibliotecaScreen(
    viewModel: BibliotecaViewModel,
    modifier: Modifier = Modifier
) {
    val soloNoTerminados by viewModel.soloNoTerminados.collectAsState()
    val librosNoTerminados by viewModel.librosNoTerminados.collectAsState()
    val todosLosLibros by viewModel.todosLosLibros.collectAsState()

    val librosAMostrar = if (soloNoTerminados) librosNoTerminados else todosLosLibros

    var tituloInput by remember { mutableStateOf("") }
    var autorInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Biblioteca Personal", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alta de libro (Punto 3)
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Registrar nuevo libro",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = tituloInput,
                        onValueChange = { tituloInput = it },
                        label = { Text("Título del libro") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = autorInput,
                        onValueChange = { autorInput = it },
                        label = { Text("Autor") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            viewModel.agregarLibro(tituloInput, autorInput)
                            tituloInput = ""
                            autorInput = ""
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Agregar libro")
                    }
                }
            }

            // Filtro (Punto 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (soloNoTerminados) "Libros no terminados (${librosAMostrar.size})" else "Todos los libros (${librosAMostrar.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                FilterChip(
                    selected = soloNoTerminados,
                    onClick = { viewModel.alternarFiltro() },
                    label = { Text(if (soloNoTerminados) "Filtrando: No terminados" else "Viendo: Todos") }
                )
            }

            // Lista reactiva
            if (librosAMostrar.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (soloNoTerminados) "¡No hay libros pendientes ni en lectura!" else "No hay libros cargados.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(librosAMostrar, key = { it.id }) { libro ->
                        LibroItem(
                            libro = libro,
                            onAvanzarEstado = { viewModel.avanzarEstado(libro) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LibroItem(
    libro: Libro,
    onAvanzarEstado: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) }
    val fechaFormateada = remember(libro.fechaActualizacion) {
        formatoFecha.format(Date(libro.fechaActualizacion))
    }

    val colorBadge = when (libro.estado) {
        EstadoLectura.PENDIENTE -> Color(0xFFF57C00) // Naranja
        EstadoLectura.LEYENDO -> Color(0xFF1976D2)   // Azul
        EstadoLectura.TERMINADO -> Color(0xFF388E3C) // Verde
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = libro.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Autor: ${libro.autor}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Última act.: $fechaFormateada",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .background(colorBadge.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = libro.estado.name,
                        color = colorBadge,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            OutlinedButton(
                onClick = onAvanzarEstado,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Cambiar Estado", fontSize = 12.sp)
            }
        }
    }
}
