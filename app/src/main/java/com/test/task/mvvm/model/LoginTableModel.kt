package com.test.task.mvvm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Login")
data class LoginTableModel (

    @ColumnInfo(name = "username")
    var Username: String,

    @ColumnInfo(name = "xAcc")
    var XAcc: String

) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null

}