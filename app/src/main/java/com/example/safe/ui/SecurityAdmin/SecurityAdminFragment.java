package com.example.safe.ui.SecurityAdmin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.safe.R;
import com.example.safe.util.Result;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class SecurityAdminFragment extends Fragment {
    private static final int PERMISSON_REQUESTCODE = 0;
    private boolean isNeedCheck = true;
    private static final int TAKE_PHOTO=1;
    private static final int CHOOSE_PHOTO=2;
    private static final int REQUEST_CODE = 3;

//    private String url="10.0.2.2:8088";
    private String url="192.168.43.183:8088";
//    private String url="192.168.43.233:8088";

    protected String[] needPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private TextView rectificationTimeText;
    private TextView rectificationDocumentText;

    private Uri documentUri;
    private Uri imageUri1;
    private Button confirm;
    private File document;
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_danger_rectification, container, false);
        rectificationTimeText=root.findViewById(R.id.rectificationTimeText);
        image1=root.findViewById(R.id.image1);
        image2=root.findViewById(R.id.image2);
        image3=root.findViewById(R.id.image3);
        confirm=root.findViewById(R.id.confirmButton);
        rectificationDocumentText=root.findViewById(R.id.rectificationDocumentText);

        image1.setOnClickListener(v -> takePhoto());

        rectificationTimeText.setOnClickListener(v -> {
            Calendar calendar=Calendar.getInstance();
            new DatePickerDialog( getActivity(), (view, year, month, dayOfMonth) ->
                rectificationTimeText.setText(year+"-"+(month+1)+"-"+dayOfMonth)
                    ,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

//        confirm.setOnClickListener(v -> confirmRectification());
        rectificationDocumentText.setOnClickListener( v-> pickFile());
        return root;
    }

    private void takePhoto() {
        //创建File对象，用于储存拍照后的图片
        File outputImage = new File(getContext().getExternalCacheDir(), "image1.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri1 = FileProvider.getUriForFile(getActivity(),
                    "com.example.cameraalbumtest.fileprovider", outputImage);
        } else {
            imageUri1 = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri1);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri1));
                        image1.setImageBitmap(bitmap);
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
                    String path =getPath(getContext(), documentUri);
                    document = new File(path);
                    Log.e("documentUri",document.getName());
                    String end = document.getName().substring(document.getName().lastIndexOf(".") + 1, document.getName	().length()).toLowerCase();
                    Log.e("fileType",end);
                    if(end.equals("doc")){
                        uploadDoc();
                    }else {
                        Toast.makeText(getActivity(),"请选择.doc文件",Toast.LENGTH_SHORT).show();
                    }
                }
            default:
                break;
        }
    }

    private void confirmRectification() {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONArray jsonArray = new JSONArray();
                jsonArray.put("杭州市");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("position", jsonArray);
                jsonObject.put("type","完成整控");
                jsonObject.put("measure", "111");
                jsonObject.put("uid","1");
                jsonObject.put("did","1");
                String data = jsonObject.toString();
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), data);
                Request request = new Request.Builder()//创建Request 对象。
                        .url("http://10.0.2.2:8088/term/rectification")
                        .post(body)//传递请求体
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                        Log.d("kwwl", "response.code()==" + response.code());
//                        Log.d("kwwl", "response.body().string()==" + response.body().toString());
                        try {
                            JSONObject jsonData = new JSONObject(response.body().toString());
                            Log.d("kwwl", "response.body().string()==" + response.body().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e("kwwl", e.getMessage());
                    }
                });
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

    public void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        this.startActivityForResult(intent, REQUEST_CODE);
    }

    public void uploadDoc(){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("document", document.getName(), RequestBody.create(document, MediaType.parse("application/msword")));
        OkHttpClient okHttpClient = new OkHttpClient();
        /*Request request = new Request.Builder()
                .url("http://192.168.43.233:8088/photo/upload?id=1")
                .post(builder.build())
                .build();*/
        Request request = new Request.Builder()
                .url("http://"+url+"/term//rectification/document/1")
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
            ActivityCompat.requestPermissions(getActivity(),
                    needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(), perm)) {
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
