package com.flores.laboratorio05.model

import com.flores.laboratorio05.R

data class Student(
    val id: String,
    val name: String,
    val career: String,
    val email: String,
    val faculty: String,
    val bio: String,
    val imageRes: Int
)

val sampleStudents = listOf(
    Student(
        id = "2024-0091",
        name = "Yoselin Fabiola Flores Quispe",
        career = "Ingeniería de Software",
        email = "yoselin.flores@tecsup.edu.pe",
        faculty = "Tecnología de la Información",
        bio = "Estudiante destacada apasionada por el desarrollo de aplicaciones móviles con Jetpack Compose.",
        imageRes = R.drawable.profile_user
    ),
    Student(
        id = "2024-0102",
        name = "Carlos Andrés Ramírez Soto",
        career = "Ingeniería de Sistemas",
        email = "carlos.ramirez@tecsup.edu.pe",
        faculty = "Tecnología de la Información",
        bio = "Entusiasta de la inteligencia artificial y el desarrollo backend con Kotlin.",
        imageRes = R.drawable.student_1
    ),
    Student(
        id = "2024-0115",
        name = "María Fernanda Torres Vega",
        career = "Diseño Gráfico Digital",
        email = "maria.torres@tecsup.edu.pe",
        faculty = "Diseño y Comunicación",
        bio = "Apasionada por el diseño de interfaces y la experiencia de usuario.",
        imageRes = R.drawable.student_2
    ),
    Student(
        id = "2024-0128",
        name = "Jorge Luis Paredes Mamani",
        career = "Ingeniería Industrial",
        email = "jorge.paredes@tecsup.edu.pe",
        faculty = "Ingeniería Industrial",
        bio = "Interesado en la optimización de procesos y la gestión de calidad.",
        imageRes = R.drawable.student_3
    ),
    Student(
        id = "2024-0134",
        name = "Ana Lucía Gómez Rojas",
        career = "Administración de Empresas",
        email = "ana.gomez@tecsup.edu.pe",
        faculty = "Ciencias Empresariales",
        bio = "Enfocada en emprendimiento e innovación tecnológica aplicada a los negocios.",
        imageRes = R.drawable.student_4
    ),
    Student(
        id = "2024-0147",
        name = "Diego Alonso Huamán Castro",
        career = "Electrónica y Automatización",
        email = "diego.huaman@tecsup.edu.pe",
        faculty = "Ingeniería Electrónica",
        bio = "Apasionado por la robótica y los sistemas embebidos.",
        imageRes = R.drawable.student_5
    )
)
