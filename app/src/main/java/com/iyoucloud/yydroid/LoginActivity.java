package com.iyoucloud.yydroid;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.iyoucloud.yydroid.util.BaseAsyncHttpResponseHandler;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.IOException;

import io.socket.SocketIO;

public class LoginActivity extends BaseActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void login(View view) {


        String username = ((EditText)findViewById(R.id.userNameText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordText)).getText().toString();

        //do login check here

        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);

        HttpEntity entity=null;


        try {
            entity = params.getEntity(new BaseAsyncHttpResponseHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        final LoginActivity self = this;
        client.post(getApplicationContext(), app.URL_HELPER.getLoginUrl(),  entity, "application/x-www-form-urlencoded", new BaseAsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Intent intent = new Intent(self, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getApplicationContext(),
                        throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
