package com.example.safe.ui.SecurityAdmin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.safe.R;
import com.example.safe.entity.PhotoEntity;
import com.example.safe.entity.RectificationEntity;
import com.example.safe.form.RectificationForm;
import com.example.safe.ui.HeadOfSafety.AcceptanceInfActivity;
import com.example.safe.util.JsonUtils;
import com.example.safe.util.Result;
import com.example.safe.util.UrlUtil;
import com.example.safe.vo.RectificationPhotoVo;

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

public class RectificationActivity extends AppCompatActivity {
    private static final int PERMISSON_REQUESTCODE = 0;
    private static final int TAKE_PHOTO1 = 1;
    private static final int TAKE_PHOTO2 = 2;
    private static final int TAKE_PHOTO3 = 3;
    private boolean isNeedCheck = true;
    private static final int REQUEST_CODE = 4;
    private SharedPreferences pref;
    private int dangerId;
    private int uid;
    protected String[] needPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private Uri imageUri1;
    private Uri imageUri2;
    private Uri imageUri3;
    private File photo1;
    private File photo2;
    private File photo3;

    private AppCompatSpinner rectificationTypeSpinner;
//    private TextView rectificationTimeText;
    private TextView rectificationDocumentText;
    private AppCompatEditText rectificationMeasureText;




    private final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=UTF-8");
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private AMapLocationClient mLocationClient = null;
    private AMapLocationListener mLocationListener = new RectificationActivity.MyAMapLocationListener();
    private AMapLocationClientOption mLocationOption = null;

    private Uri documentUri;
    private Button confirm;
    private File document;
    private String position;

    private boolean photoCommit1=false;
    private boolean photoCommit2=false;
    private boolean photoCommit3=false;
    private boolean fileCommit=false;

    private List<PhotoEntity> photoEntityList;
    private RectificationEntity rectificationEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rectification);
        Toolbar toolbar = findViewById(R.id.tool_bar_login);
        toolbar.setNavigationOnClickListener(v -> finish());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#008dea"));
        }
        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image3=findViewById(R.id.image3);
        rectificationTypeSpinner=findViewById(R.id.rectificationTypeSpinner);
        rectificationMeasureText=findViewById(R.id.rectificationMeasureText);
        rectificationDocumentText=findViewById(R.id.rectificationDocumentText);
//        rectificationTimeText=findViewById(R.id.rectificationTimeText);
        confirm=findViewById(R.id.confirmButton);

        Intent intent = getIntent();
        dangerId=intent.getIntExtra("dangerId",-1);
        init();
        pref=getSharedPreferences("storage", MODE_PRIVATE);
        uid=pref.getInt("uid",-1);

        image1.setOnClickListener(v -> takePhoto1());
        image2.setOnClickListener(v -> takePhoto2());
        image3.setOnClickListener(v -> takePhoto3());

//        rectificationTimeText.setOnClickListener(v -> {
//            Calendar calendar=Calendar.getInstance();
//            new DatePickerDialog( this, (view, year, month, dayOfMonth) ->
//                    rectificationTimeText.setText(year+"-"+(month+1)+"-"+dayOfMonth)
//                    ,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
//        });
        rectificationDocumentText.setOnClickListener( v-> pickFile());
        confirm.setOnClickListener(v-> confirm());
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
                    Log.e("getLocation1", aMapLocation.getAddress());
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
            imageUri1 = FileProvider.getUriForFile(this,
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
            imageUri2 = FileProvider.getUriForFile(this,
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
            imageUri3 = FileProvider.getUriForFile(this,
                    "com.example.cameraalbumtest.fileprovider", photo3);
        } else {
            imageUri3 = Uri.fromFile(photo3);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri3);
        startActivityForResult(intent, TAKE_PHOTO3);
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
                    if(fileCommit==true){
                        uploadDoc(rectificationEntity.getId());
                    }
                    new AlertDialog.Builder(RectificationActivity.this).setTitle("提示")
                            .setMessage("提交成功")
                            .setPositiveButton("确定", null)
                            .show();
                    break;
                default:
                    break;
            }
        }
    };
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
                        photoCommit1=true;
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
                        photoCommit2=true;
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
                        photoCommit3=true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    documentUri = data.getData();
                    String path = getPath(this, documentUri);
                    document = new File(path);
                    Log.e("documentUri", document.getName());
                    String end = document.getName().substring(document.getName().lastIndexOf(".") + 1, document.getName().length()).toLowerCase();
                    Log.e("fileType", end);
                    if (end.equals("doc")) {
                        rectificationDocumentText.setText(document.getName());
                        fileCommit=true;
                    } else {
                        Toast.makeText(this, "请选择.doc文件", Toast.LENGTH_SHORT).show();
                    }
                }
            default:
                break;
        }
    }

    private void confirm(){
        if(position == null){
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("定位失败,请打开位置服务")
                    .setPositiveButton("确定", null)
                    .show();
        }
        else if(photoCommit1==false && photoCommit2==false && photoCommit3==false){
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("至少提交一张照片")
                    .setPositiveButton("确定", null)
                    .show();
        }
        else {
            confirmRectification();
        }
//        uploadPhoto();
    }

    private void confirmRectification() {
        List<String> positions = getPositions();
        String type = rectificationTypeSpinner.getSelectedItem().toString();
        String measure = Objects.requireNonNull(rectificationMeasureText.getText()).toString();
        RectificationForm rectificationForm=new RectificationForm(type,measure,uid,dangerId,positions);
        Log.d("confirmRectificationType",type);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JsonUtils.objectToJson(rectificationForm), MEDIA_TYPE_JSON);
        Request request = new Request.Builder()
                .url("http://"+UrlUtil.url+"/term/rectification")
                .method("POST", requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("Rectification调用接口失败", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    Result result = JsonUtils.jsonToObject(response.body().string(), Result.class);
                    RectificationPhotoVo rectificationPhotoVo= JsonUtils.jsonToObject(
                            JsonUtils.objectToJson(result.getData()),
                            RectificationPhotoVo.class);
                    rectificationEntity=JsonUtils.jsonToObject(
                            JsonUtils.objectToJson(rectificationPhotoVo.getEntity()),
                            RectificationEntity.class);
                    Log.e("Rectification",rectificationEntity.toString());
                    photoEntityList=JsonUtils.jsonToList(
                            JsonUtils.objectToJson(rectificationPhotoVo.getPhotoEntityList()),
                            PhotoEntity.class);
                    Log.e("Rectification",photoEntityList.toString());
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        });

    }

    public void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        this.startActivityForResult(intent, REQUEST_CODE);
    }

    public void uploadDoc(int id){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("document", document.getName(), RequestBody.create(document, MediaType.parse("application/msword")));
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+ UrlUtil.url +"/term//rectification/document/"+id)
                .method("POST", builder.build())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("doc", response.body().string());
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

    @SuppressLint("NewApi")
     public String getPath(final Context context, final Uri uri) {

                 final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

                 // DocumentProvider
                 if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                         // ExternalStorageProvider
                        if (isExternalStorageDocument(uri)) {
                                 final String docId = DocumentsContract.getDocumentId(uri);
                                 final String[] split = docId.split(":");
                                 final String type = split[0];

                                if ("primary".equalsIgnoreCase(type)) {
                                         return Environment.getExternalStorageDirectory() + "/" + split[1];
                                     }
                             }
                         // DownloadsProvider
                         else if (isDownloadsDocument(uri)) {

                                 final String id = DocumentsContract.getDocumentId(uri);
                                 final Uri contentUri = ContentUris.withAppendedId(
                                                 Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                                 return getDataColumn(context, contentUri, null, null);
                             }
                         // MediaProvider
                         else if (isMediaDocument(uri)) {
                                 final String docId = DocumentsContract.getDocumentId(uri);
                                 final String[] split = docId.split(":");
                                 final String type = split[0];

                                 Uri contentUri = null;
                                 if ("image".equals(type)) {
                                         contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                     } else if ("video".equals(type)) {
                                         contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                     } else if ("audio".equals(type)) {
                                         contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                                     }

                                 final String selection = "_id=?";
                                 final String[] selectionArgs = new String[]{split[1]};

                                 return getDataColumn(context, contentUri, selection, selectionArgs);
                             }
                     }
                 // MediaStore (and general)
                 else if ("content".equalsIgnoreCase(uri.getScheme())) {
                         return getDataColumn(context, uri, null, null);
                     }
                 // File
                else if ("file".equalsIgnoreCase(uri.getScheme())) {
                         return uri.getPath();
                     }
                 return null;
             }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }

    private void checkPermissions(String... permissions) {
        //获取权限列表
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            //list.toarray将集合转化为数组
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }

    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                isNeedCheck = false;
            }
        }
    }
}
