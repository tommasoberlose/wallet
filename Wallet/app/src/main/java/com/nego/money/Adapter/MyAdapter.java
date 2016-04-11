package com.nego.money.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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
import java.util.Calendar;
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
        public CardView import_container;
        public ViewHolder(View v, RelativeLayout back_item, TextView people, TextView date, ImageView icon, TextView letter, ImageView tick, RelativeLayout back_icon, TextView importo, CardView import_container) {
            super(v);
            this.back_item = back_item;
            this.people = people;
            this.date = date;
            this.icon = icon;
            this.tick = tick;
            this.back_icon = back_icon;
            this.letter = letter;
            this.importo = importo;
            this.import_container = import_container;
        }

    }

    public MyAdapter(DbAdapter dbHelper, String query, int archived, Context mContext) {
        this.mContext = mContext;
        generate_list(dbHelper, query, archived);
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
            vh = new ViewHolder(v, (RelativeLayout) v.findViewById(R.id.back_item), (TextView) v.findViewById(R.id.people), (TextView) v.findViewById(R.id.date), (ImageView) v.findViewById(R.id.icon), (TextView) v.findViewById(R.id.letter), (ImageView) v.findViewById(R.id.tick), (RelativeLayout) v.findViewById(R.id.back_icon), (TextView) v.findViewById(R.id.importo), (CardView) v.findViewById(R.id.import_container));
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Item i = mDataset.get(position);
        if (mDataset.get(position).getType() == 4) {
            final Element e = i.getItem();

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

            if (e.getNote().equals("")) {
                if (e.Done()) {
                    date.setText(mContext.getString(R.string.action_payed) + ": " + getDate(e.getDated()));
                } else {
                    date.setText(getDate(e.getDatec()));
                }
            } else {
                date.setText(e.getNote());
            }

            // PEOPLE
            people.setText(e.getPeople());
            String[] contact = Utils.fetchContacts(mContext, e.getPeople());
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

            // ICON

            if (e.Done()) {
                back_icon.setBackgroundResource(R.drawable.checked_icon);
            } else {
                back_icon.setBackgroundResource(R.drawable.iconb_l);
            }

            icon = holder.icon;
            back_icon = holder.back_icon;

            if (i.isSelected()) {
                letter.setVisibility(View.GONE);
                icon.setVisibility(View.GONE);
                tick.setVisibility(View.VISIBLE);
                item.setSelected(true);
            } else {
                tick.setVisibility(View.GONE);
                letter.setVisibility(View.VISIBLE);
                item.setSelected(false);

                String[] iniziali = e.getPeople().split(" ");
                if (iniziali.length == 0) {
                    people.setText(mContext.getString(R.string.unknow));
                    letter.setText(mContext.getString(R.string.unknow).charAt(0));
                } else if (iniziali.length == 1) {
                    letter.setText("" + iniziali[0].charAt(0));
                } else {
                    letter.setText("" + iniziali[0].charAt(0) + iniziali[1].charAt(0));
                }
            }

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(mContext);
            String currency = SP.getString(Costants.ACTUAL_CURRENCY, Currency.getInstance(Locale.getDefault()).getSymbol());

            String negative = "";
            if (e.Me())
                negative = "-";
            importo.setText(negative + e.getImporto() + currency);

            if (e.Done())
                holder.import_container.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.third_text));
            else {
                if (e.Me())
                    holder.import_container.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.accent));
                else
                    holder.import_container.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.primary));
            }

            final Intent intent = new Intent(mContext, ViewElement.class);
            intent.setAction(Costants.ACTION_VIEW);
            intent.putExtra(Costants.EXTRA_ELEMENT, e);

            // ON CLICK
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getSelectedItemCount() > 0) {
                        toggleSelection(i);
                    } else {
                        new ViewElement(mContext, intent).show();
                    }
                }
            });

            // ON LONG CLICK
            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    toggleSelection(i);
                    return true;
                }
            });

            // ON CLICK ICON
            back_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSelection(i);
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
    public void generate_list(DbAdapter dbHelper, String query, int archived) {
        mDataset.clear();
        Cursor cursor = null;
        if (query.equals("NULL")) {
            if (archived == 0)
                cursor = dbHelper.fetchAllElements();
            else if (archived == 1)
                cursor = dbHelper.fetchAllElementsOld();
            else
                cursor = dbHelper.fetchAllElementsAll();

            if (cursor.getCount() == 0) {
                mDataset.add(new Item(0));
            } else {
                while (cursor.moveToNext())
                    mDataset.add(new Item(4, new Element(cursor)));
                mDataset.add(new Item(1));
            }
            cursor.close();
        } else {
            if (archived == 0)
                cursor = dbHelper.fetchElementsByFilterPeople(query);
            else if (archived == 1)
                cursor = dbHelper.fetchElementsByFilterPeopleOld(query);
            else
                cursor = dbHelper.fetchElementsByFilterPeopleAll(query);

            if (cursor.getCount() == 0) {
                mDataset.add(new Item(0));
            } else {
                while (cursor.moveToNext())
                    mDataset.add(new Item(4, new Element(cursor)));
                mDataset.add(new Item(1));
            }
            cursor.close();
        }
    }

    public void toggleSelection(Item i) {
        int pos = findPosition(i);
        mDataset.get(pos).toggleSelected();
        notifyItemChanged(pos);
        ((Main)mContext).toggleCab(true);
    }

    public void clearSelections() {
        for(Item i : mDataset)
            if (i.getType() == 4 && i.isSelected())
                i.toggleSelected();
        notifyDataSetChanged();
        ((Main)mContext).toggleCab(false);
    }

    public int getSelectedItemCount() {
        int f = 0;
        for(Item i : mDataset)
            if (i.getType() == 4 && i.isSelected())
                f++;
        return f;
    }

    public float getSelectedItemAmount() {
        float f = 0;
        for(Item i : mDataset)
            if (i.getType() == 4 && i.isSelected())
                f += Float.parseFloat(i.getItem().getImporto());
        return f;
    }

    public void selectAll() {
        if (getSelectedItemCount() != getItemCount() - 1) {
            for (Item i : mDataset)
                if (i.getType() == 4 && !i.isSelected())
                    toggleSelection(i);
        } else {
            clearSelections();
        }
        ((Main)mContext).toggleCab(true);
    }

    public ArrayList<Element> getSelectedItem() {
        ArrayList<Element> selected = new ArrayList<>();
        for (Item i: mDataset)
            if(i.getType() == 4 && i.isSelected()) {
                selected.add(i.getItem());
            }
        return selected;
    }


    public String getDate(Long date) {
        Date data = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM d, HH:mm");
        return dateFormat.format(data);
    }


    public static String getLastDate(Context context, long date) {
        if (date == 0) {
            return context.getString(R.string.text_never);
        } else {
            long difference = (Calendar.getInstance().getTimeInMillis() - date) / 1000;
            if (difference < 60) { // Secondi
                return difference + context.getString(R.string.seconds);
            } else if (difference >= 60 && difference < 3600) { // Minuti
                return difference / 60 + context.getString(R.string.minutes);
            } else if (difference >= (60 * 60) && difference < (60*60*24)) { // Ore
                return difference / (60 * 60) + context.getString(R.string.hours);
            } else if (difference >= (60*60*24) && difference < (60*60*24*7)) { // Giorni
                return difference / (60*60*24) + context.getString(R.string.days);
            } else { // Settimane
                return difference / (60*60*24*7) + context.getString(R.string.weeks);
            }
        }
    }

    public Element getElement(int position) {
        return mDataset.get(position).getItem();
    }

    public int findPosition(Item i) {
        for (int k = 0; k < mDataset.size(); k++) {
            if (i.getItem().getId() == mDataset.get(k).getItem().getId())
                return k;
        }
        return 0;
    }
}