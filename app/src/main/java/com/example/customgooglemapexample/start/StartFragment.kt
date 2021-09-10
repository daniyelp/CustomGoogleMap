package com.example.customgooglemapexample.start

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.customgooglemapexample.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_start.*

class StartFragment: Fragment(R.layout.fragment_start) {

    private val demoButtonEnabled = mutableStateOf(true)
    private val playButtonEnabled = mutableStateOf(true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resetStates()

        view_compose_start.setContent {
            Buttons()
        }
    }

    private fun resetStates() {
        demoButtonEnabled.value = true
        playButtonEnabled.value = true
    }

    @Preview
    @Composable
    private fun Buttons() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Button(
                onClick = { navigateToDemoFragment(); demoButtonEnabled.value = false },
                enabled = demoButtonEnabled.value
            ) {
                Text(text = "DEMO")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { navigateToPlayFragment(); playButtonEnabled.value = false },
                enabled = playButtonEnabled.value
            ) {
                Text(text = "PLAY")
            }
        }
    }

    private fun navigateToDemoFragment() {
        findNavController().navigate(StartFragmentDirections.actionStartFragmentToDemoFragment())
    }

    private fun navigateToPlayFragment() {
        findNavController().navigate(StartFragmentDirections.actionStartFragmentToMyFragment())
    }

    override fun onResume() {
        super.onResume()
        resetStates()
        Log.d("RESUME", "RESUMING")
    }
}