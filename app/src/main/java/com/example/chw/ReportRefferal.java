package com.example.chw;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Timer;

public class ReportRefferal extends AppCompatActivity {

    Button btn_getRefferal;
    MyRecyclerViewAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    General general;
    String Title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_refferal);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        Title = "Referral";

        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>"+Title+"</font>"));
        general = new General(ReportRefferal.this);

        btn_getRefferal = (Button) findViewById(R.id.btn_getRefferal);
        btn_getRefferal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    general.getRefferalList(ReportRefferal.this);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        getReportReferrals();

    }

    private void getReportReferrals(){
        general = new General(ReportRefferal.this);
        JSONArray jsonArray = null;
        try{
            try {
                jsonArray = new JSONArray(general.getFile("referral", ReportRefferal.this));
/*                Title.concat("("+jsonArray.length()+")");
                getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+Title+"</font>"));*/
                if(jsonArray != null){
                    recyclerView = (RecyclerView) findViewById(R.id.recycler_referral);
                    adapter = new MyRecyclerViewAdapter(ReportRefferal.this, jsonArray);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(adapter);
                }else{
                    jsonArray = new JSONArray();
/*                    Title.concat("("+jsonArray.length()+")");
                    getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+Title+"</font>"));*/
                    recyclerView = (RecyclerView) findViewById(R.id.recycler_referral);
                    adapter = new MyRecyclerViewAdapter(ReportRefferal.this, jsonArray);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(adapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
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

}