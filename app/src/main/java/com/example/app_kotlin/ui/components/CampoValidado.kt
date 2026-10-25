package com.example.app_kotlin.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.app_kotlin.viewmodel.CampoState

@Composable
fun CampoValidado(
    campo: CampoState,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    placeholder: String? = null,
    maxCaracteres: Int? = null,
    esMultilinea: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = campo.valor,
            onValueChange = {
                if (maxCaracteres == null || it.length <= maxCaracteres) {
                    onValueChange(it)
                }
            },
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            leadingIcon = icono?.let { { Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.primary) } },
            trailingIcon = when {
                campo.error != null -> {
                    { Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error) }
                }
                campo.esValido -> {
                    { Icon(Icons.Default.CheckCircle, contentDescription = "Válido", tint = com.example.app_kotlin.ui.theme.SuccessEmerald) }
                }
                else -> null
            },
            isError = campo.error != null,
            singleLine = !esMultilinea,
            maxLines = if (esMultilinea) 5 else 1,
            keyboardOptions = keyboardOptions,
            enabled = enabled,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 2.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = campo.error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.weight(1f)
            ) {
                campo.error?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (maxCaracteres != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${campo.valor.length}/$maxCaracteres",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}