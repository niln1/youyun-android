package com.iyoucloud.yydroid.fragment;

import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;

import com.iyoucloud.yydroid.R;
import com.iyoucloud.yydroid.util.OnSocketMessageListener;

import org.json.JSONObject;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;


public class BaseFragment extends Fragment implements IOCallback, SwipeRefreshLayout.OnRefreshListener, OnSocketMessageListener {

    protected String name;

    public String getName() {
        return name;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onMessage(String s, IOAcknowledge ioAcknowledge) {

    }

    @Override
    public void onMessage(JSONObject jsonObject, IOAcknowledge ioAcknowledge) {

    }

    @Override
    public void on(String s, IOAcknowledge ioAcknowledge, Object... objects) {

    }

    @Override
    public void onError(SocketIOException e) {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onSocketMessage(String event, Object... jsonObject) {

    }
}
