package com.example.safe.ui.HeadOfSafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.safe.R;
import com.example.safe.entity.PhotoEntity;
import com.example.safe.entity.RectificationEntity;
import com.example.safe.util.JsonUtils;
import com.example.safe.util.Result;
import com.example.safe.util.UrlUtil;
import com.example.safe.vo.DangerInfoVo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AcceptanceInfActivity extends AppCompatActivity {
    SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private TextView rectificationPeopleText;
    private TextView rectificationTypeSpinner;
    private TextView rectificationMeasureText;
    private TextView rectificationTimeText;
    private TextView rectificationDocumentText;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private Button confirm;

    private int dangerId;
    private RectificationEntity rectificationEntity;
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
        setContentView(R.layout.activity_acceptance_inf);
        Toolbar toolbar = findViewById(R.id.tool_bar_login);
        toolbar.setNavigationOnClickListener(v -> finish());

        Intent intent = getIntent();
        dangerId=intent.getIntExtra("dangerId",-1);
        rectificationPeopleText=findViewById(R.id.rectificationPeopleText);
        rectificationTypeSpinner=findViewById(R.id.rectificationTypeSpinner);
        rectificationMeasureText=findViewById(R.id.rectificationMeasureText);
        rectificationTimeText=findViewById(R.id.rectificationTimeText);
        rectificationDocumentText=findViewById(R.id.rectificationDocumentText);

        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image3=findViewById(R.id.image3);
        confirm=findViewById(R.id.confirmButton);

        if(dangerId!=-1)
            getRectificationInf(dangerId);

        confirm.setOnClickListener(v->{
            Intent intent1=new Intent(this, AcceptanceActivity.class);
            intent1.putExtra("rid",rectificationEntity.getId());
            startActivity(intent1);
        });
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    rectificationPeopleText.setText(String.valueOf(rectificationEntity.getUid()));
                    rectificationTypeSpinner.setText(rectificationEntity.getStatus());
                    rectificationMeasureText.setText(rectificationEntity.getMeasure());
                    rectificationTimeText.setText(timeFormat.format(rectificationEntity.getCreateDate()));
                    String name=rectificationEntity.getDocument();
                    String end = name.substring(name.lastIndexOf("/") + 1, name.length()).toLowerCase();
                    rectificationDocumentText.setText(end);
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
                case 6:
                    new AlertDialog.Builder(AcceptanceInfActivity.this).setTitle("提示")
                            .setMessage("该隐患不存在整改措施")
                            .setPositiveButton("确定", null)
                            .show();
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private void getRectificationInf(int dangerId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+ UrlUtil.url +"/term/rectification/did/"+dangerId)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("getRectificationInf调用接口失败", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    Result result = JsonUtils.jsonToObject(response.body().string(), Result.class);
                    Log.d("getRectificationInf",result.toString());
                    rectificationEntity=JsonUtils.jsonToObject(
                            JsonUtils.objectToJson(result.getData()),
                            RectificationEntity.class);
                   if(rectificationEntity==null){
                       Message message=new Message();
                       message.what=6;
                       handler.sendMessage(message);
                   }
                   else {
                       if(rectificationEntity.getPid1()!=null){
                           photoList.add(rectificationEntity.getPid1());
                       }
                       if(rectificationEntity.getPid2()!=null){
                           photoList.add(rectificationEntity.getPid2());
                       }
                       if(rectificationEntity.getPid3()!=null){
                           photoList.add(rectificationEntity.getPid3());
                       }
                       Log.e("data1", rectificationEntity.toString());
                       Message message=new Message();
                       message.what=1;
                       handler.sendMessage(message);
                   }
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
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(path)
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
