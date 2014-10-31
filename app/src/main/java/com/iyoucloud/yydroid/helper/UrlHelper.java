package com.iyoucloud.yydroid.helper;



public class UrlHelper {

    private static String SERVER_URL;

    public UrlHelper(String serverUrl) {
        SERVER_URL = serverUrl;
    }

    private static final String LOGIN_URL = "api/v1/account/login";

    private static final String LOGOUT_URL = "";

    public static String getServerUrl() {
        return SERVER_URL;
    }


    public static String getLoginUrl() {
        return SERVER_URL + LOGIN_URL;
    }

    public static String getUserAccountUrl() {
        return SERVER_URL + "api/v1/account?signature=tempkey";
    }

    public static String getLogoutUrl() {
        return SERVER_URL + LOGOUT_URL;
    }
}
