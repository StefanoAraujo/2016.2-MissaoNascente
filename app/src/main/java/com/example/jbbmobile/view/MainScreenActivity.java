package com.example.jbbmobile.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jbbmobile.R;
import com.example.jbbmobile.controller.BooksController;
import com.example.jbbmobile.controller.LoginController;
import com.example.jbbmobile.controller.MainController;
import com.example.jbbmobile.controller.PreferenceController;
import com.example.jbbmobile.controller.RegisterElementController;
import com.example.jbbmobile.model.Element;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

public class MainScreenActivity extends AppCompatActivity  implements View.OnClickListener{

    private TextView textViewNickname;
    private LoginController loginController;
    private ImageButton menuMoreButton;
    private ImageButton almanacButton;
    private ImageView readQrCodeButton;
    private MainController mainController;
    private RegisterElementFragment registerElementFragment;
    private static final String TAG = "MainScreenActivity";

    private void showPopup(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        popupMenu.getMenuInflater().inflate(R.menu.settings_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.achievement:
                        //call achievement activity
                        return true;
                    case R.id.rankingIcon:
                        Intent rankingIntent = new Intent(MainScreenActivity.this, RankingScreenActivity.class);
                        MainScreenActivity.this.startActivity(rankingIntent);
                        return true;
                    case R.id.preferenceIcon:
                        goToPreferenceScreen();
                        return true;
                    case R.id.aboutIcon:
                        //call about activity
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        if (savedInstanceState == null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            registerElementFragment = new RegisterElementFragment();
            fragmentTransaction.add(R.id.register_fragment, registerElementFragment, "RegisterElementFragment");
            fragmentTransaction.commit();
        }

        initViews();
        this.loginController = new LoginController();
        this.loginController.loadFile(this.getApplicationContext());


        BooksController booksController = new BooksController(this);
        booksController.currentPeriod();
    }

    @Override
    protected void onStart() {
        super.onStart();
        textViewNickname = (TextView) findViewById(R.id.titleID);

        if (loginController.checkIfUserHasGoogleNickname()) {
            enterNickname();
        } else {
            textViewNickname.setText("");
            textViewNickname.setText("Bem-vindo" + " " + loginController.getExplorer().getNickname());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.almanacButton:
                goToAlmacScreen();
                break;
            case R.id.menuMoreButton:
               // goToPreferenceScreen();
                showPopup(findViewById(R.id.menuMoreButton));
                break;
            case R.id.readQrCodeButton:
                mainController = new MainController(MainScreenActivity.this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        try{
            RegisterElementController registerElementController = registerElementFragment.getController();
            if (result != null) {
                if (result.getContents() == null) {
                    mainController.setCode(null);
                } else {
                    try {
                        registerElementController.associateElementbyQrCode(result.getContents(), getContext());
                    } catch(SQLException exception){
                        Toast.makeText(this,"Elemento já registrado!", Toast.LENGTH_SHORT).show();
                    } catch(IllegalArgumentException exception){
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Element element = registerElementController.getElement();

                    registerElementFragment.showElement(element);
                    findViewById(R.id.readQrCodeButton).setVisibility(View.INVISIBLE);
                    findViewById(R.id.register_fragment).setVisibility(View.VISIBLE);
                    findViewById(R.id.register_fragment).requestLayout();

                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }catch (IllegalArgumentException exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initViews() {
        this.menuMoreButton = (ImageButton) findViewById(R.id.menuMoreButton);
        this.almanacButton = (ImageButton) findViewById(R.id.almanacButton);
        this.readQrCodeButton = (ImageView) findViewById(R.id.readQrCodeButton);
        this.menuMoreButton.setOnClickListener(this);
        this.almanacButton.setOnClickListener(this);
        this.readQrCodeButton.setOnClickListener(this);
    }

    private void invalidNicknameError() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("ERROR");
        alert.setMessage("Nickname inválido!");
        alert.setCancelable(false);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterNickname();
            }
        });
        alert.show();
    }

    private void enterNickname() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setTitle("NICKNAME");
        alert.setCancelable(false);
        alert.setMessage("Coloque o novo nickname!");
        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterNicknameOnClick(input);
            }
        });
        alert.show();
    }

    private void enterNicknameOnClick(EditText input) {
        try {
            String newNickname = input.getText().toString();
            PreferenceController preferenceController = new PreferenceController();
            preferenceController.updateNickname(newNickname, loginController.getExplorer().getEmail(), MainScreenActivity.this.getApplicationContext());
            loginController.deleteFile(MainScreenActivity.this);
            loginController.loadFile(MainScreenActivity.this);
            new LoginController().realizeLogin(loginController.getExplorer().getEmail(), MainScreenActivity.this);
            MainScreenActivity.this.recreate();
        } catch (IOException e) {
            Toast.makeText(MainScreenActivity.this, "Error!", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException i) {
            invalidNicknameError();
        }
    }


    private void goToPreferenceScreen() {
        Intent registerIntent = new Intent(MainScreenActivity.this, PreferenceScreenActivity.class);
        MainScreenActivity.this.startActivity(registerIntent);
        finish();
    }

    private void goToAlmacScreen() {
        Intent almanacIntent = new Intent(MainScreenActivity.this, AlmanacScreenActivity.class);
        MainScreenActivity.this.startActivity(almanacIntent);
        finish();
    }

    private Context getContext() {
        return this;
    }


}