package ar.edu.uade.axelhiga.ejercicios

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.uade.axelhiga.ejercicios.ui.theme.EjerciciosTheme
import ar.edu.uade.axelhiga.ejercicios.presentation.organizadorcursada.OrganizadorCursadaScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EjerciciosTheme {
                OrganizadorCursadaScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompartirRecomendacionPreview () {
    EjerciciosTheme() {
        OrganizadorCursadaScreen()
    }
}

/*

El proyecto tiene una base muy sólida y bien encaminada para MVVM con Jetpack Compose, aunque presenta algunos detalles técnicos y arquitectónicos importantes a tener en cuenta.

  A continuación tienes un análisis detallado:
  ──────
  ### Lo que está bien implementado

  1. Separación de responsabilidades y modularización:
      • La estructura de paquetes (domain, presentation, components) sigue el estándar recomendado.
      • La vista no realiza mutaciones directas sobre la lista; delega los eventos al ViewModel a través de callbacks (agregarActividad, completarActividad, eliminarActividad).
  2. Encapsulamiento del estado en el ViewModel (OrganizadorCursadaViewModel.kt):
      • Utilizas un _actividadesList privado (MutableStateFlow) y expones una versión inmutable con .asStateFlow(). Esto evita que la vista pueda alterar el estado directamente desde afuera.
  3. State Hoisting en los componentes UI:
      • Componentes como ActividadCard.kt y OrganizadorForm.kt son stateless respecto a la lógica de negocio general: reciben datos y emiten eventos mediante lambdas. Esto facilita enormemente su reutilización y testeo.
  4. Inmutabilidad del modelo de dominio (Actividad.kt):
      • Usas data class con propiedades val y creas nuevas instancias con .copy(), respetando el paradigma reactivo e inmutable.

  ──────
  ### Puntos a mejorar y corregir

  #### 1. Instanciación del ViewModel en el Composable

  En OrganizadorCursadaScreen.kt:15-17:

    fun OrganizadorCursadaScreen(
        viewModel: OrganizadorCursadaViewModel = OrganizadorCursadaViewModel()
    )

  • Problema: Al hacer = OrganizadorCursadaViewModel(), estás creando una instancia ordinaria de Kotlin. No queda registrada en el ViewModelStore del ciclo de vida de Android. Si el usuario rota la pantalla o el sistema recrea
  la Activity, se perderá todo el estado.
  • Solución recomendada en MVVM: Usar la función proveída por Compose:
    viewModel: OrganizadorCursadaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()


  #### 2. Lógica de cálculo en la Vista

  En OrganizadorCursadaScreen.kt:43:

    Text("Te faltan completar ${actividadesList.value.filter { !it.completada }.size} actividades")

  • Problema: En MVVM, la Vista debe ser lo más pasiva posible. El filtrado y conteo de actividades incompletas es lógica de presentación/negocio que debería resolverse en el ViewModel o exponerse dentro de un UiState.
  • Solución recomendada: Exponer un UiState consolidado o un Flow derivado desde el ViewModel:
    data class OrganizadorUiState(
        val actividades: List<Actividad> = emptyList(),
        val pendientesCount: Int = 0
    )


  #### 3. Falta de identificador único (id) en el modelo

  En Actividad.kt:

  • Actualmente el modelo solo tiene titulo, materia, prioridad, completada.
  • Si el usuario agrega dos tareas con los mismos datos (por ejemplo, dos tareas de "TP1"), al ejecutar it == actividad o it != actividad en completarActividad o eliminarActividad, se modificarán o borrarán ambas a la vez.
  • Además, en OrganizadorCursadaScreen.kt:28-41, definir una key = { actividad.id } es fundamental para el rendimiento y la animación de items.
  • Solución recomendada: Agregar un identificador único (ej: val id: String = java.util.UUID.randomUUID().toString()).

  #### 4. Capas data y domain/repository vacías

  • En el proyecto existen los paquetes data/ y domain/repository/, pero actualmente no contienen archivos y el ViewModel maneja la persistencia en memoria por sí mismo.
  • Si este es un ejercicio práctico/académico introductorio, manejar la lista en memoria en el ViewModel es aceptable. Sin embargo, para una arquitectura MVVM limpia formal, se suele definir:
      1. ActividadRepository (interfaz en domain/repository)
      2. ActividadRepositoryImpl (implementación en data/repository)
      3. El ViewModel inyecta o recibe el Repositorio en lugar de mutar los datos en memoria por cuenta propia.


  #### 5. Ciclo de vida y colección del Flow

  En OrganizadorCursadaScreen.kt:18:

  • Usas collectAsState(). En Android es una mejor práctica usar collectAsStateWithLifecycle() (de la dependencia androidx.lifecycle.compose) para pausar la recolección si la app entra en segundo plano.
  ──────
  ### Conclusión

  El concepto central de MVVM está bien asimilado: la UI observa un flujo de estado expuesto por el ViewModel y le envía acciones sin manipular la lista directamente. Los cambios principales para dejarlo impecable son:

  1. Usar viewModel() en lugar de invocar el constructor directamente.
  2. Añadir un id único a Actividad.kt.
  3. Trasladar el cálculo de tareas pendientes al OrganizadorCursadaViewModel.kt.

 */