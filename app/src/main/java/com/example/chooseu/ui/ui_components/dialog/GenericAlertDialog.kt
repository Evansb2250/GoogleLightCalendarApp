package com.example.chooseu.ui.ui_components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true
)
@Composable
fun GenericAlertDialog(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    text: @Composable () -> Unit = {},
    icon: @Composable () -> Unit = {},
    confirmButton: @Composable () -> Unit = {},
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {  },
        confirmButton = confirmButton,
        icon = icon,
        title = title,
        text = text,
        tonalElevation = AlertDialogDefaults.TonalElevation,
    )
}


@Composable
fun ErrorAlertDialog(
    title: String,
    error: String,
    onDismiss: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    GenericAlertDialog(
        title = {
            Text(
                text = title,
            )
        },
        text = {
            Text(
                text = error,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    //clear focus on previous screen
                    focusManager.clearFocus(true)

                    onDismiss()
                },
            ) {
                Text("Retry")
            }
        }
    )
}