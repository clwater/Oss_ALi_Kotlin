package com.clwater.oss_android

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.alibaba.sdk.android.oss.model.OSSObjectSummary
import com.clwater.oss_android.manager.ALiOssManager
import com.clwater.oss_android.ui.theme.Oss_AndroidTheme
import com.clwater.oss_android.viewmodel.MainViewModel
import kotlin.math.log


class MainActivity : ComponentActivity() {
    lateinit var context: Context
    var currentPath: String = ""
    private val mainViewModel: MainViewModel by viewModels()
    val showImageUrl = mutableStateOf("")
    val showDownloadImageUrl = mutableStateOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        initView()
        initData()
    }


    private fun initData() {
        ALiOssManager.init(this)
        mainViewModel.stsModel.observe(this) {
            updateView(it)
        }
//        mainViewModel.getSTSInfo()

    }

    private fun updateView(list: List<OSSObjectSummary>) {
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

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun OssFile(list: List<OSSObjectSummary>) {
        val isFinish = mainViewModel.isFinish.observeAsState(false)
        LazyVerticalGrid(
            cells = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            list.forEach { item ->

                item {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillParentMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp))
                            .background(Color(0xFFE9E9E9))
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        ) {
                            if (item.size != 0L) {
                                if (Regex(".*?(?:png|jpg|jpeg)").matches(item.key)) {

                                    val url =
                                        "https://" + Constants.BUCKET_NAME + ".oss-cn-beijing.aliyuncs.com/" + item.key
                                    val painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(data = url)
                                            .apply(block = fun ImageRequest.Builder.() {
                                                crossfade(true)
                                            }).build()
                                    )

                                    Box(modifier = Modifier.clickable {
                                        showImageUrl.value = url
                                    }) {
                                        // 显示图片
                                        Image(
                                            painter = painter,
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .height(100.dp)
                                                .fillMaxWidth()
                                                .align(Alignment.Center)
                                                .clip(
                                                    RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)
                                                )
                                        )

                                        when (painter.state) {
                                            is AsyncImagePainter.State.Loading -> {
                                                // Display a circular progress indicator whilst loading
                                                CircularProgressIndicator(
                                                    Modifier.align(
                                                        Alignment.Center
                                                    )
                                                )
                                            }
                                            is AsyncImagePainter.State.Error -> {
                                                Text("Image Loading Error")
                                            }
                                        }
                                    }

                                } else {

                                    Image(
                                        painterResource(id = R.drawable.ic_twotone_insert_drive_file_64),
                                        contentDescription = "R.drawable.ic_twotone_insert_drive_file_64",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(100.dp)
                                            .fillMaxWidth()
                                            .align(Alignment.Center)
                                            .clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp))
                                    )
                                }

                            } else {
                                Image(
                                    painterResource(id = R.drawable.ic_twotone_folder_64),
                                    contentDescription = "R.drawable.ic_twotone_folder_64",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .height(100.dp)
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                        .clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp))
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .height(20.dp)
                                .fillMaxWidth()
                                .background(Color(0xFFCECECE))
                                .align(Alignment.CenterHorizontally)
                        ) {
                            var fileName = item.key
                            fileName = if (item.size != 0L) {
                                fileName.split("/").last()
                            } else {
                                fileName.removeRange(
                                    0,
                                    fileName.indexOf(currentPath) + currentPath.length
                                )
                            }
                            Text(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(start = 4.dp, end = 4.dp),
                                text = fileName,
                                maxLines = 1,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            if (isFinish.value.not()) {

                val offset =
                    when (list.size % 3) {
                        0 -> {
                            1
                        }
                        1 -> {
                            3
                        }
                        2 -> {
                            2
                        }
                        else -> {
                            0
                        }
                    }

                for (i in 1..offset) {
                    item {
                        Text(text = "")
                    }
                }
                item() {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    LaunchedEffect(Unit) {
                        mainViewModel.getSTSInfoNext()
                    }
                }
            }
        }
    }

    @Composable
    fun Greeting(title: String, list: List<OSSObjectSummary>) {
        Column() {
            TopAppBar(title = { Text(text = title) })
            val scrollState = rememberScrollState()
            Row(modifier = Modifier.horizontalScroll(scrollState)) {
                for (i in 1..10) {
                    Text(
                        text = "" + i,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(100.dp)
                            .background(Color.Blue)
                    )
                }
            }
            OssFile(list)
            ShowImageDialog()
            ShowDownloadDialog()
        }
    }

    @Composable
    private fun ShowImageDialog() {
        if (showImageUrl.value.isNotEmpty()) {
            AlertDialog(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                onDismissRequest = {
                    showImageUrl.value = ""
                },
                properties = DialogProperties(dismissOnClickOutside = true),
                text = {
                    Box(modifier = Modifier) {
                        val painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = showImageUrl.value)
                                .apply(block = fun ImageRequest.Builder.() {
                                    crossfade(true)
                                }).build()
                        )
                        Image(
                            painter = painter, contentDescription = "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .clip(
                                    RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)
                                )
                        )
                    }
                },

                confirmButton = {

                    Button(
                        onClick = { showDownloadImageUrl.value = showImageUrl.value },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow)
                    ) {
                        Text(text = "下载")
                    }
                    Button(
                        onClick = { showImageUrl.value = "" },
                    ) {
                        Text(text = "关闭")
                    }
                },


                )
        }
    }
    
    @Composable
    private fun ShowDownloadDialog(){
        if (showDownloadImageUrl.value.isNotEmpty()) {
            AlertDialog(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                onDismissRequest = {
                    showDownloadImageUrl.value = ""
                },
                properties = DialogProperties(dismissOnClickOutside = true),
                text = {
                    Box(modifier = Modifier) {
                        Text(text = "13")
                    }
                },

                confirmButton = {

                    Button(
                        onClick = { showImageUrl.value = "" },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow)
                    ) {
                        Text(text = "下载")
                    }

                },


                )
        }
    }

}


