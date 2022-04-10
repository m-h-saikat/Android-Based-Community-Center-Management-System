package com.example.ccms.UserBooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccms.R;

import java.util.List;

public class BookingInfoAdapter extends RecyclerView.Adapter<BookingInfoAdapter.InfoViewHolder> {
    private Context mContext;
    private List<BookingInfo> mUploads;
    private OnItemClickListener mListener;

    public BookingInfoAdapter(Context context, List<BookingInfo> uploads) {
        mContext = context;
        mUploads = uploads;
    }


    @NonNull
    @Override
    public BookingInfoAdapter.InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycle_booking_history, parent, false);
        return new InfoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingInfoAdapter.InfoViewHolder holder, int position) {
        BookingInfo uploadCurrent = mUploads.get(position);
        holder.booking_id.setText(uploadCurrent.getBooking_Id());
        holder.booking_shift.setText(uploadCurrent.getShift());
        holder.book_date.setText(uploadCurrent.getBooked_date());
        holder.status.setText(uploadCurrent.getBooking_status());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class InfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView booking_id,booking_shift,book_date,status;
        public InfoViewHolder(View itemView){
            super(itemView);
            booking_id=itemView.findViewById(R.id.b_id);
            booking_shift=itemView.findViewById(R.id.book_shift);
            book_date=itemView.findViewById(R.id.book_date);
            status=itemView.findViewById(R.id.status);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }
    }



    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
