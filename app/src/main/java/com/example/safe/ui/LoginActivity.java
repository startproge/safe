package com.example.safe.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.safe.R;
import com.example.safe.ui.Emergency.EmergencyActivity;
import com.example.safe.ui.HeadOfSafety.HeadOfSafetyActivity;
import com.example.safe.ui.SecurityAdmin.SecurityAdminActivity;
import com.example.safe.ui.SecurityAdmin.SecurityAdminFragment;

public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText edtUser;
    private EditText edtPasswd;
    private RadioGroup types;

    private String urlStr = "http://47.98.229.17:8002/blm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = findViewById(R.id.tool_bar_login);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edtUser = findViewById(R.id.edt_user);
        edtPasswd = findViewById(R.id.edt_passwd);
        types=(RadioGroup) findViewById(R.id.RadioGroup);
    }

    public void loginClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                try {
                    int userId = Integer.parseInt(edtUser.getText().toString());
//                    loging(userId, edtPasswd.getText().toString());
                    String type=getCheckedItem();
                    if (type.equals("应急局人员")) {
                        Intent  intent = new Intent(this, EmergencyActivity.class);
                        startActivity(intent);
                    }else if(type.equals("安全管理员")){
                        Intent  intent = new Intent(this, SecurityAdminActivity.class);
                        startActivity(intent);
                    }else if(type.equals("安全管理负责人")){
                        Intent  intent = new Intent(this, SecurityAdminActivity.class);
                        startActivity(intent);
                    }else if(type.equals("安全管理验收员")){
                        Intent  intent = new Intent(this, HeadOfSafetyActivity.class);
                        startActivity(intent);
                    }

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
                    break;
                default:
                    break;
            }
        }
    };

    void loging(int id, String passwd) {
        new Thread(() -> {
            Message message = new Message();
            message.what=1;
            handler.sendMessage(message);
        }).start();
    }

    private String getCheckedItem(){
        String result=null;
        RadioButton rb = (RadioButton)findViewById(types.getCheckedRadioButtonId());
        result=rb.getText().toString();
        return result;
    }
}
