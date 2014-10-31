package com.iyoucloud.yydroid.util;


import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

public class BaseAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
    @Override
    public void onSuccess(int i, Header[] headers, byte[] bytes) {

    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

    }
}
