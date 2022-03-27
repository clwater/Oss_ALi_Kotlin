package com.clwater.oss_android.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.model.*
import com.clwater.oss_android.manager.ALiOssManager


class MainViewModel : ViewModel() {
    //列表文件信息
    var stsModel: MutableLiveData<List<OSSObjectSummary>> = MutableLiveData<List<OSSObjectSummary>>()
    //错误信息
    var errorCode: MutableLiveData<String> = MutableLiveData()
    //是否列举完成
    var isFinish: MutableLiveData<Boolean> = MutableLiveData()
    //列举信息缓存
    var list: MutableList<OSSObjectSummary> = ArrayList()
    //分页查找标识
    var nextMarker = ""

    //获取列表信息(初始)
    fun getSTSInfo(prefix: String){
        nextMarker = ""
        list.clear()
        stsModel.postValue(listOf())
        isFinish.value = false
    }

    //获取下一页列表信息
    fun getSTSInfoNext(prefix: String){
        getSTSInfo(nextMarker, prefix)
    }

    //上传本地图片
    fun upload(uploadPath: Uri, path: String, name: String, callback: ALiOssManager.UploadCallBack){
        ALiOssManager.upload(uploadPath, path, name, callback)
    }

    //获取指定列表信息
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

    //下载文件到本地
    fun download(url: String, downloadCallBack: ALiOssManager.DownloadCallBack){
        ALiOssManager.download(url, downloadCallBack)
    }




}