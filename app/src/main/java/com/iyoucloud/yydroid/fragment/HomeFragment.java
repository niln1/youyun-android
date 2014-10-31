package com.iyoucloud.yydroid.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;

import com.iyoucloud.yydroid.R;
import com.iyoucloud.yydroid.YYDroidApplication;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

public class HomeFragment extends Fragment {

    YYDroidApplication app;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        app = (YYDroidApplication)(getActivity().getApplication());
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try{
            CookieManager cookieManager = CookieManager.getInstance();
            String cookie = cookieManager.getCookie(app.URL_HELPER.getServerUrl());
            SocketIO socket = new SocketIO(app.URL_HELPER.getServerUrl());
            //todo fix index out of bound
            String cookieStr = app.getCookies().get(0).toString();
            cookieStr = cookieStr.replace(']',';').replace(':','=').replace("[","");
            socket.addHeader("Cookie", "yy.sid="+app.getCookies().get(0).getValue());
         //   socket.addHeader()
            socket.connect(new IOCallback() {
                @Override
                public void onMessage(JSONObject json, IOAcknowledge ack) {
                    try {
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
                public void on(String event, IOAcknowledge ack, Object... args) {
                    System.out.println("Server triggered event '" + event + "'");
                }
            });

            // This line is cached until the connection is establisched.
        //    socket.send("Hello Server!");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}