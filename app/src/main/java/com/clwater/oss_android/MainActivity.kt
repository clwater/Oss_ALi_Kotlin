package com.clwater.oss_android

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.model.ListObjectsRequest
import com.alibaba.sdk.android.oss.model.ListObjectsResult
import com.clwater.oss_android.manager.ALiOssManager
import com.clwater.oss_android.model.OssFileModel
import com.clwater.oss_android.ui.theme.Oss_AndroidTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : ComponentActivity() {
    lateinit var context: Context
    val list: MutableList<OssFileModel>  = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

        context = this
        initData()

//        ALiOssManager.
    }

    private fun initData() {
        ALiOssManager.init(this)



        val callback = object : ALiOssManager.ALiOssCallBack {
            override fun onResult(request: ListObjectsRequest?, result: ListObjectsResult) {
                updateListInfo(Gson().toJson(result.objectSummaries), true)
            }
            override fun onFail(
                request: ListObjectsRequest?,
                clientException: ClientException,
                serviceException: ServiceException
            ) {
            }
        }


        ALiOssManager.getObjectList(callback)
    }

    private fun updateListInfo(result: String, isFirst: Boolean) {
        Log.d("gzb", "result: " + result)
        val itemType = object : TypeToken<List<OssFileModel>>() {}.type
        var _list: List<OssFileModel> = Gson().fromJson(result, itemType)

        if (isFirst){
            list.clear()
            list.addAll(_list)
        }
    }

//    private fun initData() {
//        val callback: STSModelCallBack = object : STSModelCallBack {
//            override fun fail() {}
//            override fun call(stsModel: STSModel) {
//                Log.d("gzb", "STSModel: ${Gson().toJson(stsModel)}")
//                genALiOssInfo(stsModel)
//            }
//        }
//        FuelManager.getToken(callback)
//    }

//    private fun genALiOssInfo(stsModel: STSModel){
//        Constants.OSS_ACCESS_KEY_ID = stsModel.AccessKeyId
//        Constants.OSS_ACCESS_KEY_SECRET = stsModel.AccessKeySecret
//        Constants.OSS_STS_TOKEN = stsModel.SecurityToken
//
//        ALiOssManager.init(this)
//        ALiOssManager.getAllObject()
//    }


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
                item {
                    Text(text = "First item")
                }
                //todo https://elegantaccess.org/2021/09/28/android-kt-jetpack-compose-list/
                items(5){
                    Text(text = "$it")
                }
                items(list){
//                    ossFile ->
//                    Log.d("gzb", "it" + Gson().toJson(ossFile))
//                    Text(text = ossFile.key, modifier = Modifier.padding(12.dp).background(Color.Green))
                    Text(text = "$it")
                }

                items(5){
                    Text(text = "2$it")
                }
            }
//            Column(Modifier.padding(12.dp, 12.dp)) {
//                Image(
//                    painter = painterResource(id = R.mipmap.test),
//                    contentDescription = "contentDescription",
//                    modifier = Modifier
//                        .size(100.dp)
//                        .clip(CircleShape)
//                        .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
//                )
//                Text(text = "Hello")
//
//                Button(
//                    onClick = {
//                        Log.d("gzb", "click")
////                    ALiOssManager.init(context)
////                    ALiOssManager.getAllObject()
//                        initData()
//                    },
//                    contentPadding = PaddingValues(
//                        start = 20.dp,
//                        top = 12.dp,
//                        end = 20.dp,
//                        bottom = 12.dp
//                    )
//                ) {
//                    Text("Test")
//                }
//
//
//            }
        }
    }
}


