package com.flores.laboratorio05.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.flores.laboratorio05.R
import com.flores.laboratorio05.navigation.Screen
import com.flores.laboratorio05.ui.theme.CardGray
import com.flores.laboratorio05.ui.theme.IconDarkGray
import com.flores.laboratorio05.ui.theme.PrimaryPurple
import com.flores.laboratorio05.ui.theme.ScreenBackground
import com.flores.laboratorio05.ui.theme.TextDark

// Modelo de datos del alumno (declarado aquí por requerimiento del laboratorio)
data class Student(
    val id: String,
    val name: String,
    val career: String,
    val email: String,
    val faculty: String,
    val bio: String,
    val imageRes: Int
)

// Lista estática de alumnos de prueba
val sampleStudents = listOf(
    Student(
        id = "2024-0091",
        name = "Carlos Eduardo Mendoza",
        career = "Ingeniería de Software",
        email = "carlos.mendoza@tecsup.edu.pe",
        faculty = "Tecnología de la Información",
        bio = "Estudiante apasionado por el desarrollo móvil.",
        imageRes = R.drawable.student_1
    ),
    Student(
        id = "2024-0092",
        name = "María Fernanda Torres",
        career = "Diseño y Desarrollo de Software",
        email = "maria.torres@tecsup.edu.pe",
        faculty = "Tecnología de la Información",
        bio = "Especialista en experiencia de usuario UI/UX.",
        imageRes = R.drawable.student_2
    ),
    Student(
        id = "2024-0093",
        name = "Juan Pedro Gómez",
        career = "Redes y Comunicaciones",
        email = "juan.gomez@tecsup.edu.pe",
        faculty = "Ingeniería",
        bio = "Entusiasta de la ciberseguridad e infraestructura.",
        imageRes = R.drawable.student_3
    ),
    Student(
        id = "2024-0094",
        name = "Ana Lucía Benítez",
        career = "Big Data y Analítica",
        email = "ana.benitez@tecsup.edu.pe",
        faculty = "Tecnología de la Información",
        bio = "Interesada en analítica de datos e IA.",
        imageRes = R.drawable.student_4
    ),
    Student(
        id = "2024-0095",
        name = "Diego Alonso Ruiz",
        career = "Ingeniería de Software",
        email = "diego.ruiz@tecsup.edu.pe",
        faculty = "Tecnología de la Información",
        bio = "Desarrollador backend explorando soluciones nativas.",
        imageRes = R.drawable.student_5
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Directorio de Alumnos",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryPurple)
            )
        },
        containerColor = ScreenBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleStudents) { student ->
                Card(
                    onClick = {
                        navController.navigate(Screen.Detail.createRoute(student.id))
                    },
                    colors = CardDefaults.cardColors(containerColor = CardGray),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = student.imageRes),
                            contentDescription = student.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Text(
                                text = student.career,
                                fontSize = 14.sp,
                                color = PrimaryPurple
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Ver detalle",
                            tint = IconDarkGray
                        )
                    }
                }
            }
        }
    }
}
