package com.example.loancalcandroid.ui.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

object SpeechRecognition {
    fun createIntent(prompt: String): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        }
    }

    fun resultText(data: Intent?): String? {
        return data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
            ?.takeIf { it.isNotBlank() }
    }
}

@Composable
fun rememberSpeechRecognitionLauncher(
    prompt: String,
    onResult: (String) -> Unit,
    onUnavailable: () -> Unit,
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        SpeechRecognition.resultText(result.data)?.let(onResult)
    }
    return {
        try {
            launcher.launch(SpeechRecognition.createIntent(prompt))
        } catch (_: ActivityNotFoundException) {
            onUnavailable()
        }
    }
}
