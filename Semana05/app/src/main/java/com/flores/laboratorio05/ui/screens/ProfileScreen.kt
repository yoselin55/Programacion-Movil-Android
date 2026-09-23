package com.flores.laboratorio05.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.flores.laboratorio05.R
import com.flores.laboratorio05.navigation.Screen
import com.flores.laboratorio05.ui.theme.HeaderGradientEnd
import com.flores.laboratorio05.ui.theme.HeaderGradientStart
import com.flores.laboratorio05.ui.theme.IconDarkGray
import com.flores.laboratorio05.ui.theme.PrimaryPurple
import com.flores.laboratorio05.ui.theme.ScreenBackground
import com.flores.laboratorio05.ui.theme.SoftIconPurple
import com.flores.laboratorio05.ui.theme.SoftRedBg
import com.flores.laboratorio05.ui.theme.TextDark
import com.flores.laboratorio05.ui.theme.TextMuted
import com.flores.laboratorio05.ui.theme.TextRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración de Perfil",
                        color = TextDark,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextDark
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
                .background(ScreenBackground)
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(HeaderGradientStart, Color(0xFF6E4D85), HeaderGradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_user),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(width = 3.dp, color = Color.White, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Yoselin Fabiola Flores Quispe",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "INFORMACIÓN PERSONAL",
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ProfileInfoItem(
                    icon = Icons.Default.Person,
                    label = "Nombre Completo",
                    value = "Yoselin Fabiola Flores Quispe"
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileInfoItem(
                    icon = Icons.Default.Email,
                    label = "Correo",
                    value = "yoselin.flores@tecsup.edu.pe"
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileInfoItem(
                    icon = Icons.Default.Phone,
                    label = "Teléfono",
                    value = "+51 987 654 321"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ACADÉMICO",
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ProfileInfoItem(
                    icon = Icons.Default.School,
                    label = "Carrera",
                    value = "Ingeniería de Software"
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProfileInfoItem(
                    icon = Icons.Default.DateRange,
                    label = "Ciclo Actual",
                    value = "VI Ciclo"
                )

                Spacer(modifier = Modifier.weight(1f))

                Card(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    colors = CardDefaults.cardColors(containerColor = SoftRedBg),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = TextRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cerrar Sesión",
                            color = TextRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(icon: ImageVector, label: String, value: String) {
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
