package com.example.chw;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity {

    General general;
    ProgressDialog p;

    LinearLayout emergency;
    LinearLayout report;

    TextView greetings;
    TextView _username;
    LinearLayout logout;
    LinearLayout Menu3;
    Filling filling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        general = new General(this);
        String Title = "Chaid-HF "+ general.getVersionNumber();
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>"+ Title +" </font>"));

        general = new General(MainActivity.this);
        filling = new Filling();
        requestPermision();
        greetings = (TextView) findViewById(R.id.greetings);
        _username = (TextView) findViewById(R.id.username);
        if(general.getFile("token", MainActivity.this).equals("")){
            loginDialog();
        }

        logout = (LinearLayout) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog();
            }
        });
        emergency = (LinearLayout) findViewById(R.id.emergency);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReportedVisits.class);
                startActivity(intent);
            }
        });

        report = (LinearLayout) findViewById(R.id.report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Report.class);
                startActivity(intent);
            }
        });

        String usr = general.getFile("initialData", MainActivity.this);
        JSONObject json = null;
        try {
            json = new JSONObject(usr);
            String full_name = json.getString("full_name");
            if(usr.equals("")){
                //loginDialog();

            }else {
                greetUser(full_name);
            }
        } catch (JSONException e) {
            //loginDialog();
            e.printStackTrace();
        }
        Menu3 = (LinearLayout) findViewById(R.id.Menu3);
        Menu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListOfRefferal.class);
                startActivity(intent);
            }
        });
    }

    private void greetUser(String username) {
        _username = (TextView) findViewById(R.id.username);
        greetings = (TextView) findViewById(R.id.greetings);
        _username.setText(username.toString().toUpperCase().charAt(0)+username.toString().substring(1,username.toString().length()));
        greetings.setText(MainActivity.this.getResources().getString(R.string.welcome));
    }

    protected void loginDialog(){
        general = new General(MainActivity.this);
        //database = new Database(this);
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.login_dialog, null);
        final EditText UserName = (EditText) promptsView.findViewById(R.id.UserName);
        final EditText Password = (EditText) promptsView.findViewById(R.id.Password);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Login",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if(general.isNetworkAvailable(MainActivity.this)){
                                    updateAccesToken(UserName.getText().toString(), Password.getText().toString());
                                }else{
                                    Toast.makeText(MainActivity.this, "Switch ON your mobile data", Toast.LENGTH_SHORT).show();
                                    loginDialog();
                                }

                            }
                        })
                .setNegativeButton("Exit",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void updateAccesToken(String usernamesdata,String password) {
        p = new ProgressDialog(MainActivity.this);
        p.setMessage("Verifying credentials");
        p.setIndeterminate(false);
        p.setCancelable(false);
        general = new General(MainActivity.this);
        try {
            String urls = general.url+"login";

            RequestParams params = new RequestParams();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("username", usernamesdata);
            jsonObject.put("password", password);

            StringEntity entity = new StringEntity(jsonObject.toString());


            final int DEFAULT_TIMEOUT = 20 * 1000;
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(DEFAULT_TIMEOUT);
            client.post(getApplicationContext(), urls, entity, "application/json", new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            super.onStart();
                            p.show();
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            p.hide();
                            try {
                                String resuldata = new String(responseBody, "UTF-8");
                                Log.d("splashAccessToken: ", resuldata);

                                JSONObject jsonObject = new JSONObject(resuldata);

                                String access_token =jsonObject.getString("access_token");
                                String username=jsonObject.getString("username");
                                //greetUser(username);

                                Filling filling = new Filling();
                                filling.writeToFile("token", access_token, MainActivity.this);
                                filling.writeToFile("username", username, MainActivity.this);
                                general.getInitialData(MainActivity.this);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            p.hide();
                            try {
                                String resuldata = new String(responseBody, "UTF-8");
                                Toast.makeText(MainActivity.this, "Wrong credentials please try again", Toast.LENGTH_LONG).show();
                                loginDialog();

                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void requestPermision(){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.VIBRATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.SEND_SMS};
        if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("ARE YOU SURE?");
        builder.setMessage("Inorder to login again you will need internet bundle. Just QUIT to be safe");

        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                filling.writeToFile("token", "", MainActivity.this);
                filling.writeToFile("username", "", MainActivity.this);
                finish();
            }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }

        }).setNeutralButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}