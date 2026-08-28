package com.flores.lab02carritokotlin

fun main() {
    print("Ingrese el nombre del estudiante: ")
    val nombreEstudiante = readLine()!!

    print("Ingrese la cantidad de cursos: ")
    val cantidadCursos = readLine()!!.toInt()

    print("Ingrese el valor de cada crédito: ")
    val valorCredito = readLine()!!.toDouble()

    var totalCreditos = 0
    var totalAPagar = 0.0
    var detalleCursos = ""

    for (i in 1..cantidadCursos) {
        println("\n--- Registro del Curso $i ---")
        print("Nombre del curso: ")
        val nombreCurso = readLine()!!

        print("Cantidad de créditos: ")
        val creditosCurso = readLine()!!.toInt()

        val costoCurso = creditosCurso * valorCredito
        totalCreditos += creditosCurso
        totalAPagar += costoCurso

        detalleCursos += "$nombreCurso\t\t$creditosCurso\t\t$costoCurso\n"
    }

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
    println("Estudiante: $nombreEstudiante\n")
    println("Curso\t\tCréditos\tCosto")
    print(detalleCursos)
    println("\nCursos Matr.: $cantidadCursos")
    println("TOTAL Crédito: $totalCreditos")
    println("TOTAL A PAGAR: $totalAPagar")
    println("Carga Académica: $cargaAcademica")
    println("Forma de Pago: $cuotas CUOTAS de $montoCuota")
}