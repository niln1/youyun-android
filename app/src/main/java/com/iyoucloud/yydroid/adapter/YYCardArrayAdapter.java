package com.iyoucloud.yydroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.iyoucloud.yydroid.R;
import com.iyoucloud.yydroid.YYCard;
import com.iyoucloud.yydroid.fragment.PickupFragment;
import com.iyoucloud.yydroid.helper.ImageLoadTask;
import com.iyoucloud.yydroid.util.OnToggleSwitchListener;
import com.iyoucloud.yydroid.view.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class YYCardArrayAdapter extends ArrayAdapter<YYCard> {

    private final Context context;
    private ArrayList<YYCard> list;
    protected OnToggleSwitchListener mListener;
    private PickupFragment parentFragment;


    public YYCardArrayAdapter(Context context, int resource, ArrayList<YYCard> objects, PickupFragment parentFragment) {
        super(context, resource, objects);
        this.list = objects;
        this.context = context;
        this.parentFragment = parentFragment;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.card_content, parent, false);

        try {
            mListener = (OnToggleSwitchListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnToggleCheckboxListener");
        }

        final YYCard card = list.get(position);
        TextView nameTextView = (TextView) rowView.findViewById(R.id.yy_thumb_card_student_name);
        nameTextView.setText(card.getStudentName());
        TextView pickupLocTextView = (TextView) rowView.findViewById(R.id.yy_thumb_card_pickup_loc);
        pickupLocTextView.setText(card.getPickupLocation());
        TextView pickupTimeTextView = (TextView) rowView.findViewById(R.id.yy_thumb_card_pickup_time);
        pickupTimeTextView.setText(card.getPickupTime());
        RoundedImageView imageView = (RoundedImageView) rowView.findViewById(R.id.yy_thumb_card_profile_image);
        imageView.setImageResource(R.drawable.icon);
        new ImageLoadTask(card.getUserImage(), imageView).execute(null, null);


        final ToggleButton toggleButton = (ToggleButton) rowView.findViewById(R.id.toggleButton_pickup);
        toggleButton.setChecked(card.getPickedup());

        toggleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("reportID", card.getReportId());
                    jsonObject.put("studentID", card.getId());
                    jsonObject.put("pickedUp", String.valueOf(((ToggleButton)v).isChecked()));
                } catch (JSONException e) {

                }
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                mListener.onSwitchToggled((ToggleButton) v, jsonArray, parentFragment);
            }
        });


        return rowView;
    }

    public void addAll(ArrayList<YYCard> cards) {
        this.list.addAll(cards);
    }
}
