package com.reyes.tareaestudiante

import java.util.Scanner

fun main() {
    val scanner = Scanner(System.in)

    println("==========================================")
    println("   SISTEMA DE MATRÍCULA Y REGISTRO ACADÉMICO   ")
    println("==========================================")

    var aforo = 0
    while (true) {
        print("Defina el aforo máximo de estudiantes: ")
        val input = scanner.nextLine().trim()
        val convertido = input.toIntOrNull()
        if (convertido != null && convertido > 0) {
            aforo = convertido
            break
        }
        println("Error: Ingrese un aforo válido (número entero positivo).")
    }

    for (alumno in 1..aforo) {
        println("\n>>> PROCESANDO REGISTRO $alumno DE $aforo <<<")

        var nombreEstudiante = ""
        while (true) {
            print("Nombre y apellido del alumno: ")
            nombreEstudiante = scanner.nextLine().trim()
            if (nombreEstudiante.isEmpty()) {
                println("Error: El nombre no puede quedar en blanco.")
            } else if (nombreEstudiante.any { it.isDigit() }) {
                println("Error: El nombre no debe incluir números.")
            } else {
                break
            }
        }

        var turno = ""
        while (true) {
            print("Turno de estudio (Mañana / Tarde / Noche): ")
            turno = scanner.nextLine().trim().lowercase()
            if (turno == "mañana" || turno == "manana" || turno == "tarde" || turno == "noche") {
                break
            }
            println("Error: Opción no válida. Elija Mañana, Tarde o Noche.")
        }

        var categoria = ""
        while (true) {
            print("Tipo de categoría (Ordinario / Becario): ")
            categoria = scanner.nextLine().trim().lowercase()
            if (categoria == "ordinario" || categoria == "becario") {
                break
            }
            println("Error: Ingrese una categoría válida.")
        }

        var costoMatricula = 0.0
        if (categoria == "ordinario") {
            while (true) {
                print("Monto de la matrícula (S/): ")
                val input = scanner.nextLine().trim()
                val convertido = input.toDoubleOrNull()
                if (convertido != null && convertido >= 0) {
                    costoMatricula = convertido
                    break
                }
                println("Error: Ingrese un valor numérico válido para la matrícula.")
            }
        } else {
            println("-> Categoría Becario asignada: Matrícula S/ 0.00 (Automático)")
        }

        var cantidadCursos = 0
        while (true) {
            print("Número de asignaturas a inscribir: ")
            val input = scanner.nextLine().trim()
            val convertido = input.toIntOrNull()
            if (convertido != null && convertido > 0) {
                cantidadCursos = convertido
                break
            }
            println("Error: Ingrese una cantidad de cursos mayor a 0.")
        }

        var valorPorCreditos = 0.0
        while (true) {
            print("Costo por crédito (S/): ")
            val input = scanner.nextLine().trim()
            val convertido = input.toDoubleOrNull()
            if (convertido != null && convertido > 0) {
                valorPorCreditos = convertido
                break
            }
            println("Error: Ingrese un precio por crédito válido.")
        }

        var totalCreditos = 0
        var totalAPagar = 0.0
        var cursoActual = ""
        var creditosCurso = 0
        var costoCurso = 0.0
        var detalleCursos = ""

        println("\n--- DETALLE DE ASIGNATURAS ---")
        for (i in 0 until cantidadCursos) {

            while (true) {
                print("Nombre de la asignatura ${i + 1}: ")
                cursoActual = scanner.nextLine().trim()
                if (cursoActual.isEmpty()) {
                    println("Error: El nombre de la asignatura no puede estar vacío.")
                } else if (cursoActual.any { it.isDigit() }) {
                    println("Error: El nombre solo debe contener letras.")
                } else {
                    break
                }
            }

            while (true) {
                print("Número de créditos de $cursoActual: ")
                val input = scanner.nextLine().trim()
                val convertido = input.toIntOrNull()
                if (convertido != null && convertido > 0) {
                    creditosCurso = convertido
                    break
                }
                println("Error: Los créditos deben ser un número mayor a 0.")
            }

            costoCurso = creditosCurso * valorPorCreditos
            detalleCursos += String.format(
                " • %-20s | %d créditos | S/ %.2f%n",
                cursoActual, creditosCurso, costoCurso
            )

            println(String.format("   Subtotal curso: S/ %.2f", costoCurso))

            totalCreditos += creditosCurso
            totalAPagar += costoCurso
        }

        var recargoTurno = 0.0
        if (turno == "mañana" || turno == "manana") {
            recargoTurno = totalAPagar * 0.10
        } else if (turno == "tarde") {
            recargoTurno = totalAPagar * 0.15
        } else if (turno == "noche") {
            recargoTurno = totalAPagar * 0.20
        }
        totalAPagar += recargoTurno

        totalAPagar += costoMatricula

        val igv = totalAPagar * 0.18
        totalAPagar += igv

        var cargaAcademica = ""
        if (totalCreditos <= 12) {
            cargaAcademica = "Malla Regular (M.R.)"
        } else if (totalCreditos in 13..18) {
            cargaAcademica = "Carga Completa"
        } else {
            cargaAcademica = "Requiere Autorización"
        }

        val numeroCuotas: Int
        if (totalAPagar > 1500) {
            numeroCuotas = 3
        } else {
            numeroCuotas = 2
        }
        val montoPorCuota = totalAPagar / numeroCuotas

        println("\n==========================================")
        println("       RESUMEN DE MATRÍCULA - ALUMNO $alumno     ")
        println("==========================================")
        println("Estudiante: $nombreEstudiante")
        println("Cursos inscritos: $cantidadCursos")
        println("Relación de asignaturas:")
        print(detalleCursos)

        println("Total créditos acumulados: $totalCreditos")
        println(String.format("Recargo por turno (%s): S/ %.2f", turno.uppercase(), recargoTurno))
        println(String.format("Costo de matrícula: S/ %.2f", costoMatricula))
        println(String.format("IGV (18%%): S/ %.2f", igv))
        println(String.format("MONTO TOTAL A PAGAR: S/ %.2f", totalAPagar))

        println("Estado de Carga Académica: $cargaAcademica")
        println(String.format("Plan de Pago: %d cuotas de S/ %.2f", numeroCuotas, montoPorCuota))

        if (alumno < aforo) {
            var continuar = ""
            while (true) {
                print("\n¿Desea registrar al siguiente alumno? (Si/No): ")
                continuar = scanner.nextLine().trim().lowercase()
                if (continuar == "si" || continuar == "sí" || continuar == "no") {
                    break
                }
                println("Error: Ingrese únicamente 'Si' o 'No'.")
            }

            if (continuar == "no") {
                println("Proceso de matrícula terminado por el operador.")
                break
            }
        }
        println("-------------------------------------------\n")
    }

    println("=== Sistema cerrado correctamente ===")
    scanner.close()
}