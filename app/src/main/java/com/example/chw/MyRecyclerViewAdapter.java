package com.example.chw;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter {


    Context context;
    private JSONArray mData;
    private LayoutInflater mInflater;

    String reg_no = "";
    String house_hold = "";
    String respondent_name = "";
    String village_name = "";
    private int focusedItem = 0;

    public MyRecyclerViewAdapter(Context context, JSONArray data){
        this.mData = data;
        this.context = context;
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // Handle key up and key down and attempt to move selection
        recyclerView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();

                // Return false if scrolled to the bounds and allow focus to move off the list
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return tryMoveSelection(lm, 1);
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        return tryMoveSelection(lm, -1);
                    }
                }

                return false;
            }
        });
    }

    private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
        int tryFocusItem = focusedItem + direction;

        // If still within valid bounds, move the selection, notify to redraw, and scroll
        if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
            notifyItemChanged(focusedItem);
            focusedItem = tryFocusItem;
            notifyItemChanged(focusedItem);
            lm.scrollToPosition(focusedItem);
            return true;
        }

        return false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setSelected(focusedItem == position);
        try {
            JSONObject object = mData.getJSONObject(position);
            reg_no = (object.getString("reg_no"));
            try{
                house_hold = (object.getString("house_hold"));
            }catch (Exception e){
                house_hold = "";
            }
            try{
                respondent_name = (object.getString("name"));
            }catch (Exception e){
                respondent_name = "";
            }

            try {
                village_name = (object.getString("hamlet"));
            }catch (Exception e){
                village_name = "";
            }

            JSONArray jsonArray = new JSONArray(object.getJSONArray("statuses").toString());
            JSONObject jsonObject = null;
            ((ViewHolder) holder).status_list.removeAllViews();
            for(int i=0; i<jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                String status = jsonObject.getString("name");
                String comment = jsonObject.getString("comment");

                TextView txt_status = new TextView(context);
                txt_status.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                txt_status.setTextColor(context.getResources().getColor(R.color.black));
                txt_status.setText(status);
                ((ViewHolder) holder).status_list.addView(txt_status);

                TextView txt_comment = new TextView(context);
                txt_comment.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                txt_comment.setTextColor(context.getResources().getColor(R.color.orange2));
                txt_comment.setBackgroundResource(R.drawable.corner_edittext);
                txt_comment.setText(comment);
                ((ViewHolder) holder).status_list.addView(txt_comment);
            }

            ((ViewHolder) holder).value_regNo.setText(String.valueOf(reg_no));
            ((ViewHolder) holder).value_house_hold.setText(String.valueOf(house_hold));
            ((ViewHolder) holder).value_respondent_name.setText(String.valueOf(respondent_name));
            ((ViewHolder) holder).value_village_name.setText(String.valueOf(village_name));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView value_regNo;
        TextView value_house_hold;
        TextView value_respondent_name;
        TextView value_village_name;
        EditText comment;
        LinearLayout status_list;

        ViewHolder(View itemView) {
            super(itemView);
            value_regNo = itemView.findViewById(R.id.value_regNo);
            value_house_hold = itemView.findViewById(R.id.value_house_hold);
            value_respondent_name = itemView.findViewById(R.id.value_respondent_name);
            value_village_name = itemView.findViewById(R.id.value_village_name);
            comment = itemView.findViewById(R.id.comment);
            status_list = itemView.findViewById(R.id.status_list);



        }
    }


}

