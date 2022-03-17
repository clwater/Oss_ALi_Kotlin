package com.clwater.oss_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.clwater.oss_android.ui.theme.Oss_AndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

        ALiOssManager.
    }

    private fun initView() {
        setContent(content = {
            Oss_AndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting(stringResource(id = R.string.app_name))
                }
            }
        })

    }
}

@Composable
fun Greeting(title: String) {
    Column() {
        TopAppBar(title = { Text(text = title) })

        Column(Modifier.padding(12.dp, 12.dp)) {
            Image(
                painter = painterResource(id = R.mipmap.test),
                contentDescription = "contentDescription",
                modifier = Modifier.size(100.dp).clip(CircleShape).border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
            )
            Text(text = "Hello")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Oss_AndroidTheme {
        Greeting(stringResource(id = R.string.app_name))
    }
}