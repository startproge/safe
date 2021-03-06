package com.example.safe.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.safe.R;

import com.example.safe.entity.UserEntity;
import com.example.safe.ui.Emergency.EmergencyActivity;
import com.example.safe.ui.HeadOfSafety.HeadOfSafetyActivity;
import com.example.safe.ui.SecurityAdmin.SecurityAdminActivity;
import com.example.safe.util.JsonUtils;
import com.example.safe.util.Result;
import com.example.safe.util.UrlUtil;
import com.example.safe.vo.DangerVo;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText edtUser;
    private EditText edtPasswd;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor ;
    private String token;
    private UserEntity userEntity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#008dea"));
        }
        setContentView(R.layout.activity_login);
        pref = getSharedPreferences("storage", MODE_PRIVATE);
        editor=pref.edit();
        toolbar = findViewById(R.id.tool_bar_login);
        toolbar.setNavigationOnClickListener(v -> finish());
        edtUser = findViewById(R.id.edt_user);
        edtPasswd = findViewById(R.id.edt_passwd);
    }

    public void loginClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                try {
                    String userName=edtUser.getText().toString();
                    String passWord=edtPasswd.getText().toString();
                    Login(userName,passWord);
                } catch (NumberFormatException e) {
                    new AlertDialog.Builder(LoginActivity.this).setTitle("提示")
                        .setMessage("账号格式错误(•_•)")
                        .setPositiveButton("确定", null)
                        .show();
                }
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    editor.putString("token",token);
                    editor.apply();
                    getUserInf(token);
                    break;
                case 2:
                    int type=userEntity.getType();
                    switch (type) {
                        case 1: {
                            editor.putInt("uid", userEntity.getId());
                            editor.putInt("type", userEntity.getType());
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, EmergencyActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case 2: {
                            editor.putInt("uid", userEntity.getId());
                            editor.putInt("type", userEntity.getType());
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, SecurityAdminActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case 3: {
                            editor.putInt("uid", userEntity.getId());
                            editor.putInt("type", userEntity.getType());
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, SecurityAdminActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case 4: {
                            editor.putInt("uid", userEntity.getId());
                            editor.putInt("type", userEntity.getType());
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, HeadOfSafetyActivity.class);
                            startActivity(intent);
                            break;
                        }
                        default:
                            Toast.makeText(getApplicationContext(), "登录类型错误", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void Login(String userName, String passWord) {
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userName);
            jsonObject.put("password",passWord);
            String data = jsonObject.toString();
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), data);
            Request request = new Request.Builder()
                    .url("http://"+ UrlUtil.url +"/term/user/login")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, @NotNull IOException e) {
                    Log.e("Login调用失败", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Result result = JsonUtils.jsonToObject(response.body().string(), Result.class);
                        if(result.getStatus()==200){
                            token= JsonUtils.jsonToObject(
                                    JsonUtils.objectToJson(result.getData()),
                                    String.class);
                            Log.e("token",token);
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            });
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserInf(String token){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+UrlUtil.url+"/term/user")
                .addHeader("token",token)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, @NotNull IOException e) {
                Log.e("GetUserInf调用失败", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Result result = JsonUtils.jsonToObject(response.body().string(), Result.class);
                    if(result.getStatus()==200){
                       userEntity= JsonUtils.jsonToObject(
                                JsonUtils.objectToJson(result.getData()),
                                UserEntity.class);
                        Log.e("getUserInf",userEntity.getUsername());
                            Message message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                    }
                }
            }
        });
    }

}
