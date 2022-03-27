package com.clwater.oss_android.manager

import com.clwater.oss_android.Constants
import com.clwater.oss_android.model.STSModel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson

/**
 * @author: gengzhibo
 * @date: 2022/3/17
 */
//服务器获取token信息
object  FuelManager {
    fun getToken(callBack: STSModelCallBack){
        val httpAsync = Constants.STS_SERVER_URL
            .httpGet()
            .responseString { request, response, result ->
                when (result) {
                    is Result.Failure -> {
                        val ex = result.getException()
//                        println(ex)
                        callBack.fail()
                    }
                    is Result.Success -> {
                        val data = result.get()
                        callBack.call(Gson().fromJson(data, STSModel::class.java))
                    }
                }
            }

        httpAsync.join()
    }

    interface STSModelCallBack{
        fun call(stsModel: STSModel)
        fun fail()
    }
}