package com.moutamid.trip4petadmin.utilis;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.moutamid.trip4petadmin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class Constants {
    public static final String DUMMY_IMAGE = "https://firebasestorage.googleapis.com/v0/b/multistreamz-10cba.appspot.com/o/trip4pet%2Fimages%2F01062024113321?alt=media&token=f2840b63-8f4d-48b8-a04a-db72ca4d9de9";
    public static Dialog dialog;
    public static final String STASH_USER = "STASH_USER";
    public static final String MODEL = "MODEL";
    public static final String CITIES = "CITIES";
    public static final String FILTERS = "FILTERS";
    public static final String PLACES = "PLACES";
    public static final String ISVIP = "ISVIP";
    public static final String PLACE = "PLACE";
    public static final String VISITED = "VISITED";
    public static final String USER = "USER";
    public static final String COORDINATES = "COORDINATES";
    public static final String Metric = "Metric";
    public static final String Vehicle = "Vehicle";
    public static final String Imperial = "Imperial";
    public static final String AROUND_PLACE = "AROUND_PLACE";
    public static final String MEASURE = "MEASURE";
    public static final String EDIT = "EDIT";
    public static final int FAVORITE_SIZE = 200;
    public static final String LANGUAGE = "LANGUAGE";
    public static final String FAVORITE = "FAVORITE";
    public static final String FAVORITE_FOLDER = "FAVORITE_FOLDER";

    public static void initDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
    }

    public static void showDialog() {
        dialog.show();
    }

    public static void dismissDialog() {
        dialog.dismiss();
    }

    public static void setLocale(Context context, String lng) {
        Locale locale = new Locale(lng);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    public static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    public static DatabaseReference databaseReference() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("trip4pet");
        db.keepSynced(true);
        return db;
    }

    public static StorageReference storageReference() {
        return FirebaseStorage.getInstance().getReference().child("trip4pet");
    }

    public static void checkApp(Activity activity) {
        String appName = "trip4petAdmin";

        new Thread(() -> {
            URL google = null;
            try {
                google = new URL("https://raw.githubusercontent.com/Moutamid/Moutamid/main/apps.txt");
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(google != null ? google.openStream() : null));
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String input = null;
            StringBuilder stringBuffer = new StringBuilder();
            while (true) {
                try {
                    if ((input = in != null ? in.readLine() : null) == null) break;
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                stringBuffer.append(input);
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            String htmlData = stringBuffer.toString();

            try {
                JSONObject myAppObject = new JSONObject(htmlData).getJSONObject(appName);

                boolean value = myAppObject.getBoolean("value");
                String msg = myAppObject.getString("msg");

                if (value) {
                    activity.runOnUiThread(() -> {
                        new AlertDialog.Builder(activity)
                                .setMessage(msg)
                                .setCancelable(false)
                                .show();
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }
}
