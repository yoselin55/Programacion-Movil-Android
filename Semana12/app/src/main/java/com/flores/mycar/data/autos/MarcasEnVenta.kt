package com.flores.mycar.data.autos

/**
 * Las marcas de la app (de las 195 que trae la API): 10 principales con hasta 8 autos del año indicado,
 * mas Automobili Pininfarina. Son las unicas que se muestran en Marcas y en Autos en venta.
 */
object MarcasEnVenta {

    /** Año de los autos que se piden a la API. */
    const val ANIO = 2024

    /** Cuantos autos como maximo tiene cada marca en venta. */
    const val MAX_AUTOS = 8

    val principales: List<String> = listOf(
        "Tesla", "Toyota", "Hyundai", "Kia", "Ford",
        "Chevrolet", "BMW", "Mercedes-Benz", "Audi", "Porsche"
    )

    /** Marcas extra que tambien estan en venta (tienen su propio auto con foto). */
    val extras: List<String> = listOf("Automobili Pininfarina")

    /** Autos que salen como "destacados" en el inicio, en este orden (marca, auto). */
    val destacados: List<Pair<String, String>> = listOf(
        "Ford" to "F-150",
        "Kia" to "Niro",
        "Hyundai" to "Elantra N",
        "Tesla" to "Model X",
        "Automobili Pininfarina" to "Deportivo"
    )

    fun esPrincipal(marca: String): Boolean = principales.any { it.equals(marca.trim(), ignoreCase = true) }

    fun enVenta(marca: String): Boolean = esPrincipal(marca) || extras.any { it.equals(marca.trim(), ignoreCase = true) }

    /** Para ordenar: las 10 marcas en el orden de la lista. */
    fun orden(marca: String): Int {
        val i = principales.indexOfFirst { it.equals(marca.trim(), ignoreCase = true) }
        if (i >= 0) return i
        val e = extras.indexOfFirst { it.equals(marca.trim(), ignoreCase = true) }
        return if (e >= 0) principales.size + e else principales.size + extras.size
    }
}

/** Deja solo nombres de autos: sin motos de BMW (ej. "F 800 GS"), ni nombres raros que empiezan con simbolos. */
fun nombresDeAutos(nombres: List<String>): List<String> = nombres
    .map { it.trim() }
    .filter { it.isNotEmpty() && it.first().isLetterOrDigit() && !Regex("^[A-Za-z] \\d{3,4}( .*)?$").matches(it) }
    .distinct()
