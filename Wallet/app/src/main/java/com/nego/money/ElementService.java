package com.nego.money;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.nego.money.database.DbAdapter;

import java.util.ArrayList;

public class ElementService extends IntentService {

    public static void startAction(Context context, String action, Element r) {
        Intent intent = new Intent(context, ElementService.class);
        intent.setAction(action);
        intent.putExtra(Costants.EXTRA_ELEMENT, r);
        context.startService(intent);
    }

    public static void startAction(Context context, String action, ArrayList<Element> RM) {
        Intent intent = new Intent(context, ElementService.class);
        intent.setAction(action);
        intent.putParcelableArrayListExtra(Costants.EXTRA_ELEMENT, RM);
        context.startService(intent);
    }

    private void sendResponse(String s, Element r) {
        Intent i = new Intent(Costants.ACTION_UPDATE_LIST);
        i.putExtra(Costants.EXTRA_ACTION_TYPE, s);
        i.putExtra(Costants.EXTRA_ELEMENT, r);
        sendBroadcast(i);
    }

    private void sendResponse(String s, ArrayList<Element> RM) {
        Intent i = new Intent(Costants.ACTION_UPDATE_LIST);
        i.putExtra(Costants.EXTRA_ACTION_TYPE, s);
        i.putParcelableArrayListExtra(Costants.EXTRA_ELEMENT, RM);
        sendBroadcast(i);
    }

    public ElementService() {
        super("ElementService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Costants.ACTION_CREATE.equals(action)) {
                final Element r = intent.getParcelableExtra(Costants.EXTRA_ELEMENT);
                createElement(r);
            } else if (Costants.ACTION_UPDATE.equals(action)) {
                final Element r = intent.getParcelableExtra(Costants.EXTRA_ELEMENT);
                updateElement(r);
            } else if (Costants.ACTION_DELETE.equals(action)) {
                final Element r = intent.getParcelableExtra(Costants.EXTRA_ELEMENT);
                deleteElement(r);
            } else if (Costants.ACTION_UNDELETE.equals(action)) {
                final Element r = intent.getParcelableExtra(Costants.EXTRA_ELEMENT);
                undeleteElement(r);
            } else if (Costants.ACTION_DELETE_MULTI.equals(action)) {
                final ArrayList<Element> r = intent.getParcelableArrayListExtra(Costants.EXTRA_ELEMENT);
                deleteElement(r);
            } else if (Costants.ACTION_UNDELETE_MULTI.equals(action)) {
                final ArrayList<Element> r = intent.getParcelableArrayListExtra(Costants.EXTRA_ELEMENT);
                undeleteElement(r);
            } else if (Costants.ACTION_CHECKED.equals(action)) {
                final Element r = intent.getParcelableExtra(Costants.EXTRA_ELEMENT);
                doneElement(r);
            } else if (Costants.ACTION_UNCHECKED.equals(action)) {
                final Element r = intent.getParcelableExtra(Costants.EXTRA_ELEMENT);
                undodoneElement(r);
            }
        }
    }

    private void createElement(Element r) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        if (r.create_element(this, dbHelper)) {
            sendResponse(Costants.ACTION_CREATE, r);
        }
        dbHelper.close();
    }

    private void updateElement(Element r) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        if (r.update_element(this, dbHelper)) {
            sendResponse(Costants.ACTION_UPDATE, r);
        }
        dbHelper.close();
    }

    private void deleteElement(Element r) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        if (r.delete_element(this, dbHelper)) {
            sendResponse(Costants.ACTION_DELETE, r);
        }
        dbHelper.close();
    }

    private void deleteElement(ArrayList<Element> RM) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        int count = 0;
        for (Element r : RM) {
            if (r.delete_element(this, dbHelper))
                count++;
        }
        if (count == RM.size())
            sendResponse(Costants.ACTION_DELETE_MULTI, RM);
        dbHelper.close();
    }

    private void undeleteElement(Element r) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        if (r.undo_delete_element(this, dbHelper)) {
            sendResponse(Costants.ACTION_UNDELETE, r);
        }
        dbHelper.close();
    }

    private void undeleteElement(ArrayList<Element> RM) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        int count = 0;
        for (Element r : RM) {
            if (r.undo_delete_element(this, dbHelper))
                count++;
        }
        if (count == RM.size())
            sendResponse(Costants.ACTION_UNDELETE_MULTI, RM);
        dbHelper.close();
    }

    private void doneElement(Element r) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        if (r.done_element(this, dbHelper)) {
            sendResponse(Costants.ACTION_CHECKED, r);
        }
        dbHelper.close();
    }

    private void undodoneElement(Element r) {
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        if (r.undo_done_element(this, dbHelper)) {
            sendResponse(Costants.ACTION_UNCHECKED, r);
        }
        dbHelper.close();
    }
}
