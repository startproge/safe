package com.example.safe.ui.Emergency;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.safe.R;
import com.example.safe.entity.PhotoEntity;
import com.example.safe.form.DangerForm;
import com.example.safe.ui.HeadOfSafety.AcceptanceInfActivity;
import com.example.safe.util.JsonUtils;
import com.example.safe.util.Result;
import com.example.safe.util.UrlUtil;
import com.example.safe.vo.DangerPhotoVo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class EmergencyInfActivity extends AppCompatActivity {
    private static final int TAKE_PHOTO1 = 1;
    private static final int TAKE_PHOTO2 = 2;
    private static final int TAKE_PHOTO3 = 3;
//    private String url="192.168.43.233:8088";
    private String position;
    private int uid;
    private final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=UTF-8");
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private AMapLocationClient mLocationClient = null;
    private AMapLocationListener mLocationListener = new EmergencyInfActivity.MyAMapLocationListener();
    private AMapLocationClientOption mLocationOption = null;
    private SharedPreferences pref;

    private AppCompatEditText text_riskSource;
    private AppCompatSpinner spinner_type;
    private AppCompatSpinner spinner_level;
    private AppCompatEditText text_description;
    private AppCompatEditText text_measure;
    private AppCompatEditText text_limit;

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private Uri imageUri1;
    private Uri imageUri2;
    private Uri imageUri3;
    private File photo1;
    private File photo2;
    private File photo3;
    private Button button;

    private boolean photoCommit1=false;
    private boolean photoCommit2=false;
    private boolean photoCommit3=false;

    private List<PhotoEntity> photoEntityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#008dea"));
        }
        setContentView(R.layout.activity_dangershoot);
        Toolbar toolbar = findViewById(R.id.tool_bar_login);
        toolbar.setNavigationOnClickListener(v -> finish());

        text_riskSource = findViewById(R.id.riskSourceText);

        image1 =findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        button = findViewById(R.id.confirmButton);
        spinner_type =findViewById(R.id.spinner_type);
        spinner_level = findViewById(R.id.spinner_level);
        text_description = findViewById(R.id.dangerDescriptionText);
        text_measure = findViewById(R.id.dangerMeasureText);
        text_limit = findViewById(R.id.dangerLimitText);

        image1.setOnClickListener(v -> takePhoto1());
        image2.setOnClickListener(v -> takePhoto2());
        image3.setOnClickListener(v -> takePhoto3());

        button.setOnClickListener(v -> confirm());
        init();

        pref = getSharedPreferences("storage", MODE_PRIVATE);
        uid=pref.getInt("uid",-1);

    }

    private void init() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(false);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    private class MyAMapLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    Log.e("getLocation2", aMapLocation.getAddress());
                    position=aMapLocation.getAddress();
                    mLocationClient.stopLocation();
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    List<File> files=new ArrayList<>();
                    if(photoCommit1){
                        files.add(photo1);
                    }
                    if(photoCommit2) {
                        files.add(photo2);
                    }
                    if(photoCommit3){
                        files.add(photo3);
                    }
                    for (int i=0;i<photoEntityList.size();i++){
                        uploadPhoto(files.get(i),"img0"+(i+1),photoEntityList.get(i).getId());
                    }
                    new AlertDialog.Builder(EmergencyInfActivity.this).setTitle("提示")
                            .setMessage("提交成功")
                            .setPositiveButton("确定", null)
                            .show();
                    break;
                case 2:
                    new AlertDialog.Builder(EmergencyInfActivity.this).setTitle("提示")
                            .setMessage("提交失败")
                            .setPositiveButton("确定", null)
                            .show();
                    break;
                default:
                    break;
            }
        }
    };

    private void takePhoto1() {
        //创建File对象，用于储存拍照后的图片
        photo1 = new File(getExternalCacheDir(), "image1.jpg");
        try {
            if (photo1.exists()) {
                photo1.delete();
            }
            photo1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri1 = FileProvider.getUriForFile(EmergencyInfActivity.this,
                    "com.example.cameraalbumtest.fileprovider", photo1);
        } else {
            imageUri1 = Uri.fromFile(photo1);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri1);
        startActivityForResult(intent, TAKE_PHOTO1);
    }

    private void takePhoto2() {
        //创建File对象，用于储存拍照后的图片
        photo2 = new File(getExternalCacheDir(), "image2.jpg");
        try {
            if (photo2.exists()) {
                photo2.delete();
            }
            photo1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri2 = FileProvider.getUriForFile(EmergencyInfActivity.this,
                    "com.example.cameraalbumtest.fileprovider", photo2);
        } else {
            imageUri2 = Uri.fromFile(photo2);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri2);
        startActivityForResult(intent, TAKE_PHOTO2);
    }

    private void takePhoto3() {
        //创建File对象，用于储存拍照后的图片
        photo3 = new File(getExternalCacheDir(), "image3.jpg");
        try {
            if (photo3.exists()) {
                photo3.delete();
            }
            photo3.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri3 = FileProvider.getUriForFile(EmergencyInfActivity.this,
                    "com.example.cameraalbumtest.fileprovider", photo3);
        } else {
            imageUri3 = Uri.fromFile(photo3);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri3);
        startActivityForResult(intent, TAKE_PHOTO3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO1:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri1));
                        image1.setImageBitmap(bitmap);
                        image1.setBackground(null);
                        photoCommit1 = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TAKE_PHOTO2:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri2));
                        image2.setImageBitmap(bitmap);
                        image2.setBackground(null);
                        photoCommit2 = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case TAKE_PHOTO3:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri3));
                        image3.setImageBitmap(bitmap);
                        image3.setBackground(null);
                        photoCommit3 = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void confirm(){
        if(position == null){
            new AlertDialog.Builder(EmergencyInfActivity.this).setTitle("提示")
                    .setMessage("定位失败,请打开位置服务")
                    .setPositiveButton("确定", null)
                    .show();
        }
        else if(photoCommit1==false && photoCommit2==false && photoCommit3==false){
            new AlertDialog.Builder(EmergencyInfActivity.this).setTitle("提示")
                    .setMessage("至少提交一张照片")
                    .setPositiveButton("确定", null)
                    .show();
        }
        else {
            confirmDanger();
        }
//        uploadPhoto();
    }

    public void confirmDanger(){
        List<String> position = getPositions();
        String type = spinner_type.getSelectedItem().toString();
        String riskSource = Objects.requireNonNull(text_riskSource.getText()).toString();
        String level = spinner_level.getSelectedItem().toString();
        String description = Objects.requireNonNull(text_description.getText()).toString();
        String measure = Objects.requireNonNull(text_measure.getText()).toString();
        int timeLimit = Integer.parseInt(Objects.requireNonNull(text_limit.getText()).toString());
        DangerForm dangerForm = new DangerForm(type, riskSource, level,
                description, measure, timeLimit, uid, position);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JsonUtils.objectToJson(dangerForm), MEDIA_TYPE_JSON);
        Request request = new Request.Builder()
                .url("http://"+UrlUtil.url+"/term/danger")
                .method("POST", requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("danger调用接口失败", Objects.requireNonNull(e.getMessage()));
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    Result result = JsonUtils.jsonToObject(response.body().string(), Result.class);
                    DangerPhotoVo dangerPhotoVo= JsonUtils.jsonToObject(
                            JsonUtils.objectToJson(result.getData()),
                            DangerPhotoVo.class);
                    photoEntityList=JsonUtils.jsonToList(
                            JsonUtils.objectToJson(dangerPhotoVo.getPhotoEntityList()),
                            PhotoEntity.class);
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        });
    }

    public void uploadPhoto(File photo,String photoName,int id){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo", photoName, RequestBody.create(photo, MEDIA_TYPE_PNG));
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+ UrlUtil.url+"/term/photo/upload/"+id)
                .method("POST", builder.build())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("photo", response.body().string());
            }
        });
    }

    public List<String> getPositions(){
        List<String> positions = new ArrayList<>();
        if(photoCommit1){
            positions.add(position);
        }
        if(photoCommit2){
            positions.add(position);
        }
        if(photoCommit3){
            positions.add(position);
        }
        return  positions;
    }
}
