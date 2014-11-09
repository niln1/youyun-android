package com.iyoucloud.yydroid.adapter;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.iyoucloud.yydroid.R;
import com.iyoucloud.yydroid.YYDroidApplication;
import com.iyoucloud.yydroid.helper.ImageLoadTask;
import com.iyoucloud.yydroid.model.NavDrawerItem;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class NavDrawerListAdapter extends ArrayAdapter<NavDrawerItem> {

    Context context;
    List<NavDrawerItem> drawerItemList;
    int layoutResID;
    String profileImageUrl;
    private YYDroidApplication app;

    public NavDrawerListAdapter(Context context, int layoutResourceID,
                               List<NavDrawerItem> listItems, YYDroidApplication app) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.drawerItemList = listItems;
        this.layoutResID = layoutResourceID;
        this.app = app;
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

    public void updateProfile(JSONObject jsonObject) {
        try {
            JSONObject resultObject = jsonObject.getJSONObject("result");
            String userImageUrl = resultObject.getString("userImage");
            String lastName = resultObject.getString("lastname");
            String firstName = resultObject.getString("firstname");

            this.profileImageUrl =  app.SERVER_URL + userImageUrl;
            Activity activity = (Activity) this.context;
            ImageView profileImageView = (ImageView) activity.findViewById(R.id.profile_pic);
            TextView textView = (TextView) activity.findViewById(R.id.text_main_name);
            textView.setText(lastName + " " + firstName);
            new ImageLoadTask(profileImageUrl, profileImageView).execute(null, null);
        } catch (JSONException e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
    }

    private static class DrawerItemHolder {
        TextView itemTitle;
        ImageView icon, profileImage;
        LinearLayout itemLayout, userProfileLayout;
    }
}