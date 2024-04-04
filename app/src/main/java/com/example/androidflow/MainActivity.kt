package com.example.androidflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.androidflow.ui.theme.AndroidFlowTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidFlowTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    test()
                    test2()
                }
            }
        }
    }

    private val fc = object : FlowCollector<String> {
        override suspend fun emit(value: String) {
            println(value)
        }
    }

    private val emitter : suspend (FlowCollector<String>) -> Unit = { flowCollector ->
        flowCollector.emit("A")
        flowCollector.emit("B")
        flowCollector.emit("C")
    }

    private val myFlow = flow(emitter)

    @OptIn(DelicateCoroutinesApi::class)
    fun test() {
        GlobalScope.launch { // <= collect() is suspending, need a coroutine
            myFlow.collect(fc)
        }
    }

    private val myFlow2 = flow { // flow collector reference is implicit here
        showThread("myFlow2")
        emit("A") // <= is the same as this.emit()
        emit("B")
        emit("C")
    }

    fun test2() {
        GlobalScope.launch {
            showThread("test2")
            myFlow2.collect { value ->
                showThread("test2 collect")
                println(value)
            }
        }
    }

    private fun showThread(tag: String) {
        println("$tag ${Thread.currentThread().name}")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidFlowTheme {
        Greeting("Android")
    }
}