package com.iyoucloud.yydroid.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import com.iyoucloud.yydroid.R;
import com.iyoucloud.yydroid.YYCard;
import com.iyoucloud.yydroid.YYDroidApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import io.socket.IOAcknowledge;
import io.socket.SocketIO;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


public class PickupFragment extends BaseFragment {

    YYDroidApplication app;
    CardArrayAdapter mCardArrayAdapter;
    CardListView listView;
    Activity activity;


    public PickupFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        app = (YYDroidApplication)(getActivity().getApplication());

        return inflater.inflate(R.layout.fragment_pickup, container, false);

    }


    @Override
    public void on(String event, IOAcknowledge ack, Object... jsonObject) {
        try {
            final JSONArray needToPickupList =
                    ((JSONObject) jsonObject[0]).getJSONArray("needToPickupList");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initCards(needToPickupList);
                }
            });
        } catch (JSONException je) {

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = this.getActivity();


        try{
            CookieManager cookieManager = CookieManager.getInstance();
            String cookie = cookieManager.getCookie(app.URL_HELPER.getServerUrl());
            SocketIO socket = new SocketIO(app.URL_HELPER.getServerUrl());
            //todo fix index out of bound
            socket.addHeader("Cookie", "yy.sid="+app.getCookies().get(0).getValue());
            socket.connect(this);

            // This line is cached until the connection is establisched.
            socket.emit("pickup::teacher::get-report-for-today");
        }catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Card> cards = new ArrayList<Card>();

        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        listView = (CardListView) getActivity().findViewById(R.id.yy_card_list);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);

        }
    }

    private void initCards(JSONArray needToPickupList) {

        ArrayList<Card> cards = new ArrayList<Card>();

        for (int i = 0; i < needToPickupList.length(); i++) {
            try{
                JSONObject studentToPick = (JSONObject)(needToPickupList.get(i));
                String firstName = studentToPick.getString("firstname");
                String lastName = studentToPick.getString("lastname");
                YYCard card = new YYCard(getActivity(), firstName + " " + lastName, R.layout.card_content);

                cards.add(card);
            } catch (JSONException e) {
                Log.e(e.getMessage(), "");
            }

        }
        try {
            mCardArrayAdapter.addAll(cards);
            mCardArrayAdapter.notifyDataSetChanged();
        }catch(Exception e){
            Log.e("",e.getMessage());
        }
    }

}