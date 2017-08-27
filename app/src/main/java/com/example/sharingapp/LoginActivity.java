package com.example.sharingapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A login screen
 */
public class LoginActivity extends AppCompatActivity {

    private UserList user_list = new UserList();
    private UserListController user_list_controller = new UserListController(user_list);

    private EditText username;
    private EditText email;
    private TextView email_tv;
    private Context context;
    private String username_str;
    private String email_str;
    private User user;
    private UserController user_controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        email_tv = (TextView) findViewById(R.id.email_tv);

        email.setVisibility(View.GONE);
        email_tv.setVisibility(View.GONE);

        context = getApplicationContext();
        user_list_controller.loadUsers(context);
    }

    public void login(View view) {

        username_str = username.getText().toString();
        email_str = email.getText().toString();

        if (user_list_controller.getUserByUsername(username_str) == null && email.getVisibility() == View.GONE) {
            email.setVisibility(View.VISIBLE);
            email_tv.setVisibility(View.VISIBLE);
            email.setError("New user! Must enter email!");
            return;
        }

        if (user_list_controller.getUserByUsername(username_str) == null && email.getVisibility() == View.VISIBLE){

            if(!validateInput()){
                return;
            }

            user = new User(username_str, email_str, null);

            boolean success = user_list_controller.addUser(user, context);

            if (!success){
                return;
            }
            Toast.makeText(getApplicationContext(), "Profile created.", Toast.LENGTH_SHORT).show();
        }

        // Either way, start MainActivity
        user = user_list_controller.getUserByUsername(username_str);
        user_controller = new UserController(user);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_id", user_controller.getId());
        Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public boolean validateInput(){
        if (email_str.equals("")) {
            email.setError("Empty field!");
            return false;
        }

        if (user_list_controller.getUserByUsername(username_str) != null) {
            username.setError("Username already taken!");
            return false;
        }

        return true;
    }
}
