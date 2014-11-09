package com.iyoucloud.yydroid.util;


import android.widget.CheckBox;
import android.widget.Switch;

import org.json.JSONObject;

public interface OnToggleSwitchListener {

    public void onSwitchToggled(Switch view, JSONObject jsonObject);

}
