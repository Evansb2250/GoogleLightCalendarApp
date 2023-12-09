package com.example.googlelightcalendar.ui_components.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat
import java.util.Date

/**
 *  the Date Picker and the DatePickerDialog
 *
 *  he Date Picker composable is designed to display a full-screen view of the DatePicker. It offers a range
 *  of features, including date validation, which allows you to disable future dates or implement custom logic based on your requirements.
 *
 *  To enable date validation, you’ll need to provide your own implementation of the SelectableDates interface.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyContent() {

}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }

            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@Composable
fun MyDatePickerDialog(
    modifier: Modifier
) {
    var date by remember {
        mutableStateOf("Open date picker dialog")
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .clickable(
                enabled = true,
                onClickLabel = null,
                onClick = { showDatePicker = true },
            )
            .wrapContentWidth(),
        contentAlignment = Alignment.Center
    ) {

        OutlinedTextField(
            value = date,
            onValueChange = {},
            readOnly = true,
            modifier = modifier,
            enabled = false,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
        )
    }

    if (showDatePicker) {
        MyDatePickerDialog(
            onDateSelected = { date = it },
            onDismiss = { showDatePicker = false }
        )
    }
}