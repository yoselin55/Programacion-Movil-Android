package com.flores.lavacampus.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

// Campo de texto reutilizable: esquinas redondeadas, icono opcional y color del perfil
@Composable
fun LavaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    esClave: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text,
    mayusculas: Boolean = false, // el teclado escribe en mayusculas (codigo de estudiante)
    error: String? = null // si no es nulo, el campo se marca en rojo y se muestra el mensaje debajo
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(etiqueta) },
        leadingIcon = icono?.let { { Icon(it, contentDescription = null) } },
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        ),
        visualTransformation = if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (esClave) KeyboardType.Password else teclado,
            capitalization = if (mayusculas) KeyboardCapitalization.Characters else KeyboardCapitalization.None
        ),
        modifier = modifier
    )
}
