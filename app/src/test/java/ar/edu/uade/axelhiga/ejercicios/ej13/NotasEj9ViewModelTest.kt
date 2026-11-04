package ar.edu.uade.axelhiga.ejercicios.ej13

import ar.edu.uade.axelhiga.ejercicios.ej9.model.CargaRepository
import ar.edu.uade.axelhiga.ejercicios.ej9.viewmodel.EstadoCarga
import ar.edu.uade.axelhiga.ejercicios.ej9.viewmodel.NotasEj9ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 1. Fake Repository (MOCK):
 * Se comporta como una base de datos o API pero sin tocar el disco.
 * Nos permite forzar fallas a voluntad para probar la reacción del ViewModel.
 */
class FakeCargaRepository(
    var textoInicial: String = "Borrador previo"
) : CargaRepository {
    var forzarErrorLectura = false
    var forzarErrorEscritura = false
    var textoGuardado: String? = null

    override suspend fun leerTexto(): String {
        if (forzarErrorLectura) throw IllegalStateException("Error de lectura forzado")
        return textoInicial
    }

    override suspend fun escribirTexto(texto: String) {
        if (forzarErrorEscritura) throw IllegalStateException("Error de escritura forzado")
        textoGuardado = texto
    }
}

/**
 * Pruebas unitarias para NotasEj9ViewModel.
 * Testeamos la carga inicial, escritura exitosa y manejo de errores.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NotasEj9ViewModelTest {

    // Necesitamos un Dispatcher de prueba porque los ViewModels usan viewModelScope.launch (que requiere el Dispatchers.Main).
    // En las pruebas unitarias (JVM pura, sin celular), el hilo Main de Android no existe y tiraría error.
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeRepo: FakeCargaRepository
    private lateinit var viewModel: NotasEj9ViewModel

    @Before
    fun setup() {
        // Justificación: setMain reemplaza el Main de Android por nuestro despachador de prueba
        // para que las corrutinas corran de forma síncrona y predecible durante el test.
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeCargaRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargaInicial exitosa recupera el texto del repositorio`() = runTest {
        viewModel = NotasEj9ViewModel(fakeRepo) // Al instanciar, llama a leerArchivo()

        // Ejecutamos las corrutinas pendientes
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals(EstadoCarga.Listo, estado.estadoLectura)
        assertEquals("Borrador previo", estado.textoFormulario)
    }

    @Test
    fun `cargaInicial fallida muestra EstadoCarga Fallo`() = runTest {
        fakeRepo.forzarErrorLectura = true
        viewModel = NotasEj9ViewModel(fakeRepo)

        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado.estadoLectura is EstadoCarga.Fallo)
        assertEquals("No se pudo leer el archivo: Error de lectura forzado", (estado.estadoLectura as EstadoCarga.Fallo).mensaje)
    }

    @Test
    fun `guardado correcto avisa Listo y envia datos al repo`() = runTest {
        viewModel = NotasEj9ViewModel(fakeRepo)
        advanceUntilIdle() // Terminar carga inicial

        viewModel.onTextoChange("Nuevo texto genial")
        viewModel.onGuardarClick()

        advanceUntilIdle() // Esperar a que la corrutina de guardado termine

        val estado = viewModel.uiState.value
        assertEquals(EstadoCarga.Listo, estado.estadoEscritura)
        assertEquals("¡Texto guardado exitosamente en el disco!", estado.mensajeExito)
        assertEquals("Nuevo texto genial", fakeRepo.textoGuardado)
    }

    @Test
    fun `error de escritura atrapa la excepcion y muestra Fallo`() = runTest {
        viewModel = NotasEj9ViewModel(fakeRepo)
        advanceUntilIdle()

        fakeRepo.forzarErrorEscritura = true
        viewModel.onTextoChange("Intento fallido")
        viewModel.onGuardarClick()

        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado.estadoEscritura is EstadoCarga.Fallo)
        assertEquals("No se pudo guardar: Error de escritura forzado", (estado.estadoEscritura as EstadoCarga.Fallo).mensaje)
    }
}
