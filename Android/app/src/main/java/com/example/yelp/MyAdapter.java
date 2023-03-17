package com.example.yelp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

//https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    private List<BusinessInfo> data;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyAdapter(List<BusinessInfo> data, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        //View view = LayoutInflater.from(parent.getContext())
        //        .inflate(R.layout.recyclerview_item, parent, false);
        //View view = View.inflate(context, R.layout.recyclerview_item, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tnumber.setText(data.get(position).getSerialNumber());
        Picasso.get().load(data.get(position).getImageUrl()).into(holder.timage);
        holder.tname.setText(data.get(position).getName());
        holder.trating.setText(data.get(position).getRating());
        holder.tdistance.setText(data.get(position).getDistance());
    }

    @Override
    public int getItemCount() {
        //return data==null?0:data.size();
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tnumber, tname, trating, tdistance;
        ImageView timage;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tnumber = itemView.findViewById(R.id.serial_number);
            timage = itemView.findViewById(R.id.business_image);
            tname = itemView.findViewById(R.id.business_name);
            trating = itemView.findViewById(R.id.rating);
            tdistance = itemView.findViewById(R.id.distance);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener.onRecyclerItemClick(getAdapterPosition());
                    }
                }
            });*/
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    BusinessInfo getItem(int id) {
        return data.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
