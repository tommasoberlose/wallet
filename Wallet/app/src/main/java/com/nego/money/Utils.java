package com.nego.money;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import com.nego.money.database.DbAdapter;

import java.util.ArrayList;


public class Utils {


    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public static void SnackbarC(final Context context, String title, final View view) {

        Snackbar.make(view, title, Snackbar.LENGTH_LONG)
                .show();

    }

    public static String[] fetchContacts(Context context, String query) {

        String contact_id = null;
        String name = null;
        String photo = null;
        boolean found = false;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String PHOTO_URI = ContactsContract.Contacts.PHOTO_URI;

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                photo = cursor.getString(cursor.getColumnIndex( PHOTO_URI ));

                if (name.toLowerCase().equals(query.toLowerCase())) {
                    found = true;
                    break;
                }

            }
        }

        cursor.close();
        if (found)
            return new String[]{contact_id, name, photo};
        else
            return null;
    }

    public static ArrayList<String> chooseContacts(Context context, String query) {

        String name;

        ArrayList<String> list = new ArrayList<>();

        Uri CONTENT_URI = Uri.parse("content://contacts/people");
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                if (name != null && name.toLowerCase().contains(query.toLowerCase())) {
                    list.add(name);
                }

            }
        }

        cursor.close();
        return list;
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
                if (e.Me())
                    count = count - Float.parseFloat(e.getImporto());
                else
                    count = count + Float.parseFloat(e.getImporto());
        }

        return "" + count;
    }

}
