package com.example.cowinguide.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cowinguide.CallBack.OnItemClickListner;
import com.example.cowinguide.R;

import java.util.ArrayList;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    Context context;
    ArrayList<CallLogPojo> arr;
    OnItemClickListner onItemClickListner;

    public CallLogAdapter(Context con, ArrayList<CallLogPojo> ar){
        this.context=con;
        this.arr=ar;
        onItemClickListner= (OnItemClickListner) con;
    }

    public void updateItem(int position, CallLogPojo carListResponse) {
        arr.set(position, carListResponse);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        arr.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arr.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.item_add_product,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        CallLogPojo res=arr.get(position);
        holder.duration.setText(res.getDuaration() + " Min");
        holder.calltype.setText(res.getType()+" Call");
        holder.phoneNumber.setText(res.getNumber());
        holder.Idate.setText(res.getDate());
        holder.ItemLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListner.onClickItem(res);
            }
        });

    }

    @Override
    public int getItemCount(){
        return arr.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView phoneNumber;
        TextView duration;
        TextView Idate;
        TextView calltype;
        LinearLayout ItemLin;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            duration = itemView.findViewById(R.id.duration);
            Idate = itemView.findViewById(R.id.Idate);
            calltype = itemView.findViewById(R.id.calltype);
            ItemLin = itemView.findViewById(R.id.ItemLin);
        }
    }
}
