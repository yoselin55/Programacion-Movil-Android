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

    for (i in 1..cantidadCursos) {
        println("\n--- Registro del Curso $i ---")
        print("Nombre del curso: ")
        val nombreCurso = readLine()!!

        print("Cantidad de créditos: ")
        val creditosCurso = readLine()!!.toInt()

        val costoCurso = creditosCurso * valorCredito
        totalCreditos += creditosCurso
        totalAPagar += costoCurso
    }

    val cargaAcademica = if (totalCreditos <= 12) {
        "Malla Regular"
    } else if (totalCreditos <= 18) {
        "Carga Completa"
    } else {
        "Requiere Autorizacion"
    }

    val cuotas = if (totalAPagar > 1500) 3 else 2
    val montoCuota = totalAPagar / cuotas

    println("\nCalculos realizados correctamente.")
}