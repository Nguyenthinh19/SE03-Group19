package com.example.orderfoodsapp.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.orderfoodsapp.Model.Request;
import com.example.orderfoodsapp.Model.User;

public class Common {
    public static User currentUser;

    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";
    public static Request currentRequest;

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Đã đặt hàng";
        else if (status.equals("1"))
            return "Đang giao";
        else
            return "Đã giao";

    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] infor = connectivityManager.getAllNetworkInfo();
            if (infor != null) {
                for (int i = 0; i < infor.length; i++) {
                    if (infor[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
