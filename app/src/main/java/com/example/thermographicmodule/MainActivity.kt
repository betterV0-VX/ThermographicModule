package com.example.thermographicmodule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thermographicmodule.data.UsbRepository
import com.example.thermographicmodule.data.UsbSerialManager
import com.example.thermographicmodule.main.MainViewModel
import com.example.thermographicmodule.main.MobileScreen
import com.example.thermographicmodule.ui.theme.ThermographicModuleTheme

class MainActivity : ComponentActivity() {

    private lateinit var usbSerialManager: UsbSerialManager
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(usbSerialManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        usbSerialManager = UsbSerialManager(context=this)

        enableEdgeToEdge()
        setContent {
            ThermographicModuleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding->
                    MobileScreen(viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshDevices()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnect()
        usbSerialManager.disconnect()
    }
}

//@Composable
//fun ForceDarkTheme(content: @Composable () -> Unit) {
//    // Всегда используем темную тему, игнорируя isSystemInDarkTheme()
//    MaterialTheme(
//        colorScheme = darkColorScheme(),
//        content = content
//    )
//}

class MainViewModelFactory(private val usbSerialManager: UsbSerialManager)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(UsbRepository(usbSerialManager)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


