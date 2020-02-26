package com.spotify.client.di

import com.spotify.client.diswap.ActivityScope
import com.spotify.client.ui.login.LoginActivity
import com.spotify.client.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [LoginActivityModule::class])
    @ActivityScope
    internal abstract fun loginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    @ActivityScope
    internal abstract fun mainActivity(): MainActivity
}