package com.spotify.client.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class NetworkStateReceiver : BroadcastReceiver() {

    val networkAvailableObservable: Subject<Boolean> = PublishSubject.create<Boolean>()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (isInitialStickyBroadcast) {
            return
        }
        checkNetwork(context)
    }

    private fun checkNetwork(context: Context?) {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo

        networkAvailableObservable.onNext(activeNetwork?.isConnected ?: false)
    }

}