package com.example.safe.ui.EnterPrise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safe.R;
import com.example.safe.ui.Danger.DangerActivity;
import com.example.safe.util.JsonUtils;
import com.example.safe.util.Result;
import com.example.safe.util.UrlUtil;
import com.example.safe.vo.DangerVo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DangerFragment extends Fragment {

    private RecyclerView recyclerView;
    private DangerAdapter adapter;
    private String urlStr = "http://47.98.229.17:8002/blm";
    private List<DangerVo> dangerList =new ArrayList<>();
    private int uid;

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dangerquery, container, false);
        recyclerView = root.findViewById(R.id.recycler_danger);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DangerAdapter(dangerList,getActivity());
        recyclerView.setAdapter(adapter);
        adapter.OnRecycleItemClickListener((view, position) -> {
            Intent intent=new Intent(getActivity(), DangerActivity.class);
            intent.putExtra("dangerId",dangerList.get(position).getId());
            startActivity(intent);
        });
        uid=1;
        getDangerList(uid);
        return root;
    }

    private void getDangerList(int uid) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+ UrlUtil.url +"/term/dangers/"+uid)
                .method("GET", null)
                .build();
        Call call = client.newCall(request);
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
//                    Log.e("data", result.toString());
                    dangerList = JsonUtils.jsonToList(
                            JsonUtils.objectToJson(result.getData()),
                            DangerVo.class);
//                    Log.e("data1", dangerList.get(0).toString());
                    Message message=new Message();
                    message.what=1;
                    handler.sendMessage(message);
                }
            }
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    adapter.setDangerList(dangerList);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };
}

class DangerAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    private List<DangerVo> dangerList;
    private DangerAdapter.OnRecycleItemClickListener onRecycleItemClickListener = null;

    public DangerAdapter(List<DangerVo> dangerForms, Context context){
        this.dangerList=dangerForms;
    }

    static class DangerViewHolder extends RecyclerView.ViewHolder {
        TextView dangerNameText;
        TextView dangerTypeText;
        TextView dangerTimeLimitText;
        TextView dangerStatusText;

        DangerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.dangerNameText=itemView.findViewById(R.id.dangerNameText);
            this.dangerTypeText = itemView.findViewById(R.id.dangerTypeText);
            this.dangerTimeLimitText = itemView.findViewById(R.id.dangerTimeLimitText);
            this.dangerStatusText = itemView.findViewById(R.id.dangerStatusText);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_danger, parent, false);
        RecyclerView.ViewHolder viewHolder = new DangerViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DangerViewHolder dangerViewHolder = (DangerViewHolder) holder;
        DangerVo danger=dangerList.get(position);
        dangerViewHolder.dangerNameText.setText(danger.getRiskSource());
        dangerViewHolder.dangerTypeText.setText(danger.getType());
        dangerViewHolder.dangerTimeLimitText.setText(""+danger.getTimeLimit());
        dangerViewHolder.dangerStatusText.setText(danger.getStatus());
        dangerViewHolder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return dangerList.size();
    }

    @Override
    public void onClick(View v) {
        if (onRecycleItemClickListener != null && v!=null) {
            onRecycleItemClickListener.OnRecycleItemClickListener(v, (Integer) v.getTag());
        }
    }

    public void OnRecycleItemClickListener(DangerAdapter.OnRecycleItemClickListener v) {
        onRecycleItemClickListener = v;
    }

    public interface OnRecycleItemClickListener {
        void OnRecycleItemClickListener(View view,int position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setDangerList(List<DangerVo> dangerList) {
        this.dangerList = dangerList;
    }
}
