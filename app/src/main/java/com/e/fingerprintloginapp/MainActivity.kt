package com.e.fingerprintloginapp

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.login_page.*
import java.net.Authenticator

class MainActivity : AppCompatActivity() {
    private var cancellationSignal: CancellationSignal? = null

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
    get() =
        object : BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                NotifyUser("Authentication Error")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                NotifyUser("Authentication Success")
                startActivity(Intent(this@MainActivity, LoginPage::class.java))
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                NotifyUser("Authentication Failed")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        checkBiometricSupport()
        button.setOnClickListener {
            val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setDescription("This app uses fingerprint protection")
                .setNegativeButton("Cancel", this.mainExecutor, DialogInterface.OnClickListener { dialogInterface, i ->
                    NotifyUser("Authentication Cancelled")
                }).build()

            biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
        }
    }

    private fun NotifyUser(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            NotifyUser("Authentication was cancelled")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isKeyguardSecure){
            NotifyUser("Authentication System has not be enable in Settings")
            return false
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            NotifyUser("Authentication System has not be enable in Settings")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
        }
    }