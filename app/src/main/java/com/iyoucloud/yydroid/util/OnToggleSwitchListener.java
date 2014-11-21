package com.iyoucloud.yydroid.util;


import android.app.Fragment;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.iyoucloud.yydroid.fragment.PickupFragment;

import org.json.JSONArray;
import org.json.JSONObject;

public interface OnToggleSwitchListener {

    public void onSwitchToggled(ToggleButton view, JSONArray jsonObject, PickupFragment fragment);

}
