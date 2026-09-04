package com.flores.lab02carritokotlin

fun main() {
    print("Ingrese el nombre del estudiante: ")
    val nombreEstudiante = readLine()!!

    println("\nSeleccione la categoría del estudiante:")
    println("1. Ordinario")
    println("2. Becado")
    print("Opción: ")
    val opcionCategoria = readLine()!!.toInt()

    var montoMatricula = 0.0
    var categoriaTexto = "Becado"

    if (opcionCategoria == 1) {
        categoriaTexto = "Ordinario"
        print("Ingrese el monto de la matrícula: ")
        montoMatricula = readLine()!!.toDouble()
    } else {
        println("Categoría Becado seleccionada. Matrícula: S/ 0.00 (Automático)")
    }

    println("\nSeleccione el turno:")
    println("1. Mañana (+10%)")
    println("2. Tarde (+15%)")
    println("3. Noche (+20%)")
    print("Opción: ")
    val opcionTurno = readLine()!!.toInt()

    val (turnoTexto, porcentajeTurno) = when (opcionTurno) {
        1 -> Pair("Mañana", 0.10)
        2 -> Pair("Tarde", 0.15)
        3 -> Pair("Noche", 0.20)
        else -> Pair("Mañana", 0.10)
    }

    print("\nIngrese la cantidad de cursos: ")
    val cantidadCursos = readLine()!!.toInt()

    print("Ingrese el valor de cada crédito: ")
    val valorCredito = readLine()!!.toDouble()

    var totalCreditos = 0
    var costoCursos = 0.0
    var detalleCursos = ""

    for (i in 1..cantidadCursos) {
        println("\n--- Registro del Curso $i ---")
        print("Nombre del curso: ")
        val nombreCurso = readLine()!!

        print("Cantidad de créditos: ")
        val creditosCurso = readLine()!!.toInt()

        val costoCurso = creditosCurso * valorCredito
        totalCreditos += creditosCurso
        costoCursos += costoCurso

        detalleCursos += "$nombreCurso\t\t$creditosCurso\t\t$costoCurso\n"
    }

    val recargoTurno = costoCursos * porcentajeTurno

    // AGREGADO: Desglose de Subtotal, IGV y Total Final
    val subtotal = costoCursos + recargoTurno + montoMatricula
    val igv = subtotal * 0.18
    val totalAPagar = subtotal + igv

    val cargaAcademica = if (totalCreditos <= 12) {
        "M.R."
    } else if (totalCreditos <= 18) {
        "Carga Completa"
    } else {
        "Req. Autoriz."
    }

    val cuotas = if (totalAPagar > 1500) 3 else 2
    val montoCuota = totalAPagar / cuotas

    println("\n--- RESULTADO FINAL ---")
    println("Estudiante: $nombreEstudiante")
    println("Categoría: $categoriaTexto")
    println("Turno: $turnoTexto (Recargo ${(porcentajeTurno * 100).toInt()}%)\n")
    println("Curso\t\tCréditos\tCosto")
    print(detalleCursos)
    println("\nCursos Matr.: $cantidadCursos")
    println("TOTAL Crédito: $totalCreditos")
    println("Costo Cursos: S/ $costoCursos")
    println("Recargo Turno: S/ $recargoTurno")
    println("Matrícula: S/ $montoMatricula")
    println("Subtotal: S/ $subtotal")
    println("IGV (18%): S/ $igv")
    println("TOTAL A PAGAR: S/ $totalAPagar")
    println("Carga Académica: $cargaAcademica")
    println("Forma de Pago: $cuotas CUOTAS de S/ $montoCuota")
}