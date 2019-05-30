package com.miancky.hvislar.complementary;

import android.content.Context;
import android.widget.Toast;

import com.miancky.hvislar.R;

public class Security {
    public static boolean checkPassword(String password, Context context){
        if(password.length() < 5 || password.length() > 50){
            Toast.makeText(context,context.getString(R.string.password_too_short),Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean checkEmail(String email, Context context){
        if(email.length() < 50 && email.contains("@") && email.indexOf("@") == email.lastIndexOf("@") &&
                email.contains(".") && email.indexOf(".") == email.lastIndexOf(".") &&
                email.indexOf(".") > email.indexOf("@") && email.indexOf("@") > 2 &&
                email.length() - email.indexOf(".") > 1) return true;
        Toast.makeText(context,context.getString(R.string.incorrect_email),Toast.LENGTH_LONG).show();
        return false;
    }

    public static boolean checkUserName(String name, Context context){
        if(name.length() < 3 || name.length() > 40){
            Toast.makeText(context,context.getString(R.string.password_too_short),Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
