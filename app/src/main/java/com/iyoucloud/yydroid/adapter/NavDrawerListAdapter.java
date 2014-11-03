package com.iyoucloud.yydroid.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyoucloud.yydroid.R;
import com.iyoucloud.yydroid.helper.ImageLoadTask;
import com.iyoucloud.yydroid.model.NavDrawerItem;

import java.util.List;

public class NavDrawerListAdapter extends ArrayAdapter<NavDrawerItem> {

    Context context;
    List<NavDrawerItem> drawerItemList;
    int layoutResID;
    String profileImageUrl;
    ImageView profileImageView;

    public NavDrawerListAdapter(Context context, int layoutResourceID,
                               List<NavDrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.drawerItemList = listItems;
        this.layoutResID = layoutResourceID;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            drawerHolder.itemTitle = (TextView) view
                    .findViewById(R.id.drawer_itemTitle);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);

            drawerHolder.itemLayout = (LinearLayout) view
                    .findViewById(R.id.itemLayout);
            drawerHolder.userProfileLayout = (LinearLayout) view
                    .findViewById(R.id.userProfileLayout);
            drawerHolder.profileImage = (ImageView) view.findViewById(R.id.profile_pic);
            view.setTag(drawerHolder);
        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();

        }

        NavDrawerItem dItem = this.drawerItemList.get(position);

        if (dItem.isUserProfile()) {
            drawerHolder.itemLayout.setVisibility(LinearLayout.INVISIBLE);
            drawerHolder.userProfileLayout.setVisibility(LinearLayout.VISIBLE);
            this.profileImageView = (ImageView) view.findViewById(R.id.profile_pic);
        }
        else {

            drawerHolder.userProfileLayout.setVisibility(LinearLayout.GONE);
            drawerHolder.itemLayout.setVisibility(LinearLayout.VISIBLE);

            drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
                    dItem.getImgResID()));
            drawerHolder.itemTitle.setText(dItem.getItemTitle());

        }
        return view;
    }

    public void updateProfile(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        new ImageLoadTask(profileImageUrl, this.profileImageView).execute(null, null);
    }

    private static class DrawerItemHolder {
        TextView itemTitle;
        ImageView icon, profileImage;
        LinearLayout itemLayout, userProfileLayout;
    }
}