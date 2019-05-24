package com.miancky.hvislar.Complementary;

import android.content.Context;
import android.widget.Toast;

import com.miancky.hvislar.R;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {
    public static String hashString(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return new String(digest.digest(string.getBytes(Charset.forName("UTF-8"))));
    }

    public static boolean checkPassword(String password, Context context){
        if(password.length() < 5){
            Toast.makeText(context,context.getString(R.string.password_too_short),Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean checkEmail(String email, Context context){
        if(email.contains("@") && email.indexOf("@") == email.lastIndexOf("@") &&
                email.contains(".") && email.indexOf(".") == email.lastIndexOf(".") &&
                email.indexOf(".") > email.indexOf("@") && email.indexOf("@") > 2 &&
                email.length() - email.indexOf(".") > 1) return true;
        Toast.makeText(context,context.getString(R.string.incorrect_email),Toast.LENGTH_LONG).show();
        return false;
    }

    public static boolean checkUserName(String name, Context context){
        if(name.length() < 3){
            Toast.makeText(context,context.getString(R.string.password_too_short),Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
