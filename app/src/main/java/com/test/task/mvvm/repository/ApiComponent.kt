package com.test.task.mvvm.repository

import com.test.task.mvvm.model.AppModule
import com.test.task.mvvm.view.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApiClientModule::class, AppModule::class])
interface ApiComponent {
    fun inject(mainActivity: MainActivity?)
}