package com.clwater.oss_android

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.clwater.oss_android.manager.ALiOssManager
import com.clwater.oss_android.model.OssFileModel
import com.clwater.oss_android.ui.theme.Oss_AndroidTheme
import com.clwater.oss_android.viewmodel.MainViewModel
import com.google.gson.Gson


class MainActivity : ComponentActivity() {
    lateinit var context: Context
    private val mainViewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        initView()
        initData()
    }

    private fun initData() {
        ALiOssManager.init(this)
        mainViewModel.stsModel.observe(this){
            Log.d("gzb", "=================:" + Gson().toJson(it))
            updateView(it)
        }
        mainViewModel.getSTSInfo()

    }

    private fun updateView(list: List<OssFileModel>){
        setContent(content = {
            Oss_AndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting(stringResource(id = R.string.app_name), list)
                }
            }
        })
    }

    private fun initView() {
        updateView(listOf())
    }

    @Composable
    fun OssFile(list: List<OssFileModel>){
        val isFinish = mainViewModel.isFinish.observeAsState(false)
        LazyColumn(modifier = Modifier.fillMaxHeight()){


            list.forEach { item ->
                item {
                    Text(text = item.key)
                    Box() {
                    if (item.size != "0") {
                        val painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = "https://" + Constants.BUCKET_NAME + ".oss-cn-beijing.aliyuncs.com/" + item.key)
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                }).build()
                        )
                        Image(
                            painter = painter,
                            contentDescription = "",
                            Modifier.size(100.dp, 100.dp)
                        )
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> {
                                // Display a circular progress indicator whilst loading
                                CircularProgressIndicator(Modifier.align(Alignment.Center))
                            }
                            is AsyncImagePainter.State.Error -> {
                                Text("Image Loading Error")
                            }
                        }
                    }
                } }
            }
            if (isFinish.value.not()) {
                item {
                    CircularProgressIndicator(color = Color.Red)
                    LaunchedEffect(Unit) {
                        mainViewModel.getSTSInfoNext()
                    }
                }
            }
        }

    }
    @Composable
    fun Greeting(title: String, list: List<OssFileModel>) {
//        val list by mainViewModel.stsModel.observeAsState(listOf())
//        val painter = rememberCoilPainter("")
        Column() {
            TopAppBar(title = { Text(text = title) })
            val scrollState = rememberScrollState()
            Row(modifier = Modifier.horizontalScroll(scrollState) ) {
                for ( i in 1..10){
                    Text(text = "" + i,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(100.dp)
                            .background(Color.Blue))
                }
            }
            OssFile(list)
        }
    }
}


