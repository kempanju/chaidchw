package com.example.chw;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ListOfRefferal extends AppCompatActivity {

    ListView listView;
    Button btn_upload;
    ArrayList<String> filenames = new ArrayList<>();
    General general;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_answers);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        general = new General(ListOfRefferal.this);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        String Title = "Upload Feedbacks";
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>"+ Title +" </font>"));
        File yourDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Feedback/");
        if(yourDir.listFiles() != null){
            for (File f : yourDir.listFiles()) {
                if (f.isFile())
                    filenames.add(f.getName());
                // Do your stuff
            }
        }else{
            Toast.makeText(ListOfRefferal.this, "No data", Toast.LENGTH_SHORT).show();
        }


        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filenames);

        listView = findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);

        listView = findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stringText;

                //in normal case
                stringText= ((TextView)view).getText().toString();

                deleteDialog(stringText);
            }

        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDialog();
            }
        });
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

    public void uploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListOfRefferal.this);

        builder.setTitle("Confirm");
        builder.setMessage("We are going to delete all successful sent feedback from your memory.\nAre you sure your ready to send all your feedback?");

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                File yourDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Feedback/");
                File file = null;
                StringBuilder text;
                if(yourDir.listFiles() != null){
                    for (File f : yourDir.listFiles()) {
                        if (f.isFile())
                            file = new File(yourDir,f.getName());
                        text = new StringBuilder();
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String line;

                            while ((line = br.readLine()) != null) {
                                text.append("").append(line);
                            }
                            br.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        // Do your stuff
                        if(general.isNetworkAvailable(ListOfRefferal.this)){
                            try {
                                JSONObject object = new JSONObject(String.valueOf(text));

                                general.sedFeedback(ListOfRefferal.this,object.getInt("user_id") , object.getInt("chad_id") , object.getString("comment"), object.getString("code"), yourDir+"/"+f.getName() );
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast.makeText(ListOfRefferal.this, "Please SWITCH ON your mobile data", Toast.LENGTH_SHORT).show();
                        }

                    }
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

    public void deleteDialog(final String filename) {
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Feedback/";
        general = new General(ListOfRefferal.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(ListOfRefferal.this);

        builder.setTitle("DELETE");
        builder.setMessage(filename);

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                File yourDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Feedback/");

                File fdelete = new File(yourDir+"/"+filename);
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        Toast.makeText(ListOfRefferal.this, "File deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ListOfRefferal.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ListOfRefferal.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
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