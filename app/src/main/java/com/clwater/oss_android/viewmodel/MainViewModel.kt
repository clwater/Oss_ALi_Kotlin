package com.clwater.oss_android.viewmodel

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

class MainViewModel : ViewModel() {
    var stsModel: MutableLiveData<List<OssFileModel>> = MutableLiveData<List<OssFileModel>>()
    var errorCode: MutableLiveData<String> = MutableLiveData()

    fun getSTSInfo(){
        val callback = object : ALiOssManager.ALiOssCallBack {
            override fun onResult(request: ListObjectsRequest?, result: ListObjectsResult) {
                val itemType = object : TypeToken<List<OssFileModel>>() {}.type
                stsModel.postValue(Gson().fromJson( Gson().toJson(result.objectSummaries), itemType))
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