
package com.flores.lab02carritokotlin

import java.util.Locale

fun main() {
    println("=== Sistema de Matricula Universitaria ===")

    var aforo = 0
    while (true) {
        print("Ingrese el aforo (cantidad maxima de alumnos a matricular): ")
        val input = readln().trim()
        val num = input.toIntOrNull()
        if (num != null && num > 0) {
            aforo = num
            break
        }
        println("Error: Ingrese un numero valido mayor a 0.")
    }

    for (alumno in 1..aforo) {
        println("\n--- Registrando Alumno $alumno de $aforo ---")

        var nombreEstudiante = ""
        while (true) {
            print("Nombre del estudiante: ")
            nombreEstudiante = readln().trim()
            if (nombreEstudiante.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$"))) {
                break
            }
            println("Error: Ingrese un nombre valido (solo letras).")
        }

        var turno = ""
        var porcentajeTurno = 0.0
        while (true) {
            print("Turno (Mañana, Tarde, Noche): ")
            val inputTurno = readln().trim()
            when (inputTurno.lowercase()) {
                "mañana", "manana" -> {
                    turno = "Mañana"
                    porcentajeTurno = 0.10
                    break
                }
                "tarde" -> {
                    turno = "Tarde"
                    porcentajeTurno = 0.15
                    break
                }
                "noche" -> {
                    turno = "Noche"
                    porcentajeTurno = 0.20
                    break
                }
                else -> println("Error: Ingrese un turno valido.")
            }
        }

        var categoria = ""
        var montoMatricula = 0.0
        while (true) {
            print("Categoria (Ordinario, Becario): ")
            val inputCat = readln().trim()
            when (inputCat.lowercase()) {
                "ordinario" -> {
                    categoria = "Ordinario"
                    while (true) {
                        print("Ingrese el costo de matricula (S/): ")
                        val inputMat = readln().trim()
                        val monto = inputMat.toDoubleOrNull()
                        if (monto != null && monto > 0) {
                            montoMatricula = monto
                            break
                        }
                        println("Error: Ingrese un monto valido mayor a 0.")
                    }
                    break
                }
                "becario" -> {
                    categoria = "Becario"
                    montoMatricula = 0.0
                    break
                }
                else -> println("Error: Ingrese una categoria valida.")
            }
        }

        var cantidadCursos = 0
        while (true) {
            print("Cantidad de cursos a llevar: ")
            val inputCursos = readln().trim()
            val numCursos = inputCursos.toIntOrNull()
            if (numCursos != null && numCursos > 0) {
                cantidadCursos = numCursos
                break
            }
            println("Error: Ingrese un numero valido mayor a 0.")
        }

        var valorCredito = 0.0
        while (true) {
            print("Valor de cada credito (S/): ")
            val inputCred = readln().trim()
            val valCred = inputCred.toDoubleOrNull()
            if (valCred != null && valCred > 0) {
                valorCredito = valCred
                break
            }
            println("Error: Ingrese un monto valido mayor a 0.")
        }

        var totalCreditos = 0
        var costoCursos = 0.0
        var detalleCursos = ""

        for (i in 1..cantidadCursos) {
            var nombreCurso = ""
            while (true) {
                print("\nNombre del curso $i: ")
                nombreCurso = readln().trim()
                if (nombreCurso.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\\s]+$"))) {
                    break
                }
                println("Error: Ingrese un nombre de curso valido (solo letras, numeros y espacios).")
            }

            var creditosCurso = 0
            while (true) {
                print("Cantidad de creditos de $nombreCurso: ")
                val inputCreds = readln().trim()
                val numCreds = inputCreds.toIntOrNull()
                if (numCreds != null && numCreds > 0) {
                    creditosCurso = numCreds
                    break
                }
                println("Error: Ingrese un numero valido mayor a 0.")
            }

            val costoCurso = creditosCurso * valorCredito
            totalCreditos += creditosCurso
            costoCursos += costoCurso
            detalleCursos += String.format(Locale.US, " - %s: %d creditos (S/ %.2f)\n", nombreCurso, creditosCurso, costoCurso)
        }

        val recargoTurno = costoCursos * porcentajeTurno
        val subtotal = costoCursos + recargoTurno + montoMatricula
        val igv = subtotal * 0.18
        val totalAPagar = subtotal + igv

        val cargaAcademica = when {
            totalCreditos <= 12 -> "M.R."
            totalCreditos <= 18 -> "Carga Completa"
            else -> "Req. Autoriz."
        }

        val cuotas = if (totalAPagar > 1500) 3 else 2
        val montoCuota = totalAPagar / cuotas

        println("\n=== RESUMEN DE MATRÍCULA ===")
        println("Estudiante: $nombreEstudiante")
        println("Categoría: $categoria")
        println("Turno: $turno (+${(porcentajeTurno * 100).toInt()}%)")
        println("Detalle de Cursos:")
        print(detalleCursos)
        println("Total Créditos: $totalCreditos")
        println(String.format(Locale.US, "Costo Cursos: S/ %.2f", costoCursos))
        println(String.format(Locale.US, "Recargo Turno: S/ %.2f", recargoTurno))
        println(String.format(Locale.US, "Matrícula: S/ %.2f", montoMatricula))
        println(String.format(Locale.US, "Subtotal: S/ %.2f", subtotal))
        println(String.format(Locale.US, "IGV (18%%): S/ %.2f", igv))
        println(String.format(Locale.US, "TOTAL A PAGAR: S/ %.2f", totalAPagar))
        println("Carga Académica: $cargaAcademica")

        println("\n--- CALENDARIO DE PAGOS ($cuotas Cuotas) ---")
        for (c in 1..cuotas) {
            val etiquetaCuota = when (c) {
                1 -> "Cuota 1 (Pago Inicial / Matricula)"
                2 -> "Cuota 2 (Mes 1)"
                3 -> "Cuota 3 (Mes 2)"
                else -> "Cuota $c"
            }
            println(String.format(Locale.US, " * %s: S/ %.2f", etiquetaCuota, montoCuota))
        }

        if (alumno < aforo) {
            var continuar = ""
            while (true) {
                print("\n¿Desea matricular a otro alumno? (Si/No): ")
                continuar = readln().trim().lowercase()
                if (continuar == "si" || continuar == "sí" || continuar == "no") {
                    break
                }
                println("Error: Por favor, responda 'Si' o 'No'.")
            }

            if (continuar == "no") {
                println("Finalizando las matriculas por decision del usuario...")
                break
            }
        }
        println("-------------------------------------------\n")
    }
}