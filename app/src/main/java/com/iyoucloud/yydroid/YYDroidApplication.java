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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

public class YYDroidApplication extends Application implements IOCallback,
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
    private SocketIO socket;
    private Map<String, OnSocketMessageListener> listenersMap;
    private SocketIOClient socketIOClient;

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

    public boolean isAuthed() {
        return !cookieStore.getCookies().isEmpty();
    }

    public AsyncHttpClient getClient() {
        return client;
    }

    public void clearCookie() {
        cookieStore.clear();
    }

    public void connectSocket(OnSocketMessageListener listener) {
        listenersMap.put("connect", listener);
        listenersMap.put("all::failure", listener);

        this.connectSocket();
    }

    private void connectSocket() {
        if(socket != null && socket.isConnected()){
            return;
        }

        try {
            Uri.Builder b = Uri.parse(URL_HELPER.getServerUrl()).buildUpon();
            b.appendQueryParameter("Cookie", "yy.sid=" + cookieStore.getCookies().get(0).getValue());

            SocketIOClient.SocketIORequest request = new SocketIOClient.SocketIORequest(URL_HELPER.getServerUrl());
            request.setHeader("cookie", "yy.sid=" + cookieStore.getCookies().get(0).getValue());

            SocketIOClient.connect(
                    com.koushikdutta.async.http.AsyncHttpClient.getDefaultInstance(),
                    request,
                    this);

//            socket = new SocketIO(URL_HELPER.getServerUrl());
//            //todo fix index out of bound
//            socket.addHeader("Cookie", "yy.sid=" + cookieStore.getCookies().get(0).getValue());
//
//            socket.connect(this);
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
    //    socketIOClient.emit(message);
    //    socket.emit(message);
    }

    public void sendSocketMessage(String message, JSONObject jsonObject, OnSocketMessageListener listener) {
        listenersMap.put(message + "::success", listener);
        listenersMap.put(message + "::fail", listener);

   //     socketIOClient.emit(message, jsonObject);
    }

    public void sendSocketMessage(String message, JSONArray jsonArray, OnSocketMessageListener listener) {
        listenersMap.put(message + "::success", listener);
        listenersMap.put(message + "::fail", listener);

        socketIOClient.emit(message, jsonArray);
    }

    @Override
    public void onDisconnect() {
        OnSocketMessageListener listener = listenersMap.get("disconnect");
        listener.onSocketMessage("disconnected");

        new CountDownTimer(5000,1000){

            @Override
            public void onTick(long milliseconds){

            }

            @Override
            public void onFinish(){
                connectSocket();
            }
        }.start();

    }

    @Override
    public void onConnect() {

        OnSocketMessageListener listener = listenersMap.get("connect");
        listener.onSocketMessage("connected");
    }

    @Override
    public void onMessage(String s, IOAcknowledge ioAcknowledge) {
        Log.d("app", s);
    }

    @Override
    public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {
        Log.d("app", "???");
    }

    @Override
    public void on(String event, IOAcknowledge ioAcknowledge, Object... jsonObject) {
        OnSocketMessageListener listener = listenersMap.get(event);
        listener.onSocketMessage(event, jsonObject);
    }

    @Override
    public void onError(SocketIOException e) {
        Log.e("app", e.getMessage());
    }


    @Override
    public void onConnectCompleted(Exception ex, SocketIOClient client) {
        if (ex != null) {
            ex.printStackTrace();
            return;
        }
        this.socketIOClient = client;
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
        System.out.println();
    }
}
