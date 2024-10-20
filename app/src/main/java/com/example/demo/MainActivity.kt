package com.example.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.example.demo.data.RandomService
import com.example.demo.presenter.Change
import com.example.demo.presenter.CounterEvent
import com.example.demo.presenter.CounterPresenter
import com.example.demo.presenter.Randomize
import com.example.demo.ui.CounterApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val scope = CoroutineScope(Main)
    private val randomService = RandomService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val events = remember { MutableSharedFlow<CounterEvent>() }
            val model = CounterPresenter(
                events = events,
                randomService = randomService,
            )

            CounterApp(
                model = model,
                onIncreaseOne = { scope.launch { events.emit(Change(1)) } },
                onIncreaseTen = { scope.launch { events.emit(Change(10)) } },
                onDecreaseOne = { scope.launch { events.emit(Change(-1)) } },
                onDecreaseTen = { scope.launch { events.emit(Change(-10)) } },
                onRandomize = { scope.launch { events.emit(Randomize) } }
            )
        }
    }
}
