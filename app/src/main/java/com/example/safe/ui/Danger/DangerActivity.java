package com.example.safe.ui.Danger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.safe.R;
import com.example.safe.entity.DangerEntity;
import com.example.safe.util.JsonUtils;
import com.example.safe.util.Result;
import com.example.safe.vo.DangerVo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DangerActivity extends AppCompatActivity {

    private int dangerId;
    private DangerEntity dangerEntity;
    private TextView riskSourceText;
    private TextView dangerTypeText;
    private TextView dangerLevelText;
    private TextView dangerDescriptionText;
    private TextView dangerMeasureText;
    private TextView dangerLimitText;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danger);
        riskSourceText=findViewById(R.id.riskSourceText);
        dangerTypeText=findViewById(R.id.dangerTypeText);
        dangerLevelText=findViewById(R.id.dangerLevelText);
        dangerDescriptionText=findViewById(R.id.dangerDescriptionText);
        dangerMeasureText=findViewById(R.id.dangerMeasureText);
        dangerLimitText=findViewById(R.id.dangerLimitText);

        Intent intent = getIntent();
        dangerId=intent.getIntExtra("dangerId",-1);
        if(dangerId!=-1)
            getDangerInf(dangerId);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    riskSourceText.setText(dangerEntity.getRiskSource());
                    dangerTypeText.setText(String.valueOf(dangerEntity.getType()));
                    dangerLevelText.setText(String.valueOf(dangerEntity.getLevel()));
                    dangerDescriptionText.setText(dangerEntity.getDescription());
                    dangerMeasureText.setText(dangerEntity.getMeasure());
                    dangerLimitText.setText(String.valueOf(dangerEntity.getTimeLimit()));
                    break;
                default:
                    break;
            }
        }
    };

    private void getDangerInf(int dangId){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8088/term/danger/"+dangId)
                .method("GET", null)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("danger调用接口失败", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    Result result = JsonUtils.jsonToObject(response.body().string(), Result.class);
                    Log.e("data", result.toString());
                    dangerEntity=JsonUtils.jsonToObject(
                            JsonUtils.objectToJson(result.getData()),
                            DangerEntity.class);
                    Log.e("data1", dangerEntity.toString());
                    Message message=new Message();
                    message.what=1;
                    handler.sendMessage(message);
                }
            }
        });
    }


}
