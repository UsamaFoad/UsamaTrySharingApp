package com.example.sharingapp;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * ItemList class: Holds ArrayList of Items
 */
public class ItemList extends Observable {

    private static ArrayList<Item> items;
    private String FILENAME = "item_file.sav";

    public ItemList() {
        items = new ArrayList<Item>();
    }

    public void setItems(ArrayList<Item> item_list) {
        items = item_list;
        notifyObservers();
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
        notifyObservers();
    }

    public void deleteItem(Item item) {
        items.remove(item);
        notifyObservers();
    }

    public Item getItem(int index) {
        return items.get(index);
    }

    public boolean hasItem(Item item) {
        for (Item i : items) {
            if (item.getId().equals(i.getId())) {
                return true;
            }
        }
        return false;
    }

    public int getIndex(Item item) {
        int pos = 0;
        for (Item i : items) {
            if (item.getId().equals(i.getId())) {
                return pos;
            }
            pos = pos + 1;
        }
        return -1;
    }

    public int getSize() {
        return items.size();
    }

    public void loadItems(Context context) {

        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Item>>() {
            }.getType();
            items = gson.fromJson(isr, listType); // temporary
            fis.close();
        } catch (FileNotFoundException e) {
            items = new ArrayList<Item>();
        } catch (IOException e) {
            items = new ArrayList<Item>();
        }
        notifyObservers();
    }

    public boolean saveItems(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, 0);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            Gson gson = new Gson();
            gson.toJson(items, osw);
            osw.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Used for AvailableItemsFragment, BorrowedItemsFragment, and BiddedItemsFragment
    public ArrayList<Item> filterItems(String user_id, String status) {
        ArrayList<Item> selected_items = new ArrayList<>();
        for (Item i: items) {
            if (i.getOwnerId().equals(user_id) && i.getStatus().equals(status)) {
                selected_items.add(i);
            }
        }
        return selected_items;
    }

    // Used for AllItemsFragment
    public ArrayList<Item> getMyItems(String user_id) {
        ArrayList<Item> selected_items = new ArrayList<>();
        for (Item i: items) {
            if (i.getOwnerId().equals(user_id)) {
                selected_items.add(i);
            }
        }
        return selected_items;
    }

    // Used for the SearchItemsActivity
    public ArrayList<Item> getSearchItems(String user_id) {
        ArrayList<Item> selected_items = new ArrayList<>();
        for (Item i: items) {
            if (!i.getOwnerId().equals(user_id) && !i.getStatus().equals("Borrowed")) {
                selected_items.add(i);
            }
        }
        return selected_items;
    }

    public ArrayList<Item> getBorrowedItems(String username) {
        ArrayList<Item> selected_items = new ArrayList<>();
        for (Item i: items) {
            if (i != null && i.getBorrower() != null) {
                if (i.getBorrowerUsername().equals(username)) {
                    selected_items.add(i);
                }
            }
        }
        return selected_items;
    }

    public Item getItemById(String id){
        for (Item i: items) {
            if (i.getId().equals(id)) {
                return i;
            }
        }
        return null;
    }
}



