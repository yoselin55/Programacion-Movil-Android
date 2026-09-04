package com.flores.lab02carritokotlin

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
            if (nombreEstudiante.isNotEmpty() && !nombreEstudiante.any { it.isDigit() }) {
                break
            }
            println("Error: Ingrese un nombre valido.")
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
                        if (monto != null && monto >= 0) {
                            montoMatricula = monto
                            break
                        }
                        println("Error: Ingrese un monto valido.")
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
            println("Error: Ingrese un monto valido.")
        }

        var totalCreditos = 0
        var costoCursos = 0.0
        var detalleCursos = ""

        for (i in 1..cantidadCursos) {
            var nombreCurso = ""
            while (true) {
                print("\nNombre del curso $i: ")
                nombreCurso = readln().trim()
                if (nombreCurso.isNotEmpty()) break
                println("Error: Ingrese un nombre valido.")
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
            detalleCursos += " - $nombreCurso: $creditosCurso creditos (S/ %.2f)\n".format(costoCurso)
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
        println("Costo Cursos: S/ %.2f".format(costoCursos))
        println("Recargo Turno: S/ %.2f".format(recargoTurno))
        println("Matrícula: S/ %.2f".format(montoMatricula))
        println("Subtotal: S/ %.2f".format(subtotal))
        println("IGV (18%%): S/ %.2f".format(igv))
        println("TOTAL A PAGAR: S/ %.2f".format(totalAPagar))
        println("Carga Académica: $cargaAcademica")
        println("Plan de Pago: $cuotas cuotas de S/ %.2f".format(montoCuota))
    }
}