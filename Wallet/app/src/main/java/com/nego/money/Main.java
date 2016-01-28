package com.nego.money;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nego.money.Adapter.MyAdapter;
import com.nego.money.database.DbAdapter;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


public class Main extends AppCompatActivity {

    public Toolbar toolbar;
    private boolean search = false;
    private ActionMode mActionMode;

    private RecyclerView recList;
    public FloatingActionButton button;

    private String query = "";
    public SearchView searchView;

    public MyAdapter adapter;
    private Menu menu;

    private boolean archived = false;

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TOOLBAR
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        setTitle(getResources().getString(R.string.title_activity_main));

        // INTENT
        Intent intent = getIntent();

        //SEARCH
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            search = true;
            query = intent.getStringExtra(SearchManager.QUERY);
            update_list(query);
        }

        // FLOATING BUTTON
        button = (FloatingActionButton) findViewById(R.id.fab_1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddElement(Main.this, new Intent()).show();
            }
        });

        // RECYCLER LIST
        recList = (RecyclerView) findViewById(R.id.listView);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        update_list(query);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (adapter != null && adapter.getSelectedItemCount() == 0) {

            SearchManager searchManager = (SearchManager)
                    getSystemService(Context.SEARCH_SERVICE);
            MenuItem searchMenuItem = menu.findItem(R.id.action_search);
            if (searchMenuItem != null) {
                searchView = (SearchView) searchMenuItem.getActionView();

                searchView.setSearchableInfo(searchManager.
                        getSearchableInfo(getComponentName()));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        query = s;
                        update_list(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        query = s;
                        update_list(query);
                        return false;
                    }
                });

                MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        update_list(query);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        update_list(query);
                        invalidateOptionsMenu();
                        return true;
                    }
                });
            }

        }

        if (id == R.id.action_change_currency) {
            AlertDialog.Builder choose = new AlertDialog.Builder(this);

            final View titleView = LayoutInflater.from(this).inflate(R.layout.custom_title, null);
            ((TextView)titleView.findViewById(R.id.action_bar_title)).setText(getResources().getString(R.string.action_change_currency));
            ((TextView)titleView.findViewById(R.id.action_bar_subtitle)).setText(getResources().getString(R.string.action_change_currency_subtitle));
            choose.setCustomTitle(titleView);


            final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
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
                        // Locale not found
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
                            update_list(query);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            choose.show();
        }

        if (id == R.id.action_feedback) {
            Intent url_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/communities/100614116200820350356/stream/d7b99217-3611-48dc-b8bb-ebff20b105b5"));
            startActivity(url_intent);
        }

        if (id == R.id.action_archived) {
            archived = !archived;
            update_list(query);

            if (archived)
                item.setTitle(getString(R.string.options_not_archived));
            else
                item.setTitle(getString(R.string.option_archived));
        }

        return super.onOptionsItemSelected(item);
    }


    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();

            //DELETE
            if(id == R.id.action_delete) {
                new AlertDialog.Builder(Main.this)
                        .setMessage(getResources().getString(R.string.ask_delete_element) + "?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ArrayList<Element> toUse = adapter.getSelectedItem();
                                if (adapter.getSelectedItemCount() == 1) {
                                    ElementService.startAction(Main.this, Costants.ACTION_DELETE, toUse.get(0));
                                } else {
                                    ElementService.startAction(Main.this, Costants.ACTION_DELETE_MULTI, toUse);
                                }
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            }

            // SELECT ALL
            if(id == R.id.action_select_all) {
                adapter.selectAll();
                return true;
            }
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_context, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelections();
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }

    public void toggleCab(boolean open) {
        if (open) {
            if (mActionMode == null)
                mActionMode = Main.this.startSupportActionMode(new ActionBarCallBack());

            if (adapter.getSelectedItemCount() != 0)
                mActionMode.setTitle("" + adapter.getSelectedItemCount());
            else {
                mActionMode.finish();
                mActionMode = null;
            }
        } else {
            if (mActionMode != null)
                mActionMode.finish();
            mActionMode = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(Costants.ACTION_UPDATE_LIST);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action_type = intent.getStringExtra(Costants.EXTRA_ACTION_TYPE);
                toggleCab(false);
                update_list(query);
                switch (action_type) {
                    case Costants.ACTION_DELETE:
                        Utils.SnackbarC(Main.this, getString(R.string.element_deleted), findViewById(R.id.main));
                        break;
                    case Costants.ACTION_CHECKED:
                        Utils.SnackbarC(Main.this, getString(R.string.element_paid), findViewById(R.id.main));
                        break;
                    case Costants.ACTION_UNCHECKED:
                        Utils.SnackbarC(Main.this, getString(R.string.element_unpaid), findViewById(R.id.main));
                        break;
                }
            }
        };
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    // UPDATE LIST
    public void update_list(final String query) {

        recList.animate().alpha(0).start();

        this.query = query;
        final Handler mHandler = new Handler();

        new Thread(new Runnable() {
            public void run() {
                DbAdapter dbHelper = new DbAdapter(Main.this);
                dbHelper.open();

                final MyAdapter mAdapter;
                if (query.equals(""))
                    mAdapter = new MyAdapter(dbHelper, "NULL", archived, Main.this);
                else
                    mAdapter = new MyAdapter(dbHelper, query, archived, Main.this);

                final String count = Utils.countDebt(Main.this, dbHelper, query);

                dbHelper.close();

                mHandler.post(new Runnable() {
                    public void run() {
                        recList.setAdapter(mAdapter);
                        adapter = mAdapter;

                        recList.animate().alpha(1).start();

                        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(Main.this);
                        String currency = SP.getString(Costants.ACTUAL_CURRENCY, Currency.getInstance(Locale.getDefault()).getSymbol());
                        setTitle(getString(R.string.title_name) + ": " + count + currency);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (!query.equals("")) {
            query = "";
            update_list(query);
            invalidateOptionsMenu();
        } else {
            super.onBackPressed();
        }
    }

}
