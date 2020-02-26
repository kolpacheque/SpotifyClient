package com.spotify.client.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.client.PrefStore
import com.spotify.client.R
import com.spotify.client.ui.MainActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : DaggerAppCompatActivity() {

    private lateinit var prefs: PrefStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        createUtils()
        setupUI()
    }

    private fun createUtils() {
        prefs = PrefStore(this)
    }

    private fun setupUI() {
        button_login.setOnClickListener {
            startActivityResult()
        }
    }

    private fun startActivityResult() {
        val spotifyClientId =
            "84ea753e599142b8bace9b63d153227b" // Feel free to use this spotify app

        AuthenticationClient.openLoginActivity(
            this, SPOTIFY_LOGIN_REQUEST,
            AuthenticationRequest.Builder(
                spotifyClientId,
                AuthenticationResponse.Type.TOKEN, Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build().toString()
            )
                .setShowDialog(true)
                .setScopes(arrayOf("user-read-email"))
                .setCampaign("your-campaign-token")
                .build()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = AuthenticationClient.getResponse(resultCode, data)

        if (response.type == AuthenticationResponse.Type.ERROR) {
            Toast.makeText(
                this,
                "Error: ${response.error}",
                Toast.LENGTH_LONG
            )
                .show()
        } else {
            response.accessToken?.let {
                prefs.setAuthToken(it)
                startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }
        }
    }

    companion object {
        const val SPOTIFY_LOGIN_REQUEST = 101
    }
}
