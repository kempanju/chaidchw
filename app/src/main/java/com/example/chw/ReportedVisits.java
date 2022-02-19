package com.example.chw;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReportedVisits extends AppCompatActivity {

    Filling filling;
    General general;
    JSONArray jsonArray = null;
    JSONArray status_list = null;
    JSONArray report_list = null;
    JSONObject jsonObject = null;
    Spinner spinner;
    Spinner spinner2;
    EditText comment;
    Button btn_send;
    int chad_id = 0;
    String message = "";
    String code = "";

    TextView health_facility = null;
    int user_id = 0;
    ArrayList<String> repo_list = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_visits);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Reported Visits </font>"));
        health_facility = (TextView) findViewById(R.id.health_facility);
        spinner = (Spinner) findViewById(R.id.status_list);
        spinner2 = (Spinner) findViewById(R.id.reported_visit);
        comment = (EditText) findViewById(R.id.comment);
        btn_send = (Button) findViewById(R.id.btn_send);

        filling = new Filling();
        general = new General(ReportedVisits.this);


        try {
            jsonObject = new JSONObject(general.getFile("initialData", ReportedVisits.this));
            jsonArray = jsonObject.getJSONArray("reported_visit");
            String status_list_string = jsonObject.getString("status_list");
            String reported_visit_string = jsonObject.getString("reported_visit");
            health_facility.setText(jsonObject.getString("facility_name"));
            user_id = jsonObject.getInt("id");
            status_list = new JSONArray(status_list_string);
            report_list = new JSONArray(reported_visit_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject obj = null;
        list.add("STATUS");
        for(int i=0; i<status_list.length(); i++){
            try {
                obj = status_list.getJSONObject(i);
                String name = obj.getString("name");
                list.add(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject obj2 = null;
        repo_list.add("REPORTED VISIT");
        for(int i=0; i<report_list.length(); i++){
            try {
                obj2 = report_list.getJSONObject(i);
                String house_hold = obj2.getString("house_hold");
                String chad_id = obj2.getString("chad_id");
                String respondent_name = obj2.getString("respondent_name");
                String village_name = obj2.getString("village_name");
                String reg_no = obj2.getString("reg_no");
                repo_list.add(house_hold+" [ "+reg_no+" ]");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(ReportedVisits.this, android.R.layout.simple_spinner_dropdown_item, list);
        //spinner.setPrompt("STATUS");
        spinner.setAdapter(arrayAdapter);

        ArrayAdapter arrayAdapter2 = new ArrayAdapter(ReportedVisits.this, android.R.layout.simple_spinner_dropdown_item, repo_list);
        //spinner.setPrompt("STATUS");
        spinner2.setAdapter(arrayAdapter2);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if((position-1) >= 0){
                    try {
                        JSONObject obj2 = status_list.getJSONObject(position-1);
                        code = obj2.getString("code");
                        //Toast.makeText(Part40.this, number, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if((position-1) >= 0){
                    try {
                        JSONObject obj2 = report_list.getJSONObject(position-1);
                        chad_id = obj2.getInt("chad_id");
                        try {
                            message = obj2.getString("message");
                            TextView msg = (TextView) findViewById(R.id.message);
                            message = message.replace("%0a", "\n");
                            msg.setText(message);
                        }catch (Exception e){
                            TextView msg = (TextView) findViewById(R.id.message);
                            msg.setText(message);
                        }

                        //Toast.makeText(Part40.this, number, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.report_visit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshReportedVisits();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshReportedVisits() {
        general = new General(ReportedVisits.this);
        try {
            general.getInitialData(ReportedVisits.this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void saveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportedVisits.this);

        builder.setTitle("Confirm");
        builder.setMessage("Click SAVE to save your feedback in phone memory. You will see your feedback when you click UPLOAD in your dashboard");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                final JSONObject object = new JSONObject();
                try {
                    object.put("user_id", user_id);
                    object.put("chad_id", chad_id);
                    object.put("comment", comment.getText().toString());
                    object.put("code", code);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(!code.equals("") && chad_id != 0){
                    DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
                    Date dateobj = new Date();
                    String todayDate = df.format(dateobj);

                    Filling filling = new Filling();
                    String cm = comment.getText().toString();
                    String filename = chad_id+"."+todayDate;
                    try {
                        filename = chad_id+", "+todayDate+", "+cm.substring(0,10);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    filling.saveFileToFolder( filename, object.toString());
                    Toast.makeText(ReportedVisits.this, "Feedback Saved Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(ReportedVisits.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                }else{
                    Toast.makeText(ReportedVisits.this, "Please select REPORTED VISIT and STATUS", Toast.LENGTH_SHORT).show();
                }
            }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }

        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}