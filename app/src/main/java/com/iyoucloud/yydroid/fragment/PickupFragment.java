package com.iyoucloud.yydroid.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.iyoucloud.yydroid.R;
import com.iyoucloud.yydroid.YYCard;
import com.iyoucloud.yydroid.YYDroidApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


public class PickupFragment extends Fragment {

    YYDroidApplication app;


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


        try{
            CookieManager cookieManager = CookieManager.getInstance();
            String cookie = cookieManager.getCookie(app.URL_HELPER.getServerUrl());
            SocketIO socket = new SocketIO(app.URL_HELPER.getServerUrl());
            //todo fix index out of bound
            socket.addHeader("Cookie", "yy.sid="+app.getCookies().get(0).getValue());
            socket.connect(new IOCallback() {
                @Override
                public void onMessage(JSONObject json, IOAcknowledge ack) {
                    try {
                        Toast.makeText(app.getApplicationContext(),
                                "Server said:" + json.toString(2),
                                Toast.LENGTH_LONG).show();
                        System.out.println("Server said:" + json.toString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onMessage(String data, IOAcknowledge ack) {
                    System.out.println("Server said: " + data);
                }

                @Override
                public void onError(SocketIOException socketIOException) {
                    System.out.println("an Error occured");
                    socketIOException.printStackTrace();
                }

                @Override
                public void onDisconnect() {
                    System.out.println("Connection terminated.");
                }

                @Override
                public void onConnect() {
                    System.out.println("Connection established");
                }

                @Override
                public void on(String event, IOAcknowledge ack, Object... jsonObject) {
                    try {
                        JSONArray needToPickupList =
                                ((JSONObject) jsonObject[0]).getJSONArray("needToPickupList");
                        initCards(needToPickupList);
                    } catch (JSONException je) {

                    }
                }
            });

            // This line is cached until the connection is establisched.
            socket.emit("pickup::teacher::get-report-for-today");
        }catch (Exception e) {
            e.printStackTrace();
        }

   //     initCards();
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

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        CardListView listView = (CardListView) getActivity().findViewById(R.id.yy_card_list);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);

        }
        for(Card card:cards) {
            card.getCardView().refreshCard(card);
        }
    }

}