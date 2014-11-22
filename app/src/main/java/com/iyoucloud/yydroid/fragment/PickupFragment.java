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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;
import io.socket.SocketIOException;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


public class PickupFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    YYDroidApplication app;
    CardArrayAdapter mCardArrayAdapter;
    CardListView listView;
    Activity activity;
    SwipeRefreshLayout swipeLayout;
    ArrayList<Card> pickedUpCards;
    ArrayList<Card> toPickCards;
    private String name;

    SegmentedGroup segmentedGroup;

    public PickupFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        app = (YYDroidApplication)(getActivity().getApplication());
        pickedUpCards = new ArrayList<Card>();
        toPickCards = new ArrayList<Card>();
        name = "fragment_pickup";

        return inflater.inflate(R.layout.fragment_pickup, container, false);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        TextView textView = (TextView) getActivity().findViewById(R.id.connection_status);
        if (app.isSocketAlive()) {
            textView.setText("connected");
        } else {
            textView.setText("disconnected");
        }
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

        ArrayList<Card> cards = new ArrayList<Card>();

        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        listView = (CardListView) getActivity().findViewById(R.id.yy_card_list);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);

        }
        this.onRefresh();
    }

    @Override public void onRefresh() {
        app.sendSocketMessage("pickup::teacher::get-report-for-today", this);
    }

    private void initCards(JSONObject jsonObject) {


        try {
            JSONArray needToPickupList = jsonObject.getJSONArray("needToPickupList");
            JSONArray pickedUpList = jsonObject.getJSONArray("pickedUpList");
            String reportId = jsonObject.getString("_id");
            toPickCards.clear();
            pickedUpCards.clear();

            for (int i = 0; i < needToPickupList.length(); i++) {
                JSONObject studentToPick = (JSONObject)(needToPickupList.get(i));
                YYCard card = new YYCard(getActivity(), this, R.layout.card_content, reportId, false, studentToPick);
                toPickCards.add(card);
            }

            for (int i = 0; i < pickedUpList.length(); i++) {
                JSONObject studentToPick = (JSONObject)(pickedUpList.get(i));
                YYCard card = new YYCard(getActivity(), this, R.layout.card_content, reportId, true, studentToPick);

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
        if(event.equals("connected") || event.equals("disconnected") || event.equals("connecting")) {
            final String status = event;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textView = (TextView) getActivity().findViewById(R.id.connection_status);
                    textView.setText(status);
                }
            });

            return;
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

            try {
                final JSONObject object = ((JSONObject) (((JSONArray) jsonObject[0]).get(0)));
                if(object.length() != 0) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initCards(object);
                        }
                    });
                }
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