package com.nego.money;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
    private ActionMode mActionMode;

    private RecyclerView recList;
    public FloatingActionButton button;

    private String query = "";
    public SearchView searchView;

    public MyAdapter adapter;
    private Menu menu;
    private BroadcastReceiver mReceiver;

    private SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TOOLBAR
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.title_activity_main));

        SP = PreferenceManager.getDefaultSharedPreferences(this);
        final EditText pin = (EditText) findViewById(R.id.pin);
        if (!SP.getString(Costants.PREFERENCES_PIN, "").equals("") && savedInstanceState == null) {
            findViewById(R.id.action_pin).setVisibility(View.VISIBLE);
            pin.requestFocus();
            pin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (v.getText().toString().equals(SP.getString(Costants.PREFERENCES_PIN, ""))) {
                            Utils.collapse(findViewById(R.id.action_pin));
                        } else {
                            Utils.expand(findViewById(R.id.error_pin));
                            pin.requestFocus();
                        }
                    }

                    return false;
                }
            });
        } else {
            findViewById(R.id.action_pin).setVisibility(View.GONE);
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

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final Element to_archive = adapter.getElement(viewHolder.getAdapterPosition());
                if (to_archive != null) {
                    if (!to_archive.Done()) {
                        new AlertDialog.Builder(Main.this)
                            .setMessage(getResources().getString(R.string.ask_archive_items) + "?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ElementService.startAction(Main.this, Costants.ACTION_CHECKED, to_archive);
                                    Snackbar.make(toolbar, getString(R.string.element_paid), Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    update_list(query);
                                }
                            })
                            .show();
                    } else {
                        new AlertDialog.Builder(Main.this)
                            .setMessage(getResources().getString(R.string.ask_delete_element) + "?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ElementService.startAction(Main.this, Costants.ACTION_DELETE, to_archive);
                                    Snackbar.make(toolbar, getString(R.string.element_deleted), Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    update_list(query);
                                }
                            })
                            .show();
                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recList);

        update_list(query);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }

    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Costants.EXTRA_ALREADY_OPEN, true);
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

        if (id == R.id.action_settings) {
            Intent setting_i = new Intent(this, Settings.class);
            startActivityForResult(setting_i, 1);
        }

        if (id == R.id.action_filter) {
            AlertDialog.Builder filter = new AlertDialog.Builder(this);
            filter.setTitle(getString(R.string.action_filter_title));


            final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
            int filter_archive = SP.getInt(Costants.ARCHIVE_FILTER, 0);

            filter.setSingleChoiceItems(new String[] {getString(R.string.filter_archive_0), getString(R.string.filter_archive_1), getString(R.string.filter_archive_2)}, filter_archive, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            SP.edit().putInt(Costants.ARCHIVE_FILTER, selectedPosition).apply();
                            update_list(query);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
            filter.show();
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

            // DONE ALL
            if(id == R.id.action_done_all) {
                new AlertDialog.Builder(Main.this)
                        .setMessage(getResources().getString(R.string.ask_archive_items) + "?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final ArrayList<Element> toUse = adapter.getSelectedItem();
                                if (adapter.getSelectedItemCount() == 1) {
                                    ElementService.startAction(Main.this, Costants.ACTION_CHECKED, toUse.get(0));
                                } else {
                                    ElementService.startAction(Main.this, Costants.ACTION_CHECKED_MULTI, toUse);
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
                mActionMode.setTitle(getString(R.string.app_name) + ": " + adapter.getSelectedItemAmount() + SP.getString(Costants.ACTUAL_CURRENCY, Currency.getInstance(Locale.getDefault()).getSymbol())/* + " (" + adapter.getSelectedItemCount() + ")"*/);
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
        update_list(query);
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
                    mAdapter = new MyAdapter(dbHelper, "NULL", SP.getInt(Costants.ARCHIVE_FILTER, 0), Main.this);
                else
                    mAdapter = new MyAdapter(dbHelper, query, SP.getInt(Costants.ARCHIVE_FILTER, 0), Main.this);

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

    public void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                Costants.CODE_REQUEST_PERMISSION_READ_CONTACTS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            update_list(query);
        }
    }

}
