package com.flores.laboratorio05.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.flores.laboratorio05.ui.theme.CardGray
import com.flores.laboratorio05.ui.theme.IconDarkGray
import com.flores.laboratorio05.ui.theme.PrimaryPurple
import com.flores.laboratorio05.ui.theme.ScreenBackground
import com.flores.laboratorio05.ui.theme.SoftIconPurple
import com.flores.laboratorio05.ui.theme.TextDark
import com.flores.laboratorio05.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, studentId: String) {
    // Busca al alumno correspondiente al ID recibido desde ListScreen
    val student = sampleStudents.find { it.id == studentId } ?: sampleStudents.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Expediente Académico",
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = PrimaryPurple
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBackground)
            )
        },
        containerColor = ScreenBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        color = PrimaryPurple,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-55).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = student.imageRes),
                    contentDescription = student.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(width = 3.dp, color = Color.White, shape = CircleShape)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = student.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = student.career,
                    fontSize = 15.sp,
                    color = PrimaryPurple,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardGray),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailInfoRow(
                            icon = Icons.Default.DateRange,
                            label = "ID Estudiante",
                            value = student.id
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailInfoRow(
                            icon = Icons.Default.Email,
                            label = "Correo Electrónico",
                            value = student.email
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailInfoRow(
                            icon = Icons.Default.School,
                            label = "Facultad",
                            value = student.faculty
                        )

                        HorizontalDivider(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Text(
                            text = "Biografía",
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = student.bio,
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DetailInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SoftIconPurple),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = IconDarkGray)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, color = TextMuted, fontSize = 13.sp)
            Text(text = value, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
