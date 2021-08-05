package com.example.cowinguide.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cowinguide.CallBack.OnConsumerItemClicked;
import com.example.cowinguide.CallBack.OnItemClickListner;
import com.example.cowinguide.R;

import java.util.ArrayList;

public class ConsumerApdater extends RecyclerView.Adapter<ConsumerApdater.ViewHolder> {

    Context context;
    ArrayList<CustomerServicePojo> arr;
    OnConsumerItemClicked onconsumerItemClicked;

    public ConsumerApdater(Context con, ArrayList<CustomerServicePojo> ar){
        this.context=con;
        this.arr=ar;
        onconsumerItemClicked= (OnConsumerItemClicked) con;
    }

    public void updateItem(int position, CustomerServicePojo carListResponse) {
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
        View view=layoutInflater.inflate(R.layout.consumer_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConsumerApdater.ViewHolder holder, int position) {
        CustomerServicePojo res=arr.get(position);

        holder.Caddress.setText(res.getLocation());
        holder.Cdate.setText(res.getDate());
        holder.Cname.setText(res.name);
        holder.Consumer_calltype.setText(res.getCalltype());
        holder.consumer_service_type.setText(res.getServiceType());
        holder.CphoneNumber.setText(res.getNumber());
        
        holder.CItemLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onconsumerItemClicked.onConsumerClickItem(res);
            }
        });
    }

    @Override
    public int getItemCount(){
        return arr.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView CphoneNumber;
        TextView Cname;
        TextView consumer_service_type;
        TextView Cdate;
        TextView Caddress;
        TextView Consumer_calltype;
        LinearLayout CItemLin;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            CphoneNumber = itemView.findViewById(R.id.CphoneNumber);
            Cname = itemView.findViewById(R.id.Cname);
            consumer_service_type = itemView.findViewById(R.id.consumer_service_type);
            Cdate = itemView.findViewById(R.id.Cdate);
            Caddress = itemView.findViewById(R.id.Caddress);
            Consumer_calltype = itemView.findViewById(R.id.Consumer_calltype);
            CItemLin = itemView.findViewById(R.id.CItemLin);
        }
    }
}
