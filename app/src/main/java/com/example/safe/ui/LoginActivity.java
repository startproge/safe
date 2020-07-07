package com.example.safe.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.safe.R;

import com.example.safe.ui.Emergency.EmergencyActivity;
import com.example.safe.ui.HeadOfSafety.HeadOfSafetyActivity;
import com.example.safe.ui.SecurityAdmin.SecurityAdminActivity;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText edtUser;
    private EditText edtPasswd;
    private RadioGroup types;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = findViewById(R.id.tool_bar_login);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtUser = findViewById(R.id.edt_user);
        edtPasswd = findViewById(R.id.edt_passwd);
        types=findViewById(R.id.RadioGroup);
    }

    public void loginClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                try {
                    String userName=edtUser.getText().toString();
                    String passWord=edtPasswd.getText().toString();
//                    Login(userName,passWord);
                    Message message = new Message();
                    message.what=1;
                    handler.sendMessage(message);
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
                    String type=getCheckedItem();
                    if (type.equals("应急局人员")) {
                        Intent  intent = new Intent(LoginActivity.this, EmergencyActivity.class);
                        startActivity(intent);
                    }else if(type.equals("安全管理员")){
                        Intent  intent = new Intent(LoginActivity.this, SecurityAdminActivity.class);
                        startActivity(intent);
                    }else if(type.equals("安全管理负责人")){
                        Intent  intent = new Intent(LoginActivity.this, SecurityAdminActivity.class);
                        startActivity(intent);
                    }else if(type.equals("安全管理验收员")){
                        Intent  intent = new Intent(LoginActivity.this, HeadOfSafetyActivity.class);
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    void Login(String userName, String passWord) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("username",userName)
                .add("password",passWord)
                .build();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8088/term/user/login")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, @NotNull IOException e) {
                Log.e("kwwl",e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    String result = response.body().string();
                    Log.e("kwwl",result);
                    Message message = new Message();
                    message.what=1;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private String getCheckedItem(){
        String result=null;
        RadioButton rb = (RadioButton)findViewById(types.getCheckedRadioButtonId());
        result=rb.getText().toString();
        return result;
    }

}
