package com.example.safe.ui.SecurityAdmin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.safe.R;
import com.example.safe.util.JsonUtils;
import com.example.safe.util.Result;
import com.example.safe.util.UrlUtil;
import com.example.safe.vo.DangerVo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class SecurityDangerFragment extends Fragment {
    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SearchView searchView;
    private RecyclerView recyclerView;
    private DangerAdapter adapter;
    private List<DangerVo> dangerList =new ArrayList<>();
    private SharedPreferences pref;
    private int type;
    private String token;

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dangerquery, container, false);
        recyclerView = root.findViewById(R.id.recycler_danger);
        searchView=root.findViewById(R.id.searchView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DangerAdapter(dangerList,getActivity());
        recyclerView.setAdapter(adapter);
        adapter.OnRecycleItemClickListener((view, position) -> {
            Intent intent=new Intent(getActivity(), RectificationActivity.class);
            intent.putExtra("dangerId",dangerList.get(position).getId());
            startActivity(intent);
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    adapter.setDangerList(dangerList);
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        pref = getActivity().getSharedPreferences("storage", MODE_PRIVATE);
        type=pref.getInt("type",-1);
        token=pref.getString("token",null);
        Log.e("type",""+type);
        if(type==2){
            getDangerList("others");
        }else if(type==3){
            getDangerList("keynote");
        }
        return root;
    }

    public void search(String text){
        List<DangerVo> resultList=new ArrayList<>();
        for(DangerVo dangerVo:dangerList){
            if(dangerVo.getRiskSource().contains(text)||format.format(dangerVo.getCreateDate()).contains(text)||
                    dangerVo.getStatus().contains(text)||dangerVo.getType().contains(text)){
                resultList.add(dangerVo);
            }
        }
        Log.e("reviewList",""+resultList.size());
        adapter.setDangerList(resultList);
        adapter.notifyDataSetChanged();
    }

    private void getDangerList(String type) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://"+ UrlUtil.url +"/term/dangers/"+type)
                .addHeader("token",token)
                .get()
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
                    Result result = JsonUtils.jsonToObject(response.body().string(), Result.class);
                    dangerList = JsonUtils.jsonToList(
                            JsonUtils.objectToJson(result.getData()),
                            DangerVo.class);
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
                    if(dangerList!=null){
                        adapter.setDangerList(dangerList);
                        adapter.notifyDataSetChanged();
                    }
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
        void OnRecycleItemClickListener(View view, int position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setDangerList(List<DangerVo> dangerList) {
        this.dangerList = dangerList;
    }
}
