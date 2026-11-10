package com.flores.mycar.ui.lista

import com.flores.mycar.data.repository.ItemRepository
import com.flores.mycar.domain.model.Item
import com.flores.mycar.notification.AvisoAutos
import com.flores.mycar.notification.Notificador
import com.flores.mycar.ui.UiState
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Repositorio falso: guarda las marcas en memoria y devuelve el resultado que la prueba indique. */
private class FakeItemRepository(
    guardadas: List<Item> = emptyList(),
    private val remotas: List<Item> = emptyList(),
    private val falla: Throwable? = null
) : ItemRepository {

    private val lista = MutableStateFlow(guardadas)
    override val items: Flow<List<Item>> = lista
    override val favoritos: Flow<List<Item>> = lista.map { todos -> todos.filter { it.esFavorito } }

    override suspend fun refrescar(): Result<Int> {
        if (falla != null) return Result.failure(falla)
        lista.value = remotas
        return Result.success(remotas.size)
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> = Result.failure(UnsupportedOperationException())
    override suspend fun alternarFavorito(id: Int) {}
    override suspend fun cantidadGuardada(): Int = lista.value.size
    override suspend fun cantidadFavoritos(): Int = lista.value.count { it.esFavorito }
    override suspend fun limpiarCache() { lista.value = emptyList() }
}

/** Notificador falso: en vez de mostrar una notificacion, apunta lo que se pidio mostrar. */
private class FakeNotificador : Notificador {
    val avisos = mutableListOf<Pair<String, String>>()
    override fun mostrar(titulo: String, texto: String) {
        avisos += titulo to texto
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {

    private val toyota = Item(1, "Toyota", "Auto de pasajeros", null)
    private val honda = Item(2, "Ford", "Auto de pasajeros", null)
    private val nissan = Item(3, "Kia", "Auto de pasajeros", null)

    @Before
    fun antes() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun despues() {
        Dispatchers.resetMain()
    }

    @Test
    fun alSincronizarConInternet_muestraLaListaYAvisaCuantosGuardo() {
        val notificador = FakeNotificador()
        val vm = ListaViewModel(FakeItemRepository(remotas = listOf(toyota, honda, nissan)), notificador)

        val estado = vm.estado.value
        assertTrue("Debe mostrar Exito", estado is UiState.Exito)
        assertEquals(listOf(toyota, nissan, honda), (estado as UiState.Exito).datos)   // orden de las 10 marcas: Toyota, Kia, Ford
        assertEquals(listOf(AvisoAutos.TITULO to AvisoAutos.TEXTO), notificador.avisos)
        assertNull(vm.aviso.value)
    }

    @Test
    fun sinInternetYSinDatosGuardados_muestraErrorConReintentar() {
        val notificador = FakeNotificador()
        val vm = ListaViewModel(FakeItemRepository(falla = IOException("sin red")), notificador)

        val estado = vm.estado.value
        assertTrue("Debe mostrar Error", estado is UiState.Error)
        assertEquals("No hay conexión a internet", (estado as UiState.Error).mensaje)
        assertTrue("No debe avisar nada", notificador.avisos.isEmpty())
    }

    @Test
    fun sinInternetPeroConDatosGuardados_muestraLaListaYUnAviso() {
        val notificador = FakeNotificador()
        val vm = ListaViewModel(
            FakeItemRepository(guardadas = listOf(toyota, honda), falla = IOException("sin red")),
            notificador
        )

        val estado = vm.estado.value
        assertTrue("Debe mostrar Exito con lo guardado", estado is UiState.Exito)
        assertEquals(listOf(toyota, honda), (estado as UiState.Exito).datos)
        assertEquals("Sin conexión: mostrando datos guardados", vm.aviso.value)
        assertTrue("No debe avisar de una sincronizacion fallida", notificador.avisos.isEmpty())
    }

    @Test
    fun siLaApiNoTieneMarcas_muestraExitoConListaVacia() {
        val vm = ListaViewModel(FakeItemRepository(remotas = emptyList()), FakeNotificador())

        val estado = vm.estado.value
        assertTrue("Debe mostrar Exito", estado is UiState.Exito)
        assertTrue((estado as UiState.Exito).datos.isEmpty())
    }
}
