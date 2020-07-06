package com.example.safe.ui.EnterPrise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.safe.form.DangerForm;
import com.example.safe.ui.Danger.DangerActivity;
import com.example.safe.util.JsonUtils;
import com.example.safe.util.Result;
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
    private List<DangerVo> dangerList;


    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dangerquery, container, false);
        initData();
        recyclerView = root.findViewById(R.id.recycler_danger);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DangerAdapter(dangerList,getActivity());
        recyclerView.setAdapter(adapter);
        adapter.OnRecycleItemClickListener((view, position) -> {
            Toast.makeText(getActivity(),"You click "+position,Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getActivity(), DangerActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private void initData() {
        dangerList=new ArrayList<>();
    }

    private List<DangerVo> getDangerList(int uid) {
        Log.e("0", "getDangerList");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.43.233:8088/term/dangers/"+uid)
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
}

class DangerAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    private List<DangerVo> dangerList;
    private Context mContext;
    private DangerAdapter.OnRecycleItemClickListener onRecycleItemClickListener = null;

    public DangerAdapter(List<DangerVo> dangerForms, Context context){
        this.dangerList=dangerForms;
        this.mContext=context;
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
        dangerViewHolder.dangerTimeLimitText.setText(""+danger.getTimeLevel());
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

}
