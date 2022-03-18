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
    private var marker: String? = null
    private var isCompleted = false

    private lateinit var oss: OSS
    fun init(context: Context){
        val credentialProvider = OSSAuthCredentialsProvider(Constants.STS_SERVER_URL)
//        val credentialProvider: OSSCredentialProvider = OSSStsTokenCredentialProvider(
//            Constants.OSS_ACCESS_KEY_ID,
//            Constants.OSS_ACCESS_KEY_SECRET,
//            Constants.OSS_STS_TOKEN
//        )

        oss = OSSClient(context, Constants.endpoint, credentialProvider)
    }

//    // 分页列举所有object
//    fun getAllObject() {
//        do {
//            val task = getObjectList()
//            // 阻塞等待请求完成获取NextMarker，请求下一页时需要将请求的marker设置为上一页请求返回的NextMarker。第一页无需设置。
//            // 示例中通过循环分页列举数据，因此需要阻塞等待请求完成获取NextMarker才能请求下一页数据，实际使用时可根据实际场景判断是否需要阻塞。
//            task.waitUntilFinished()
//        } while (!isCompleted)
//    }

    // 列举一页文件。
    fun getObjectList(callback: ALiOssCallBack) {
        val request = ListObjectsRequest(Constants.BUCKET_NAME)
        // 填写每页返回文件的最大个数。如果不设置此参数，则默认值为100，maxkeys的取值不能大于1000。
        request.maxKeys = 20
        request.marker = marker
        oss.asyncListObjects(
            request,
            object : OSSCompletedCallback<ListObjectsRequest?, ListObjectsResult> {
                override fun onSuccess(request: ListObjectsRequest?, result: ListObjectsResult) {
                    for (objectSummary in result.objectSummaries) {
                        Log.i("ListObjects", Gson().toJson(objectSummary))
                    }
                    callback.onResult(request, result)

                    // 最后一页。
                    if (!result.isTruncated) {
                        isCompleted = true
                        return
                    }
                    // 下一次列举文件的marker。
                    marker = result.nextMarker
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
                        Log.e("ErrorCode", serviceException.errorCode)
                        Log.e("RequestId", serviceException.requestId)
                        Log.e("HostId", serviceException.hostId)
                        Log.e("RawMessage", serviceException.rawMessage)
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