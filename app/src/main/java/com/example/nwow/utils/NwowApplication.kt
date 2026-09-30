package com.example.nwow.utils

import android.app.Application
import android.content.Context
import com.example.nwow.config.API
import com.example.nwow.config.RetrofitHelper
import com.example.nwow.ui.main.nwow.repository.NwowRepository
import com.example.nwow.ui.main.zagger.repository.ZaggerRepository

class NwowApplication : Application() {

    lateinit var zaggerRepository: ZaggerRepository
    lateinit var nwowRepository: NwowRepository

    companion object {
        private lateinit var appContext: Context
        fun getCtx(): Context {
            return appContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext;
        init()
    }

    private fun init() {
        val zaggerApiService = RetrofitHelper.invokeZagger().create(API::class.java)
        val nwowApiService = RetrofitHelper.invokeNwow().create(API::class.java)
        zaggerRepository = ZaggerRepository(zaggerApiService, nwowApiService)
        nwowRepository = NwowRepository(nwowApiService)
    }
}