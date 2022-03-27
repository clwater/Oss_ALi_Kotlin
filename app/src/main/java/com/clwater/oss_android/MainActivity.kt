package com.clwater.oss_android

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toFile
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.alibaba.sdk.android.oss.model.OSSObjectSummary
import com.clwater.oss_android.manager.ALiOssManager
import com.clwater.oss_android.ui.theme.Oss_AndroidTheme
import com.clwater.oss_android.viewmodel.MainViewModel
import com.google.gson.Gson
import java.io.InputStream


class MainActivity : ComponentActivity() {
    private lateinit var context: Context
    private var currentPath = mutableStateOf("")
    private val mainViewModel: MainViewModel by viewModels()
    private val showImageUrl = mutableStateOf("")
    private val showDownloadImageUrl = mutableStateOf("")

    //    private val showUpload = mutableStateOf(false)
    val downloadProgress = mutableStateOf(0f)
    val inProgress = mutableStateOf(false)
    private val CHOOSE_IMAGE_CODE = 1

    private val uploadPath = mutableStateOf("")


//    private val bufferedImage: ImageBitmap? = null
    private var camera1bufferedImage: ImageBitmap? = null

//    private val _uri: ? = null
    private var uploadUri : Uri = Uri.EMPTY

    //上传进度
    val uploadrogress = mutableStateOf(0f)

    //上传Dialog状态
    // 0: 关闭 1: 未选择图片 2: 选择图片完成 3: 图片上传中 4: 图片上传完成
    val uploadStatus = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        initView()
        initData()
    }


    private fun initData() {
        ALiOssManager.init(this)
        mainViewModel.stsModel.observe(this) {
            Log.d("gzb", "" + Gson().toJson(it))
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


    @Preview
    @Composable
    fun preview() {
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
                                Box(modifier = Modifier.clickable {
                                    if (currentPath.value != item.key) {
                                        currentPath.value = item.key
                                        mainViewModel.getSTSInfo(currentPath.value)
//                                    mainViewModel.stsModel.value = listOf()
                                        inProgress.value = false
                                    }
                                }) {
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
                                if (fileName.split("/").size >= 2 && fileName.split("/")
                                        .isNotEmpty()
                                ) {
                                    fileName.removeRange(
                                        0,
                                        fileName.indexOf(currentPath.value) + currentPath.value.length
                                    )
                                } else {
                                    fileName
                                }
                            }
                            if (fileName.isEmpty()) {
                                fileName = "/"
                            }

                            Text(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(start = 4.dp, end = 4.dp),
                                text = fileName,
                                maxLines = 1,
                                fontSize = 12.sp,

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
                        mainViewModel.getSTSInfoNext(currentPath.value)
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

            val paths = currentPath.value.split("/").toMutableList()
            paths.add(0, "")
            if (paths.size > 1 && paths.last().isEmpty()) {
                paths.removeLast()
            }
            Row(modifier = Modifier.horizontalScroll(scrollState)) {
                paths.forEach {
                    Box(modifier = Modifier.clickable {
                        var aliPath = ""
                        for (item in 0..paths.indexOf(it)) {
                            aliPath += paths[item] + "/"
                        }
                        aliPath = aliPath.removePrefix("/")
                        if (currentPath.value != aliPath) {
                            currentPath.value = aliPath
                            mainViewModel.getSTSInfo(currentPath.value)
                            inProgress.value = false
                        }
                    }) {
                        Text(
                            text = "$it/",
                            modifier = Modifier
                                .padding(4.dp)
                                .background(Color(0xFFD8D8D8))
                                .padding(4.dp),
                            color = Color(0xFF3C3C3C)
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                OssFile(list)
            }

            Row() {
                Button(
                    modifier = Modifier.padding(12.dp),
                    onClick = { uploadStatus.value = 1 }) {
                    Text(text = "上传图片")
                }

                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.padding(12.dp),
                    onClick = {
                        mainViewModel.getSTSInfo(currentPath.value)
                        inProgress.value = false
                    }) {
                    
                    Image(painter = painterResource(id = R.drawable.ic_baseline_refresh_24), contentDescription = "")
                }
            }


            ShowImageDialog()
            ShowDownloadDialog()
            ShowUpLoadDialog()


        }
    }


    fun chooseLocalImage() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_IMAGE_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_IMAGE_CODE) {
            uploadUri = data?.data!!
            var imageInputStream: InputStream? = null
            imageInputStream = contentResolver.openInputStream(uploadUri)
            camera1bufferedImage = BitmapFactory.decodeStream(imageInputStream).asImageBitmap()
            uploadStatus.value = 2
        }
    }


    @SuppressLint("Range")
    fun uriToFileName(uri: Uri): String {
        return when (uri.scheme) {
            ContentResolver.SCHEME_FILE -> uri.toFile().name
            ContentResolver.SCHEME_CONTENT -> {
                val cursor = context.contentResolver.query(uri, null, null, null, null, null)
                cursor?.let {
                    it.moveToFirst()
                    val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor.close()
                    displayName
                } ?: "${System.currentTimeMillis()}.${
                    MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(context.contentResolver.getType(uri))
                }}"

            }
            else -> "${System.currentTimeMillis()}.${
                MimeTypeMap.getSingleton()
                    .getExtensionFromMimeType(context.contentResolver.getType(uri))
            }}"
        }
    }

    @Composable
    fun ShowUpLoadDialog() {
        if (uploadStatus.value != 0) {
            AlertDialog(
                modifier = Modifier
                    .padding(12.dp)
                    .wrapContentWidth()
                    .wrapContentHeight(),
                onDismissRequest = {
                    uploadStatus.value = 0
                },
//                properties = DialogProperties(dismissOnClickOutside = !inProgress.value),
                title = {},
                text = {
                       if (uploadStatus.value == 1){
                           Button(onClick = {
                               chooseLocalImage()
                           }) {
                               Text(text = "选择文件", )
                           }
                       }else{
                           Column() {
                               Box() {
                                   Image(
                                       bitmap = camera1bufferedImage!!, contentDescription = "",
                                       modifier = Modifier
                                           .fillMaxWidth()
                                           .align(Alignment.Center)
                                           .clip(
                                               RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp)
                                           )
                                   )
                               }
                               if (uploadStatus.value == 3 || uploadStatus.value == 4){
                                   LinearProgressIndicator(modifier = Modifier.padding(top = 12.dp), progress = uploadrogress.value)
                               }
                           }

                       }

                },

                confirmButton = {

                    if (uploadStatus.value == 2){
                        Button(onClick = {
                            val callback =  object  : ALiOssManager.UploadCallBack{
                                override fun onProgress(progress: Float) {
                                    uploadrogress.value = progress
                                    if (progress == 1f){
                                        uploadStatus.value  = 4
                                    }
                                }

                                override fun onFail() {
                                    TODO("Not yet implemented")
                                }


                            }
                            ALiOssManager.upload(uploadUri, currentPath.value, uriToFileName(uploadUri), callback)
                        }) {
                            Text(text = "上传")
                        }
                    }else if (uploadStatus.value == 4){
                        Button(onClick = {
                            uploadStatus.value = 0
                        }) {
                            Text(text = "完成")
                        }
                    }
                }
            )
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
                        onClick = {
                            inProgress.value = true
                            showDownloadImageUrl.value = showImageUrl.value
                            val callback = object : ALiOssManager.DownloadCallBack {
                                override fun onProgress(progress: Float) {
                                    downloadProgress.value = progress
                                    inProgress.value = downloadProgress.value != 1f
                                }

                                override fun onFail() {
                                    inProgress.value = false
                                }
                            }
                            val name = showDownloadImageUrl.value.replace(
                                "https://" + Constants.BUCKET_NAME + ".oss-cn-beijing.aliyuncs.com/",
                                ""
                            )
                            mainViewModel.download(name, callback)
                        },
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
    private fun ShowDownloadDialog() {
        if (showDownloadImageUrl.value.isNotEmpty()) {
            val name = showDownloadImageUrl.value.replace(
                "https://" + Constants.BUCKET_NAME + ".oss-cn-beijing.aliyuncs.com/",
                ""
            )
            AlertDialog(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                onDismissRequest = {
                    showDownloadImageUrl.value = ""
                },
                properties = DialogProperties(dismissOnClickOutside = !inProgress.value),
                title = {
                    Box(modifier = Modifier) {
                        Text(text = "$name 下载中")
                    }
                },
                text = {
                    LinearProgressIndicator(progress = downloadProgress.value)
                },

                confirmButton = {
                    Button(
                        onClick = { showDownloadImageUrl.value = "" },
                        modifier = Modifier.clickable { !inProgress.value }

                    ) {
                        Text(
                            text = (if (inProgress.value) {
                                "下载中"
                            } else {
                                "下载完成"
                            })
                        )
                    }

                },


                )
        }
    }

}


