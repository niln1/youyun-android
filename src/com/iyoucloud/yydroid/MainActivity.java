package com.iyoucloud.yydroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import com.iyoucloud.yydroid.globals.Globals;
import com.loopj.android.http.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;


import java.io.IOException;

public class MainActivity extends Activity {

    Globals g = (Globals)getApplication();


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("username", "admin");
        params.put("password", "adminpw");
        HttpEntity entity=null;
        try {
             entity = params.getEntity(new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                    Toast.makeText(getApplicationContext(),
                            "200",
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(getApplicationContext(),
                            throwable.getMessage(),
                            Toast.LENGTH_LONG).show();
                }


                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.post(getApplicationContext(), g.SERVER_URL+"api/v1/account/login",  entity, "application/x-www-form-urlencoded", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                Toast.makeText(getApplicationContext(),
                        "200",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getApplicationContext(),
                        throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }


            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
}
