package com.example.sharingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewItemBidsActivity extends AppCompatActivity implements Observer {

    private BidList bid_list = new BidList();
    private BidListController bid_list_controller = new BidListController(bid_list);

    private ArrayList<Bid> item_bid_list; // bids placed on the item

    private ItemList item_list = new ItemList();
    private ItemListController item_list_controller = new ItemListController(item_list);

    private UserList user_list = new UserList();
    private UserListController user_list_controller = new UserListController(user_list);

    private Context context;

    private ListView item_bids;
    private ArrayAdapter<Bid> adapter;

    private String user_id;
    private String item_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_bids);

        // Get intent from EditItemActivity
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        item_id = intent.getStringExtra("item_id");

        context = getApplicationContext();

        bid_list_controller.loadBids(context);
        bid_list_controller.addObserver(this);
        item_bid_list = bid_list_controller.getItemBids(item_id);

        item_list_controller.addObserver(this);
        item_list_controller.loadItems(context);
        user_list_controller.loadUsers(context);
    }

    public void acceptBid(View view) {

        int pos = item_bids.getPositionForView(view);

        // set status to borrowed
        Bid bid = adapter.getItem(pos);
        BidController bid_controller = new BidController(bid);

        Item item = item_list_controller.getItemById(item_id);
        ItemController item_controller = new ItemController(item);

        String borrower_username = bid_controller.getBidderUsername();
        User borrower = user_list_controller.getUserByUsername(borrower_username);

        String title = item_controller.getTitle();
        String maker = item_controller.getMaker();
        String description = item_controller.getDescription();
        String owner_id = item_controller.getOwnerId();
        String minimum_bid = item_controller.getMinBid().toString();
        Bitmap image = item_controller.getImage();
        String length = item_controller.getLength();
        String width = item_controller.getWidth();
        String height = item_controller.getHeight();
        String status = "Borrowed";

        Item updated_item = new Item(title, maker, description, owner_id, minimum_bid, image, item_id);
        ItemController updated_item_controller = new ItemController(updated_item);
        updated_item_controller.setDimensions(length, width, height);
        updated_item_controller.setStatus(status);
        updated_item_controller.setBorrower(borrower);

        boolean success = item_list_controller.editItem(item, updated_item, context);

        if (!success){
            return;
        }

        // delete all other bids related to that item.
        bid_list_controller.removeItemBids(item_id);
        bid_list_controller.saveBids(context); // Save the changes

        item_list_controller.removeObserver(this);
        bid_list_controller.removeObserver(this);

        // End ViewItemBidsActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_id", user_id);
        Toast.makeText(getApplicationContext(), "Bid accepted.", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void declineBid(View view){
        int pos = item_bids.getPositionForView(view);

        // set status to borrowed
        Bid bid = adapter.getItem(pos);
        BidController bid_controller = new BidController(bid);

        Item item = item_list_controller.getItemById(item_id);
        ItemController item_controller = new ItemController(item);

        String borrower_username = bid_controller.getBidderUsername();
        User borrower = user_list_controller.getUserByUsername(borrower_username);

        String title = item_controller.getTitle();
        String maker = item_controller.getMaker();
        String description = item_controller.getDescription();
        String owner_id = item_controller.getOwnerId();
        String minimum_bid = item_controller.getMinBid().toString();
        Bitmap image = item_controller.getImage();
        String length = item_controller.getLength();
        String width = item_controller.getWidth();
        String height = item_controller.getHeight();
        String status = item_controller.getStatus(); // "Borrowed"

        // delete selected bid.
        bid_list_controller.removeBid(bid);
        item_bid_list.remove(bid);
        bid_list_controller.saveBids(context); // Save the changes, call to update

        if (bid_list_controller.isEmpty()) {
            status = "Available";
        }

        Item updated_item = new Item(title, maker, description, owner_id, minimum_bid, image, item_id);
        ItemController updated_item_controller = new ItemController(updated_item);
        updated_item_controller.setDimensions(length, width, height);
        updated_item_controller.setStatus(status);

        boolean success = item_list_controller.editItem(item, updated_item, context);

        if (!success){
            return;
        }
        Toast.makeText(getApplicationContext(), "Bid declined.", Toast.LENGTH_SHORT).show();
    }

    public void declineAllBids(View view){
        Item item = item_list_controller.getItemById(item_id);
        ItemController item_controller = new ItemController(item);

        String title = item_controller.getTitle();
        String maker = item_controller.getMaker();
        String description = item_controller.getDescription();
        String owner_id = item_controller.getOwnerId();
        String minimum_bid = item_controller.getMinBid().toString();
        Bitmap image = item_controller.getImage();
        String length = item_controller.getLength();
        String width = item_controller.getWidth();
        String height = item_controller.getHeight();
        String status = "Available";

        Item updated_item = new Item(title, maker, description, owner_id, minimum_bid, image, item_id);
        ItemController updated_item_controller = new ItemController(updated_item);
        updated_item_controller.setDimensions(length, width, height);
        updated_item_controller.setStatus(status);

        boolean success = item_list_controller.editItem(item, updated_item, context);

        if (!success){
            return;
        }

        // delete all bids related to that item.
        bid_list_controller.removeItemBids(item_id);
        bid_list_controller.saveBids(context); // Save the changes

        item_list_controller.removeObserver(this);
        bid_list_controller.removeObserver(this);

        // End ViewItemBidsActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_id", user_id);
        Toast.makeText(getApplicationContext(), "All bids declined.", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    /**
     * Update the view
     */
    public void update() {

        item_bids = (ListView) findViewById(R.id.item_bids);
        adapter = new BidActivityAdapter(this, item_bid_list);
        item_bids.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
