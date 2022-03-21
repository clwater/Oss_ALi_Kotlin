package com.clwater.oss_android.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.model.ListObjectsRequest
import com.alibaba.sdk.android.oss.model.ListObjectsResult
import com.clwater.oss_android.manager.ALiOssManager
import com.clwater.oss_android.model.OssFileModel
import com.clwater.oss_android.model.STSModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainViewModel : ViewModel() {
//    var stsModel: MutableLiveData<List<OssFileModel>>
    var test: MutableLiveData<Int>

    init{
//        stsModel = MutableLiveData<List<OssFileModel>>()
        test = MutableLiveData<Int>()
    }

//    private val _stsModel: MutableLiveData<List<OssFileModel>> = MutableLiveData<List<OssFileModel>>()


    /** Add hard code test data**/
//    init {
//        _stsModel.value = getSTSList()
//    }

//    fun onDevicesChange(listDevices: List<OssFileModel>) {
//        _stsModel.value = listDevices
//    }

    fun getSTSInfo(){
//        test.value = 6
//    Log.d("gzb", "getSTSInfo")
        val callback = object : ALiOssManager.ALiOssCallBack {
            override fun onResult(request: ListObjectsRequest?, result: ListObjectsResult) {
//                Log.d("gzb", "onResult")
//                Log.d("gzb", "" + Gson().toJson(result));

//                updateListInfo(Gson().toJson(result.objectSummaries), true)

//                Log.d("gzb", "onResult1")
                test.value = 66

                result.objectSummaries.forEach {
                    test.value = 6666

                    val ossFileModel = OssFileModel(it.key, it.lastModified.toString(), it.size.toString())
                    val itemType = object : TypeToken<List<OssFileModel>>() {}.type
//                    stsModel.value =  Gson().fromJson(Gson().toJson(listOf(ossFileModel)), itemType)
                    test.value = 66666

                }
                test.value = 666

//                val itemType = object : TypeToken<List<OssFileModel>>() {}.type
//                stsModel =  Gson().fromJson(Gson().toJson(result.objectSummaries), itemType)
//                Log.d("gzb", "onResult2")

//                Log.d("gzb", "" + Gson().toJson(stsModel));
            }
            override fun onFail(
                request: ListObjectsRequest?,
                clientException: ClientException,
                serviceException: ServiceException
            ) {
                test.value = 777

                Log.d("gzb", "fail")

            }
        }


        ALiOssManager.getObjectList(callback)
    }


}