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

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>{

    private List<CalendarInfo> data;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public CalendarAdapter(List<CalendarInfo> data, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_calendar_item, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.bookid.setText(data.get(position).getBookid());
        holder.bookname.setText(data.get(position).getBooked_name());
        holder.bookdate.setText(data.get(position).getBooked_date());
        holder.booktime.setText(data.get(position).getBooked_time());
        holder.bookemail.setText(data.get(position).getBooked_email());
    }

    @Override
    public int getItemCount() {
        //return data==null?0:data.size();
        return data.size();
    }

    public int removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        return position;
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder{
        TextView bookid, bookname, bookdate, booktime, bookemail;

        CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            bookid = itemView.findViewById(R.id.bookid);
            bookname = itemView.findViewById(R.id.bookname);
            bookdate = itemView.findViewById(R.id.bookdate);
            booktime = itemView.findViewById(R.id.booktime);
            bookemail = itemView.findViewById(R.id.bookemail);
        }
    }

    // convenience method for getting data at click position
    CalendarInfo getItem(int id) {
        return data.get(id);
    }

}
