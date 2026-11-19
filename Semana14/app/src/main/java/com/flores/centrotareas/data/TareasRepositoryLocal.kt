package com.flores.centrotareas.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

// Commit 1: cargas simuladas con delay (contarPrimos se completa en el Commit 6)
class TareasRepositoryLocal : TareasRepository {

    // Commit 10 (Mejora 1): flow { emit } con flowOn(Dispatchers.Default): 100 vueltas de 40 ms
    override fun tareaConProgreso(): Flow<Float> = flow {
        Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
        for (i in 1..100) {
            delay(40)
            emit(i / 100f)
        }
    }.flowOn(Dispatchers.Default)

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
    // Commit 11 (Mejora 2): además informa el porcentaje cada 1 % del recorrido
    override suspend fun contarPrimos(hasta: Int, alAvanzar: (Float) -> Unit): Int =
        withContext(Dispatchers.Default) {
            var cantidad = 0
            val paso = maxOf(hasta / 100, 1)
            for (n in 2..hasta) {
                ensureActive()                    // permite cancelar a mitad del cálculo
                if (esPrimo(n)) cantidad++
                if (n % paso == 0) alAvanzar(n.toFloat() / hasta)
            }
            alAvanzar(1f)
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
