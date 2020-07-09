package com.example.safe.ui.HeadOfSafety;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;


import com.example.safe.R;
import com.example.safe.entity.PhotoEntity;
import com.example.safe.entity.RectificationEntity;
import com.example.safe.form.AcceptanceForm;
import com.example.safe.form.RectificationForm;
import com.example.safe.ui.LoginActivity;
import com.example.safe.util.JsonUtils;
import com.example.safe.util.Result;
import com.example.safe.util.UrlUtil;
import com.example.safe.vo.RectificationPhotoVo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AcceptanceActivity extends AppCompatActivity {

    private final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=UTF-8");

    private SharedPreferences pref;
    private AppCompatEditText acceptanceOptionText;
    private AppCompatSpinner acceptanceTypeSpinner;
    private Button confirm;
    private int uid;
    private int rid;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#008dea"));
        }
        setContentView(R.layout.activity_acceptance);
        Toolbar toolbar = findViewById(R.id.tool_bar_login);
        toolbar.setNavigationOnClickListener(v -> finish());

        acceptanceOptionText=findViewById(R.id.acceptanceOptionText);
        acceptanceTypeSpinner=findViewById(R.id.acceptanceTypeSpinner);
        confirm=findViewById(R.id.confirmButton);

        Intent intent = getIntent();
        rid=intent.getIntExtra("rid",-1);

        pref=getSharedPreferences("storage", MODE_PRIVATE);
        uid=pref.getInt("uid",-1);
        token=pref.getString("token","");

        confirm.setOnClickListener(v->{
            if(rid!=-1&uid!=-1&!token.equals(""))
                dangerAcceptance();
            else
                Log.e("Acceptance","Acceptance调用接口失败");
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    new AlertDialog.Builder(AcceptanceActivity.this).setTitle("提示")
                            .setMessage("提交成功")
                            .setPositiveButton("确定", null)
                            .show();
                    break;
                case 2:
                    new AlertDialog.Builder(AcceptanceActivity.this).setTitle("提示")
                            .setMessage("提交失败")
                            .setPositiveButton("确定", null)
                            .show();
                    break;
                default:
                    break;
            }
        }
    };

    private void dangerAcceptance(){
        String type = acceptanceTypeSpinner.getSelectedItem().toString();
        String option = acceptanceOptionText.getText().toString();
        AcceptanceForm acceptanceForm=new AcceptanceForm(option,uid,type,rid);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JsonUtils.objectToJson(acceptanceForm), MEDIA_TYPE_JSON);
        Request request = new Request.Builder()
                .url("http://"+ UrlUtil.url+"/term/acceptance")
                .addHeader("token",token)
                .method("POST", requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Acceptance调用接口失败", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    Result result = JsonUtils.jsonToObject(response.body().string(), Result.class);
                    Log.e("Acceptance",result.toString());
                    if(result.getStatus()==200){
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }else{
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                }
            }
        });

    }
}
