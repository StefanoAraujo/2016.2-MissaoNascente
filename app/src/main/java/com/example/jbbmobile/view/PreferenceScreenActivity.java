package com.example.jbbmobile.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jbbmobile.R;
import com.example.jbbmobile.controller.Login;
import com.example.jbbmobile.controller.Preference;

import java.io.IOException;


public class PreferenceScreenActivity extends AppCompatActivity implements View.OnClickListener{

    private RelativeLayout editNickname;
    private RelativeLayout deleteAccount;
    private TextView nicknameShow;
    private TextView emailShow;
    private Login login;
    private final int DELETE = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_screen);
        initViews();

        this.login = new Login();
        this.login.loadFile(this);

        this.nicknameShow.setText("Nickname: "+ login.getExplorer().getNickname());
        this.emailShow.setText("Email: "+ login.getExplorer().getEmail());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editNicknameButton:
                editAccount();
                break;
            case R.id.deleteAccount:
                deleteAccount();
                break;
        }
    }

    private void initViews(){
        this.editNickname = (RelativeLayout)findViewById(R.id.editNicknameButton);
        this.deleteAccount = (RelativeLayout)findViewById(R.id.deleteAccount);
        this.nicknameShow = (TextView) findViewById(R.id.nicknameShow);
        this.emailShow = (TextView)findViewById(R.id.emailShow);
        this.editNickname.setOnClickListener((View.OnClickListener) this);
        this.deleteAccount.setOnClickListener((View.OnClickListener) this);


    }

    private void deleteAccount() {
        if(login.getExplorer().getPassword().equals("Yu8redeR6CRU")) {
            googleDelete();
        }else{
            normalDelete();
        }
    }

    private void normalDelete(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setTitle("Delete Account");
        alert.setMessage("Enter your password");
        alert.setView(input);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Preference preferenceController = new Preference();
                preferenceController.deleteExplorer(input.getText().toString(), login.getExplorer().getEmail(), PreferenceScreenActivity.this.getApplicationContext());
                login.deleteFile(PreferenceScreenActivity.this);
                Intent startScreenIntet = new Intent(PreferenceScreenActivity.this, StartScreenActivity.class);
                PreferenceScreenActivity.this.startActivity(startScreenIntet);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }


    /* Deleting account from google API. MVC may be unclear */
    private void googleDelete(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Account");
        alert.setMessage("Are you sure?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Preference preferenceController = new Preference();
                preferenceController.deleteExplorer(login.getExplorer().getEmail(), PreferenceScreenActivity.this.getApplicationContext());
                login.deleteFile(PreferenceScreenActivity.this);
                Intent startScreenIntet = new Intent(PreferenceScreenActivity.this, StartScreenActivity.class);
                Bundle b = new Bundle();
                b.putInt("Delete", DELETE);
                getIntent().putExtras(b);
                PreferenceScreenActivity.this.startActivity(startScreenIntet);
                finish();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void editAccount(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setTitle("NICKNAME");
        alert.setMessage("Enter your new Nickname");
        input.setMaxLines(1);
        alert.setView(input);
        input.setInputType(128);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNickname = input.getText().toString();
                Preference preferenceController = new Preference();
                if(!preferenceController.updateNickname(newNickname, login.getExplorer().getEmail(), PreferenceScreenActivity.this.getApplicationContext())){
                    existentNickname();
                }else{
                    login.deleteFile(PreferenceScreenActivity.this);
                    try {
                        new Login().realizeLogin(login.getExplorer().getEmail(), PreferenceScreenActivity.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PreferenceScreenActivity.this.recreate();

                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();


    }

    private void existentNickname(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("ERROR");
        alert.setMessage("This nickname already exists!");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

}