package com.flores.tecsupfit.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flores.tecsupfit.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterClick: (User) -> Unit,
    onBackClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Indica si el usuario ya escribió en el campo (para no mostrar errores al abrir la pantalla)
    var nameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmTouched by remember { mutableStateOf(false) }

    // Validación mínima: solo se pide que no estén vacíos (acepta cualquier dato de prueba)
    val nameError = if (fullName.isBlank()) "Ingresa un nombre" else null
    val emailError = if (email.isBlank()) "Ingresa un correo" else null
    val passwordError = if (password.isEmpty()) "Ingresa una contraseña" else null
    // Confirmar contraseña es opcional
    val confirmError: String? = null

    // El botón se habilita cuando nombre, correo y contraseña tienen algo escrito
    val isFormValid = nameError == null && emailError == null &&
            passwordError == null && confirmError == null

    val primary = Color(0xFF0F6A52)
    val visualTransformation =
        if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text("Regístrate en TECSUP Fit", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Completa tus datos para empezar", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(20.dp))

            // Nombre completo
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; nameTouched = true },
                label = { Text("Nombre completo") },
                singleLine = true,
                isError = nameTouched && nameError != null,
                supportingText = { if (nameTouched) nameError?.let { Text(it) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailTouched = true },
                label = { Text("Correo electrónico") },
                singleLine = true,
                isError = emailTouched && emailError != null,
                supportingText = { if (emailTouched) emailError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Contraseña con icono para mostrar/ocultar
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordTouched = true },
                label = { Text("Contraseña") },
                singleLine = true,
                isError = passwordTouched && passwordError != null,
                supportingText = { if (passwordTouched) passwordError?.let { Text(it) } },
                visualTransformation = visualTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Confirmar contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmTouched = true },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                isError = confirmTouched && confirmError != null,
                supportingText = { if (confirmTouched) confirmError?.let { Text(it) } },
                visualTransformation = visualTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onRegisterClick(
                        User(
                            fullName = fullName.trim(),
                            email = email.trim(),
                            password = password
                        )
                    )
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Crear cuenta", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
