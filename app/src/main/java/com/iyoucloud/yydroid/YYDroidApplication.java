package com.iyoucloud.yydroid;


import android.app.Application;
import android.util.Log;

import com.iyoucloud.yydroid.helper.UrlHelper;
import com.iyoucloud.yydroid.util.OnSocketMessageListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.cookie.Cookie;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

public class YYDroidApplication extends Application implements IOCallback {

    //replace with your server url and port number
    public static final String SERVER_URL = "http://172.20.10.3:3000/";//"http://192.168.1.77:3000";
    public static UrlHelper URL_HELPER;
    private AsyncHttpClient client;
    private PersistentCookieStore cookieStore;
    private SocketIO socket;
    private Map<String, OnSocketMessageListener> listenersMap;

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

    public void connectSocket() {
        try {
            socket = new SocketIO(URL_HELPER.getServerUrl());
            //todo fix index out of bound
            socket.addHeader("Cookie", "yy.sid=" + cookieStore.getCookies().get(0).getValue());

            socket.connect(this);
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }

    }

    public void sendSocketMessage(String message, OnSocketMessageListener listener) {
        listenersMap.put(message + "::success", listener);
        socket.emit(message);
    }

    public void sendSocketMessage(String message, JSONObject jsonObject, OnSocketMessageListener listener) {
        listenersMap.put(message + "::success", listener);
        socket.emit(message, jsonObject);
    }

    @Override
    public void onDisconnect() {
        Log.d("app", "wtf?");
    }

    @Override
    public void onConnect() {

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

}
