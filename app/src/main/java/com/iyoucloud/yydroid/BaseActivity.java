package com.iyoucloud.yydroid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * The base class for activities
 */
public class BaseActivity extends Activity {

    YYDroidApplication app;
    AsyncHttpClient client;

    // used to store app title
    protected CharSequence mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (YYDroidApplication)getApplication();
        client = app.getClient();
    }

    @Override
    public void onStart() {
        super.onStart();
        authCheck();
    }

    @Override
    public void onResume() {
        super.onResume();
        authCheck();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void clearTextBox(View view) {
        EditText textBox = (EditText) view;
        textBox.setText("");
    }

    private void authCheck() {
        if(!app.isAuthed() && !this.getClass().getName().equals(LoginActivity.class.getName()) ) {
            Log.d(this.getLocalClassName(), "not logged in");
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public Map<String, String> parseJsonResponse(byte[] response) {
        String jsonString = new String(response);
        Gson gson=new Gson();
        Map<String, String> map = new HashMap<String, String>();
        return (Map<String, String>) gson.fromJson(jsonString, map.getClass());
    }
}
