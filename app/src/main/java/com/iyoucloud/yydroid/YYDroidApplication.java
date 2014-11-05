package com.iyoucloud.yydroid;


import android.app.Application;

import com.iyoucloud.yydroid.helper.UrlHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.cookie.Cookie;

import java.util.List;

public class YYDroidApplication extends Application {

    //replace with your server url and port number
    public static final String SERVER_URL = "http://192.168.1.77:3000/";
    public static UrlHelper URL_HELPER;
    private AsyncHttpClient client;
    private PersistentCookieStore cookieStore;

    @Override
    public void onCreate() {
        super.onCreate();

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
}
