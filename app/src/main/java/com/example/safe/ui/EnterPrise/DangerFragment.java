package com.example.safe.ui.EnterPrise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safe.R;
import com.example.safe.form.dangerForm;
import com.example.safe.ui.Danger.DangerActivity;
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
    private List<dangerForm> dangerList;
    private DangerAdapter dangerAdapter;


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

        getDangerList();

        return root;
    }

    private void initData() {
        dangerList=new ArrayList<>();
        dangerList.add(new dangerForm(1,"风险源","机械安全","未整改",20));
        dangerList.add(new dangerForm(1,"风险源","烟花爆竹","未整改",20));
        dangerList.add(new dangerForm(1,"风险源","冶金类","未整改",20));
        dangerList.add(new dangerForm(1,"风险源","危险化学品管理","未整改",20));
        dangerList.add(new dangerForm(1,"风险源","机械安全","未整改",20));
        dangerList.add(new dangerForm(1,"风险源","烟花爆竹","未整改",20));
        dangerList.add(new dangerForm(1,"风险源","冶金类","未整改",20));
        dangerList.add(new dangerForm(1,"风险源","危险化学品管理","未整改",20));
        dangerList.add(new dangerForm(1,"风险源","机械安全","未整改",20));
        dangerList.add(new dangerForm(1,"风险源","烟花爆竹","未整改",20));
        dangerList.add(new dangerForm(1,"风险源","冶金类","未整改",20));
        dangerList.add(new dangerForm(1,"风险源","危险化学品管理","未整改",20));
    }

    private List<DangerVo> getDangerList(){
        Log.e("0", "getDangerList");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8088/term/dangers/1")
                .method("GET", null)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("danger调用接口失败", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.isSuccessful())
                    Log.e("1", "call success");
                Log.e("response", response.toString());
            }
        });


        return null;
    }

}

class DangerAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    private List<dangerForm> dangerList;
    private Context mContext;
    private DangerAdapter.OnRecycleItemClickListener onRecycleItemClickListener = null;

    public DangerAdapter(List<dangerForm> dangerForms,Context context){
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
        dangerForm danger=dangerList.get(position);
        dangerViewHolder.dangerNameText.setText(danger.getRiskResource());
        dangerViewHolder.dangerTypeText.setText(danger.getDangerType());
        dangerViewHolder.dangerTimeLimitText.setText(""+danger.getDangerTimeLimit());
        dangerViewHolder.dangerStatusText.setText(danger.getDangerStatus());
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
