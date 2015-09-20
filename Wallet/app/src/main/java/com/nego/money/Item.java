package com.nego.money;

public class Item {
    private int type;
    private Element item;
    private String subtitle;
    private boolean selected = false;

    public Item(int type) {
        this.type = type;
    }
    public Item(int type, Element item) {
        this.type = type;
        this.item = item;
    }
    public Item(int type, String subtitle) {
        this.type = type;
        this.subtitle = subtitle;
    }
    public int getType() { return type; }

    public Element getItem() {
        return item;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean isSelected() {
        return selected;
    }
    public void toggleSelected() {
        if(selected)
            selected = false;
        else
            selected = true;
    }
}
