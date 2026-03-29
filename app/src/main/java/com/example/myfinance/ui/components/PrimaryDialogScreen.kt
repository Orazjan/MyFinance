import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myfinance.ui.theme.MyFinanceTheme

@Composable
fun PrimaryAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    tonalElevation: Dp = 6.dp
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        modifier = modifier,
        dismissButton = dismissButton,
        icon = icon,
        title = title,
        text = text,
        tonalElevation = tonalElevation,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
@Preview(showBackground = true)
fun PrimaryAlertDialogPreview() {
    MyFinanceTheme {
        PrimaryAlertDialog(
            onDismissRequest = { },
            title = {
                Text(text = "Удалить транзакцию?")
            },
            text = {
                Text(text = "Это действие нельзя будет отменить. Вы уверены?")
            },
            confirmButton = {
                TextButton(onClick = { }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { }) {
                    Text("Отмена")
                }
            }
        )
    }
}