package com.test.task.mvvm

import android.app.Application
import com.test.task.mvvm.model.AppModule
import com.test.task.mvvm.repository.ApiClientModule
import com.test.task.mvvm.repository.ApiComponent
import com.test.task.mvvm.repository.DaggerApiComponent


class MyApplication : Application() {
    private var mApiComponent: ApiComponent? = null

    override fun onCreate() {
        super.onCreate()
        mApiComponent = DaggerApiComponent.builder()
            .appModule(AppModule(this))
            .apiClientModule(ApiClientModule("http://private-222d3-homework5.apiary-mock.com/api/"))
            .build()
    }

    fun getComponent(): ApiComponent? {
        return mApiComponent
    }
}