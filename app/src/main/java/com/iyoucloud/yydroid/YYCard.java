package com.iyoucloud.yydroid;

import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.iyoucloud.yydroid.fragment.PickupFragment;
import com.iyoucloud.yydroid.util.OnToggleSwitchListener;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

public class YYCard extends Card {

    protected String mTitleHeader;
    protected String mTitleMain;
    protected OnToggleSwitchListener mListener;
    private String id;
    private String reportId;
    private boolean pickedUp;
    private String pickupTime;
    private String pickupLocation;
    private PickupFragment parentFragment;
//
//    public YYCard(Context context, String titleHeader, String titleMain) {
//        super(context, R.layout.card_thumbnail_layout);
//        this.mTitleHeader = titleHeader;
//        this.mTitleMain = titleMain;
//        init(context);
//    }
//
//    public YYCard(Context context, String title, int innerLayout, String id, String reportId, boolean pickedUp) {
//        super(context, innerLayout);
//        try {
//            mListener = (OnToggleCheckboxListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + " must implement OnToggleCheckboxListener");
//        }
//        this.mTitleHeader = title;
//        this.id = id;
//        this.reportId = reportId;
//        this.pickedUp = pickedUp;
//        init(context);
//    }


    public YYCard(Context context, PickupFragment parent, int innerLayout, String reportId, boolean pickedUp, JSONObject studentJSON) {
        super(context, innerLayout);
        parentFragment = parent;
        try {
            mListener = (OnToggleSwitchListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnToggleCheckboxListener");
        }
        this.reportId = reportId;

        try {
            String firstName = studentJSON.getString("firstname");
            String lastName = studentJSON.getString("lastname");
            String pickupLocation = studentJSON.getString("pickupLocation");
            String id = studentJSON.getString("_id");
            String classes = studentJSON.getString("classes");
            String userImage = studentJSON.getString("userImage");
            JSONObject pickupDetail = studentJSON.getJSONObject("studentPickupDetail");
            String weekDay;
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

            Calendar calendar = Calendar.getInstance();
            weekDay = dayFormat.format(calendar.getTime()).toLowerCase();
            this.mTitleHeader = firstName + " " + lastName;
            this.id = id;
            this.pickedUp = pickedUp;
            this.pickupTime = pickupDetail.getString(weekDay + "PickupTime");
            this.pickupLocation = pickupLocation;

        } catch (JSONException e) {

        }

        init(context);
    }


    @Override
    public void setupInnerViewElements(final ViewGroup parent, View view) {
        TextView mTitle = (TextView) parent.findViewById(R.id.yy_thumb_card_student_name);
        if (mTitle != null){
            mTitle.setText(mTitleHeader);
        }
        TextView mPickupLocation = (TextView) parent.findViewById(R.id.yy_thumb_card_pickup_loc);
        mPickupLocation.setText(pickupLocation);
        TextView mPickupTime = (TextView) parent.findViewById(R.id.yy_thumb_card_pickup_time);
        mPickupTime.setText(pickupTime);

        final ToggleButton toggleButton = (ToggleButton) parent.findViewById(R.id.toggleButton_pickup);
        toggleButton.setChecked(pickedUp);

        toggleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("reportID", reportId);
                    jsonObject.put("studentID", id);
                    jsonObject.put("pickedUp", String.valueOf(((ToggleButton)v).isChecked()));
                } catch (JSONException e) {

                }
                mListener.onSwitchToggled((ToggleButton) v, jsonObject, parentFragment);
            }
        });
    }

    private void init(Context context) {

        CardThumbnail cardThumbnail = new CardThumbnail(mContext);
        cardThumbnail.setDrawableResource(R.drawable.default_user);
        addCardThumbnail(cardThumbnail);



        //Add ClickListener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(getContext(), "Clicked on student " + mTitleHeader, Toast.LENGTH_SHORT).show();
            }
        });

        //Set the card inner text
    //    setTitle(mTitleMain);

    }
}
