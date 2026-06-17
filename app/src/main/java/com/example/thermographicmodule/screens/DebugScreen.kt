package com.example.thermographicmodule.screens

import android.util.Log
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.thermographicmodule.main.MainViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.dimensionResource
import com.example.thermographicmodule.R


private const val TAG = "DebugScreen"

@Composable
fun DebugScreen(viewModel: MainViewModel){



    Column(verticalArrangement = Arrangement.Center) {
        ElevatedButton(onClick = {
            viewModel.clearLog()
            Log.i(TAG, " called")
        },
            modifier = Modifier.fillMaxWidth()) {
            Text("Очистить логи")

        }
        Column(modifier=Modifier
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.padding_small))) {
            Text(viewModel.log)
        }

    }

}