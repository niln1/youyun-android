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

    private void initCards(JSONObject jsonObject) {


        try {
            JSONArray needToPickupList = jsonObject.getJSONArray("needToPickupList");
            JSONArray pickedUpList = jsonObject.getJSONArray("pickedUpList");
            String reportId = jsonObject.getString("_id");
            ArrayList<Card> cards = new ArrayList<Card>();

            for (int i = 0; i < needToPickupList.length(); i++) {
                JSONObject studentToPick = (JSONObject)(needToPickupList.get(i));
                YYCard card = new YYCard(getActivity(), R.layout.card_content, reportId, false, studentToPick);
                cards.add(card);
            }
            for (int i = 0; i < pickedUpList.length(); i++) {
                JSONObject studentToPick = (JSONObject)(pickedUpList.get(i));
                YYCard card = new YYCard(getActivity(), R.layout.card_content, reportId, true, studentToPick);

                cards.add(card);
            }

            mCardArrayAdapter.clear();
            mCardArrayAdapter.addAll(cards);
            mCardArrayAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            Log.e(this.getTag(), e.getMessage());
        }
    }


    @Override
    public void onSocketMessage(final Object... jsonObject) {

        swipeLayout.setRefreshing(false);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initCards((JSONObject)jsonObject[0]);
            }
        });

    }

}