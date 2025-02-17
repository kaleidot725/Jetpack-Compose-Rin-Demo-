package com.example.demo.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.demo.data.RandomService
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun CounterPresenter(
    events: Flow<CounterEvent>,
    randomService: RandomService,
): CounterModel {
    var count by rememberRetained { mutableIntStateOf(0) }
    var loading by rememberRetained { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is Change -> {
                    count += event.delta
                }

                Randomize -> {
                    loading = true
                    launch {
                        count = randomService.get(-20, 20)
                        loading = false
                    }
                }
            }
        }
    }

    return CounterModel(count, loading)
}
