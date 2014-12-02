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
import com.koushikdutta.async.http.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

public class YYCard extends Card {

    protected String mTitleHeader;
    protected OnToggleSwitchListener mListener;
    private String id;
    private String reportId;
    private boolean pickedUp;
    private String pickupTime;
    private String pickupLocation;
    private PickupFragment parentFragment;
    private String studentName;
    private String userImage;
    private YYDroidApplication app;

    public YYCard(Context context, PickupFragment parent, int innerLayout, String reportId, boolean pickedUp, JSONObject jsonObject, YYDroidApplication app) {
        super(context, innerLayout);
        parentFragment = parent;
        this.app = app;

        try {
            mListener = (OnToggleSwitchListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnToggleCheckboxListener");
        }
        this.reportId = reportId;
        JSONObject pickupDetail = null;

        try {
            String pickedUpTime = null;
            JSONObject pickedBy = null;
            JSONObject studentJSON = jsonObject;
            if(jsonObject.has("student")){
                studentJSON = jsonObject.getJSONObject("student");
                pickedUpTime = jsonObject.getString("pickedUpTime");
                pickedBy = jsonObject.getJSONObject("pickedBy");

                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(pickedUpTime);
                    String formattedDate = new SimpleDateFormat("hh:mm a").format(date);
                    this.pickupTime = formattedDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                    this.pickupTime = pickedUpTime;
                }

            } else {
                pickupDetail = studentJSON.getJSONObject("studentPickupDetail");

                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
                String weekDay;
                Calendar calendar = Calendar.getInstance();
                weekDay = dayFormat.format(calendar.getTime()).toLowerCase();
                this.pickupTime = pickupDetail.getString(weekDay + "PickupTime");

            }
            String firstName = studentJSON.getString("firstname");
            String lastName = studentJSON.getString("lastname");
            String pickupLocation = studentJSON.getString("pickupLocation");
            String id = studentJSON.getString("_id");
            String classes = studentJSON.getString("classes");
            String userImage = studentJSON.getString("userImage");

            this.mTitleHeader = firstName + " " + lastName;
            this.studentName = firstName + " " + lastName;
            this.id = id;
            this.pickedUp = pickedUp;
            this.pickupLocation = pickupLocation;
            this.userImage = userImage;

        } catch (JSONException e) {
            e.getMessage();
        }

        init(context);
    }

    public String getPickupTime() {
        return this.pickupTime;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public String getPickupLocation() {
        return this.pickupLocation;
    }

    public String getId() {
        return this.id;
    }

    public boolean getPickedup() {
        return this.pickedUp;
    }

    public String getReportId() {
        return this.reportId;
    }

    public String getUserImage() {
        return app.SERVER_URL + this.userImage;
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
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                mListener.onSwitchToggled((ToggleButton) v, jsonArray, parentFragment);
            }
        });
    }

    private void init(Context context) {
//
//        CardThumbnail cardThumbnail = new CardThumbnail(mContext, R.id.yy_thumb_card_image);
//        cardThumbnail.setDrawableResource(R.drawable.default_user);
//        addCardThumbnail(cardThumbnail);



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
