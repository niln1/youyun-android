package com.iyoucloud.yydroid;


import android.app.Application;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import com.iyoucloud.yydroid.helper.UrlHelper;
import com.iyoucloud.yydroid.util.OnSocketMessageListener;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.SocketIOClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.cookie.Cookie;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YYDroidApplication extends Application implements
        SocketIOClient.SocketIOConnectCallback,
        SocketIOClient.JSONCallback,
        SocketIOClient.StringCallback,
        SocketIOClient.EventCallback,
        CompletedCallback {

    //replace with your server url and port number
    public static final String SERVER_URL = "http://172.20.10.2:3000";//"http://192.168.1.77:3000";
    public static UrlHelper URL_HELPER;
    private AsyncHttpClient client;
    private PersistentCookieStore cookieStore;
    private Map<String, OnSocketMessageListener> listenersMap;
    private SocketIOClient socketIOClient;
    private CountDownTimer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        listenersMap = new HashMap<String, OnSocketMessageListener>();

        URL_HELPER = new UrlHelper(SERVER_URL);
        client = new AsyncHttpClient();
        // Use the application's context so that memory leakage doesn't occur
        cookieStore = new PersistentCookieStore(getApplicationContext());
        client.setCookieStore(cookieStore);

    }

    public List<Cookie> getCookies() {
        return cookieStore.getCookies();
    }

    public boolean isSocketAlive() {
        return socketIOClient.isConnected();
    }

    public boolean isAuthed() {
        return !cookieStore.getCookies().isEmpty()
                && !cookieStore.getCookies().get(0).isExpired(new Date());
    }

    public AsyncHttpClient getClient() {
        return client;
    }

    public void clearCookie() {
        cookieStore.clear();
    }

    public void connectSocket(OnSocketMessageListener listener) {
        listenersMap.put("connect", listener);
        listenersMap.put("disconnect", listener);
        listenersMap.put("connecting", listener);
        listenersMap.put("all::failure", listener);

        this.connectSocket();
    }

    private void connectSocket() {
        if(socketIOClient != null && socketIOClient.isConnected()){
            return;
        }

        try {
            OnSocketMessageListener listener = listenersMap.get("connecting");
            listener.onSocketMessage("connecting");

            Uri.Builder b = Uri.parse(URL_HELPER.getServerUrl()).buildUpon();
            b.appendQueryParameter("Cookie", "yy.sid=" + cookieStore.getCookies().get(0).getValue());

            SocketIOClient.SocketIORequest request = new SocketIOClient.SocketIORequest(URL_HELPER.getServerUrl());
            request.setHeader("cookie", "yy.sid=" + cookieStore.getCookies().get(0).getValue());

            SocketIOClient.connect(
                    com.koushikdutta.async.http.AsyncHttpClient.getDefaultInstance(),
                    request,
                    this);
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
    }

    public void sendSocketMessage(String message, OnSocketMessageListener listener) {
        listenersMap.put(message + "::success", listener);
        listenersMap.put(message + "::fail", listener);
        JSONArray tmp = new JSONArray();
        tmp.put("");
        socketIOClient.emit(message, tmp);
    }


    public void sendSocketMessage(String message, JSONArray jsonArray, OnSocketMessageListener listener) {
        listenersMap.put(message + "::success", listener);
        listenersMap.put(message + "::fail", listener);

        socketIOClient.emit(message, jsonArray);
    }

    @Override
    public void onConnectCompleted(Exception ex, SocketIOClient client) {
        if ((ex != null && client == null)
                || (ex != null && client != null && !client.isConnected())) {
            ex.printStackTrace();
            if((client == null || !client.isConnected()) && !ex.getClass().getName().equals("java.util.concurrent.TimeoutException")) {
                OnSocketMessageListener listener = listenersMap.get("disconnect");
                listener.onSocketMessage("disconnected");
            }

            connectCountDown();
            return;
        }
        this.socketIOClient = client;

        OnSocketMessageListener listener = listenersMap.get("connect");
        listener.onSocketMessage("connected");

        client.setStringCallback(this);
        client.setEventCallback(this);
        client.setJSONCallback(this);
        client.setClosedCallback(this);
    }

    @Override
    public void onEvent(String event, JSONArray jsonArray) {
        OnSocketMessageListener listener = listenersMap.get(event);
        listener.onSocketMessage(event, jsonArray);
    }

    @Override
    public void onJSON(JSONObject jsonObject) {
        System.out.println("json: " + jsonObject.toString());
    }

    @Override
    public void onString(String s) {

    }

    @Override
    public void onCompleted(Exception e) {
        OnSocketMessageListener listener = listenersMap.get("disconnect");
        listener.onSocketMessage("disconnected123");
        connectCountDown();
    }

    private void connectCountDown() {

        if(timer == null) {
            timer = new CountDownTimer(15000,1000){

                @Override
                public void onTick(long milliseconds){
                    if(socketIOClient != null && socketIOClient.isConnected()) {
                        OnSocketMessageListener listener = listenersMap.get("connect");
                        listener.onSocketMessage("connected1231241251");
                        timer.cancel();
                    }
                }

                @Override
                public void onFinish(){
                    if(socketIOClient == null || !socketIOClient.isConnected()) {
                        connectSocket();
                        start();
                    }

                }
            }.start();
        }

    }
}
