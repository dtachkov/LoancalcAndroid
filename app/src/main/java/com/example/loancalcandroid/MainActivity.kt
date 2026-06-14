package com.example.loancalcandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.loancalcandroid.navigation.LoanCalcNavGraph
import com.example.loancalcandroid.ui.theme.LoanCalcAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoanCalcAndroidTheme {
                LoanCalcNavGraph(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
