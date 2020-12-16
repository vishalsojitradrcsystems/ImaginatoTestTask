package com.test.task.mvvm.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.test.task.mvvm.model.LoginTableModel
import com.test.task.mvvm.room.LoginDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class LoginRepository {

    companion object {

        private var loginDatabase: LoginDatabase? = null

        private var loginTableModel: LiveData<LoginTableModel>? = null

        private fun initializeDB(context: Context): LoginDatabase {
            return LoginDatabase.getDatabaseClient(context)
        }

        fun insertData(context: Context, username: String, password: String) {

            loginDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                val loginDetails = LoginTableModel(username, password)
                loginDatabase!!.loginDao().insertData(loginDetails)
            }

        }

        fun getLoginDetails(context: Context, username: String): LiveData<LoginTableModel>? {

            loginDatabase = initializeDB(context)

            loginTableModel = loginDatabase!!.loginDao().getLoginDetails(username)

            return loginTableModel
        }

    }
}