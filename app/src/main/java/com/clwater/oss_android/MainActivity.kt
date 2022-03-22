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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.clwater.oss_android.manager.ALiOssManager
import com.clwater.oss_android.model.OssFileModel
import com.clwater.oss_android.model.STSModel
import com.clwater.oss_android.ui.theme.Oss_AndroidTheme
import com.clwater.oss_android.viewmodel.MainViewModel
import com.google.android.material.color.MaterialColors
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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
//        mainViewModel.stsModel.observe(this){
//            Log.d("gzb", "" + Gson().toJson(it))
//        }
        mainViewModel.getSTSInfo()

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



    @Composable
    fun Greeting(title: String) {
        val list: List<OssFileModel> by mainViewModel.stsModel.observeAsState(listOf())


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

            LazyColumn(modifier = Modifier.fillMaxHeight()){
                items(list){
                    Text(text = "${it.key}")
                    Box() {
                        if (it.size != "0") {
                            val painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(data = "https://" + Constants.BUCKET_NAME + ".oss-cn-beijing.aliyuncs.com/" + it.key)
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
                    }
                }

            }
        }
    }
}


