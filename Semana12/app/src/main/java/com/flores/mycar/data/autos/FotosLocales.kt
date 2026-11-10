package com.flores.mycar.data.autos

/** Un auto en venta con foto propia: de que marca es, cual es el modelo y su foto (res/drawable-nodpi). */
data class AutoLocal(val marca: String, val modelo: String, val foto: String, val extras: List<String> = emptyList()) {
    val url: String get() = FotosLocales.BASE + foto
}

/**
 * Los autos en venta de la app. La marca es la de la lista de NHTSA (se escribe igual que alli);
 * el modelo es el auto que aparece en la foto.
 */
object FotosLocales {

    const val BASE = "android.resource://com.flores.mycar/drawable/"

    val autos: List<AutoLocal> = listOf(
        AutoLocal("1955 Custom Belair", "Bel Air 1955", "auto_1955_custom"),
        AutoLocal("AAS", "Sportback", "auto_aas"),
        AutoLocal("AC Propulsion", "Sedán", "auto_acpropulsion"),
        AutoLocal("Acura", "TLX", "auto_acura"),
        AutoLocal("Alfa Romeo", "Stelvio Quadrifoglio", "auto_alfa"),
        AutoLocal("Allard Motor Works", "Coupé", "auto_allard"),
        AutoLocal("AM General", "Coupé deportivo", "auto_am_general"),
        AutoLocal("American Motors", "Fastback GT", "auto_american_motors"),
        AutoLocal("Ameritech Corporation", "Corvette", "auto_ameritech"),
        AutoLocal("Armbruster Stageway", "Bel Air 1957", "auto_armbruster"),
        AutoLocal("Aston Martin", "Vantage", "auto_aston_martin"),
        AutoLocal("Audi", "S4", "auto_audi"),
        AutoLocal("Autocar Ltd", "Camión de volteo", "auto_autocar"),
        AutoLocal("Autodelta USA Inc", "Coupé GT", "auto_autodelta"),
        AutoLocal("Automobili Pininfarina", "Deportivo", "auto_pininfarina"),
        AutoLocal("Avanti", "Avanti", "auto_avanti"),
        AutoLocal("BMW", "M4", "auto_bmw"),
        AutoLocal("Bugatti", "Chiron Pur Sport", "auto_bugatti"),
        AutoLocal("Chevrolet", "Corvette", "auto_chevrolet"),
        AutoLocal("Chevrolet", "Bel Air 1956", "auto_chevrolet_2"),
        AutoLocal("Ferrari", "F8 Tributo", "auto_ferrari_1"),
        AutoLocal("Ferrari", "SF90 Stradale", "auto_ferrari_2"),
        AutoLocal("Ford", "Mustang Shelby GT500", "auto_ford"),
        AutoLocal("Mercedes-Benz", "AMG GT", "auto_mercedes"),
        AutoLocal("Porsche", "911 GT3", "auto_porsche"),
        AutoLocal("Toyota", "Supra", "auto_toyota"),
        AutoLocal("Tesla", "Model S", "auto_tesla_s"),
        AutoLocal("Tesla", "Model 3", "auto_tesla_3"),
        AutoLocal("Tesla", "Semi", "auto_tesla_semi"),
        AutoLocal("Tesla", "Cybertruck", "auto_tesla_cybertruck"),
        AutoLocal("Tesla", "Model X", "auto_tesla_x"),
        AutoLocal("Tesla", "Model Y", "auto_tesla_y"),
        AutoLocal("Toyota", "Corolla", "auto_toyota_corolla"),
        AutoLocal("Toyota", "Prius", "auto_toyota_prius"),
        AutoLocal("Toyota", "Camry", "auto_toyota_camry"),
        AutoLocal("Toyota", "Mirai", "auto_toyota_mirai"),
        AutoLocal("Toyota", "Prius Prime (PHEV)", "auto_toyota_prius_prime"),
        AutoLocal("Toyota", "GR86", "auto_toyota_gr86"),
        AutoLocal("Toyota", "Crown", "auto_toyota_crown"),
        AutoLocal("Hyundai", "Sonata", "auto_hyundai_sonata"),
        AutoLocal("Hyundai", "Elantra", "auto_hyundai_elantra"),
        AutoLocal("Hyundai", "Venue", "auto_hyundai_venue"),
        AutoLocal("Hyundai", "Elantra N", "auto_hyundai_elantra_n"),
        AutoLocal("Hyundai", "Ioniq 6", "auto_hyundai_ioniq6"),
        AutoLocal("Hyundai", "Santa Cruz", "auto_hyundai_santa_cruz"),
        AutoLocal("Hyundai", "Xcient", "auto_hyundai_xcient"),
        AutoLocal("Hyundai", "Santa Fe", "auto_hyundai_santa_fe"),
        AutoLocal("Kia", "Forte", "auto_kia_forte"),
        AutoLocal("Kia", "K5", "auto_kia_k5"),
        AutoLocal("Kia", "Soul", "auto_kia_soul"),
        AutoLocal("Kia", "Sorento", "auto_kia_sorento"),
        AutoLocal("Kia", "Sportage", "auto_kia_sportage"),
        AutoLocal("Kia", "Telluride", "auto_kia_telluride"),
        AutoLocal("Kia", "Seltos", "auto_kia_seltos"),
        AutoLocal("Ford", "GT MKII", "auto_ford_gt"),
        AutoLocal("Ford", "F-150", "auto_ford_f150"),
        AutoLocal("Ford", "Ranger", "auto_ford_ranger"),
        AutoLocal("Ford", "F-250", "auto_ford_f250"),
        AutoLocal("Ford", "F-350", "auto_ford_f350"),
        AutoLocal("Ford", "F-450", "auto_ford_f450"),
        AutoLocal("Ford", "F-550", "auto_ford_f550"),
        AutoLocal("Chevrolet", "Camaro SS", "auto_chevrolet_camaro"),
        AutoLocal("Chevrolet", "Malibu", "auto_chevrolet_malibu"),
        AutoLocal("Chevrolet", "Onix", "auto_chevrolet_onix"),
        AutoLocal("Chevrolet", "Express", "auto_chevrolet_express"),
        AutoLocal("Kia", "Niro", "auto_kia_niro"),
        AutoLocal("Chevrolet", "Silverado", "auto_chevrolet_silverado"),
        AutoLocal("Chevrolet", "Colorado", "auto_chevrolet_colorado"),
        AutoLocal("BMW", "R18 Classic", "auto_bmw_r18_classic", extras = listOf("auto_bmw_r18_classic_b")),
        AutoLocal("BMW", "R18", "auto_bmw_r18", extras = listOf("auto_bmw_r18_b")),
        AutoLocal("BMW", "CE 04", "auto_bmw_ce04"),
        AutoLocal("BMW", "CE 02", "auto_bmw_ce02"),
        AutoLocal("BMW", "R18 Roctane", "auto_bmw_r18_roctane"),
        AutoLocal("BMW", "R18 R", "auto_bmw_r18r"),
        AutoLocal("Mercedes-Benz", "SL-Class", "auto_mb_sl"),
        AutoLocal("Mercedes-Benz", "E-Class", "auto_mb_e"),
        AutoLocal("Mercedes-Benz", "CLA-Class", "auto_mb_cla"),
        AutoLocal("Mercedes-Benz", "C-Class", "auto_mb_c"),
        AutoLocal("Mercedes-Benz", "S-Class", "auto_mb_s"),
        AutoLocal("Mercedes-Benz", "EQS-Class", "auto_mb_eqs"),
        AutoLocal("Mercedes-Benz", "EQE-Class", "auto_mb_eqe"),
        AutoLocal("Audi", "A4", "auto_audi_a4"),
        AutoLocal("Audi", "A6", "auto_audi_a6"),
        AutoLocal("Audi", "A3", "auto_audi_a3"),
        AutoLocal("Audi", "S6", "auto_audi_s6"),
        AutoLocal("Audi", "S8", "auto_audi_s8"),
        AutoLocal("Audi", "A5", "auto_audi_a5"),
        AutoLocal("Audi", "S5", "auto_audi_s5"),
        AutoLocal("Porsche", "Panamera", "auto_porsche_panamera"),
        AutoLocal("Porsche", "718 Boxster", "auto_porsche_boxster"),
        AutoLocal("Porsche", "718 Spyder", "auto_porsche_spyder"),
        AutoLocal("Porsche", "718 Cayman", "auto_porsche_cayman"),
        AutoLocal("Porsche", "Cayenne", "auto_porsche_cayenne"),
        AutoLocal("Porsche", "Macan", "auto_porsche_macan")
    )

    /** Logos propios de la app para marcas cuyo logo no se encuentra en internet. */
    private val logos = mapOf("audi" to "logo_audi")

    fun logoDe(marca: String): String? = logos[marca.trim().lowercase()]?.let { BASE + it }

    /** Los autos en venta de una marca (lista vacia si no tiene). */
    fun autosDe(marca: String): List<AutoLocal> =
        autos.filter { it.marca.equals(marca.trim(), ignoreCase = true) }

    /** Fotos extra de un auto (ademas de la principal), por si tiene mas de una. */
    fun extrasDe(marca: String, modelo: String): List<String> =
        autosDe(marca).firstOrNull { it.modelo.equals(modelo, ignoreCase = true) }?.extras.orEmpty().map { BASE + it }
}
