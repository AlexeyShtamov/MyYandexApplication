package com.example.myyandexapplicationv3

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplicationyandex.model.Priority
import com.example.myyandexapplicationv3.ui.ColorPicker
import com.example.myyandexapplicationv3.ui.PrioritySelector
import com.example.myyandexapplicationv3.ui.SelfDestruction
import com.example.myyandexapplicationv3.ui.SimpleLayout

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val colors = listOf(
                Color(0xFFFFC0CB),
                Color(0xFFFFE0B2),
                Color(0xFFB2FFB2),
                Color(0xFFADD8E6),
                Color(0xFFFFFF99),)

            val priorities = listOf(Priority.LOW, Priority.NORMAL, Priority.HIGH)

            var selectedColor by remember { mutableStateOf(colors[0]) }
            var selectedPriority by remember { mutableStateOf(Priority.NORMAL) }

            Column (
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ){
                SimpleLayout()
                SelfDestruction()
                ColorPicker(
                    colors = colors,
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )
                PrioritySelector(
                    priorities = priorities,
                    selectedPriority = selectedPriority,
                    onPrioritySelected = { selectedPriority = it }
                )
            }

        }
    }
}







