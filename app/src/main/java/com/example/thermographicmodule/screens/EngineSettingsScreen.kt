package com.example.thermographicmodule.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private const val TAG = "DebugScreen"

@Composable
fun EngineSettingsScreen(){
    Column(verticalArrangement = Arrangement.Center) {
        ElevatedButton(onClick = {
            Log.i(TAG, " called")
        },
            modifier = Modifier.fillMaxWidth()) {
            Text("Настройки НТМ Модуля")
        }
    }

}