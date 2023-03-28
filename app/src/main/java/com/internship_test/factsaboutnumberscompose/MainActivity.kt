package com.internship_test.factsaboutnumberscompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.internship_test.factsaboutnumberscompose.MainActivity.Companion.RANDOM_URL
import com.internship_test.factsaboutnumberscompose.MainActivity.Companion.URL
import com.internship_test.factsaboutnumberscompose.model.StudentDetails
import com.internship_test.factsaboutnumberscompose.ui.theme.FactsAboutNumbersComposeTheme
import kotlinx.coroutines.*
import java.net.URL

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val details by remember { mutableStateOf(mutableListOf<StudentDetails>()) }
            repeat(50) {
                with(details) {
                    add(StudentDetails("alfa", "20", true))
                    add(StudentDetails("beta", "21", true))
                    add(StudentDetails("gamma", "22", true))
                }
            }

            FactsAboutNumbersComposeTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        FactsAboutNumber()
                        LazyColumn {
                            itemsIndexed(details) { index, _ ->
                                Students(details[index], details)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val URL = "http://numbersapi.com"
        const val RANDOM_URL = "$URL/random/math"
    }
}

@Composable
fun FactsAboutNumber() {
    val number = remember { mutableStateOf("") }
    var fact by remember { mutableStateOf(mutableListOf<String>()) }

    TextField(
        value = number.value,
        onValueChange = { number.value = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
    Button(
        onClick = { getFact("$URL/${number.value}", fact) },
        enabled = number.value.isNotEmpty()
    ) {
        Text(text = "Отримати факт")
    }
    Button(onClick = {
        getFact(RANDOM_URL, fact)
        val sec = fact
        fact = mutableListOf()
        fact = sec
    }) {
        Text(text = "Отримати факт \n про випадкове число", textAlign = TextAlign.Center)
    }
    FactLazyColumn(fact)
}

@Composable
fun FactLazyColumn(fact: MutableList<String>) {
    LazyColumn {
        items(fact) { items ->
            Card(
                shape = RoundedCornerShape(15.dp),
                elevation = 5.dp
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = items)
                }
            }
        }
    }
}

@Composable
fun Students(detail: StudentDetails, listDetails: MutableList<StudentDetails>) {
    var isStudent by remember { mutableStateOf(detail.isStudent) }
    var studentSetting by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(5.dp)
            .clickable {
                studentSetting = !studentSetting
            },
        shape = RoundedCornerShape(15.dp),
        elevation = 5.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth()
        ) {
            Text(text = detail.name, Modifier.width(200.dp))
            Text(text = detail.age, Modifier.width(30.dp))
            Text(text = isStudent.toString(), Modifier.width(35.dp))
        }
    }

    if (studentSetting) {
        Dialog(onDismissRequest = { studentSetting = false }) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Text(text = detail.name, Modifier.width(200.dp), fontSize = 20.sp)
                    Text(text = detail.age, Modifier.width(30.dp), fontSize = 20.sp)
                    Checkbox(
                        checked = isStudent,
                        onCheckedChange = {
                            isStudent = !isStudent
                            detail.isStudent = isStudent
                        },
                        modifier = Modifier.offset(0.dp, (-10).dp)
                    )
                }
                Row {
                    FloatingActionButton(onClick = { studentSetting = false }) {
                        Text(
                            text = "Close setting",
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                    FloatingActionButton(
                        onClick = { listDetails.remove(detail) },
                        backgroundColor = Color.Red,
                        modifier = Modifier.offset(10.dp)
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}

private fun getFact(url: String, fact: MutableList<String>) {
    CoroutineScope(Job() + Dispatchers.Default).launch {
        kotlin.runCatching {
            val text = async { URL(url).readText() }
            fact.add(text.await())
        }
    }
}

