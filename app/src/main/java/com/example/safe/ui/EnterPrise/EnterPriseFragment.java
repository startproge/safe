package com.example.safe.ui.EnterPrise;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.example.safe.R;
import com.example.safe.entity.EnterpriseEntity;
import com.example.safe.util.Result;
import com.example.safe.vo.RectificationPhotoVo;

import org.json.JSONException;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EnterPriseFragment extends Fragment {

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_infquery, container, false);
        getEnterPriseInf();
        return root;
    }

    private void getEnterPriseInf() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8088/term/enterprise/1")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("getEnterPriseInfError",e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    String json=response.body().string();
                    Log.d("kwwl", "data:" + json);
                    Result result = JSONObject.parseObject(json, Result.class);
//                    EnterpriseEntity inf=JSON.toJavaObject(userJson,User.class);;
                    Log.d("kwwl", "data:" + result.getData().toString());
                    EnterpriseEntity inf=JSONObject.parseObject(result.getData().toString(),EnterpriseEntity.class);
                    Log.d("kwwl", "data:" + inf.getId());
                }
            }
        });
    }
}
