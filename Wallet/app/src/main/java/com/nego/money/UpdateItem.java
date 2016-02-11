package com.nego.money;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.udojava.evalex.Expression;

public class UpdateItem extends AppCompatActivity {

    private String count_string = "";
    private float result = 0;
    private Element e = null;

    private TextView result_view;
    private TextView count_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);

        Intent intent = getIntent();
        if (intent.getAction().equals(Costants.ACTION_UPDATE)) {
            e = intent.getParcelableExtra(Costants.EXTRA_ELEMENT);

            try {
                if (e.Me()) {
                    count_string = "-" + e.getImporto();
                } else {
                    count_string = e.getImporto();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                viewError();
            }

            if (e != null) {

                result_view = (TextView) findViewById(R.id.result);
                count_view = (TextView) findViewById(R.id.count_string);

                findViewById(R.id.button_0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("0");
                    }
                });

                findViewById(R.id.button_1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("1");
                    }
                });

                findViewById(R.id.button_2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("2");
                    }
                });

                findViewById(R.id.button_3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("3");
                    }
                });

                findViewById(R.id.button_4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("4");
                    }
                });

                findViewById(R.id.button_5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("5");
                    }
                });

                findViewById(R.id.button_6).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("6");
                    }
                });

                findViewById(R.id.button_7).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("7");
                    }
                });

                findViewById(R.id.button_8).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("8");
                    }
                });

                findViewById(R.id.button_9).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("9");
                    }
                });

                findViewById(R.id.button_dot).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString(".");
                    }
                });

                findViewById(R.id.button_mol).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("*");
                    }
                });

                findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("+");
                    }
                });

                findViewById(R.id.button_sot).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("-");
                    }
                });

                findViewById(R.id.button_div).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToString("/");
                    }
                });

                // DEL
                findViewById(R.id.button_del).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeToString(false);
                    }
                });

                findViewById(R.id.button_del).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        removeToString(true);
                        return true;
                    }
                });

                // SAVE
                findViewById(R.id.action_update).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateElement();
                    }
                });

                // CANCEL
                findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                updateUI();
            } else {
                viewError();
            }
        } else {
            viewError();
        }
    }

    public void updateUI() {
        count_view.setText(count_string);
        result_view.setText(updateResult());
    }

    public String updateResult() {
        float old_result = result;
        try {
            result = Float.parseFloat((new Expression(count_string)).eval().toString());
        } catch (Exception ex) {
            result = old_result;
        }
        return "" + result;
    }

    public void addToString(String s) {
        count_string = count_string + s;
        updateUI();
    }

    public void removeToString(boolean all) {
        if (!all) {
            count_string = count_string.substring(0, count_string.length() - 1);
        } else {
            count_string = e.getImporto();
        }
        updateUI();
    }

    public void viewError() {
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        setResult(RESULT_CANCELED);
        finish();
    }

    public void updateElement() {
        updateResult();
        if (result > 0) {
            e.setWho("0");
            e.setImporto("" + result);
            ElementService.startAction(this, Costants.ACTION_UPDATE, e);
        } else if (result < 0) {
            e.setWho("1");
            e.setImporto("" + (-result));
            ElementService.startAction(this, Costants.ACTION_UPDATE, e);
        } else {
            ElementService.startAction(this, Costants.ACTION_CHECKED, e);
        }
        setResult(RESULT_OK);
        finish();
    }
}
