package com.example.doggo.redleaf;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;

public class SettingActivity extends AppCompatActivity {

    private final int TEXT_MAX_LENGTH = 6;
    private File file_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle foreignData = getIntent().getExtras();
        if (foreignData != null) {
            file_path = (File) foreignData.get("file_path");
        }

        //get this context
        Context context = SettingActivity.this;

        //hide title bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.setting_activity_title_string);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //create main layout
        LinearLayout.LayoutParams main_layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout main_layout = new LinearLayout(context);

        main_layout.setOrientation(LinearLayout.VERTICAL);
        main_layout.setBackgroundColor(Color.GREEN);
        main_layout.setLayoutParams(main_layout_params);

        //create scroll view
        LinearLayout.LayoutParams scrollview_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        scrollview_params.setMargins(dp2px(8), dp2px(8), dp2px(8), dp2px(8));

        ScrollView sub_scroll_view = new ScrollView(context);
        sub_scroll_view.setBackgroundColor(Color.YELLOW);
        sub_scroll_view.setLayoutParams(scrollview_params);

        //create text view
        LinearLayout.LayoutParams setting_textview_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        setting_textview_params.setMargins(dp2px(8), dp2px(8), dp2px(8), dp2px(8));

        TextView setting_textview = new TextView(context);
        setting_textview.setText(R.string.setting_string);
        setting_textview.setTextSize(dp2px(25));
        setting_textview.setGravity(Gravity.CENTER);
        setting_textview.setTextColor(Color.BLACK);

        setting_textview.setLayoutParams(setting_textview_params);


        //create sub layout (layout inside scroll view)
        LinearLayout.LayoutParams sub_layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        sub_layout_params.setMargins(dp2px(8), dp2px(8), dp2px(8), dp2px(8));

        LinearLayout sub_layout = new LinearLayout(context);
        sub_layout.setOrientation(LinearLayout.VERTICAL);

        sub_layout.setBackgroundColor(Color.BLUE);
        sub_layout.setLayoutParams(sub_layout_params);

        //----- begin create view(s) -----
        //create switch view
        LinearLayout.LayoutParams switch_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        switch_params.setMargins(dp2px(8), dp2px(8), dp2px(8), dp2px(8));

        final Switch change_PIN_switch = new Switch(context);
        change_PIN_switch.setTextOn("");
        change_PIN_switch.setTextOff("");
        change_PIN_switch.setText(R.string.change_PIN_string);
        change_PIN_switch.setTextSize(dp2px(12));

        change_PIN_switch.setLayoutParams(switch_params);

        //create current PIN edittext
        LinearLayout.LayoutParams edittext_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        edittext_params.setMargins(dp2px(50), dp2px(0), dp2px(100), dp2px(0));

        final EditText current_PIN_edittext = new EditText(context);
        current_PIN_edittext.setHint(R.string.current_PIN_string);
        current_PIN_edittext.setTextSize(dp2px(7));
        current_PIN_edittext.setSingleLine(true);
        current_PIN_edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(TEXT_MAX_LENGTH)});
        current_PIN_edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        current_PIN_edittext.setLayoutParams(edittext_params);

        //create new PIN edittext
        final EditText new_PIN_edittext = new EditText(context);
        new_PIN_edittext.setHint(R.string.new_PIN_string);
        new_PIN_edittext.setTextSize(dp2px(7));
        new_PIN_edittext.setSingleLine(true);
        new_PIN_edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(TEXT_MAX_LENGTH)});
        new_PIN_edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        new_PIN_edittext.setLayoutParams(edittext_params);

        //create confirm PIN edittext
        final EditText confirm_new_PIN_edittext = new EditText(context);
        confirm_new_PIN_edittext.setHint(R.string.confirm_new_PIN_string);
        confirm_new_PIN_edittext.setTextSize(dp2px(7));
        confirm_new_PIN_edittext.setSingleLine(true);
        confirm_new_PIN_edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(TEXT_MAX_LENGTH)});
        confirm_new_PIN_edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        confirm_new_PIN_edittext.setLayoutParams(edittext_params);

        //create save button
        LinearLayout.LayoutParams button_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        button_params.setMargins(dp2px(100), dp2px(8), dp2px(100), dp2px(8));

        final Button save_button = new Button(context);
        save_button.setText(R.string.save_PIN_string);
        save_button.setTextSize(dp2px(8));

        save_button.setLayoutParams(button_params);

        //hide edittext(s) in the begining
        current_PIN_edittext.setVisibility(EditText.GONE);
        new_PIN_edittext.setVisibility(EditText.GONE);
        confirm_new_PIN_edittext.setVisibility(EditText.GONE);
        save_button.setVisibility(Button.GONE);

        change_PIN_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    current_PIN_edittext.setVisibility(EditText.VISIBLE);
                    new_PIN_edittext.setVisibility(EditText.VISIBLE);
                    confirm_new_PIN_edittext.setVisibility(EditText.VISIBLE);
                    save_button.setVisibility(Button.VISIBLE);
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                    current_PIN_edittext.setVisibility(EditText.GONE);
                    new_PIN_edittext.setVisibility(EditText.GONE);
                    confirm_new_PIN_edittext.setVisibility(EditText.GONE);
                    save_button.setVisibility(Button.GONE);
                }
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open file to get pin
                HandleFileReadWrite handler = new HandleFileReadWrite();
                String info[] = new String[3];
                handler.readFromFile(file_path, info);

                SimpleEncDec enc = new SimpleEncDec(5, 6);

                //set pin
                String realPIN = info[2];
                String currentPIN = current_PIN_edittext.getText().toString();
                String newPIN = new_PIN_edittext.getText().toString();
                String confirmPIN = confirm_new_PIN_edittext.getText().toString();
                String writeLine = info[0] + "\n" + info[1] + "\n";

                if (newPIN.length() == TEXT_MAX_LENGTH && confirmPIN.length() == TEXT_MAX_LENGTH) {
                    if (newPIN.equals(confirmPIN)) {
                        if ((currentPIN.length() == TEXT_MAX_LENGTH && enc.simpleEnc(currentPIN).equals(realPIN))
                                || (currentPIN.length() == 0 && realPIN.equals("null"))) {
                            writeLine = writeLine + enc.simpleEnc(newPIN) + "\n";
                            handler.writeToFile(file_path, writeLine);
                            displaySimpleDialog(getString(R.string.PIN_saved_string));

                            current_PIN_edittext.setText("");
                            new_PIN_edittext.setText("");
                            confirm_new_PIN_edittext.setText("");
                            change_PIN_switch.performClick();

                        } else {
                            //current PIN error
                            displaySimpleDialog(getString(R.string.PIN_not_match));
                        }
                    } else {
                        //newPIN and confirmPIN do not match error
                        displaySimpleDialog(getString(R.string.PINs_not_match));
                    }
                } else {
                    //length error
                    displaySimpleDialog(getString(R.string.require_length_string) + " " + TEXT_MAX_LENGTH + "!");
                }
            }

        });

        //-----end create view(s) -----

        //add view(s) to sub layout
        sub_layout.addView(change_PIN_switch);
        sub_layout.addView(current_PIN_edittext);
        sub_layout.addView(new_PIN_edittext);
        sub_layout.addView(confirm_new_PIN_edittext);
        sub_layout.addView(save_button);


        //add sub layout to scrollview
        sub_scroll_view.addView(sub_layout);

        //add scrollview to main layout
        main_layout.addView(setting_textview);
        main_layout.addView(sub_scroll_view);

        //hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        //display main layout
        setContentView(main_layout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent next_activity = new Intent(this, DisplayFrameActivity.class);
        next_activity.putExtra("file_path", file_path);
        startActivity(next_activity);
        return true;
    }


    private void displaySimpleDialog(String msg) {
        final AlertDialog.Builder simpleDialog = new AlertDialog.Builder(this);
        simpleDialog.create();
        simpleDialog.setMessage(msg);
        simpleDialog.setPositiveButton(getString(R.string.ok_string), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Do nothing
            }
        });
        simpleDialog.show();
    }

    private int dp2px(int dp) {
        int px;
        Resources r = getResources();
        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }
}
