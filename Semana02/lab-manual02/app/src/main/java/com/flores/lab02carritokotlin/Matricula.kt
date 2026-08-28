package com.flores.lab02carritokotlin

fun main() {
    print("Ingrese el nombre del estudiante: ")
    val nombreEstudiante = readLine()!!

    print("Ingrese la cantidad de cursos: ")
    val cantidadCursos = readLine()!!.toInt()

    print("Ingrese el valor de cada crédito: ")
    val valorCredito = readLine()!!.toDouble()

    for (i in 1..cantidadCursos) {
        println("\n--- Registro del Curso $i ---")
        print("Nombre del curso: ")
        val nombreCurso = readLine()!!

        print("Cantidad de créditos: ")
        val creditosCurso = readLine()!!.toInt()

        println("-> Registrado: $nombreCurso ($creditosCurso créditos)")
    }

    println("\n>>> Datos ingresados correctamente. <<<")
}