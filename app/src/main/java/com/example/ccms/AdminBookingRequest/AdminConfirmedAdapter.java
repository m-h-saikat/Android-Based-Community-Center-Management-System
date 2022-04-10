package com.example.ccms.AdminBookingRequest;

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

public class AdminConfirmedAdapter extends RecyclerView.Adapter<AdminConfirmedAdapter.AdminViewHolder> {
    private Context mContext;
    private List<BookingInfo> mUploads;
    private OnItemClickListener mListener;

    public AdminConfirmedAdapter(Context context, List<BookingInfo> uploads) {
        mContext = context;
        mUploads = uploads;
    }
    @NonNull
    @Override
    public AdminConfirmedAdapter.AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycle_admin_booking, parent, false);
        return new AdminViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminConfirmedAdapter.AdminViewHolder holder, int position) {
        BookingInfo uploadCurrent = mUploads.get(position);
        holder.booking_id.setText(uploadCurrent.getBooking_Id());
        holder.email.setText(uploadCurrent.getEmail());
        holder.book_date.setText(uploadCurrent.getBooked_date());
        holder.status.setText(uploadCurrent.getBooking_status());
        holder.request_date.setText(uploadCurrent.getDate_of_booking_request());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }
    public class AdminViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView booking_id, email, book_date, status, request_date;

        public AdminViewHolder(View itemView) {
            super(itemView);
            booking_id = itemView.findViewById(R.id.id);
            email = itemView.findViewById(R.id.email);
            book_date = itemView.findViewById(R.id.booked_date);
            status = itemView.findViewById(R.id.status);
            request_date = itemView.findViewById(R.id.request_day);


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
