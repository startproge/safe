package com.example.safe.ui.Emergency;

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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.safe.R;
import com.example.safe.form.DangerForm;
import com.example.safe.util.JsonUtils;
import com.example.safe.util.Result;
import com.example.safe.vo.DangerVo;

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

public class EmergencyFragment extends Fragment {
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private String url="10.0.2.2:8088";
//    private String url="192.168.43.233:8088";

    private final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=UTF-8");
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

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
    private Button button;
    private File photo1;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dangershoot, container, false);
        text_riskSource = root.findViewById(R.id.riskSourceText);

        image1 = root.findViewById(R.id.image1);
        image2 = root.findViewById(R.id.image2);
        image3 = root.findViewById(R.id.image3);
        button = root.findViewById(R.id.confirmButton);
        spinner_type = root.findViewById(R.id.spinner_type);
        spinner_level = root.findViewById(R.id.spinner_level);
        text_description = root.findViewById(R.id.dangerDescriptionText);
        text_measure = root.findViewById(R.id.dangerMeasureText);
        text_limit = root.findViewById(R.id.dangerLimitText);

        image1.setOnClickListener(v -> takePhoto());
        button.setOnClickListener(v -> confirm());


        return root;
    }

    private void takePhoto() {
        //创建File对象，用于储存拍照后的图片
        photo1 = new File(getContext().getExternalCacheDir(), "image1.jpg");
        try {
            if (photo1.exists()) {
                photo1.delete();
            }
            photo1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri1 = FileProvider.getUriForFile(getActivity(),
                    "com.example.cameraalbumtest.fileprovider", photo1);
        } else {
            imageUri1 = Uri.fromFile(photo1);
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

    private void confirm(){
        confirmDanger();
        uploadPhoto();
    }

    public void confirmDanger(){
        List<String> position = new ArrayList<>();
        position.add("浙江大学城市学院");
        String type = spinner_type.getSelectedItem().toString();
        String riskSource = Objects.requireNonNull(text_riskSource.getText()).toString();
        String level = spinner_level.getTransitionName();
        String description = Objects.requireNonNull(text_description.getText()).toString();
        String measure = Objects.requireNonNull(text_measure.getText()).toString();
        int timeLimit = Integer.parseInt(Objects.requireNonNull(text_limit.getText()).toString());
        int uid = 1;
        DangerForm dangerForm = new DangerForm(type, riskSource, level,
                description, measure, timeLimit, uid, position);
        Log.e("form", dangerForm.toString());
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JsonUtils.objectToJson(dangerForm), MEDIA_TYPE_JSON);
        Request request = new Request.Builder()
                .url("http://"+url+"/term/danger")
                .method("POST", requestBody)
                .build();
        Call call = client.newCall(request);
        Log.e("1", "emergency");
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
                }
            }
        });
    }


    public void uploadPhoto(){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo", "img01.jpg", RequestBody.create(photo1, MEDIA_TYPE_PNG));
        OkHttpClient okHttpClient = new OkHttpClient();
        /*Request request = new Request.Builder()
                .url("http://192.168.43.233:8088/photo/upload?id=1")
                .post(builder.build())
                .build();*/
        Request request = new Request.Builder()
                .url("http://"+url+"/term/photo/upload/1")
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

}
