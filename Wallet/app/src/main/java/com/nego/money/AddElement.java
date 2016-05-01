package com.nego.money;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

public class AddElement extends Dialog {

    Context mContext;

    private EditText importo;
    private AutoCompleteTextView persona;
    private EditText nota;
    private AppCompatSpinner who;
    private TextView currency;

    private Element e;

    public AddElement(Context context, Intent intent) {
        super(context);

        mContext = context;

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_element, null);

        importo = (EditText) dialogView.findViewById(R.id.importo);
        persona = (AutoCompleteTextView) dialogView.findViewById(R.id.persona);
        nota = (EditText) dialogView.findViewById(R.id.nota);
        who = (AppCompatSpinner) dialogView.findViewById(R.id.title);
        currency = (TextView) dialogView.findViewById(R.id.currency);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(mContext);
        currency.setText(SP.getString(Costants.ACTUAL_CURRENCY, Currency.getInstance(Locale.getDefault()).getSymbol()));

        String[] titles = new String[] {mContext.getString(R.string.they_own), mContext.getString(R.string.you_own)};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, titles);
        who.setAdapter(spinnerAdapter);
        who.setSelection(0, true);

        if (intent.getAction() != null && intent.getAction().equals(Costants.ACTION_EDIT)) {
            e = intent.getParcelableExtra(Costants.EXTRA_ELEMENT);
            importo.setText(e.getImporto());
            persona.setText(e.getPeople());
            nota.setText(e.getNote());
            if (!e.Me())
                who.setSelection(0, true);
            else
                who.setSelection(1, true);
        }

        dialogView.findViewById(R.id.action_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        persona.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setContactsArrayAdapter(s.toString());
            }
        });

        importo.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        this.setContentView(dialogView);
    }



    @Override
    public void onBackPressed() {
        if (!Utils.isEmpty(importo) && ((e == null) || !("" + e.getImporto()).equals(importo.getText().toString()))) {
            new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getResources().getString(R.string.attention))
                    .setMessage(mContext.getResources().getString(R.string.ask_exit) + "?")
                    .setPositiveButton(R.string.action_exit_editor, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            dismiss();
        }
    }

    public void save() {
        if (Utils.isEmpty(importo)) {
            Toast.makeText(mContext, mContext.getString(R.string.error_importo), Toast.LENGTH_SHORT).show();
        } else if (Utils.isEmpty(persona)) {
            Toast.makeText(mContext, mContext.getString(R.string.errore_people), Toast.LENGTH_SHORT).show();
        } else {
            try {
                float f = Float.parseFloat(importo.getText().toString());
                if (e == null) {
                    Calendar c = Calendar.getInstance();
                    String chi = "0";
                    if (who.getSelectedItemPosition() == 1)
                        chi = "1";
                    e = new Element(persona.getText().toString(), nota.getText().toString(), "0", chi, importo.getText().toString(), "null", c.getTimeInMillis(), 0);
                    ElementService.startAction(mContext, Costants.ACTION_CREATE, e);
                } else {
                    String chi = "0";
                    if (who.getSelectedItemPosition() == 1)
                        chi = "1";
                    e.setPeople(persona.getText().toString());
                    e.setNote(nota.getText().toString());
                    e.setWho(chi);
                    e.setImporto(importo.getText().toString());
                    ElementService.startAction(mContext, Costants.ACTION_UPDATE, e);
                }
                dismiss();
            } catch (Exception e) {
                Toast.makeText(mContext, mContext.getString(R.string.error_importo_sbagliato), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setContactsArrayAdapter(String query) {
        ArrayList<String> suggestions = Utils.chooseContacts(mContext, query);
        if (suggestions != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                    android.R.layout.simple_dropdown_item_1line, suggestions.toArray(new String[suggestions.size()]));
            persona.setAdapter(adapter);
        }
    }
}
