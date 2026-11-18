package com.flores.centrotareas.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.IOException

// Commit 1: cargas simuladas con delay (contarPrimos se completa en el Commit 6)
class TareasRepositoryLocal : TareasRepository {

    override suspend fun cargarPerfil(): String {
        delay(2000)
        return "Ana Torres (ficticia)"
    }

    override suspend fun cargarPedidos(simularError: Boolean): List<String> {
        delay(3000)
        if (simularError) throw IOException("Sin conexión")
        return listOf("Pedido 101", "Pedido 102", "Pedido 103")
    }

    // Commit 6 (Paso 5): cálculo pesado en Dispatchers.Default y cancelable
    override suspend fun contarPrimos(hasta: Int): Int = withContext(Dispatchers.Default) {
        var cantidad = 0
        for (n in 2..hasta) {
            ensureActive()                    // permite cancelar a mitad del cálculo
            if (esPrimo(n)) cantidad++
        }
        cantidad
    }

    private fun esPrimo(n: Int): Boolean {
        var d = 2
        while (d.toLong() * d <= n) {         // evita desbordes sin calcular la raíz
            if (n % d == 0) return false
            d++
        }
        return true
    }
}
