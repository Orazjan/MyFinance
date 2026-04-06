package com.atnzvdev.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ProfileButton(
    icon: ImageVector,
    text: String?,
    onClick: () -> Unit,
    secondIcon: ImageVector? = Icons.TwoTone.ChevronRight
) {
    PrimaryButton(text = "", onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.width(40.dp)) {

                Icon(
                    icon,
                    contentDescription = text,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            Box(Modifier.weight(1f)) {
                PrimaryText(
                    text,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)

                )
            }
            Box(Modifier.width(40.dp)) {

                if (secondIcon != null) {
                    Icon(
                        secondIcon,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }

        }
    }
}