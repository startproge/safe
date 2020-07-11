package com.example.safe.ui.Danger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.safe.R;
import com.example.safe.entity.DangerEntity;
import com.example.safe.entity.PhotoEntity;
import com.example.safe.util.JsonUtils;
import com.example.safe.util.Result;
import com.example.safe.util.UrlUtil;
import com.example.safe.vo.DangerInfoVo;
import com.example.safe.vo.DangerVo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DangerActivity extends AppCompatActivity {

    private int dangerId;
    private DangerInfoVo dangerInf;
    private TextView riskSourceText;
    private TextView dangerTypeText;
    private TextView dangerLevelText;
    private TextView dangerDescriptionText;
    private TextView dangerMeasureText;
    private TextView dangerLimitText;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private List<Integer> photoList=new ArrayList<>();
    private List<PhotoEntity> photoEntityList=new ArrayList<>();
    private Bitmap bitmap;
    int photoNum=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#008dea"));
        }
        setContentView(R.layout.activity_danger);
        Toolbar toolbar = findViewById(R.id.tool_bar_login);
        toolbar.setNavigationOnClickListener(v -> finish());

        riskSourceText=findViewById(R.id.riskSourceText);
        dangerTypeText=findViewById(R.id.dangerTypeText);
        dangerLevelText=findViewById(R.id.dangerLevelText);
        dangerDescriptionText=findViewById(R.id.dangerDescriptionText);
        dangerMeasureText=findViewById(R.id.dangerMeasureText);
        dangerLimitText=findViewById(R.id.dangerLimitText);
        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image3=findViewById(R.id.image3);

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
                    riskSourceText.setText(dangerInf.getRiskSource());
                    dangerTypeText.setText(String.valueOf(dangerInf.getType()));
                    dangerLevelText.setText(String.valueOf(dangerInf.getLevel()));
                    dangerDescriptionText.setText(dangerInf.getDescription());
                    dangerMeasureText.setText(dangerInf.getMeasure());
                    dangerLimitText.setText(String.valueOf(dangerInf.getTimeLimit()));
                    photoNum=photoList.size();
                    for(Integer id:photoList){
                        getPhotoInf(id);
                    }
                    break;
                case 2:
                    for(int i=0;i<photoEntityList.size();i++){
                        if(photoEntityList.get(i).getUrl()!=null)
                         getImage(i,photoEntityList.get(i).getUrl());
                    }
                    break;
                case 3:
                    image1.setImageBitmap(bitmap);
                    break;
                case 4:
                    image2.setImageBitmap(bitmap);
                    break;
                case 5:
                    image3.setImageBitmap(bitmap);
                    break;
                default:
                    break;
            }
        }
    };

    private void getDangerInf(int dangId){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+ UrlUtil.url +"/term/danger/"+dangId)
                .method("GET", null)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("getDangerInf调用接口失败", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    Result result = JsonUtils.jsonToObject(response.body().string(), Result.class);
                    dangerInf=JsonUtils.jsonToObject(
                            JsonUtils.objectToJson(result.getData()),
                            DangerInfoVo.class);
                    if(dangerInf.getPid1()!=null){
                        photoList.add(dangerInf.getPid1());
                    }
                    if(dangerInf.getPid2()!=null){
                        photoList.add(dangerInf.getPid2());
                    }
                    if(dangerInf.getPid3()!=null){
                        photoList.add(dangerInf.getPid3());
                    }
                    Log.e("data1", dangerInf.toString());
                    Message message=new Message();
                    message.what=1;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void getPhotoInf(int pid){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+ UrlUtil.url +"/term/photo/"+pid)
                .method("GET", null)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("getPhotoInf调用接口失败", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    Result result = JsonUtils.jsonToObject(response.body().string(), Result.class);
                    PhotoEntity photoEntity=JsonUtils.jsonToObject(
                            JsonUtils.objectToJson(result.getData()),
                            PhotoEntity.class);
                    photoEntityList.add(photoEntity);
                    photoNum--;
                    if(photoNum==0){
                        Message message=new Message();
                        message.what=2;
                        handler.sendMessage(message);
                    }
                }
            }
        });
    }

    public void getImage(int index,String path) {
        Log.d("imagePath",path);
        String end = path.substring(path.lastIndexOf("/") + 1, path.length()).toLowerCase();
        Log.d("imagePath",end);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+UrlUtil.url+"/term/images/user/"+end)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("getImage调用接口失败", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    InputStream inputStream = response.body().byteStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    Message msg = new Message();
                    msg.what = index+3;
                    handler.sendMessage(msg);
                }
            }
        });

    }
}
