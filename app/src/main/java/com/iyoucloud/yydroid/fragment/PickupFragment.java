package com.iyoucloud.yydroid.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.SwipeRefreshLayout;
import com.iyoucloud.yydroid.R;
import com.iyoucloud.yydroid.YYCard;
import com.iyoucloud.yydroid.YYDroidApplication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


public class PickupFragment extends BaseFragment {

    YYDroidApplication app;
    CardArrayAdapter mCardArrayAdapter;
    CardListView listView;
    Activity activity;
    SwipeRefreshLayout swipeLayout;
    String reportId;

    public PickupFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        app = (YYDroidApplication)(getActivity().getApplication());

        return inflater.inflate(R.layout.fragment_pickup, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = this.getActivity();

        swipeLayout = (SwipeRefreshLayout)
                getActivity().findViewById(R.id.swipe_refresh_layout_pickup);
        swipeLayout.setOnRefreshListener(this);

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

    private void initCards(JSONArray needToPickupList, String reportId) {

        ArrayList<Card> cards = new ArrayList<Card>();

        for (int i = 0; i < needToPickupList.length(); i++) {
            try{
                JSONObject studentToPick = (JSONObject)(needToPickupList.get(i));
                String firstName = studentToPick.getString("firstname");
                String lastName = studentToPick.getString("lastname");
                String pickupLocation = studentToPick.getString("pickupLocation");
                String id = studentToPick.getString("_id");
                String classes = studentToPick.getString("classes");
                String userImage = studentToPick.getString("userImage");
                YYCard card = new YYCard(getActivity(), firstName + " " + lastName, R.layout.card_content, id, reportId);

                cards.add(card);
            } catch (JSONException e) {
                Log.e(e.getMessage(), "");
            }

        }
        try {
            mCardArrayAdapter.clear();
            mCardArrayAdapter.addAll(cards);
            mCardArrayAdapter.notifyDataSetChanged();
        }catch(Exception e){
            Log.e("",e.getMessage());
        }
    }


    @Override
    public void onSocketMessage(Object... jsonObject) {
        try {

            swipeLayout.setRefreshing(false);

            final JSONArray needToPickupList =
                    ((JSONObject) jsonObject[0]).getJSONArray("needToPickupList");
            reportId = ((JSONObject) jsonObject[0]).getString("_id");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initCards(needToPickupList, reportId);
                }
            });

        } catch (JSONException je) {

        }
    }

}