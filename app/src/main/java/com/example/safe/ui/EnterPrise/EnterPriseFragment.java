package com.example.safe.ui.EnterPrise;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.safe.R;
import com.example.safe.entity.EnterpriseEntity;
import com.example.safe.vo.RectificationPhotoVo;

import org.json.JSONException;
import org.json.JSONObject;

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
                if(response.isSuccessful()){
//                    Log.d("kwwl","获取数据成功了");
                    Log.d("kwwl","response.code()=="+response.code());
//                    Log.d("kwwl","response.body().string()=="+response.body().string());
                    try {
                        JSONObject jsonData = new JSONObject(response.body().string());
                        Log.d("kwwl",jsonData.get("data").toString());
                        EnterpriseEntity result = (EnterpriseEntity)jsonData.get("data");
                        Log.d("kwwl", "data" + result.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
