package com.clwater.oss_android.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.model.ListObjectsRequest
import com.alibaba.sdk.android.oss.model.ListObjectsResult
import com.alibaba.sdk.android.oss.model.OSSObjectSummary
import com.clwater.oss_android.manager.ALiOssManager
import com.clwater.oss_android.model.OssFileModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.log

class MainViewModel : ViewModel() {
    var stsModel: MutableLiveData<List<OSSObjectSummary>> = MutableLiveData<List<OSSObjectSummary>>()
    var errorCode: MutableLiveData<String> = MutableLiveData()
    var isFinish: MutableLiveData<Boolean> = MutableLiveData()

    var list: MutableList<OSSObjectSummary> = ArrayList()

    var nextMarker = ""
    fun getSTSInfo(){
        nextMarker = ""
        getSTSInfo(nextMarker)
    }

    fun getSTSInfoNext(){
        getSTSInfo(nextMarker)
    }

    fun getSTSInfo(marker: String){
        val callback = object : ALiOssManager.ALiOssCallBack {
            override fun onResult(request: ListObjectsRequest?, result: ListObjectsResult) {
//                val itemType = object : TypeToken<List<OssFileModel>>() {}.type
                list.addAll(result.objectSummaries)

                if (result.isTruncated.not()){
                    isFinish.postValue(true)
                }
//                getSTSInfo(result.nextMarker)
                nextMarker = result.nextMarker
                stsModel.postValue(list)
            }
            override fun onFail(
                request: ListObjectsRequest?,
                clientException: ClientException,
                serviceException: ServiceException
            ) {
                errorCode.postValue("fail")
            }
        }
        ALiOssManager.getObjectList(callback, marker)
    }


}