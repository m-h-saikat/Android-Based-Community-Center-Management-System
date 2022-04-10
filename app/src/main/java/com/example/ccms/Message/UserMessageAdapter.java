package com.example.ccms.Message;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ccms.R;
import java.util.List;

public class UserMessageAdapter extends RecyclerView.Adapter<UserMessageAdapter.UserMessageViewHolder> {

    private Context mContext;
    private List<UserMessage> mUploads;
    private OnItemClickListener mListener;

    public UserMessageAdapter(Context context, List<UserMessage> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public UserMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_message, parent, false);
        return new UserMessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserMessageViewHolder holder, int position) {
        UserMessage uploadCurrent = mUploads.get(position);

        holder.textViewUserMessage.setText(uploadCurrent.getUser_Message());
        holder.repliedMessage.setText(uploadCurrent.getReplied_Message());
    }
    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class UserMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewUserMessage;
        public TextView repliedMessage;

        public UserMessageViewHolder(View itemView) {
            super(itemView);

            textViewUserMessage = itemView.findViewById(R.id.text_view_user_message);
            repliedMessage = itemView.findViewById(R.id.text_view_replied);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
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

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            //MenuItem reply = menu.add(Menu.NONE, 1, 1, "Reply");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            //reply.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position);

       // void onReplyClick(int position);
       void onWhatEverClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
