package com.nego.money.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nego.money.Costants;
import com.nego.money.Element;
import com.nego.money.Item;
import com.nego.money.Main;
import com.nego.money.R;
import com.nego.money.Utils;
import com.nego.money.ViewElement;
import com.nego.money.database.DbAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Item> mDataset = new ArrayList<>();
    private Context mContext;

    private RelativeLayout item = null;
    private TextView people;
    private TextView date;
    private TextView letter;
    private ImageView icon;
    private ImageView tick;
    private RelativeLayout back_icon;
    private TextView importo;

    private TextView debt_text;
    private TextView debt_count;
    private ProgressBar debt_bar;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mView;
        public ViewHolder(View v) {
            super(v);
            mView = (LinearLayout) v;
        }

        public RelativeLayout back_item;
        public TextView people;
        public TextView date;
        public TextView letter;
        public ImageView icon;
        public ImageView tick;
        public RelativeLayout back_icon;
        public TextView importo;
        public ViewHolder(View v, RelativeLayout back_item, TextView people, TextView date, ImageView icon, TextView letter, ImageView tick, RelativeLayout back_icon, TextView importo) {
            super(v);
            this.back_item = back_item;
            this.people = people;
            this.date = date;
            this.icon = icon;
            this.tick = tick;
            this.back_icon = back_icon;
            this.letter = letter;
            this.importo = importo;
        }

    }

    public MyAdapter(DbAdapter dbHelper, String query, Context mContext) {
        this.mContext = mContext;
        generate_list(dbHelper, query);
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        ViewHolder vh;
        View v;

        if(viewType == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_noitem, parent, false);
            vh = new ViewHolder(v);
        } else if(viewType == 1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_topdivider, parent, false);
            vh = new ViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            vh = new ViewHolder(v, (RelativeLayout) v.findViewById(R.id.back_item), (TextView) v.findViewById(R.id.people), (TextView) v.findViewById(R.id.date), (ImageView) v.findViewById(R.id.icon), (TextView) v.findViewById(R.id.letter), (ImageView) v.findViewById(R.id.tick), (RelativeLayout) v.findViewById(R.id.back_icon), (TextView) v.findViewById(R.id.importo));
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mDataset.get(position).getType() == 4) {
            // ITEM
            item = holder.back_item;
            people = holder.people;
            date = holder.date;
            letter = holder.letter;
            icon = holder.icon;
            back_icon = holder.back_icon;
            importo = holder.importo;
            tick = holder.tick;

            // INFO

            if (mDataset.get(position).getItem().Done()) {
                date.setText(mContext.getString(R.string.action_payed) + ": " + getDate(mDataset.get(position).getItem().getDated()));
                back_icon.setBackgroundResource(R.drawable.checked_icon);
            } else {
                back_icon.setBackgroundResource(R.drawable.iconb_l);
                date.setText(getDate(mDataset.get(position).getItem().getDatec()));
            }


            // ICON
            icon = holder.icon;
            back_icon = holder.back_icon;

            if (mDataset.get(position).isSelected()) {
                letter.setVisibility(View.GONE);
                icon.setVisibility(View.GONE);
                tick.setVisibility(View.VISIBLE);
                item.setSelected(true);
            } else {
                tick.setVisibility(View.GONE);
                letter.setVisibility(View.VISIBLE);
                item.setSelected(false);

                String[] iniziali = mDataset.get(position).getItem().getPeople().split(" ");
                if (iniziali.length == 0) {
                    people.setText(mContext.getString(R.string.unknow));
                    letter.setText(mContext.getString(R.string.unknow).charAt(0));
                } else if (iniziali.length == 1) {
                    letter.setText("" + iniziali[0].charAt(0));
                    people.setText(mDataset.get(position).getItem().getPeople());
                } else {
                    letter.setText("" + iniziali[0].charAt(0) + iniziali[1].charAt(0));
                    people.setText(mDataset.get(position).getItem().getPeople());
                }

                String[] contact = Utils.fetchContacts(mContext, mDataset.get(position).getItem().getPeople());
                if (contact != null) {
                    if (contact[2] != null) {
                        icon.setVisibility(View.VISIBLE);
                        icon.setImageURI(Uri.parse(contact[2]));
                    } else {
                        icon.setVisibility(View.GONE);
                    }
                    people.setText(contact[1]);
                } else {
                    icon.setVisibility(View.GONE);
                }
            }

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(mContext);
            String currency = SP.getString(Costants.ACTUAL_CURRENCY, Currency.getInstance(Locale.getDefault()).getSymbol());

            String negative = "";
            if (mDataset.get(position).getItem().Me())
                negative = "-";
            importo.setText(negative + mDataset.get(position).getItem().getImporto() + currency);

            if (mDataset.get(position).getItem().Done())
                importo.setTextColor(mContext.getResources().getColor(R.color.third_text));
            else {
                if (mDataset.get(position).getItem().Me())
                    importo.setTextColor(mContext.getResources().getColor(R.color.accent));
                else
                    importo.setTextColor(mContext.getResources().getColor(R.color.primary));
            }

            final Intent intent = new Intent(mContext, ViewElement.class);
            intent.setAction(Costants.ACTION_VIEW);
            intent.putExtra(Costants.EXTRA_ELEMENT, mDataset.get(position).getItem());

            // ON CLICK
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getSelectedItemCount() > 0) {
                        toggleSelection(position);
                    } else {
                        new ViewElement(mContext, intent).show();
                    }
                }
            });

            // ON LONG CLICK
            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    toggleSelection(position);
                    return true;
                }
            });

            // ON CLICK ICON
            back_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSelection(position);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        Item obj = mDataset.get(position);
        return obj.getType();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    // GENERATE LIST
    public void generate_list(DbAdapter dbHelper, String query) {
        mDataset.clear();
        Cursor cursor;
        if (query.equals("NULL")) {
            int count = dbHelper.getElementsN();
            if (count == 0) {
                mDataset.add(new Item(0));
            } else {

                cursor = dbHelper.fetchAllElements();
                while (cursor.moveToNext())
                    mDataset.add(new Item(4, new Element(cursor)));
                cursor.close();
                mDataset.add(new Item(1));
            }
        } else {
            int count = dbHelper.getElementsNByFilterPeople(query);
            if (count == 0) {
                mDataset.add(new Item(0));
            } else {

                cursor = dbHelper.fetchElementsByFilterPeople(query);
                while (cursor.moveToNext())
                    mDataset.add(new Item(4, new Element(cursor)));
                cursor.close();
                mDataset.add(new Item(1));
            }
        }
    }

    public void toggleSelection(int pos) {
        mDataset.get(pos).toggleSelected();
        notifyItemChanged(pos);
        ((Main)mContext).toggleCab(true);
    }

    public void clearSelections() {
        for(int k=0;k<mDataset.size();k++)
            if (mDataset.get(k).getType() == 4 && mDataset.get(k).isSelected())
                mDataset.get(k).toggleSelected();
        notifyDataSetChanged();
        ((Main)mContext).toggleCab(false);
    }

    public int getSelectedItemCount() {
        int f = 0;
        for(int k=0;k<mDataset.size();k++)
            if (mDataset.get(k).getType() == 4 && mDataset.get(k).isSelected())
                f++;
        return f;
    }

    public void selectAll() {
        if (getSelectedItemCount() != getItemCount() - 1) {
            for (int k = 0; k < mDataset.size(); k++)
                if (mDataset.get(k).getType() == 4 && !mDataset.get(k).isSelected())
                    toggleSelection(k);
        } else {
            clearSelections();
        }
        ((Main)mContext).toggleCab(true);
    }

    public ArrayList<Element> getSelectedItem() {
        ArrayList<Element> selected = new ArrayList<>();
        for (int k=0;k<mDataset.size();k++)
            if(mDataset.get(k).getType() == 4 && mDataset.get(k).isSelected()) {
                selected.add(mDataset.get(k).getItem());
            }
        return selected;
    }


    public String getDate(Long date) {
        Date data = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM d, HH:mm");
        return dateFormat.format(data);
    }
}