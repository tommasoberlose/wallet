package com.nego.money;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewElement extends Dialog {

    Context mContext;

    public ViewElement(final Context context, Intent intent) {
        super(context);

        mContext = context;

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_view_element, null);

        if (intent.getAction().equals(Costants.ACTION_VIEW)) {
            final Element e = intent.getParcelableExtra(Costants.EXTRA_ELEMENT);
            ((TextView) dialogView.findViewById(R.id.importo)).setText("" + e.getImporto() + "\u20ac");
            ((TextView) dialogView.findViewById(R.id.datac)).setText(getDate(e.getDatec()));

            if (e.getNote().equals("")) {
                dialogView.findViewById(R.id.note).setVisibility(View.GONE);
            } else {
                dialogView.findViewById(R.id.note).setVisibility(View.VISIBLE);
                ((TextView) dialogView.findViewById(R.id.note)).setText(e.getNote());
            }

            String[] iniziali = e.getPeople().split(" ");
            if (iniziali.length == 0) {
                ((TextView) dialogView.findViewById(R.id.people)).setText(mContext.getString(R.string.unknow));
                ((TextView) dialogView.findViewById(R.id.letter)).setText(mContext.getString(R.string.unknow).charAt(0));
            } else if (iniziali.length == 1) {
                ((TextView) dialogView.findViewById(R.id.letter)).setText("" + iniziali[0].charAt(0));
                ((TextView) dialogView.findViewById(R.id.people)).setText(e.getPeople());
            } else {
                ((TextView) dialogView.findViewById(R.id.letter)).setText("" + iniziali[0].charAt(0) + iniziali[1].charAt(0));
                ((TextView) dialogView.findViewById(R.id.people)).setText(e.getPeople());
            }

            final String[] contact = Utils.fetchContacts(mContext, e.getPeople());
            if (contact != null) {
                if (contact[2] != null) {
                    dialogView.findViewById(R.id.icon).setVisibility(View.VISIBLE);
                    ((ImageView) dialogView.findViewById(R.id.icon)).setImageURI(Uri.parse(contact[2]));
                }
                ((TextView) dialogView.findViewById(R.id.people)).setText(contact[1]);

                dialogView.findViewById(R.id.icon).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contact[0]));
                        i.setData(uri);
                        mContext.startActivity(i);
                    }
                });

            } else {
                dialogView.findViewById(R.id.icon).setVisibility(View.GONE);
                dialogView.findViewById(R.id.icon).setOnClickListener(null);
            }

            if (e.Done()) {
                ((TextView) dialogView.findViewById(R.id.buttonpositive)).setText(mContext.getString(R.string.action_unpayed));
            } else {
                ((TextView) dialogView.findViewById(R.id.buttonpositive)).setText(mContext.getString(R.string.action_payed));
            }

            if (e.Me()) {
                ((TextView) dialogView.findViewById(R.id.own)).setText(mContext.getString(R.string.you_own));
                dialogView.findViewById(R.id.toolbar).setBackgroundColor(context.getResources().getColor(R.color.accent));
            } else {
                ((TextView) dialogView.findViewById(R.id.own)).setText(mContext.getString(R.string.they_own));
                dialogView.findViewById(R.id.toolbar).setBackgroundColor(context.getResources().getColor(R.color.primary));
            }

            if (e.Done())
                dialogView.findViewById(R.id.toolbar).setBackgroundColor(context.getResources().getColor(R.color.divider));


            dialogView.findViewById(R.id.control).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                        PopupMenu popup = new PopupMenu(mContext, v);
                        popup.inflate(R.menu.menu_popup_item);

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {

                                    case R.id.action_modify:
                                        Intent i = new Intent(mContext, AddElement.class);
                                        i.setAction(Costants.ACTION_EDIT);
                                        i.putExtra(Costants.EXTRA_ELEMENT, e);
                                        onBackPressed();
                                        new AddElement(mContext, i).show();
                                        return true;
                                    case R.id.action_delete:
                                        new AlertDialog.Builder(mContext)
                                                .setMessage(mContext.getResources().getString(R.string.ask_delete_element) + "?")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        ElementService.startAction(mContext, Costants.ACTION_DELETE, e);
                                                        onBackPressed();
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, null).show();
                                        return true;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
            });

            dialogView.findViewById(R.id.cancel_action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            dialogView.findViewById(R.id.buttonpositive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!e.Done())
                        ElementService.startAction(context, Costants.ACTION_CHECKED, e);
                    else
                        ElementService.startAction(context, Costants.ACTION_UNCHECKED, e);
                    onBackPressed();
                }
            });

        }


        this.setContentView(dialogView);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public String getDate(Long date) {
        Date data = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM d, HH:mm");
        return dateFormat.format(data);
    }


}