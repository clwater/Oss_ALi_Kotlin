package com.clwater.oss_android.manager

import android.content.Context
import android.util.Log
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSS
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
import com.alibaba.sdk.android.oss.model.ListObjectsRequest
import com.alibaba.sdk.android.oss.model.ListObjectsResult
import com.clwater.oss_android.Constants
import com.google.gson.Gson


/**
 * @author: gengzhibo
 * @date: 2022/3/17
 */
object ALiOssManager {
//    private var marker: String? = null
    private var isCompleted = false

    private lateinit var oss: OSS
    fun init(context: Context){
        val credentialProvider = OSSAuthCredentialsProvider(Constants.STS_SERVER_URL)
        oss = OSSClient(context, Constants.endpoint, credentialProvider)
    }


    // 列举一页文件。
    fun getObjectList(callback: ALiOssCallBack, marker: String) {
        Log.d("gzb", "marker: $marker");
        val request = ListObjectsRequest(Constants.BUCKET_NAME)
        // 填写每页返回文件的最大个数。如果不设置此参数，则默认值为100，maxkeys的取值不能大于1000。
        request.maxKeys = 100
        request.marker = marker
//        request.prefix = "image_library_clwater"
        oss.asyncListObjects(
            request,
            object : OSSCompletedCallback<ListObjectsRequest?, ListObjectsResult> {
                override fun onSuccess(request: ListObjectsRequest?, result: ListObjectsResult) {
                    callback.onResult(request, result)

                    // 最后一页。
                    if (!result.isTruncated) {
                        isCompleted = true
                        return
                    }
                }

                override fun onFailure(
                    request: ListObjectsRequest?,
                    clientException: ClientException,
                    serviceException: ServiceException
                ) {
                    isCompleted = true
                    callback.onFail(request, clientException, serviceException)
                    // 请求异常。
                    if (clientException != null) {
                        // 客户端异常，例如网络异常等。
                        clientException.printStackTrace()
                    }
                    if (serviceException != null) {
                        // 服务端异常。
                        Log.e("gzb", serviceException.errorCode)
                        Log.e("gzb", serviceException.requestId)
                        Log.e("gzb", serviceException.hostId)
                        Log.e("gzb", serviceException.rawMessage)
                    }
                }
            })

    }

    interface ALiOssCallBack{
        fun onResult(request: ListObjectsRequest?, result: ListObjectsResult)
        fun onFail(request: ListObjectsRequest?,
                   clientException: ClientException,
                   serviceException: ServiceException)
    }
}