package com.example.administrator.college1.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

public class Local {
    //学校官网
    public static String url = "http://jwc.mmvtc.cn/";
    public static String sessionID = "";
    public static String number = "";
    //学校官网连接集合
    public static Map<String,String> urls = new HashMap<>();

    //保存户账号和密码
    public static void saveUserInfo(Context context ,String user, String password){
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user",user);
        editor.putString("password",password);
        editor.commit();
    }

    //获取账号和密码
    public static Bundle getUserInfo (Context context){
        Bundle bundle = new Bundle();
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        bundle.putString("user",sharedPreferences.getString("user",""));
        bundle.putString("password",sharedPreferences.getString("password",""));
        return bundle;
    }

    //保存用户账号和登录状态
    public static void loginInfo(Context context, boolean login, String user){
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin",login);
        editor.putString("user",user);
        editor.commit();
    }
}
