package com.clwater.oss_android.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.model.*
import com.clwater.oss_android.manager.ALiOssManager


class MainViewModel : ViewModel() {
    var stsModel: MutableLiveData<List<OSSObjectSummary>> = MutableLiveData<List<OSSObjectSummary>>()
    var errorCode: MutableLiveData<String> = MutableLiveData()
    var isFinish: MutableLiveData<Boolean> = MutableLiveData()

    var list: MutableList<OSSObjectSummary> = ArrayList()

    var nextMarker = ""
    fun getSTSInfo(prefix: String){
        nextMarker = ""
        list.clear()
        stsModel.postValue(listOf())
        isFinish.value = false
    }

    fun getSTSInfoNext(prefix: String){
        getSTSInfo(nextMarker, prefix)
    }

    fun upload(uploadPath: Uri, path: String, name: String, callback: ALiOssManager.UploadCallBack){
        ALiOssManager.upload(uploadPath, path, name, callback)
    }

    private fun getSTSInfo(marker: String, prefix: String){
        val callback = object : ALiOssManager.ALiOssCallBack {
            override fun onResult(request: ListObjectsRequest?, result: ListObjectsResult) {
                list.addAll(result.objectSummaries)

                if (result.isTruncated.not()){
                    isFinish.postValue(true)
                }
                if (result.nextMarker != null){
                    nextMarker = result.nextMarker
                }
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
        ALiOssManager.getObjectList(callback, marker, prefix)
    }

    fun download(url: String, downloadCallBack: ALiOssManager.DownloadCallBack){
        ALiOssManager.download(url, downloadCallBack)
    }




}