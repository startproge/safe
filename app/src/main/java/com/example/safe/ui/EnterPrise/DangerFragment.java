package com.example.safe.ui.EnterPrise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class DangerFragment extends Fragment {

    private RecyclerView recyclerView;
    private DangerAdapter adapter;
    private String urlStr = "http://47.98.229.17:8002/blm";
    private List<DangerForm> dangerList;
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

        return root;
    }

    private void initData() {
        dangerList=new ArrayList<>();
        dangerList.add(new DangerForm(1,"风险源","机械安全","未整改",20));
        dangerList.add(new DangerForm(1,"风险源","烟花爆竹","未整改",20));
        dangerList.add(new DangerForm(1,"风险源","冶金类","未整改",20));
        dangerList.add(new DangerForm(1,"风险源","危险化学品管理","未整改",20));
        dangerList.add(new DangerForm(1,"风险源","机械安全","未整改",20));
        dangerList.add(new DangerForm(1,"风险源","烟花爆竹","未整改",20));
        dangerList.add(new DangerForm(1,"风险源","冶金类","未整改",20));
        dangerList.add(new DangerForm(1,"风险源","危险化学品管理","未整改",20));
        dangerList.add(new DangerForm(1,"风险源","机械安全","未整改",20));
        dangerList.add(new DangerForm(1,"风险源","烟花爆竹","未整改",20));
        dangerList.add(new DangerForm(1,"风险源","冶金类","未整改",20));
        dangerList.add(new DangerForm(1,"风险源","危险化学品管理","未整改",20));
    }

}

class DangerAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    private List<DangerForm> dangerList;
    private Context mContext;
    private DangerAdapter.OnRecycleItemClickListener onRecycleItemClickListener = null;

    public DangerAdapter(List<DangerForm> dangerForms, Context context){
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
        DangerForm danger=dangerList.get(position);
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
