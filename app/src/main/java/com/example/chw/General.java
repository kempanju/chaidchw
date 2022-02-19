package com.example.chw;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class General {
    //Database database;
    Context context;
    ProgressDialog p;
    //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

    public General(Context context){
        this.context = context;
    }

    public String url = "http://chaid.mkapafoundation.or.tz/api/";//http://www.habarisasa.com:8080/chaid/api/
    public String version_number = "2.0";

    public boolean isNetworkAvailable(Context ctx){
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo ni = cm.getActiveNetworkInfo();

        return (ni != null && ni.isConnected());
    }
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddress = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddress.equals("");
        } catch (Exception e) {
            return true;
        }
    }

    public String getVersionNumber(){
        return this.version_number;
    }

    public String getFile(String  filename,Context context){
        Filling filling = new Filling();
        return filling.readFromFile(filename, context);
    }

    public void getInitialData(final Context context) throws UnsupportedEncodingException {
        p = new ProgressDialog(context);
        p.setMessage("Getting Data...");
        p.setIndeterminate(false);
        p.setCancelable(false);
        String urls = url+"home/initialDataAdmin";

        final int DEFAULT_TIMEOUT = 20 * 1000;
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer "+ getFile("token", context));
        RequestParams params = new RequestParams();


        params.put("username", getFile("username", context));


        client.setTimeout(DEFAULT_TIMEOUT);
        client.post(context, urls, params, new AsyncHttpResponseHandler() {

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
                            Toast.makeText(context, "Initial data has been updated", Toast.LENGTH_LONG).show();
                            JSONObject json_data_info = new JSONObject(resuldata);
                            Filling filling = new Filling();
                            filling.writeToFile("initialData", json_data_info.toString(), context);
                            if(context instanceof ReportedVisits){
                                ((ReportedVisits) context).finish();
                                Intent intent = new Intent(context, ReportedVisits.class);
                                context.startActivity(intent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        p.hide();
                        try {
                            String resuldata = new String(responseBody, "UTF-8");
                            Toast.makeText(context, "Something went wrong "+ statusCode, Toast.LENGTH_LONG).show();
/*
                            Calendar today = Calendar.getInstance();
                            today.clear(Calendar.HOUR); today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
                            Date todayDate = today.getTime();

                            //JSONObject jsonObject = new JSONObject(resuldata);
                            JSONObject object = new JSONObject(getFile("MainObject", context));
                            String respondent = object.getString("respondent");
                            JSONObject house_hold = object.getJSONObject("house_hold");
                            int house_hold_id  = house_hold.getInt("house_hold_id");
                            Filling filling = new Filling();
                            filling.saveFileToFolder(todayDate + " " + respondent + " " + house_hold_id, getFile("MainObject", context));
                            Intent intent = new Intent(context, Dashboard.class);
                            context.startActivity(intent);*/
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }
        );
    }
    public void getRefferalList(final Context context) throws UnsupportedEncodingException {
        p = new ProgressDialog(context);
        p.setMessage("Getting Referral...");
        p.setIndeterminate(false);
        p.setCancelable(false);
        String urls = url+"home/getReferralList";

        final int DEFAULT_TIMEOUT = 20 * 1000;
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer "+ getFile("token", context));
        RequestParams params = new RequestParams();


        params.put("username", getFile("username", context));


        client.setTimeout(DEFAULT_TIMEOUT);
        client.post(context, urls, params, new AsyncHttpResponseHandler() {

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
                            //Toast.makeText(context, resuldata, Toast.LENGTH_LONG).show();
                            JSONArray json_data_info = new JSONArray(resuldata);
                            Filling filling = new Filling();
                            filling.writeToFile("referral", json_data_info.toString(), context);

                            ((Activity) context).finish();
                            Intent intent = new Intent(context, ReportRefferal.class);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        p.hide();
                        try {
                            String resuldata = new String(responseBody, "UTF-8");
                            Toast.makeText(context, "Please check your network "+ statusCode, Toast.LENGTH_LONG).show();
/*
                            Calendar today = Calendar.getInstance();
                            today.clear(Calendar.HOUR); today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
                            Date todayDate = today.getTime();

                            //JSONObject jsonObject = new JSONObject(resuldata);
                            JSONObject object = new JSONObject(getFile("MainObject", context));
                            String respondent = object.getString("respondent");
                            JSONObject house_hold = object.getJSONObject("house_hold");
                            int house_hold_id  = house_hold.getInt("house_hold_id");
                            Filling filling = new Filling();
                            filling.saveFileToFolder(todayDate + " " + respondent + " " + house_hold_id, getFile("MainObject", context));
                            Intent intent = new Intent(context, Dashboard.class);
                            context.startActivity(intent);*/
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }
        );
    }


    public void sedFeedback(final Context context, int user_id, int chad_id, String comment, String code, final String filePath) throws UnsupportedEncodingException {
        p = new ProgressDialog(context);
        p.setMessage("Sending feedback...");
        p.setIndeterminate(false);
        p.setCancelable(false);
        String urls = url+"home/chadStatusApi";

        final int DEFAULT_TIMEOUT = 20 * 1000;
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "Bearer "+ getFile("token", context));
        RequestParams params = new RequestParams();

        params.put("user_id", user_id);
        params.put("chad_id", chad_id);
        params.put("comment", comment);
        params.put("code", code);

        final JSONObject object = new JSONObject();
        try {
            object.put("user_id", user_id);
            object.put("chad_id", chad_id);
            object.put("comment", comment);
            object.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        client.setTimeout(DEFAULT_TIMEOUT);
        client.post(context, urls, params, new AsyncHttpResponseHandler() {

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
                            Toast.makeText(context, "Feedback Sent", Toast.LENGTH_LONG).show();
                            File fdelete = new File(filePath);
                            if (fdelete.exists()) {
                                if (fdelete.delete()) {
                                    Toast.makeText(context, "file Deleted :" + filePath, Toast.LENGTH_SHORT).show();
                                } else {
                                    System.out.println("file not Deleted :" + filePath);
                                }
                            }
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EXIT", true);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        p.hide();
                        try {
                            String resuldata = new String(responseBody, "UTF-8");
                            Toast.makeText(context, statusCode+" : Failed to upload", Toast.LENGTH_LONG).show();
                            Calendar today = Calendar.getInstance();
                            today.clear(Calendar.HOUR); today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
                            Date todayDate = today.getTime();

                            //Filling filling = new Filling();
                            //filling.saveFileToFolder(String.valueOf(System.currentTimeMillis())+""+todayDate, object.toString());
                            ////Intent intent = new Intent(context, MainActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //intent.putExtra("EXIT", true);
                            //context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }
        );
    }
}
