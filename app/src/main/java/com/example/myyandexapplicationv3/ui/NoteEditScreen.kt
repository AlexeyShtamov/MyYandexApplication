package com.example.myyandexapplicationv3.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationyandex.model.Priority
import com.example.myyandexapplicationv3.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun NewText(){
    Text(text = "Lexa", color = Color.Red)
}

@Composable
fun SimpleLayout() {
    Column{

        val title = remember { mutableStateOf("") }
        TextField(
            value = title.value,
            onValueChange = {newText -> title.value = newText},
            label = { Text(stringResource(R.string.note_name))},
            placeholder = { Text(stringResource(R.string.note_name)) },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            singleLine = true)

        val text = remember { mutableStateOf("") }
        TextField(
            value = text.value,
            onValueChange = {newText -> text.value = newText},
            label = { Text(stringResource(R.string.note_text)) },
            placeholder = { Text(stringResource(R.string.note_text)) },
            modifier = Modifier.fillMaxWidth().padding(16.dp))
    }
}

@Composable
fun SelfDestruction(){

    val checkedState = remember { mutableStateOf(true) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it }
        )
        Text(stringResource(R.string.self_destructive), fontSize = 15.sp, modifier = Modifier.padding(4.dp))
    }

    if (checkedState.value){
        DatePickerButton()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerButton() {
    var showDatePicker by remember { mutableStateOf(false) }

    var selectedDate by remember { mutableStateOf("_____________") }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Button(onClick = { showDatePicker = true }) {
            Text(text = stringResource(R.string.choose_date))
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = { showDatePicker = false }) {
                        Text("ОК")
                    }
                }
            ) {
                val datePickerState = rememberDatePickerState()

                DatePicker(
                    state = datePickerState
                )

                val selectedMillis = datePickerState.selectedDateMillis
                if (selectedMillis != null) {
                    selectedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        .format(Date(selectedMillis))
                }
            }
        }
        Text("Выбрано: ${selectedDate}", fontSize = 18.sp, modifier = Modifier.padding(top = 5.dp))
    }
}

@Composable
fun ColorPicker(
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Column {
        Text(stringResource(R.string.note_color), fontSize = 20.sp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(color, shape = RoundedCornerShape(8.dp))
                        .clickable { onColorSelected(color) }
                        .border(
                            width = if (color == selectedColor) 3.dp else 0.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (color == selectedColor) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.note_color),
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrioritySelector(
    priorities: List<Priority>,
    selectedPriority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {
    Column {
        Text("Важность:", fontSize = 20.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            priorities.forEach { priority ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (priority == selectedPriority) Color(0xFFBDAAE0) else Color.White)
                        .border(
                            width = if (priority == selectedPriority) 1.dp else 0.dp,
                            color = if (priority == selectedPriority) Color(0xFF8B64D5) else Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onPrioritySelected(priority) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (priority) {
                            Priority.LOW -> "\uD83D\uDE34 Низкий"
                            Priority.NORMAL -> "\uD83D\uDE4F Средний"
                            Priority.HIGH -> "❗ Высокий"
                        },
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }

}