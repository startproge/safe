package com.example.safe.ui.SecurityAdmin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class SecurityAdminFragment extends Fragment {
    private static final int TAKE_PHOTO=1;
    private static final int CHOOSE_PHOTO=2;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private TextView rectificationTimeText;
    private Uri imageUri1;
    private Button confirm;
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_danger_rectification, container, false);
        rectificationTimeText=root.findViewById(R.id.rectificationTimeText);
        image1=root.findViewById(R.id.image1);
        image2=root.findViewById(R.id.image2);
        image3=root.findViewById(R.id.image3);
        confirm=root.findViewById(R.id.confirmButton);
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        rectificationTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                new DatePickerDialog( getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        rectificationTimeText.setText(year+"-"+(month+1)+"-"+dayOfMonth);
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRectificationData();
            }
        });
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
            default:
                break;
        }
    }

    private void postRectificationData() {

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
                Log.d("kwwl",data);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), data);
                Request request = new Request.Builder()//创建Request 对象。
                        .url("http://10.0.2.2:8088/term/rectification")
                        .post(body)//传递请求体
                        .build();
                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.d("kwwl", "response.code()==" + response.code());
                        Log.d("kwwl", "response.body().string()==" + response.body().toString());
                        try {
                            JSONObject jsonData = new JSONObject(response.body().toString());
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
}
