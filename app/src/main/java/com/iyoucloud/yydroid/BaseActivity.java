package com.iyoucloud.yydroid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.iyoucloud.yydroid.fragment.BaseFragment;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

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
            forceLogout();
        }
    }

    protected void forceLogout() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public JSONObject parseJsonResponse(byte[] response) throws JSONException{
        String jsonString = new String(response);
        return new JSONObject(jsonString);
    }
}
