package com.example.panos.client;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.panos.Connectivity.InternetChecker;
import com.example.panos.clientlisteners.FlashListener;
import com.example.panos.clientlisteners.MusicListener;
import com.example.panos.parameters.AppParameters;
import com.example.panos.parameters.AppState;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String status = "UNINITIALIZED";
    boolean doubleBackToExitPressedOnce = false;
    boolean permissionGranted = false;

    private AlertDialog alertDialogNoFlash;
    private AlertDialog alertDialogError;

    private FlashListener flashListener = new FlashListener(AppState.controller);
    private MusicListener musicListener = new MusicListener(AppState.controller);

    public Handler mHandler = new Handler();

    private InternetChecker checker;
    private Thread checkerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean hasFlash = false;

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setIcon(R.mipmap.my_logo);


        // Read Shared Preferences
        final String PREF_FILE_NAME = "PrefFile";
        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

        AppParameters.ip = (preferences.getString("storedIP", AppParameters.ip));
        AppParameters.port = (preferences.getInt("storedPort", AppParameters.port));
        AppParameters.clientId = (preferences.getString("storedClientId", AppParameters.clientId));
        AppParameters.frequency = (preferences.getInt("storedFrequency", AppParameters.frequency));

        final Button flash_button = (Button) this.findViewById(R.id.flashButton);
        final Button music_button = (Button) this.findViewById(R.id.musicButton);


        // Check Permissions
        try {
            if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                hasFlash = true;

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;

                    Camera camera;
                    Camera.Parameters parameters;


                    camera = Camera.open();
                    parameters = camera.getParameters();

                    if (camera != null && parameters != null) {
                        AppState.controller.initCamera(camera, parameters);
                    } else {
                        throw new Exception("No camera");
                    }
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        } catch (Exception ex) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("ERROR....");
            builder.setMessage("Exception while initializing camera" + ex.getMessage());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();

                }
            });
            alertDialogError = builder.create();
            alertDialogError.show();
            return;
        } finally {
            if (!hasFlash) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("ERROR....");
                builder.setMessage("Flashlight app is not available here");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();

                    }
                });
                alertDialogNoFlash = builder.create();
                alertDialogNoFlash.show();
                return;
            }
        }

        flash_button.setOnClickListener(flashListener);

        if (permissionGranted) {
            flash_button.setEnabled(true);
        } else {
            flash_button.setEnabled(false);
        }

        // https://github.com/JohnsAndroidStudioTutorials/SoundButtonClick/blob/master/app/src/main/java/com/johnsandroidstudiotutorials/soundbuttonclick/MainActivity.java

        music_button.setOnClickListener(musicListener);

        AppState.controller.init();
        AppState.controller.initPlayer(this);

        checker = new InternetChecker(this, AppState.controller, AppParameters.poll);

        checkerThread = new Thread(checker);

        checkerThread.start();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;

                    Camera camera;
                    Camera.Parameters parameters;


                    camera = Camera.open();
                    parameters = camera.getParameters();

                    if (camera != null && parameters != null) {
                        AppState.controller.initCamera(camera, parameters);
                    }
                }
            }
        }


        if (AppState.controller.verify() == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("ERROR....");
            builder.setMessage("Initialization failed");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            final Button flash_button = (Button) this.findViewById(R.id.flashButton);
            final Button music_button = (Button) this.findViewById(R.id.musicButton);

            flash_button.setEnabled(false);
            music_button.setEnabled(false);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            final Button flash_button = (Button) this.findViewById(R.id.flashButton);
            flash_button.setEnabled(true);

            Toast.makeText(this, "Camera and Flash initialized ok", Toast.LENGTH_LONG).show();
        }
    }

    /*@Override
    protected void onRestart() {
        super.onRestart();
        if (AppParameters.reconnect == true) {
            AppState.controller.unsubscribeFromAll();
            status = AppState.controller.subscribeToAll(this.getApplicationContext());
            AppParameters.reconnect = false;
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settingsid:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.modeid:
                Intent intent2 = new Intent(this, ConnectionActivity.class);
                startActivity(intent2);
                return true;
            case R.id.exitid:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Exit");
                builder.setMessage("Do you really want to exit ?");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppState.controller.stopMusic();

            if (permissionGranted) {
                AppState.controller.disableFlash();
            }

            if (AppState.controller.subscribed()) {
                AppState.controller.unsubscribeFromAll();
            }

            AppState.controller.destroy();
        } catch (Exception ex) {

        }

        try {
            checker.stop();
            checkerThread.interrupt();
        }catch (Exception ex ) {

        }
    }

    //https://stackoverflow.com/questions/33910437/android-double-back-press-to-close-the-app-having-fragments
    @Override
    public void onBackPressed() {
        // Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
    }
}