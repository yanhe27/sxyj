package com.ffcc66.sxyj.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.ffcc66.sxyj.MainActivity;
import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.util.LoadingDialogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private Dialog logindialog;
    private TextView tvForgetPassword, tvRegister;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {      //接收其他子线程的消息
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    LoadingDialogUtils.closeDialog(logindialog);
                    Toast.makeText(LoginActivity.this,"用户登录失败，账号或密码错误",Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    LoadingDialogUtils.closeDialog(logindialog);
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    break;
                case 2:
                    LoadingDialogUtils.closeDialog(logindialog);
                    Toast.makeText(LoginActivity.this,"网络错误，请检查网络设置",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

    }

    private void initView() {

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        tvRegister = findViewById(R.id.tvRegister);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnLogin:
                login();
                break;

        }
    }

    private void login() {
        logindialog = LoadingDialogUtils.createLoadingDialog(LoginActivity.this,"正在登陆");

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        HashMap<String,String> loginparams = new HashMap<String,String>();
        loginparams.put("username",username);
        loginparams.put("password",password);

        OkHttpUtils.post()
                .url("http://192.168.137.1:8080/SXYJServer/LoginServlet")
                .params(loginparams)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Message msg = new Message();
                        msg.what = 2;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!response.toString().equals("null")) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.toString());
                                String name = jsonObject.getString("username");
                                Message msg = new Message();
                                msg.what = 1;
                                msg.obj = name;
                                mHandler.sendMessage(msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {

                            Message msg = new Message();
                            msg.what = 0;
                            mHandler.sendMessage(msg);

                        }
                    }
                });
    }

//    private void Login(final String username, final String password) {
//
//
//        Thread thread=new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                try {
//
//                    StringBuffer stringBuilder = new StringBuffer();
//
//                    String str = "http://192.168.137.1:8080/SXYJServer/LoginServlet";
//                    URL url = new URL(str);
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    httpURLConnection.setRequestMethod("POST");
//                    httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
//                    httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
//                    // 设置请求体的类型是文本类型,表示当前提交的是文本数据
//                    httpURLConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
//
//                    DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());//获取数据输出流输出流
//                    String paramter = "username="+username+"&&password="+password;   //定义请求参数
//                    dataOutputStream.writeBytes(paramter);//将参数输出到服务器
//                    dataOutputStream.flush();
//                    dataOutputStream.close();
//
//                    InputStream inputStream = httpURLConnection.getInputStream();
//
//                    // 读取服务器返回的数据
//                    byte[] bytes = new byte[1024];
//                    int length = 0;
//                    while ((length = inputStream.read(bytes)) > 0) {
//                        String temp = new String(bytes, 0, length);//将我们的字节流转换为String格式
//                        stringBuilder.append(temp);
//                    }
//
//                    inputStream.close();
//
//                    if (!stringBuilder.toString().equals("null")) {
//
//                        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
//                        String name = jsonObject.getString("username");
//                        Message msg = new Message();
//                        msg.what = 1;
//                        msg.obj = name;
//                        mHandler.sendMessage(msg);
//
//                    } else {
//
//                        Message msg = new Message();
//                        msg.what = 0;
//                        mHandler.sendMessage(msg);
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();
//    }
}
