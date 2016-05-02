package com.nego.money;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.Toast;

import com.nego.money.database.DbAdapter;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;


public class Utils {


    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public static void SnackbarC(final Context context, String title, final View view) {

        Snackbar.make(view, title, Snackbar.LENGTH_LONG)
                .show();

    }

    public static String[] fetchContacts(Context context, String query) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            String contact_id = null;
            String name = null;
            String photo = null;
            boolean found = false;

            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            String PHOTO_URI = ContactsContract.Contacts.PHOTO_URI;

            ContentResolver contentResolver = context.getContentResolver();

            Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {

                    while (cursor.moveToNext()) {
                        name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                        contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                        photo = cursor.getString(cursor.getColumnIndex(PHOTO_URI));
                        if (name != null && name.toLowerCase().equals(query.toLowerCase())) {
                            found = true;
                            break;
                        }

                    }

                }
                cursor.close();
            }
            if (found)
                return new String[]{contact_id, name, photo};
            else
                return null;
        } else {
            return null;
        }
    }

    public static ArrayList<String> chooseContacts(Context context, String query) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            String name;
            ArrayList<String> list = new ArrayList<>();

            Uri CONTENT_URI = Uri.parse("content://contacts/people");
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;

            ContentResolver contentResolver = context.getContentResolver();

            Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {

                    while (cursor.moveToNext()) {
                        name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                        if (name != null && name.toLowerCase().contains(query.toLowerCase())) {
                            list.add(name);
                        }

                    }
                }

                cursor.close();
            }
            return list;
        } else {
            return null;
        }
    }



    public static String countDebt(Context context, DbAdapter dbAdapter, String query) {
        Cursor c;
        if (!query.equals(""))
            c = dbAdapter.fetchElementsByFilterPeople(query);
        else
            c = dbAdapter.fetchAllElements();
        float count = 0;
        while (c.moveToNext()) {
            Element e = new Element(c);
            if (!e.Done())
                if (!e.getImporto().equals(""))
                    try {
                        if (e.Me())
                            count = count - Float.parseFloat(e.getImporto());
                        else
                            count = count + Float.parseFloat(e.getImporto());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        //Toast.makeText(context, context.getString(R.string.error_importo_sbagliato), Toast.LENGTH_SHORT).show();
                    }
        }
        c.close();

        return "" + count;
    }

    public static float countDebt(Context context) {
        DbAdapter dbAdapter = new DbAdapter(context);
        dbAdapter.open();
        Cursor c = dbAdapter.fetchAllElements();
        float count = 0;
        while (c.moveToNext()) {
            Element e = new Element(c);
            if (!e.Done())
                if (e.Me())
                    count = count - Float.parseFloat(e.getImporto());
                else
                    count = count + Float.parseFloat(e.getImporto());
        }

        c.close();
        dbAdapter.close();

        return count;
    }

    public static void showNotification(Context context) {
        float debt = countDebt(context);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        if (SP.getBoolean(Costants.PREFERENCE_SHOW_NOTIFICATION, false) && debt > 0) {

            Intent i = new Intent(context, Main.class);
            PendingIntent pi = PendingIntent.getActivity(context, -1, i, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder n = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_stat_money)
                    .setContentIntent(pi)
                    .setOngoing(true)
                    .setPriority(-1)
                    .setColor(ContextCompat.getColor(context, R.color.primary))
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.subtitle_notification, "" + debt) + SP.getString(Costants.ACTUAL_CURRENCY, Currency.getInstance(Locale.getDefault()).getSymbol()));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                n.setPriority(Notification.PRIORITY_MIN);
            }

            notificationManager.notify(-1, n.build());
        } else {
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
    }



    public static void expand(final View v) {
        if (v.getVisibility() != View.VISIBLE) {
            v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final int targetHeight = v.getMeasuredHeight();

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.getLayoutParams().height = 1;
            v.setVisibility(View.VISIBLE);
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    v.getLayoutParams().height = interpolatedTime == 1
                            ? ViewGroup.LayoutParams.WRAP_CONTENT
                            : (int) (targetHeight * interpolatedTime);
                    v.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density) * 2);
            a.setInterpolator(new AccelerateDecelerateInterpolator());
            v.startAnimation(a);
        }
    }

    public static void collapse(final View v) {
        if (v.getVisibility() == View.VISIBLE) {
            final int initialHeight = v.getMeasuredHeight();

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        v.setVisibility(View.GONE);
                    } else {
                        v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                        v.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density) * 2);
            a.setInterpolator(new AccelerateDecelerateInterpolator());
            v.startAnimation(a);
        }
    }
}
