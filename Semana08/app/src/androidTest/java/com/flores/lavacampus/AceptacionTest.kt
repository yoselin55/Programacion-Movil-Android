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
import com.flores.lavacampus.data.prefs.PreferenciasStore
import com.flores.lavacampus.data.prefs.FotoPerfilStore
import com.flores.lavacampus.model.Rol
import com.flores.lavacampus.model.ValidacionRegistro
import com.flores.lavacampus.viewmodel.SesionViewModel
import com.flores.lavacampus.viewmodel.GestionPedidoViewModel
import com.flores.lavacampus.viewmodel.NuevoPedidoViewModel
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
import org.junit.Assert.assertNotNull
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
    fun mas_de_5_kg_de_lavado_aplica_10_por_ciento_y_se_guarda_en_el_pedido() = runBlocking {
        val id = repo.crearPedido(3, listOf(LineaPedido(1, 6.0)), LocalDate.now().plusDays(1).toString(), "")
        val pedido = repo.observarPedido(id.toInt()).first()!!.pedido
        assertEquals(4.8, pedido.descuento, 0.001)   // 10 % de 48.00
        assertEquals(43.2, pedido.total, 0.001)      // 48.00 - 4.80
    }

    @Test
    fun hasta_5_kg_no_hay_descuento() = runBlocking {
        val id = repo.crearPedido(3, listOf(LineaPedido(1, 5.0)), LocalDate.now().plusDays(1).toString(), "")
        val pedido = repo.observarPedido(id.toInt()).first()!!.pedido
        assertEquals(0.0, pedido.descuento, 0.001)
        assertEquals(40.0, pedido.total, 0.001)
    }

    @Test
    fun el_descuento_solo_alcanza_al_lavado_y_no_a_las_prendas() = runBlocking {
        val id = repo.crearPedido(
            3, listOf(LineaPedido(1, 5.5), LineaPedido(2, 4.0)), LocalDate.now().plusDays(2).toString(), ""
        )
        val pedido = repo.observarPedido(id.toInt()).first()!!.pedido
        assertEquals(4.4, pedido.descuento, 0.001)   // 10 % de 44.00 (5.5 kg x 8)
        assertEquals(49.6, pedido.total, 0.001)      // 44.00 + 10.00 - 4.40
    }

    @Test
    fun el_repository_rechaza_cantidades_fuera_de_regla_y_servicios_inactivos() = runBlocking {
        val antes = repo.observarPedidos().first().size
        val fecha = LocalDate.now().plusDays(1).toString()
        for (lineas in listOf(
            listOf(LineaPedido(1, 10.5)),   // mas de 10 kg
            listOf(LineaPedido(1, 1.3)),    // no es multiplo de 0.5
            listOf(LineaPedido(2, 2.5)),    // prendas no admiten decimales
            listOf(LineaPedido(4, 1.0)),    // servicio inactivo
            emptyList()
        )) {
            try {
                repo.crearPedido(3, lineas, fecha, "")
                throw AssertionError("Debia rechazar $lineas")
            } catch (e: IllegalArgumentException) {
                // esperado
            }
        }
        assertEquals(antes, repo.observarPedidos().first().size)
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
    fun pedidos_atrasados_son_los_vencidos_que_no_estan_entregados_ni_cancelados() = runBlocking {
        val hoy = LocalDate.now().toString()
        val ids = repo.observarPedidosAtrasados(hoy).first()
        assertEquals(listOf(7), ids) // el pedido de ejemplo con entrega de ayer
        // Al entregarlo deja de estar atrasado
        repo.cambiarEstado(7, EstadoPedido.ENTREGADO, null)
        assertTrue(repo.observarPedidosAtrasados(hoy).first().isEmpty())
    }

    @Test
    fun pedir_de_nuevo_carga_las_mismas_lineas_con_los_precios_actuales() = runBlocking {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        val marta = repo.obtenerUsuario(3)!!
        // Antes de repetir, el lavado en seco sube de S/ 12.00 a S/ 15.00
        db.servicioDao().actualizar(db.servicioDao().obtener(3)!!.copy(precio = 15.0))
        val vm = NuevoPedidoViewModel(repo, marta, PreferenciasStore(contexto), repetirId = 3) // pedido 3: 2 prendas de lavado en seco
        delay(800)
        assertEquals(mapOf(3 to 2.0), vm.cantidades.value)
        val jobs = listOf(launch(Dispatchers.IO) { vm.total.collect { } }, launch(Dispatchers.IO) { vm.servicios.collect { } })
        delay(500)
        assertEquals(30.0, vm.total.value, 0.001) // 2 x 15.00 (precio actual, no el congelado de 12.00)
        assertTrue(vm.aviso.value!!.contains("precios cambiaron"))
        jobs.forEach { it.cancel() }
    }

    @Test
    fun pedir_de_nuevo_omite_servicios_inactivos_y_rechaza_pedidos_ajenos() = runBlocking {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        val ana = repo.obtenerUsuario(1)!!
        db.servicioDao().cambiarActivo(2, false) // planchado ya no esta disponible
        val vm = NuevoPedidoViewModel(repo, ana, PreferenciasStore(contexto), repetirId = 1) // lavado 4 kg + planchado 3
        delay(800)
        assertEquals(mapOf(1 to 4.0), vm.cantidades.value)
        assertTrue(vm.aviso.value!!.contains("se omitieron"))

        // Un pedido de otro estudiante no se puede repetir
        val ajeno = NuevoPedidoViewModel(repo, ana, PreferenciasStore(contexto), repetirId = 2)
        delay(800)
        assertTrue(ajeno.cantidades.value.isEmpty())
        assertEquals("No se encontró el pedido que quieres repetir", ajeno.aviso.value)
    }

    @Test
    fun cambiar_de_perfil_exige_las_credenciales_del_otro_perfil() = runBlocking {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = PreferenciasStore(contexto)
        val vm = SesionViewModel(repo, prefs, FotoPerfilStore(contexto))
        val jobs = listOf(launch(Dispatchers.IO) { vm.usuarioActual.collect { } }, launch(Dispatchers.IO) { vm.errorCambio.collect { } })
        // Se inicia sesion como estudiante (Ana)
        var rolFinal: Rol? = null
        vm.login("ana@correo.test", "1234", Rol.CLIENTE) { rolFinal = it }
        delay(800)
        assertEquals(Rol.CLIENTE, rolFinal)

        // Clave incorrecta del administrador: no cambia y avisa
        var cambio: Rol? = null
        vm.cambiarPerfil("admin@lavacampus.test", "mala") { cambio = it }
        delay(600)
        assertNull(cambio)
        assertEquals("Correo o contraseña incorrectos", vm.errorCambio.value)

        // Una cuenta de estudiante no sirve para cambiar a administrador
        vm.cambiarPerfil("luis@correo.test", "1234") { cambio = it }
        delay(600)
        assertNull(cambio)
        assertEquals("Ese usuario no es Administrador", vm.errorCambio.value)

        // Credenciales correctas del administrador: cambia
        vm.cambiarPerfil("admin@lavacampus.test", "admin") { cambio = it }
        delay(800)
        assertEquals(Rol.ADMIN, cambio)
        assertEquals("ADMIN", vm.usuarioActual.value!!.rol)
        jobs.forEach { it.cancel() }
    }

    @Test
    fun las_validaciones_del_registro_rechazan_datos_invalidos() {
        assertNotNull(ValidacionRegistro.nombre(""))
        assertNotNull(ValidacionRegistro.nombre("Carla"))            // falta el apellido
        assertNotNull(ValidacionRegistro.nombre("Carla 123"))
        assertNull(ValidacionRegistro.nombre("Carla Mendoza"))
        assertNotNull(ValidacionRegistro.codigo("202312345"))        // falta la U
        assertNotNull(ValidacionRegistro.codigo("U2023123"))         // faltan numeros
        assertNull(ValidacionRegistro.codigo("u202312345"))          // minuscula: se acepta y se pasa a mayuscula
        assertNotNull(ValidacionRegistro.correo("carla@correo"))
        assertNotNull(ValidacionRegistro.correo("carla correo.test"))
        assertNull(ValidacionRegistro.correo("carla@correo.test"))
        assertNotNull(ValidacionRegistro.clave("123"))               // minimo 4
        assertNotNull(ValidacionRegistro.clave("12 34"))             // sin espacios
        assertNull(ValidacionRegistro.clave("1234"))
        assertNotNull(ValidacionRegistro.confirmacion("1234", "1235"))
        assertNull(ValidacionRegistro.confirmacion("1234", "1234"))
    }

    @Test
    fun registrar_crea_un_estudiante_nuevo_y_no_permite_correos_ni_codigos_repetidos() = runBlocking {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        val vm = SesionViewModel(repo, PreferenciasStore(contexto), FotoPerfilStore(contexto))
        val jobs = listOf(
            launch(Dispatchers.IO) { vm.erroresRegistro.collect { } },
            launch(Dispatchers.IO) { vm.usuarioActual.collect { } }
        )
        var rol: Rol? = null

        // Datos vacios: un error por cada campo
        vm.registrar("", "", "", "", "") { rol = it }
        delay(300)
        assertEquals(setOf("nombre", "codigo", "correo", "clave", "confirmacion"), vm.erroresRegistro.value.keys)
        assertNull(rol)

        // Correo que ya existe (sin importar mayusculas)
        vm.registrar("Carla Mendoza", "U202312345", "ANA@correo.test", "abcd", "abcd") { rol = it }
        delay(600)
        assertEquals("Ese correo ya está registrado", vm.erroresRegistro.value["correo"])
        assertNull(rol)

        // Codigo que ya existe
        vm.registrar("Carla Mendoza", "U202310234", "carla@correo.test", "abcd", "abcd") { rol = it }
        delay(600)
        assertEquals("Ese código ya está registrado", vm.erroresRegistro.value["codigo"])
        assertNull(rol)

        // Datos validos: se crea como estudiante, con el nombre bien escrito, y se inicia sesion
        vm.registrar("carla  mendoza", "u202312345", "Carla@Correo.test", "abcd", "abcd") { rol = it }
        delay(900)
        assertEquals(Rol.CLIENTE, rol)
        val creado = vm.usuarioActual.value!!
        assertEquals("Carla Mendoza", creado.nombre)
        assertEquals("U202312345", creado.codigo)
        assertEquals("carla@correo.test", creado.correo)
        assertEquals("CLIENTE", creado.rol)
        // Ya puede iniciar sesion con su cuenta (el correo no distingue mayusculas)
        assertEquals(creado.id, repo.login("CARLA@correo.test", "abcd")!!.id)
        jobs.forEach { it.cancel() }
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
