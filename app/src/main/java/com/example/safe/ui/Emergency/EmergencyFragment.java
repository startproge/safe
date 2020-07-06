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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.safe.R;
import com.example.safe.entity.DangerEntity;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class EmergencyFragment extends Fragment {
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;

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

        getDangerList();

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

    private List<DangerVo> getDangerList() {
        Log.e("0", "getDangerList");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.43.233:8088/term/dangers/1")
                .method("GET", null)
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
//                    Log.e("dataJson", response.body().string());
                    Result result = JsonUtils.jsonToObject(response.body().string(), Result.class);
                    Log.e("data", result.toString());
                    List<DangerVo> list = JsonUtils.jsonToList(
                            JsonUtils.objectToJson(result.getData()),
                                DangerVo.class);
                    Log.e("data1", list.get(0).toString());
                }
            }
        });

        return null;
    }

    private Result confirm(){
        List<String> position = new ArrayList<>();
        position.add("浙江大学城市学院");
        String type = spinner_type.getSelectedItem().toString();
        String riskSource = text_riskSource.getText().toString();
        String level = spinner_level.getTransitionName();
        String description = text_description.getText().toString();
        String measure = text_measure.getText().toString();
        int timeLimit = Integer.parseInt(text_limit.getText().toString());
        int uid = 1;
        DangerForm dangerForm = new DangerForm(type, riskSource, level,
                description, measure, timeLimit, uid, position);
        Log.e("form", dangerForm.toString());
        return null;
    }

}
