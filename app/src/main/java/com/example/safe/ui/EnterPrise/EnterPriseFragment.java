package com.example.safe.ui.EnterPrise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.example.safe.R;
import com.example.safe.entity.EnterpriseEntity;
import com.example.safe.ui.LoginActivity;
import com.example.safe.util.Result;
import com.example.safe.util.UrlUtil;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EnterPriseFragment extends Fragment {
    private EnterpriseEntity enterpriseEntity;
    private TextView nameText;
    private TextView codeText;
    private TextView representativeText;
    private TextView telephoneText;
    private TextView addressText;
    private TextView positionText;
    private TextView logoutText;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_infquery, container, false);
        nameText=root.findViewById(R.id.nameText);
        codeText=root.findViewById(R.id.codeText);
        representativeText=root.findViewById(R.id.representativeText);
        telephoneText=root.findViewById(R.id.telephoneText);
        addressText=root.findViewById(R.id.addressText);
        positionText=root.findViewById(R.id.positionText);
        logoutText=root.findViewById(R.id.logoutText);
        logoutText.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), LoginActivity.class);
//            startActivity(intent);
            getActivity().finish();
        });
        getEnterPriseInf(1);
        return root;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    nameText.setText(enterpriseEntity.getName());
                    codeText.setText(enterpriseEntity.getCreditCode());
                    representativeText.setText(enterpriseEntity.getRepresentative());
                    telephoneText.setText(enterpriseEntity.getTelephone());
                    addressText.setText(enterpriseEntity.getAddress());
                    positionText.setText(enterpriseEntity.getPosition());
                    break;
                default:
                    break;
            }
        }
    };

    private void getEnterPriseInf(int id) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+ UrlUtil.url +"/term/enterprise/"+id)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, @NotNull IOException e) {
                Log.e("getEnterPriseInfError",e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    String json=response.body().string();
                    Result result = JSONObject.parseObject(json, Result.class);
                    enterpriseEntity=JSONObject.parseObject(result.getData().toString(),EnterpriseEntity.class);
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        });
    }
}
