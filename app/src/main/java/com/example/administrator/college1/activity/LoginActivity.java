package com.example.administrator.college1.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.college1.R;
import com.example.administrator.college1.Utils.Local;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivVertift;
    private EditText etUser;
    private EditText etPassword;
    private EditText etVertify;
    private CheckBox cbIsSave;
    private Button btnLogin;
    private String user;
    private String password;
    private String vertift;
    private String cookie = "";
    private String sessionID;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ivVertift.setImageBitmap(((Bitmap) msg.obj));
        }
    };
    private ImageView iv_select;
    private ImageView iv_show;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        init();
        vertiftUpdate();
    }

    private void init() {
        etUser = (EditText) findViewById(R.id.et_user);
        iv_select = (ImageView) findViewById(R.id.iv_select);
        etPassword = (EditText) findViewById(R.id.et_password);
        iv_show = (ImageView) findViewById(R.id.iv_show);
        etVertify = (EditText) findViewById(R.id.et_vertify);
        ivVertift = (ImageView) findViewById(R.id.iv_vertify);
        cbIsSave = (CheckBox) findViewById(R.id.cb_isSave);
        btnLogin = (Button) findViewById(R.id.btn_login);
        iv_select.setOnClickListener(this);
        iv_show.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        ivVertift.setOnClickListener(this);
        Bundle userInfo = Local.getUserInfo(this);
        if (!userInfo.get("password").equals("") && !userInfo.get("user").equals("")){
            etUser.setText(""+userInfo.get("user"));
            etPassword.setText(""+userInfo.get("password"));
        }

    }


   // private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>(); //第一种方法
    //或
    List<Cookie> cookies=new ArrayList<>(); //第二种方法
    private void vertiftUpdate() {
        new Thread(){
            @Override
            public void run() {

                //创建OkHttpClient对象
                   OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new CookieJar() {
                            @Override
                            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                               // cookieStore.put(httpUrl.host(), list); //第一种方法
                                //或
                                cookies.addAll(list);   //第二种方法

                               // System.out.println("cookies-size:"+cookies.size());
                               // System.out.println("cookies:"+cookies.get(0));

                            }

                            @Override
                            public List<Cookie> loadForRequest(HttpUrl httpUrl) {

                               // List<Cookie> cookies = cookieStore.get(httpUrl.host()); //第一种方法



                                    return  cookies != null ? cookies : new ArrayList<Cookie>();
                                    //或
//                                    if (cookies != null)
//                                        return cookies;
//                                    return new ArrayList<Cookie>();
                            }

                        })
                        .build();

                //创建网络请求
                final Request request = new Request.Builder()
                        .get()
                        .url("http://jwc.mmvtc.cn/CheckCode.aspx")
                        .build();

                //创建请求呼叫
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    //无法执行请求时调用
                    @Override
                    public void onFailure(Call call, IOException e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "验证码加载失败", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    //返回HTTP响应时调用
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                       // Log.d(getLocalClassName(),response.toString());

                        if(cookie.isEmpty()){
                            //获取session的操作，session放在cookie头，且取出后含有“；”，取出后为下面的 s （也就是jsesseionid）
                            Headers headers = response.headers();
                            // Log.d("info_headers", "header " + headers);
                            List<String> cookies = headers.values("Set-Cookie");
                            // Log.d("cookies",cookies.toString());
                            cookie = cookies.get(0);
                           // Log.d("cookie",cookie);
                             sessionID = cookie.substring(0, cookie.indexOf(";"));
                           // Log.d("sessionID",sessionID);
                            Local.sessionID = sessionID;
                        }


                        //将响应数据转换为输入流数据
                        InputStream inputStream = response.body().byteStream();
                        //将输入流数据转换为Bitmap位图数据
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                        //创建Message消息并发送
                        Message msg = Message.obtain();
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }
                });

            }
        }.start();
    }

    Boolean bool=true;
    @Override
    public void onClick(View view) {
        user = etUser.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        vertift = etVertify.getText().toString().trim();
        switch (view.getId()){
            case R.id.iv_vertify:
                vertiftUpdate();
                break;
            case R.id.iv_select:

                if (bool){
                    iv_select.setImageResource(R.drawable.a4);
                    bool = false;
                }else {
                    iv_select.setImageResource(R.drawable.a3);
                    bool = true;
                }

                break;
            case R.id.iv_show:

                if (etPassword.getInputType() == InputType.TYPE_CLASS_NUMBER) {
                    etPassword.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

                    iv_show.setImageResource(R.drawable.a5_1);
                } else {
                    iv_show.setImageResource(R.drawable.a5_2);
                    etPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                break;
            case R.id.btn_login:
                if (user.isEmpty())
                    Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
                else if (password.isEmpty())
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                else if (vertift.isEmpty())
                    Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                else{
                    login();
                    if (cbIsSave.isChecked())
                        Local.saveUserInfo(this, user, password);
                    else Local.saveUserInfo(this, "", password);
                }
                break;
        }
    }

    private void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(Local.url + "default2.aspx")
                            .cookie("ASP.NET_SessionId", sessionID.substring(sessionID.indexOf("=") + 1, sessionID.length()))
                            .data("__VIEWSTATE", "dDw3OTkxMjIwNTU7Oz5qFv56B08dbR82AMSOW+P8WDKexA==")
                            .data("TextBox1", user)
                            .data("TextBox2", password)
                            .data("TextBox3", vertift)
                            .data("RadioButtonList1", "学生")
                            .data("Button1", "")
                            .timeout(3000)
                            .post();
                   // Log.d("doc",doc.toString());
                    Element body = doc.body();
                   // Log.d("body",body.toString());
                  //  Log.d("doc.html",doc.html());
                   final String msg = massage(doc.html());
                   if (msg.isEmpty()){
                       Elements as = body.getElementsByTag("a");
                      // Log.d("asAAAA:",as.toString());
                       for (Element a : as){
                           Local.urls.put(a.text(),a.attr("href"));
                       }
                       Local.number = user;
                       Local.loginInfo(LoginActivity.this,true,body.getElementById("xhxm").text());
                       startActivity(new Intent(LoginActivity.this,MainActivity.class));
                       LoginActivity.this.finish();


                   }else {
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                               etPassword.setText("");
                               etVertify.setText("");
                           }
                       });
                   }
                  // Log.i("run:",body.text()+":"+sessionID);
                   vertiftUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String massage(String msg) {
        String str = "";
        //indexOf( )的用方法：返回字符串中indexOf(string)中字串string在父串中首次出现的位置，从0开始！没有返回 -1 ；方便判断和截取字符串！
        if (msg.indexOf("密码错误") != -1){
            str = "如忘记密码，请与教务处联系！";
        }else if (msg.indexOf("验证码不正确") != -1){
            str = "验证码不正确";
        }else if (msg.indexOf("用户名不存在或未按照要求参加教学活动") != -1){
            str = "用户名不存在或未按照要求参加教学活动";
        }
        return str;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){

            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("退出")
                    .setMessage("是否退出？")
                    .setIcon(R.drawable.what)
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
