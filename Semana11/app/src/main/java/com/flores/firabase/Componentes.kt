package com.flores.firabase

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.flores.firabase.ui.theme.GradienteFin
import com.flores.firabase.ui.theme.GradienteInicio
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException

const val AUTORA = "Yoselin Flores Quispe"

/** Pone los íconos de la barra de estado oscuros (fondo claro) o claros (fondo oscuro). */
@Composable
fun IconosBarraEstado(oscuros: Boolean) {
    val vista = LocalView.current
    if (!vista.isInEditMode) {
        SideEffect {
            val ventana = (vista.context as Activity).window
            WindowCompat.getInsetsController(ventana, vista).isAppearanceLightStatusBars = oscuros
        }
    }
}

/** Cabecera roja con degradado que ocupa también la barra de estado. */
@Composable
fun EncabezadoDegradado(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(GradienteInicio, GradienteFin)),
                RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
            )
            .statusBarsPadding()
    ) {
        content()
    }
}

/** Fondo claro con ondas rojas arriba a la derecha y abajo, como en el diseño. */
@Composable
fun FondoOndas(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val degradado = Brush.linearGradient(
                listOf(GradienteInicio, GradienteFin),
                start = Offset(w, 0f),
                end = Offset(w * 0.4f, h * 0.2f)
            )
            // Onda superior derecha
            val arriba = Path().apply {
                moveTo(w * 0.58f, 0f)
                cubicTo(w * 0.78f, h * 0.01f, w * 0.97f, h * 0.06f, w, h * 0.16f)
                lineTo(w, 0f)
                close()
            }
            drawPath(arriba, degradado)
            // Onda inferior (una clara detrás y otra intensa delante)
            val abajoClara = Path().apply {
                moveTo(0f, h * 0.9f)
                cubicTo(w * 0.3f, h * 0.84f, w * 0.7f, h * 0.97f, w, h * 0.88f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(abajoClara, GradienteFin.copy(alpha = 0.35f))
            val abajo = Path().apply {
                moveTo(0f, h * 0.95f)
                cubicTo(w * 0.35f, h * 0.9f, w * 0.65f, h * 1.0f, w, h * 0.94f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                abajo,
                Brush.horizontalGradient(listOf(GradienteInicio, GradienteFin))
            )
        }
        content()
        // Firma fija sobre la onda inferior
        Text(
            "Desarrollado por $AUTORA",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 12.dp)
        )
    }
}

/** Logo: birrete (Material Design Icons) en un cuadrado redondeado. */
@Composable
fun LogoApp(
    modifier: Modifier = Modifier,
    tamano: Dp = 84.dp,
    rojo: Boolean = false
) {
    val forma = RoundedCornerShape(26.dp)
    val fondo = if (rojo) {
        Modifier.background(Brush.verticalGradient(listOf(GradienteFin, GradienteInicio)), forma)
    } else {
        Modifier.background(MaterialTheme.colorScheme.surface, forma)
    }
    Box(
        modifier
            .size(tamano)
            .shadow(10.dp, forma, clip = false)
            .clip(forma)
            .then(fondo),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(R.drawable.ic_school),
            contentDescription = "Mis Cursos",
            tint = if (rojo) Color.White else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(tamano * 0.58f)
        )
    }
}

/** Botón circular con la flecha para volver. */
@Composable
fun BotonVolver(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
        modifier = modifier.size(46.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Volver")
        }
    }
}

/** Campo de texto blanco, redondeado y con sombra, con ícono a la izquierda. */
@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    @DrawableRes icono: Int? = null,
    tipo: KeyboardType = KeyboardType.Text,
    esClave: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    onAccion: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }
    val forma = RoundedCornerShape(16.dp)

    val inicio: (@Composable () -> Unit)? = if (icono != null) {
        { Icon(painterResource(icono), contentDescription = null) }
    } else null

    val fin: (@Composable () -> Unit)? = if (esClave) {
        {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    painterResource(if (visible) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        }
    } else null

    TextField(
        value = valor,
        onValueChange = onCambio,
        placeholder = { Text(etiqueta) },
        singleLine = true,
        shape = forma,
        leadingIcon = inicio,
        trailingIcon = fin,
        visualTransformation = if (esClave && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (esClave) KeyboardType.Password else tipo,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = { onAccion() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, forma, clip = false)
    )
}

/** Botón rojo con degradado. Con [flecha] muestra una flecha a la derecha. */
@Composable
fun BotonPrincipal(
    texto: String,
    cargando: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    flecha: Boolean = false
) {
    val forma = RoundedCornerShape(18.dp)
    Button(
        onClick = onClick,
        enabled = !cargando,
        shape = forma,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(8.dp, forma, clip = false)
            .background(
                Brush.horizontalGradient(listOf(GradienteFin, GradienteInicio)),
                forma
            )
    ) {
        if (cargando) {
            CircularProgressIndicator(
                Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current
            )
        } else {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(texto, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                if (flecha) {
                    Icon(
                        painterResource(R.drawable.ic_arrow_forward),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

@Composable
fun MensajeError(texto: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            texto,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun PieAutora(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            "Desarrollado por",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            AUTORA,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}

/** Traduce los errores de Firebase Authentication a mensajes claros en español. */
fun mensajeAuth(e: Exception?): String = when {
    e is FirebaseNetworkException -> "Sin conexión a Internet. Revisa tu red."
    e is FirebaseAuthException -> when (e.errorCode) {
        "ERROR_INVALID_CREDENTIAL", "ERROR_WRONG_PASSWORD", "ERROR_USER_NOT_FOUND" ->
            "Correo o contraseña incorrectos."
        "ERROR_INVALID_EMAIL" -> "El correo no tiene un formato válido."
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Ese correo ya está registrado."
        "ERROR_WEAK_PASSWORD" -> "La contraseña es muy débil."
        "ERROR_USER_DISABLED" -> "Esta cuenta fue deshabilitada."
        "ERROR_OPERATION_NOT_ALLOWED" ->
            "El acceso con correo no está habilitado en Firebase."
        else -> e.localizedMessage ?: "Ocurrió un error."
    }
    else -> e?.localizedMessage ?: "Ocurrió un error."
}
