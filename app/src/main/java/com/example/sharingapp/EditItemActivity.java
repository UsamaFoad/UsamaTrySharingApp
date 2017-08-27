package com.example.sharingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Edit pre-existing item: editing an item consists of deleting the old item and adding a new item
 * with the old item's id.
 */
public class EditItemActivity extends AppCompatActivity implements Observer {

    private ItemList item_list = new ItemList();
    private ItemListController item_list_controller = new ItemListController(item_list);

    private Item item;
    private ItemController item_controller;

    private Context context;

    private UserList user_list = new UserList();
    private UserListController user_list_controller = new UserListController(user_list);

    private Bitmap image;
    private int REQUEST_CODE = 1;
    private ImageView photo;

    private EditText title;
    private EditText maker;
    private EditText description;
    private EditText length;
    private EditText width;
    private EditText height;
    private EditText minimum_bid;

    private TextView borrower_left_tv;
    private TextView borrower_right_tv;
    private TextView status_right_tv;

    private Button save_button;
    private Button view_bids_button;
    private Button view_contact_info_button;
    private Button delete_button;
    private Button set_available_button;
    private ImageButton add_image_button;
    private ImageButton delete_image_button;

    private boolean on_create_update;
    private int pos;

    String title_str;
    String maker_str;
    String description_str;
    String length_str;
    String width_str;
    String height_str;
    String user_id;
    String minimum_bid_str;
    String status_str;
    String borrower_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        title = (EditText) findViewById(R.id.title);
        maker = (EditText) findViewById(R.id.maker);
        description = (EditText) findViewById(R.id.description);
        length = (EditText) findViewById(R.id.length);
        width = (EditText) findViewById(R.id.width);
        height = (EditText) findViewById(R.id.height);
        status_right_tv = (TextView) findViewById(R.id.status_right_tv);
        minimum_bid = (EditText) findViewById(R.id.minimum_bid);                    // originally GONE
        borrower_left_tv = (TextView) findViewById(R.id.borrower_left_tv);          // originally GONE
        borrower_right_tv = (TextView) findViewById(R.id.borrower_right_tv);        // originally GONE
        photo = (ImageView) findViewById(R.id.image_view);

        delete_button = (Button) findViewById(R.id.delete_item);                    // originally GONE
        save_button = (Button) findViewById(R.id.save_button);                      // originally GONE
        view_bids_button = (Button) findViewById(R.id.view_bids_button);            // originally GONE
        view_contact_info_button = (Button) findViewById(R.id.view_contact_info_button);              // originally GONE
        set_available_button = (Button) findViewById(R.id.set_available_button);    // originally GONE
        add_image_button = (ImageButton) findViewById(R.id.add_image_button);       // originally GONE
        delete_image_button = (ImageButton) findViewById(R.id.cancel_image_button); // originally GONE

        // get intent from MainActivity
        Intent intent = getIntent();
        pos = intent.getIntExtra("position", 0);
        user_id = intent.getStringExtra("user_id");

        context = getApplicationContext();

        on_create_update = false; // suppress first call to update()
        item_list_controller.addObserver(this);
        item_list_controller.loadItems(context);

        on_create_update = true;
        user_list_controller.addObserver(this);
        user_list_controller.loadUsers(context); // call update occurs

        on_create_update = false; // suppress any further calls to update()
    }

    public void addPhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    public void deletePhoto(View view) {
        image = null;
        photo.setImageResource(android.R.drawable.ic_menu_gallery);
        Toast.makeText(getApplicationContext(), "Photo removed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent intent){
        if (request_code == REQUEST_CODE && result_code == RESULT_OK){
            Bundle extras = intent.getExtras();
            image = (Bitmap) extras.get("data");
            photo.setImageBitmap(image);
            Toast.makeText(getApplicationContext(), "Photo added.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        Intent main_intent = new Intent(this, MainActivity.class);
        main_intent.putExtra("user_id", user_id);
        startActivity(main_intent);
    }

    public void deleteItem(View view) {

        boolean success = item_list_controller.deleteItem(item, context);

        if (!success){
            return;
        }

        item_list_controller.removeObserver(this);
        user_list_controller.removeObserver(this);

        // End EditItemActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_id", user_id);
        Toast.makeText(getApplicationContext(), "Item removed.", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void saveItem(View view) {

        title_str = title.getText().toString();
        maker_str = maker.getText().toString();
        description_str = description.getText().toString();
        length_str = length.getText().toString();
        width_str = width.getText().toString();
        height_str = height.getText().toString();
        status_str = item_controller.getStatus();
        minimum_bid_str = minimum_bid.getText().toString();

        if(!validateInput()){
            return;
        }

        // Reuse the item id
        String item_id = item_controller.getId();

        Item updated_item = new Item(title_str, maker_str, description_str, user_id, minimum_bid_str, image, item_id);
        ItemController updated_item_controller = new ItemController(updated_item);
        updated_item_controller.setDimensions(length_str, width_str, height_str);
        updated_item_controller.setStatus(status_str);

        boolean success = item_list_controller.editItem(item, updated_item, context);

        if (!success){
            return;
        }

        item_list_controller.removeObserver(this);
        user_list_controller.removeObserver(this);

        // End EditItemActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_id", user_id);
        Toast.makeText(getApplicationContext(), "Item edited.", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void viewBids(View view){

        user_list_controller.removeObserver(this);
        item_list_controller.removeObserver(this);

        // call ViewItemBidsActivity
        Intent intent = new Intent(this, ViewItemBidsActivity.class);
        intent.putExtra("user_id", user_id);
        intent.putExtra("item_id", item_controller.getId());
        startActivity(intent);
    }

    public void setAvailable(View view){

        item_controller.setStatus("Available"); // update status
        saveItem(view); // must save the item so that the change in status is saved
    }

    /**
     * Only need to update the view once from the onCreate method
     */
    public void update() {

        if (on_create_update){

            // For all status options we do the following
            item = item_list_controller.getItem(pos);
            item_controller = new ItemController(item);

            title.setText(item_controller.getTitle());
            maker.setText(item_controller.getMaker());
            description.setText(item_controller.getDescription());
            length.setText(item_controller.getLength());
            width.setText(item_controller.getWidth());
            height.setText(item_controller.getHeight());

            status_str = item_controller.getStatus();
            status_right_tv.setText(status_str);

            minimum_bid_str = item_controller.getMinBid().toString();
            minimum_bid.setText(minimum_bid_str);

            image = item_controller.getImage();
            if (image != null) {
                photo.setImageBitmap(image);
            } else {
                photo.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // AVAILABLE
            if (status_str.equals("Available")){

                title.setEnabled(true);
                maker.setEnabled(true);
                description.setEnabled(true);
                length.setEnabled(true);
                width.setEnabled(true);
                height.setEnabled(true);
                minimum_bid.setEnabled(true);

                add_image_button.setVisibility(View.VISIBLE);
                delete_image_button.setVisibility(View.VISIBLE);
                delete_button.setVisibility(View.VISIBLE);
                save_button.setVisibility(View.VISIBLE);
            }

            // BIDDED
            if (status_str.equals("Bidded")){

                view_bids_button.setVisibility(View.VISIBLE);
                title.setEnabled(false);
                maker.setEnabled(false);
                description.setEnabled(false);
                length.setEnabled(false);
                width.setEnabled(false);
                height.setEnabled(false);
                minimum_bid.setEnabled(false);
            }

            // BORROWED
            if (status_str.equals("Borrowed")){

                title.setEnabled(false);
                maker.setEnabled(false);
                description.setEnabled(false);
                length.setEnabled(false);
                width.setEnabled(false);
                height.setEnabled(false);
                minimum_bid.setEnabled(false);

                borrower_left_tv.setVisibility(View.VISIBLE);
                borrower_right_tv.setVisibility(View.VISIBLE);
                view_contact_info_button.setVisibility(View.VISIBLE);
                set_available_button.setVisibility(View.VISIBLE);

                User borrower = item_controller.getBorrower();
                borrower_str = borrower.getUsername();
                borrower_right_tv.setText(borrower_str);
            }
        }
    }

    public boolean validateInput(){

        if (title_str.equals("")) {
            title.setError("Empty field!");
            return false;
        }

        if (maker_str.equals("")) {
            maker.setError("Empty field!");
            return false;
        }

        if (description_str.equals("")) {
            description.setError("Empty field!");
            return false;
        }

        if (length_str.equals("")) {
            length.setError("Empty field!");
            return false;
        }

        if (width_str.equals("")) {
            width.setError("Empty field!");
            return false;
        }

        if (height_str.equals("")) {
            height.setError("Empty field!");
            return false;
        }

        if (minimum_bid_str.equals("")) {
            minimum_bid.setError("Empty field!");
            return false;
        }

        if (Float.valueOf(minimum_bid_str) <= 0) {
            minimum_bid.setError("Starting bid must be above 0!");
            return false;
        }

        return true;
    }

    public void viewUserActivity(View view){

        user_list_controller.removeObserver(this);
        item_list_controller.removeObserver(this);

        // call ViewUserActivity
        Intent intent = new Intent(this, ViewUserActivity.class);
        intent.putExtra("borrower_str", borrower_str);
        startActivity(intent);
    }
}
