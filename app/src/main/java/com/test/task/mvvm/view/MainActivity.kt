package com.test.task.mvvm.view

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.test.task.mvvm.MyApplication
import com.test.task.mvvm.R
import com.test.task.mvvm.model.ApiResponseClass
import com.test.task.mvvm.repository.Webservice
import com.test.task.mvvm.utils.getIMEI
import com.test.task.mvvm.utils.getIMSI
import com.test.task.mvvm.utils.hideSoftKeyboard
import com.test.task.mvvm.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private val phonePermissionRequestCode: Int = 101
    lateinit var loginViewModel: LoginViewModel

    var retrofit: Retrofit? = null
        @Inject set


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as MyApplication).getComponent()?.inject(this)

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

//        loginAPI()

        btnAddLogin.setOnClickListener {
            hideSoftKeyboard()

            val strUsername = txtUsername.text.toString().trim()
            val strPassword = txtPassword.text.toString().trim()

            when {
                strUsername.isEmpty() -> {
                    txtUsername.error = getString(R.string.msg_user_name)
                }
                strPassword.isEmpty() -> {
                    txtPassword.error = getString(R.string.msg_password)
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        loadIMEI()
                    }
                }
            }
        }

        btnReadLogin.setOnClickListener {
            hideSoftKeyboard()

            lblReadResponse.text = getString(R.string.empty_data)
            lblUseraname.text = getString(R.string.empty_data)

            val strUsername = txtRoomUsername.text.toString().trim()

            if (strUsername.isNotEmpty()) {
                loginViewModel.getLoginDetails(this@MainActivity, strUsername)
                    ?.observe(this, {

                        if (it == null) {
                            lblReadResponse.text = getString(R.string.lbl_data_not_found)
                            lblUseraname.text = getString(R.string.empty_data)
                        } else {
                            lblUseraname.text = it.Username

                            AlertDialog.Builder(this)
                                .setTitle(it.Username)
                                .setMessage(it.XAcc)
                                .setCancelable(false)
                                .setPositiveButton(
                                    android.R.string.ok
                                ) { _, _ -> //re-request
                                }
                                .show()

                            lblReadResponse.text = getString(R.string.lbl_data_found)
                        }
                    })
            } else {
                Toast.makeText(this, getString(R.string.msg_user_name), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun loadIMEI() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // READ_PHONE_STATE permission has not been granted.
            requestReadPhoneStatePermission()
        } else {
            // READ_PHONE_STATE permission is already been granted.
            loginAPI()
        }
    }

    private fun requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_PHONE_STATE
            )
        ) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            AlertDialog.Builder(this)
                .setTitle("Permission Request")
                .setMessage("Please allow permission to login")
                .setCancelable(false)
                .setPositiveButton(
                    android.R.string.yes
                ) { _, _ -> //re-request
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.READ_PHONE_STATE),
                        phonePermissionRequestCode
                    )
                }
                .show()
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_PHONE_STATE),
                phonePermissionRequestCode
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == phonePermissionRequestCode) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                loginAPI()
            } else alertAlert(getString(R.string.lbl_allow_permission))
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun loginAPI() {

        progress.visibility = View.VISIBLE

        val apiService: Webservice? = retrofit?.create(Webservice::class.java)

        val call: Call<ApiResponseClass>? =
            apiService?.login(this.getIMSI(), this.getIMEI(), "", "")

        call?.enqueue(object : Callback<ApiResponseClass> {
            override fun onResponse(
                call: Call<ApiResponseClass>,
                response: Response<ApiResponseClass>
            ) {
                progress.visibility = View.GONE
                if (response.isSuccessful) {

                    val res = response.body()

                    if (res?.errorCode?.equals("00") == true) {
                        loginViewModel.insertData(
                            this@MainActivity, res.user?.userName ?: "", response.headers()
                                .get("X-Acc")
                                ?: ""
                        )
                        lblInsertResponse.text = getString(R.string.lbl_inserted_successfully)
//                        Toast.makeText(this@MainActivity, res.errorMessage, Toast.LENGTH_LONG)
//                            .show()
                    } else {
                        Toast.makeText(this@MainActivity, res?.errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponseClass>, t: Throwable) {
                progress.visibility = View.GONE
                t.printStackTrace()
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun alertAlert(msg: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.lbl_permission_title))
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                loadIMEI()
            }
            .show()
    }
}