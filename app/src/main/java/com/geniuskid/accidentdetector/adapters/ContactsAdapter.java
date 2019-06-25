package com.geniuskid.accidentdetector.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geniuskid.accidentdetector.R;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    private ArrayList<ContactListModel> myContactList;
    private Context context;

    public ContactsAdapter(Context context, ArrayList<ContactListModel> myContactList) {
        this.myContactList = myContactList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, number;
        AppCompatButton inviteFriend;
        ImageView ivProfilePic;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tvContactName_InviteFragment);
            number = (TextView) view.findViewById(R.id.tvContactNumber_InviteFragment);
            ivProfilePic = (ImageView) view.findViewById(R.id.cvProfileImage_InviteFriendFragment);
//            inviteFriend = view.findViewById(R.id.tvInviteFriend_InviteFriendFragment);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ContactListModel myContact = myContactList.get(position);
        if (!TextUtils.isEmpty(myContact.getName()))
            holder.title.setText(myContact.getName());
        if (!TextUtils.isEmpty(myContact.getNumber()))
            holder.number.setText(myContact.getNumber());
    }


    @Override
    public int getItemCount() {
        return myContactList.size();
    }
}
