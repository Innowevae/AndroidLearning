package com.example.sessionmanagementsharedpreference

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class SessionManagement {

    lateinit var pref :SharedPreferences
    lateinit var editor :SharedPreferences.Editor
    lateinit var con : Context
    var PrivateMode : Int = 0
    constructor(con: Context){
        this.con = con
        pref = con.getSharedPreferences(ID_EMAIL, PrivateMode)
        editor = pref.edit()
    }
    companion object{
        val ID_EMAIL = "Login_Preference"
        val IS_Login = "isLoggedin"
        val KEY_USERNAME = "username"
        val KEY_EMAIL ="email"
    }
    fun createLoginSession( username: String, email: String){
        editor.putBoolean(IS_Login, true)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_EMAIL, email)
        editor.commit()


    }
    fun checkLogin(){
        if(!this.isLoggedin()){
            var i : Intent = Intent(con, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            con.startActivity(i)
        }

    }
    fun getUserDetail(): HashMap<String, String>{
        var user:Map<String, String> = HashMap<String, String>()
        (user as HashMap).put(KEY_USERNAME, pref.getString(KEY_USERNAME, null)!!)
        (user as HashMap).put(KEY_EMAIL, pref.getString(KEY_EMAIL, null)!!)
        return user
    }
    fun LogoutUser(){
        editor.clear()
        editor.commit()
        var i : Intent = Intent(con, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        con.startActivity(i)
    }
    fun isLoggedin(): Boolean{
        return pref.getBoolean(IS_Login,false)
    }
}