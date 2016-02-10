package com.nego.money;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Settings extends AppCompatActivity {

    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.title_activity_settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SP = PreferenceManager.getDefaultSharedPreferences(this);

        findViewById(R.id.action_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent url_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/communities/100614116200820350356/stream/d7b99217-3611-48dc-b8bb-ebff20b105b5"));
                startActivity(url_intent);
            }
        });

        findViewById(R.id.action_change_currency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder choose = new AlertDialog.Builder(Settings.this);

                final View titleView = LayoutInflater.from(Settings.this).inflate(R.layout.custom_title, null);
                ((TextView)titleView.findViewById(R.id.action_bar_title)).setText(getResources().getString(R.string.action_change_currency));
                ((TextView)titleView.findViewById(R.id.action_bar_subtitle)).setText(getResources().getString(R.string.action_change_currency_subtitle));
                choose.setCustomTitle(titleView);


                String currency = SP.getString(Costants.ACTUAL_CURRENCY, Currency.getInstance(Locale.getDefault()).getSymbol());

                Set<Currency> c;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    c = Currency.getAvailableCurrencies();
                } else {
                    c = new HashSet<Currency>();
                    Locale[] locs = Locale.getAvailableLocales();

                    for(Locale loc : locs) {
                        try {
                            c.add( Currency.getInstance( loc ) );
                        } catch(Exception exc)
                        {
                            exc.printStackTrace();
                        }
                    }

                }
                int selected = 0;
                int k = 0;
                String[] to_display = new String[c.size()];
                final String[] to_include = new String[c.size()];
                for (Currency ca : c) {
                    String name = "";
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        name = ca.getDisplayName();
                    }
                    to_display[k] = ca.getSymbol() + " " + name;
                    to_include[k] = ca.getSymbol();
                    if (ca.getSymbol().equals(currency))
                        selected = k;
                    k++;
                }

                choose.setSingleChoiceItems(to_display, selected, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                SharedPreferences.Editor editor = SP.edit();
                                editor.putString(Costants.ACTUAL_CURRENCY, to_include[selectedPosition]);
                                editor.apply();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);
                choose.show();
            }
        });

        findViewById(R.id.action_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder filter = new AlertDialog.Builder(Settings.this);
                filter.setTitle(getString(R.string.action_filter_title));


                final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                int filter_archive = SP.getInt(Costants.ARCHIVE_FILTER, 0);

                filter.setSingleChoiceItems(new String[] {getString(R.string.filter_archive_0), getString(R.string.filter_archive_1), getString(R.string.filter_archive_2)}, filter_archive, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                SP.edit().putInt(Costants.ARCHIVE_FILTER, selectedPosition).apply();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);
                filter.show();
            }
        });

        findViewById(R.id.action_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean show_notification = SP.getBoolean(Costants.PREFERENCE_SHOW_NOTIFICATION, false);
                SP.edit().putBoolean(Costants.PREFERENCE_SHOW_NOTIFICATION, !show_notification).apply();
                Utils.showNotification(Settings.this);
                updateUI();
            }
        });

        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateUI() {
        boolean show_notification = SP.getBoolean(Costants.PREFERENCE_SHOW_NOTIFICATION, false);
        ((ImageView) findViewById(R.id.icon_notification)).setImageResource(show_notification ? R.drawable.ic_action_label : R.drawable.ic_action_label_outline);
        ((TextView) findViewById(R.id.action_notification_subtitle)).setText(show_notification ? R.string.action_notification_subtitle : R.string.action_notification_subtitle_not);
    }
}