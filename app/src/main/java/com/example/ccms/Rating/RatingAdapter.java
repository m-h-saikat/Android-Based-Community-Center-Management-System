package com.example.ccms.Rating;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccms.R;
import com.example.ccms.UserBooking.BookingInfo;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.InfoViewHolder> {

    private Context mContext;
    private List<BookingInfo> mUploads;
    private OnItemClickListener mListener;

    public RatingAdapter(Context context, List<BookingInfo> uploads) {
        mContext = context;
        mUploads = uploads;
    }


    @NonNull
    @Override
    public RatingAdapter.InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycle_rating, parent, false);
        return new InfoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingAdapter.InfoViewHolder holder, int position) {
        BookingInfo uploadCurrent = mUploads.get(position);
        holder.booking_id.setText(uploadCurrent.getBooking_Id());
        holder.booking_shift.setText(uploadCurrent.getShift());
        holder.book_date.setText(uploadCurrent.getBooked_date());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class InfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView booking_id,booking_shift,book_date;
        public InfoViewHolder(View itemView){
            super(itemView);
            booking_id=itemView.findViewById(R.id.booking_id);
            booking_shift=itemView.findViewById(R.id.shift);
            book_date=itemView.findViewById(R.id.booked_date);

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
