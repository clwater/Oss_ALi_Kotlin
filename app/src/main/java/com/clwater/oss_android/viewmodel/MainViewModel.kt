package com.clwater.oss_android.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.model.ListObjectsRequest
import com.alibaba.sdk.android.oss.model.ListObjectsResult
import com.clwater.oss_android.manager.ALiOssManager
import com.clwater.oss_android.model.OssFileModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.log

class MainViewModel : ViewModel() {
    var stsModel: MutableLiveData<List<OssFileModel>> = MutableLiveData<List<OssFileModel>>()
    var errorCode: MutableLiveData<String> = MutableLiveData()

    var list: MutableList<OssFileModel> = ArrayList()

    fun getSTSInfo(){
        val callback = object : ALiOssManager.ALiOssCallBack {
            override fun onResult(request: ListObjectsRequest?, result: ListObjectsResult) {
                val itemType = object : TypeToken<List<OssFileModel>>() {}.type
                list.addAll(Gson().fromJson( Gson().toJson(result.objectSummaries), itemType))
                Log.d("gzb", "result.objectSummaries: " + Gson().toJson(result.objectSummaries))
                Log.d("gzb", "list: " + Gson().toJson(list))
                if (result.isTruncated){
                    Thread.sleep(100)
                    getSTSInfo()
                }else{
                    Thread.sleep(100)
                    Log.d("gzb", "r: " + Gson().toJson(list))
                    stsModel.postValue(list)}
            }
            override fun onFail(
                request: ListObjectsRequest?,
                clientException: ClientException,
                serviceException: ServiceException
            ) {
                errorCode.postValue("fail")
            }
        }
        ALiOssManager.getObjectList(callback)
    }


}