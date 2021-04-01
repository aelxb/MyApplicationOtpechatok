package com.example.myapplicationotpechatok

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private var cancellationSignal:CancellationSignal?=null
    private  val autenteficationCallBack: BiometricPrompt.AuthenticationCallback
    get() = @RequiresApi(Build.VERSION_CODES.P)
    object : BiometricPrompt.AuthenticationCallback(){
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            notifyUser("Ошибка при авторизации $errString")
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            notifyUser("Успешная авторизация")
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkBiometricSupport()

        val btn = findViewById<Button>(R.id.btn_auth)
        btn.setOnClickListener{
            val biometricPrompt = BiometricPrompt.Builder(this).setTitle("Заголовок").setSubtitle("Подзаголовок").setDescription("Описание цели").setNegativeButton("Отмена", this.mainExecutor, DialogInterface.OnClickListener{
                dialog, which -> notifyUser("Авторизация отменена")
            }).build()
            biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, autenteficationCallBack)
        }
    }

    private fun getCancellationSignal():CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Пользователь отменил скан")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport():Boolean{
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if(keyguardManager.isKeyguardSecure){
            notifyUser("проверьте настройки")
            return false
        }

        if(ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.USE_BIOMETRIC
                )!=PackageManager.PERMISSION_GRANTED
        ){
            notifyUser("Вы не дали разрешение")
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        } else true
    }
    private fun notifyUser(message:String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}