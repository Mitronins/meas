package com.addd.measurements.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.addd.measurements.MeasurementsAPI
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Authorization
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var  APP_TOKEN : String
    private lateinit var APP_PREFERENCES_NAME : String

    private val serviceAPI = MeasurementsAPI.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        APP_TOKEN = getString(R.string.my_token)
        APP_PREFERENCES_NAME = getString(R.string.token)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val mSettings: SharedPreferences = getSharedPreferences(APP_TOKEN, Context.MODE_PRIVATE)
        if (mSettings.contains(APP_PREFERENCES_NAME)) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
        btnLogin.setOnClickListener { goLogin() }
    }

    private fun goLogin() {
        if (editLogin.length() == 0 || editPassword.length() == 0) {
            Toast.makeText(applicationContext, "Пустое поле Логин или Пароль", Toast.LENGTH_SHORT).show()
        } else {
            val call = serviceAPI.performPostCall(editLogin.text.toString(), editPassword.text.toString())

            call.enqueue(object : Callback<Authorization> {

                override fun onFailure(call: Call<Authorization>?, t: Throwable?) {
                    Toast.makeText(applicationContext, "Что-то пошло не так =(", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Authorization>?, response: Response<Authorization>?) {
                    if (response!!.body() == null) {
                        Toast.makeText(applicationContext, "Неправильный логин или пароль", Toast.LENGTH_SHORT).show()
                    } else {
                        val mSettings: SharedPreferences = getSharedPreferences(APP_TOKEN, Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = mSettings.edit()
                        editor.putString(APP_PREFERENCES_NAME, response.body().token)
                        editor.apply()
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }
                }
            })
        }
    }
}