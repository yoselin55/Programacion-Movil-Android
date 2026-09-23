package com.flores.clinicasalud.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DoctorDetail : Screen("doctor_detail/{doctorId}") {
        fun createRoute(doctorId: String) = "doctor_detail/$doctorId"
    }
    object ScheduleAppointment : Screen("schedule/{doctorId}") {
        fun createRoute(doctorId: String) = "schedule/$doctorId"
    }
    object Confirmation : Screen("confirmation/{doctorId}/{date}/{time}") {
        fun createRoute(doctorId: String, date: String, time: String) =
            "confirmation/$doctorId/$date/$time"
    }
    object MyAppointments : Screen("my_appointments")
    object MedicalHistory : Screen("medical_history")
    object Profile : Screen("profile")
}

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Double,
    val reviewsCount: Int,
    val expYears: Int,
    val bio: String
)

data class AppointmentItem(
    val id: String,
    val doctorName: String,
    val specialty: String,
    val date: String,
    val time: String,
    val status: String // "Confirmada" o "Completada"
)

val sampleDoctors = listOf(
    Doctor(
        id = "1",
        name = "Dra. Ana Torres",
        specialty = "Cardióloga",
        rating = 4.9,
        reviewsCount = 128,
        expYears = 12,
        bio = "Especialista en arritmias e hipertensión, formación en la Clínica Mayo."
    ),
    Doctor(
        id = "2",
        name = "Dr. Luis Vega",
        specialty = "Pediatra",
        rating = 4.7,
        reviewsCount = 95,
        expYears = 8,
        bio = "Atención integral infantil y pediatría preventiva con amplia experiencia."
    ),
    Doctor(
        id = "3",
        name = "Dra. Rosa Díaz",
        specialty = "Dermatóloga",
        rating = 4.8,
        reviewsCount = 110,
        expYears = 10,
        bio = "Especialista en dermatología clínica y estética dermatológica."
    )
)