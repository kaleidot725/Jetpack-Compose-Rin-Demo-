<h1 align="center">
    Jetpack-Compose-Rin-Demo
</h1>

<h3 align="center">
    <img align="center" width=399 src="./docs/sample.gif" vspace="30">
</h3>


## ✨Features

This application has next features.

- Count numbers.
- Randomize numbers.

## 🏢Implementation

This application is implemented by rin.

![](./docs/architecture.drawio.svg)

### View(Jetpack Compose UI)

```kotlin
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

@Composable
fun CounterApp(
    model: CounterModel,
    onIncreaseOne: () -> Unit,
    onIncreaseTen: () -> Unit,
    onDecreaseOne: () -> Unit,
    onDecreaseTen: () -> Unit,
    onRandomize: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            if (model.loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                )
            } else {
                Text(
                    text = model.value.toString(),
                    fontSize = 100.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
        }

        Button(onClick = onIncreaseOne, modifier = Modifier.fillMaxWidth()) {
            Text(text = "INCREASE ONE")
        }

        Button(onClick = onIncreaseTen, modifier = Modifier.fillMaxWidth()) {
            Text(text = "INCREASE TEN")
        }

        Button(onClick = onDecreaseOne, modifier = Modifier.fillMaxWidth()) {
            Text(text = "DECREASE ONE")
        }

        Button(onClick = onDecreaseTen, modifier = Modifier.fillMaxWidth()) {
            Text(text = "DECREASE TEN")
        }

        Button(onClick = onRandomize, modifier = Modifier.fillMaxWidth()) {
            Text(text = "RANDOMIZE")
        }
    }
}

```

### Presenter(Using Jetpack Compose)

```kotlin
sealed interface CounterEvent
data class Change(val delta: Int) : CounterEvent
object Randomize : CounterEvent

data class CounterModel(
    val value: Int = 0,
    val loading: Boolean = false,
)

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
```

### Model(HTTP Client)

```kotlin
interface RandomApi {
    @GET("integers/?num=1&col=1&base=10&format=plain")
    suspend fun get(
        @Query("min") min: Int,
        @Query("max") max: Int,
    ): String
}

interface RandomService {
    suspend fun get(min: Int, max: Int): Int
    companion object {
        fun create(): RandomService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.random.org/")
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(
                            HttpLoggingInterceptor { Log.d("HTTP", it) }
                                .also { it.level = HttpLoggingInterceptor.Level.BASIC },
                        )
                        .build(),
                )
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            val api = retrofit.create<RandomApi>()

            return object : RandomService {
                override suspend fun get(min: Int, max: Int): Int {
                    return api.get(min, max).trim().toInt()
                }
            }
        }
    }
}

```

## 📚Library

This application uses the libraries below.

| Name            | Link                                                         |
|-----------------| ------------------------------------------------------------ |
| Jetpack Compose | https://developer.android.com/jetpack/compose                |
| Rin             | https://github.com/takahirom/Rin                          |
| OkHttp          | https://square.github.io/okhttp/                             |
| Retrofit        | https://square.github.io/retrofit/                           |

## ⭐Reference

| Name          | Link                              |
|---------------|-----------------------------------|
| Rin \| README | https://github.com/takahirom/Rin/blob/main/README.md |

## 💡License

```
Copyright (c) 2024 Yusuke Katsuragawa

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
