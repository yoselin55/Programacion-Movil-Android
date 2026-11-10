package com.flores.mycar.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flores.mycar.R
import com.flores.mycar.domain.model.Sesion
import com.flores.mycar.ui.components.LogoMycar
import com.flores.mycar.ui.theme.Bone
import com.flores.mycar.ui.theme.FloralWhite
import com.flores.mycar.ui.theme.OliveDrab
import com.flores.mycar.ui.theme.SmokyBlack

/**
 * Pantalla de acceso: Iniciar sesion o Crear cuenta, sobre una foto de autos.
 * Es un solo acceso para todos: el rol (usuario o admin) se lee de Firestore, no se elige aqui.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onSesionIniciada: (Sesion) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    // rememberSaveable: el texto no se pierde al girar la pantalla.
    var registro by rememberSaveable { mutableStateOf(false) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf(viewModel.correoRecordado.orEmpty()) }
    var clave by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }
    var verClave by rememberSaveable { mutableStateOf(false) }
    var recordar by rememberSaveable { mutableStateOf(viewModel.correoRecordado != null) }

    LaunchedEffect(estado.sesion) {
        estado.sesion?.let {
            onSesionIniciada(it)
            viewModel.sesionConsumida()
        }
    }

    val colores = MaterialTheme.colorScheme
    val oscuro = colores.background.luminance() < 0.5f
    // Tarjeta de "vidrio": translucida, con borde fino, como en el modelo.
    val vidrio = if (oscuro) SmokyBlack.copy(alpha = 0.80f) else FloralWhite.copy(alpha = 0.92f)
    val borde = if (oscuro) Bone.copy(alpha = 0.28f) else OliveDrab.copy(alpha = 0.30f)

    fun alternar() {
        registro = !registro
        viewModel.limpiarError()
    }

    // Fuera de un Scaffold el texto no sabe de que color debe ser: se lo indicamos.
    CompositionLocalProvider(LocalContentColor provides colores.onBackground) {
        Box(modifier = modifier.fillMaxSize()) {
            FondoShowroom(oscuro)

            Text(
                "© 2026 Mycar · Proyecto académico",
                style = MaterialTheme.typography.labelLarge,
                color = colores.onSurfaceVariant,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 22.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 22.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LogoMycar(ancho = 220.dp)
                Text(
                    "Encuentra tu auto ideal",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colores.onBackground,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = vidrio,
                    modifier = Modifier.fillMaxWidth().border(1.dp, borde, RoundedCornerShape(28.dp))
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            if (registro) "Crear cuenta" else "Iniciar sesión",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            if (registro) "Únete a Mycar en un minuto" else "Accede a tu cuenta de Mycar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colores.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 20.dp)
                        )

                        if (registro) {
                            Campo(
                                valor = nombre,
                                onCambio = { nombre = it },
                                etiqueta = "Nombre",
                                icono = Icons.Filled.Badge,
                                teclado = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                        Campo(
                            valor = correo,
                            onCambio = { correo = it },
                            etiqueta = "Correo",
                            icono = Icons.Filled.Email,
                            teclado = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        Campo(
                            valor = clave,
                            onCambio = { clave = it },
                            etiqueta = "Contraseña",
                            icono = Icons.Filled.Lock,
                            teclado = KeyboardOptions(keyboardType = KeyboardType.Password),
                            oculto = !verClave,
                            alternarVisibilidad = { verClave = !verClave },
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        if (registro) {
                            Campo(
                                valor = confirmar,
                                onCambio = { confirmar = it },
                                etiqueta = "Confirmar contraseña",
                                icono = Icons.Filled.LockReset,
                                teclado = KeyboardOptions(keyboardType = KeyboardType.Password),
                                oculto = !verClave,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = recordar, onCheckedChange = { recordar = it })
                                    Text("Recordarme", style = MaterialTheme.typography.bodyMedium)
                                }
                                TextButton(onClick = { viewModel.recuperarClave(correo) }) {
                                    Text("¿Olvidaste tu contraseña?", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                        estado.error?.let { Mensaje(it, Icons.Filled.ErrorOutline, colores.error) }
                        estado.info?.let { Mensaje(it, Icons.Filled.CheckCircle, colores.primary) }

                        BotonPrincipal(
                            texto = if (registro) "Crear cuenta" else "Iniciar sesión",
                            cargando = estado.cargando,
                            oscuro = oscuro,
                            modifier = Modifier.padding(top = 18.dp)
                        ) {
                            if (registro) viewModel.registrar(nombre, correo, clave, confirmar)
                            else viewModel.iniciarSesion(correo, clave, recordar)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(Modifier.weight(1f), color = borde)
                            Text("O", style = MaterialTheme.typography.bodyMedium, color = colores.onSurfaceVariant, modifier = Modifier.padding(horizontal = 12.dp))
                            HorizontalDivider(Modifier.weight(1f), color = borde)
                        }

                        OutlinedButton(
                            onClick = ::alternar,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text(if (registro) "Ya tengo una cuenta" else "Crear una cuenta nueva")
                        }
                    }
                }

                if (!registro) {
                    Text(
                        "El administrador entra con su correo de administrador.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colores.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

            }
        }
    }
}

/** Foto de las camionetas de fondo; en modo oscuro se oscurece con la paleta. */
@Composable
private fun FondoShowroom(oscuro: Boolean) {
    val base = if (oscuro) SmokyBlack else Color(0xFFF6FBFE)
    Box(Modifier.fillMaxSize().background(base)) {
        Box(Modifier.fillMaxWidth().align(Alignment.BottomCenter).aspectRatio(767f / 1110f)) {
            Image(
                painter = painterResource(R.drawable.fondo_login),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.matchParentSize()
            )
            // La parte de arriba de la foto se funde con el color de fondo.
            Box(Modifier.matchParentSize().background(Brush.verticalGradient(0f to base, 0.22f to Color.Transparent)))
        }
        if (oscuro) {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(SmokyBlack.copy(alpha = 0.82f), SmokyBlack.copy(alpha = 0.60f)))
                )
            )
        }
    }
}

@Composable
private fun BotonPrincipal(
    texto: String,
    cargando: Boolean,
    oscuro: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Degradado con la paleta: Bone claro en oscuro, Olive Drab a Smoky Black en claro.
    val degradado = if (oscuro) Brush.horizontalGradient(listOf(Bone, FloralWhite))
    else Brush.horizontalGradient(listOf(OliveDrab, SmokyBlack))
    val colorTexto = if (oscuro) SmokyBlack else FloralWhite
    val forma = RoundedCornerShape(14.dp)

    Button(
        onClick = onClick,
        enabled = !cargando,
        shape = forma,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = colorTexto, disabledContainerColor = Color.Transparent, disabledContentColor = colorTexto),
        modifier = modifier.fillMaxWidth().height(54.dp).background(degradado, forma)
    ) {
        if (cargando) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = colorTexto)
        } else {
            Text(texto, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun Mensaje(texto: String, icono: ImageVector, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Text(texto, color = color, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun colorCampo(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    cursorColor = MaterialTheme.colorScheme.primary,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.50f)
)

@Composable
private fun Campo(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    teclado: KeyboardOptions,
    modifier: Modifier = Modifier,
    oculto: Boolean = false,
    alternarVisibilidad: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = alternarVisibilidad?.let { alternar ->
            {
                IconButton(onClick = alternar) {
                    Icon(
                        imageVector = if (oculto) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (oculto) "Mostrar contraseña" else "Ocultar contraseña"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = colorCampo(),
        visualTransformation = if (oculto) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = teclado,
        modifier = modifier.fillMaxWidth()
    )
}
