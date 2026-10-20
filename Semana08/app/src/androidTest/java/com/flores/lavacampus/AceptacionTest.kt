package com.flores.lavacampus

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flores.lavacampus.data.local.DatosIniciales
import com.flores.lavacampus.data.local.LavaDatabase
import com.flores.lavacampus.data.repository.LavaRepositoryRoom
import com.flores.lavacampus.model.EstadoPedido
import com.flores.lavacampus.model.LineaPedido
import com.flores.lavacampus.viewmodel.GestionPedidoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

// Pruebas de aceptacion del Paso 14 sobre una base en memoria (con los datos iniciales)
@RunWith(AndroidJUnit4::class)
class AceptacionTest {

    private lateinit var db: LavaDatabase
    private lateinit var repo: LavaRepositoryRoom

    @Before
    fun preparar() = runBlocking {
        val contexto: Context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(contexto, LavaDatabase::class.java).build()
        repo = LavaRepositoryRoom(db)
        DatosIniciales.sembrar(db)
    }

    @After
    fun cerrar() {
        db.close()
    }

    @Test
    fun total_de_3_5kg_lavado_y_5_prendas_planchado_es_40_50() = runBlocking {
        val id = repo.crearPedido(
            usuarioId = 3, // Marta tiene solo 2 pedidos activos
            lineas = listOf(LineaPedido(1, 3.5), LineaPedido(2, 5.0)),
            fechaEntrega = LocalDate.now().plusDays(2).toString(),
            nota = ""
        )
        val pedido = repo.observarPedido(id.toInt()).first()!!
        assertEquals(40.5, pedido.pedido.total, 0.001)
        assertEquals(2, pedido.detalles.size)
        assertEquals("RECIBIDO", pedido.pedido.estado)
        assertFalse(pedido.pedido.pagado)
        // Precio congelado en el detalle
        assertEquals(8.0, pedido.detalles.first { it.servicio.id == 1 }.detalle.precioUnitario, 0.001)
    }

    @Test
    fun un_pedido_con_servicio_inexistente_no_deja_pedido_sin_detalles() = runBlocking {
        val antes = repo.observarPedidos().first().size
        try {
            repo.crearPedido(1, listOf(LineaPedido(999, 1.0)), "2026-01-01", "")
        } catch (e: IllegalArgumentException) {
            // esperado: la transaccion se deshace
        }
        assertEquals(antes, repo.observarPedidos().first().size)
    }

    @Test
    fun luis_ya_tiene_3_pedidos_activos_asi_que_no_puede_crear_un_cuarto() = runBlocking {
        assertEquals(3, repo.contarActivos(2))
    }

    @Test
    fun el_cambio_de_estado_llega_a_la_pantalla_sin_cerrarla() = runBlocking {
        val estados = mutableListOf<String?>()
        val observador = launch(Dispatchers.IO) {
            repo.observarPedido(8).collect { estados += it?.pedido?.estado }
        }
        delay(500)
        repo.cambiarEstado(8, EstadoPedido.EN_PROCESO, null)
        delay(800)
        observador.cancel()
        assertTrue("Debe emitir RECIBIDO y luego EN_PROCESO: $estados",
            estados.contains("RECIBIDO") && estados.last() == "EN_PROCESO")
    }

    @Test
    fun ingresos_de_la_semana_coinciden_con_la_suma_de_pedidos_pagados() = runBlocking {
        val lunes = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString()
        val esperado = repo.observarPedidos().first()
            .filter { it.pedido.pagado && it.pedido.fechaRegistro >= lunes }
            .sumOf { it.pedido.total }
        assertEquals(esperado, repo.observarIngresosDesde(lunes).first(), 0.001)
        val porServicio = repo.observarIngresosPorServicio(lunes).first().sumOf { it.total }
        assertEquals(esperado, porServicio, 0.001)
    }

    @Test
    fun un_servicio_usado_en_pedidos_no_se_puede_eliminar() = runBlocking {
        assertTrue(repo.servicioTieneDetalles(1))   // Lavado y secado esta en pedidos
        assertFalse(repo.servicioTieneDetalles(4))  // Edredon no esta en ninguno
    }

    @Test
    fun pasar_a_listo_sin_casillero_se_rechaza_y_un_casillero_ocupado_no_se_elige() = runBlocking {
        val vm = GestionPedidoViewModel(repo, 6) // pedido EN_PROCESO
        val jobs = suscribir(vm)
        delay(600) // la pantalla ya cargo el pedido
        vm.avanzar()
        delay(300)
        assertEquals("Elige un casillero para pasar a Listo", vm.mensaje.value)

        vm.elegirCasillero("C-05") // ocupado por un pedido LISTO
        delay(300)
        assertNull(vm.casillero.value)

        vm.elegirCasillero("C-10") // libre
        delay(300)
        vm.avanzar()
        delay(600)
        val pedido = repo.observarPedido(6).first()!!.pedido
        assertEquals("LISTO", pedido.estado)
        assertEquals("C-10", pedido.casillero)
        jobs.forEach { it.cancel() }
    }

    @Test
    fun entregar_sin_cobrar_se_rechaza_y_al_cobrar_si_se_entrega() = runBlocking {
        val vm = GestionPedidoViewModel(repo, 4) // pedido LISTO, sin pagar
        val jobs = suscribir(vm)
        delay(600) // la pantalla ya cargo el pedido
        vm.avanzar()
        delay(300)
        assertEquals("Marca el pedido como Cobrado antes de entregarlo", vm.mensaje.value)
        assertEquals("LISTO", repo.observarPedido(4).first()!!.pedido.estado)

        vm.marcarPagado(true)
        delay(400)
        vm.avanzar()
        delay(600)
        assertEquals("ENTREGADO", repo.observarPedido(4).first()!!.pedido.estado)
        jobs.forEach { it.cancel() }
    }

    @Test
    fun solo_se_cancela_desde_recibido_o_en_proceso() = runBlocking {
        val vm = GestionPedidoViewModel(repo, 4) // LISTO
        val jobs = suscribir(vm)
        delay(600) // la pantalla ya cargo el pedido
        vm.cancelar()
        delay(300)
        assertEquals("LISTO", repo.observarPedido(4).first()!!.pedido.estado)
        assertTrue(vm.mensaje.value!!.startsWith("No se puede cancelar"))
        jobs.forEach { it.cancel() }
    }

    // Los StateFlow con WhileSubscribed necesitan un suscriptor, como la pantalla
    private fun kotlinx.coroutines.CoroutineScope.suscribir(vm: GestionPedidoViewModel) = listOf(
        launch(Dispatchers.IO) { vm.pedido.collect { } },
        launch(Dispatchers.IO) { vm.casillero.collect { } },
        launch(Dispatchers.IO) { vm.casillerosOcupados.collect { } }
    )
}
