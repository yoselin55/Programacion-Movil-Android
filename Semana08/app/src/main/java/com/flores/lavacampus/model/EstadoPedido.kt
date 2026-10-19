package com.flores.lavacampus.model

import androidx.compose.ui.graphics.Color

// Estados del pedido. En la base se guarda como String (name)
enum class EstadoPedido(val texto: String, val color: Color) {
    RECIBIDO("Recibido", Color(0xFFD97706)),
    EN_PROCESO("En proceso", Color(0xFF2563EB)),
    LISTO("Listo", Color(0xFF16A34A)),
    ENTREGADO("Entregado", Color(0xFF475569)),
    CANCELADO("Cancelado", Color(0xFFDC2626));

    // Solo se avanza un paso a la vez; ENTREGADO y CANCELADO son finales
    fun siguiente(): EstadoPedido? = when (this) {
        RECIBIDO -> EN_PROCESO
        EN_PROCESO -> LISTO
        LISTO -> ENTREGADO
        else -> null
    }
}
