package com.example.myapplication

import android.app.Application
import com.example.domain.di.domainModule
import com.example.di.networkModule
import com.example.di.repositoryModule
import com.example.myapplication.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(networkModule, repositoryModule, domainModule, appModule))
        }
    }
}
