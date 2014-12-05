package com.iyoucloud.yydroid.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.iyoucloud.yydroid.R;
import com.iyoucloud.yydroid.YYCard;
import com.iyoucloud.yydroid.YYDroidApplication;
import com.iyoucloud.yydroid.adapter.YYCardArrayAdapter;
import com.iyoucloud.yydroid.view.YYListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import info.hoang8f.android.segmented.SegmentedGroup;
import io.socket.SocketIOException;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


public class PickupFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    YYDroidApplication app;
    YYCardArrayAdapter mCardArrayAdapter;
    YYListView listView;
    Activity activity;
    SwipeRefreshLayout swipeLayout;
    ArrayList<YYCard> pickedUpCards;
    ArrayList<YYCard> toPickCards;
    TextView noResultsFound;
    private String name;
    private static final String TAG = "PickupFragment";


    SegmentedGroup segmentedGroup;

    public PickupFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        app = (YYDroidApplication)(getActivity().getApplication());
        pickedUpCards = new ArrayList<YYCard>();
        toPickCards = new ArrayList<YYCard>();
        name = "fragment_pickup";

        return inflater.inflate(R.layout.fragment_pickup, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = this.getActivity();

        swipeLayout = (SwipeRefreshLayout)
                getActivity().findViewById(R.id.swipe_refresh_layout_pickup);
        swipeLayout.setOnRefreshListener(this);

        segmentedGroup = (SegmentedGroup) getActivity().findViewById(R.id.segmentgroup_pickup);
        segmentedGroup.setOnCheckedChangeListener(this);
        segmentedGroup.setTintColor(getResources().getColor(R.color.fg_color),
                getResources().getColor(R.color.school_dark_color));

        ArrayList<YYCard> cards = new ArrayList<YYCard>();

        mCardArrayAdapter = new YYCardArrayAdapter(getActivity(), R.id.yy_card_list, cards, this);

        listView = (YYListView) getActivity().findViewById(R.id.yy_card_list);
        noResultsFound = (TextView) getActivity().findViewById(R.id.yy_no_results);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);

        }
    }

    @Override
    public void onRefresh() {
        app.sendSocketMessage("pickup::teacher::get-report-for-today", this);
    }

    private void updateReportDate(JSONObject jsonObject) {
        if (jsonObject == null) return;

        try {
            String stringDate = jsonObject.getString("date");

            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(stringDate);
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            TextView textView =  (TextView) getActivity().findViewById(R.id.pickup_report_date);
            textView.setText("reported on: " + formattedDate);
            textView.setVisibility(View.VISIBLE);
        } catch(ParseException pe) {


        } catch (JSONException e) {

        }

    }

    private void initCards(JSONObject jsonObject) {

        if(jsonObject == null) {
            noResultsFound.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            return;
        }

        if(jsonObject.length() == 0) {
            noResultsFound.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            return;
        }

        noResultsFound.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);

        try {
            JSONArray needToPickupList = jsonObject.getJSONArray("needToPickupList");
            JSONArray pickedUpList = jsonObject.getJSONArray("pickedUpList");
            String reportId = jsonObject.getString("_id");
            toPickCards.clear();
            pickedUpCards.clear();

            for (int i = 0; i < needToPickupList.length(); i++) {
                JSONObject studentToPick = (JSONObject)(needToPickupList.get(i));
                YYCard card = new YYCard(getActivity(), this, R.layout.card_content, reportId, false, studentToPick, app);
                toPickCards.add(card);
            }

            for (int i = 0; i < pickedUpList.length(); i++) {
                JSONObject studentToPick = (JSONObject)(pickedUpList.get(i));
                YYCard card = new YYCard(getActivity(), this, R.layout.card_content, reportId, true, studentToPick, app);

                pickedUpCards.add(card);
            }

            mCardArrayAdapter.clear();
            if(segmentedGroup.getCheckedRadioButtonId() == R.id.segment_picked_button) {
                mCardArrayAdapter.addAll(pickedUpCards);
            } else if(segmentedGroup.getCheckedRadioButtonId() == R.id.segment_to_pick_button) {
                mCardArrayAdapter.addAll(toPickCards);
            } else {
                //exception
            }

            mCardArrayAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            Log.e(this.getTag(), e.getMessage());
        }
    }

    @Override
    public void onError(SocketIOException e) {
        e.getMessage();
    }
    @Override
    public void onDisconnect() {
        System.out.println();
    }

    @Override
    public void onSocketMessage(String event, final Object... jsonObject) {

        final PickupFragment self = this;
//        if(event.equals("connected") || event.equals("disconnected") || event.equals("connecting")) {
//            final String status = event;
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    TextView textView = (TextView) getActivity().findViewById(R.id.connection_status);
//                    textView.setText(status);
//                }
//            });
//
//            return;
//        }
        if(event.equals("connecting") || event.equals("disconnected")) {
            return;
        }

        if(event.equals("connected")) {
            this.onRefresh();
        }

        if(event.equals("pickup::teacher::pickup-student::success")) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(app.getApplicationContext(),
                            segmentedGroup.getCheckedRadioButtonId() == R.id.segment_to_pick_button ? "picked" : "undo picked",
                            Toast.LENGTH_LONG).show();
                    //update pick table here

                    self.onRefresh();

                }
            });
        } else {

            swipeLayout.setRefreshing(false);

            if(jsonObject.length == 0) {
                return;
            }

            try {

                JSONArray jsonArray = ((JSONArray) jsonObject[0]);
                final JSONObject object = jsonArray.length() > 0
                        ? ((JSONObject) (jsonArray.get(0)))
                        : null;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateReportDate(object);
                        initCards(object);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.segment_picked_button:
                mCardArrayAdapter.clear();
                mCardArrayAdapter.addAll(pickedUpCards);
                mCardArrayAdapter.notifyDataSetChanged();
                break;
            case R.id.segment_to_pick_button:
                mCardArrayAdapter.clear();
                mCardArrayAdapter.addAll(toPickCards);
                mCardArrayAdapter.notifyDataSetChanged();
                break;
        }
    }
}