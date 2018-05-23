package com.example.doggo.redleaf;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final String filename = "common.ini";

    private Button connect_button;
    private TextView address_textview;
    private TextView port_textview;
    private EditText address_edittext;
    private EditText port_edittext;


    private final int TEXT_SIZE = 20;
    private final int HEIGHT_PROPORTION = 12;
    private final int BOTTOM_MARGIN = 20;
    private static final int SCREEN_PROPORTION = 7;
    private static final int SCREEN_SLIPT_LINE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        //
        String lang = "vn";
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        //

        //get this context
        final Context context = MainActivity.this;

        //create main layout
        RelativeLayout main_layout = new RelativeLayout(context);

        RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        layout_params.setMargins(dp2px(0), dp2px(0), dp2px(0), dp2px(0));

        main_layout.setBackgroundColor(Color.WHITE);
        main_layout.setLayoutParams(layout_params);

        //create sub layouts

        //get actionbar height
        int title_bar_height = 0;
        int status_bar_height = 0;

        int resourceId;

        int screen_width;
        int screen_height;
        int relative_screen_width;
        int relative_screen_height;

        int sub_layout_top_width;
        int sub_layout_top_height;
        int sub_layout_bot_width;
        int sub_layout_bot_height;

        TypedValue tv;

                tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            title_bar_height = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            status_bar_height = getResources().getDimensionPixelSize(resourceId);
        }

        //get screen resolution
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_width = displayMetrics.widthPixels;
        screen_height = displayMetrics.heightPixels;

        relative_screen_width = screen_width;
        relative_screen_height = screen_height - status_bar_height - title_bar_height;
        if (screen_width >= screen_height) {
            //landscape
            sub_layout_top_width = relative_screen_width / 3 * 2;
            sub_layout_top_height = relative_screen_height;
            sub_layout_bot_width = relative_screen_width - sub_layout_top_width;
            sub_layout_bot_height = relative_screen_height;
        } else {
            //portrait
            sub_layout_top_width = relative_screen_width;
            sub_layout_top_height = relative_screen_height / 5 * 3;
            sub_layout_bot_width = relative_screen_width;
            sub_layout_bot_height = relative_screen_height - sub_layout_top_height;
        }

        //create sub layout top
        RelativeLayout sub_layout_top = new RelativeLayout(context);

        RelativeLayout.LayoutParams sub_layout_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        sub_layout_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        sub_layout_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        sub_layout_params.width = sub_layout_top_width;
        sub_layout_params.height = sub_layout_top_height;
        sub_layout_params.setMargins(dp2px(8), dp2px(8), dp2px(8), dp2px(8));

        GradientDrawable border = new GradientDrawable();
        border.setShape(GradientDrawable.RECTANGLE);
        border.setStroke(3, Color.WHITE);
        border.setColorFilter(Color.rgb(100,200,255), PorterDuff.Mode.SRC_ATOP);
        border.setCornerRadius(8);
        border.setColor(Color.BLUE);
        sub_layout_top.setBackgroundDrawable(border);
        sub_layout_top.setId(R.id.sub_layout_top_id);
        //sub_layout_top.setBackgroundColor(Color.rgb(100,200,255));
        sub_layout_top.setLayoutParams(sub_layout_params);

        //create sub layout bot
        RelativeLayout sub_layout_bot = new RelativeLayout(context);

        sub_layout_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (screen_width >= screen_height) {
            //landscape
            sub_layout_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            sub_layout_params.addRule(RelativeLayout.RIGHT_OF, sub_layout_top.getId());
            sub_layout_params.setMargins(dp2px(0), dp2px(8), dp2px(8), dp2px(8));

        } else {
            //portrait
            sub_layout_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            sub_layout_params.addRule(RelativeLayout.BELOW, sub_layout_top.getId());
            sub_layout_params.setMargins(dp2px(8), dp2px(0), dp2px(8), dp2px(8));
        }
        sub_layout_params.width = sub_layout_bot_width;
        sub_layout_params.height = sub_layout_bot_height;


        sub_layout_bot.setBackgroundColor(Color.rgb(255,200,100));
        sub_layout_bot.setLayoutParams(sub_layout_params);

        //----- begein creating view(s) -----


        //create address text view
        RelativeLayout.LayoutParams text_layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        text_layout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        text_layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        text_layout.setMargins(dp2px(8), sub_layout_top_height / 5, dp2px(8), dp2px(8));

        address_textview = new TextView(context);
        address_textview.setId(R.id.address_textview_id);
        address_textview.setText(R.string.address_string);
        address_textview.setTextSize(TEXT_SIZE);
        address_textview.setWidth(sub_layout_top_width / 3);
        address_textview.setHeight(sub_layout_top_height / 6);
        address_textview.setGravity(Gravity.CENTER_VERTICAL);
        //address_textview.setBackgroundColor(Color.GREEN);

        address_textview.setLayoutParams(text_layout);

        //create address edit text
        text_layout = new RelativeLayout.LayoutParams(sub_layout_top_width / 3 * 2, sub_layout_top_height / 6);
        text_layout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        text_layout.addRule(RelativeLayout.RIGHT_OF, address_textview.getId());
        text_layout.setMargins(dp2px(8), sub_layout_top_height / 5, dp2px(30), dp2px(8));

        address_edittext = new EditText(context);
        address_edittext.setId(R.id.address_edittext_id);
        address_edittext.setText(R.string.default_string);
        address_edittext.setTextSize(TEXT_SIZE);
        address_edittext.setWidth(sub_layout_top_width / 3 * 2);
        address_edittext.setHeight(sub_layout_top_height / 6);
        address_edittext.setGravity(Gravity.CENTER_VERTICAL);
        address_edittext.setSingleLine(true);
        //address_edittext.setBackgroundColor(Color.GREEN);

        address_edittext.setLayoutParams(text_layout);

        address_edittext.setOnTouchListener(
                new EditText.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (address_edittext.getText().toString().equals(getString(R.string.default_string)))
                            address_edittext.setText("");
                        return false;
                    }
                }
        );


        //create port text view
        text_layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        text_layout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        text_layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        text_layout.setMargins(dp2px(8), sub_layout_top_height / 5 * 2, dp2px(8), dp2px(8));

        port_textview = new TextView(context);
        port_textview.setId(R.id.port_textview_id);
        port_textview.setText(R.string.port_string);
        port_textview.setTextSize(TEXT_SIZE);
        port_textview.setWidth(sub_layout_top_width / 3);
        port_textview.setHeight(sub_layout_top_height / 6);
        port_textview.setGravity(Gravity.CENTER_VERTICAL);
        //port_textview.setBackgroundColor(Color.GREEN);

        port_textview.setLayoutParams(text_layout);

        //create port edit text
        text_layout = new RelativeLayout.LayoutParams(sub_layout_top_width / 3 * 2, sub_layout_top_height / 6);
        text_layout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        text_layout.addRule(RelativeLayout.RIGHT_OF, port_textview.getId());
        text_layout.setMargins(dp2px(8), sub_layout_top_height / 5 * 2, dp2px(30), dp2px(8));

        port_edittext = new EditText(context);
        port_edittext.setId(R.id.port_edittext_id);
        port_edittext.setText(R.string.default_string);
        port_edittext.setTextSize(TEXT_SIZE);
        port_edittext.setWidth(sub_layout_top_width / 3 * 2);
        port_edittext.setHeight(sub_layout_top_height / 6);
        port_edittext.setGravity(Gravity.CENTER_VERTICAL);
        port_edittext.setSingleLine(true);
        //port_edittext.setBackgroundColor(Color.GREEN)

        port_edittext.setLayoutParams(text_layout);

        port_edittext.setOnTouchListener(
                new EditText.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (port_edittext.getText().toString().equals(getString(R.string.default_string)))
                            port_edittext.setText("");
                        return false;
                    }
                }
        );

        //create button
        int button_width;
        int button_height;
        RelativeLayout.LayoutParams button_layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (screen_width >= screen_height) {
            //landscape
            button_layout.addRule(RelativeLayout.CENTER_VERTICAL);
            button_width = sub_layout_bot_width / 3 * 2;
            button_height = sub_layout_bot_height / 5;
        } else {
            //portrait
            button_layout.addRule(RelativeLayout.CENTER_HORIZONTAL);
            button_width = sub_layout_bot_width / 5 * 2;
            button_height = sub_layout_bot_height / 4;
        }

        connect_button = new Button(context);
        connect_button.setText(R.string.connect_string);
        connect_button.setTextSize(TEXT_SIZE - 5);
        connect_button.setWidth(button_width);
        connect_button.setHeight(button_height);
        connect_button.setAllCaps(false);
        connect_button.setLayoutParams(button_layout);

        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_button_click(context);
            }
        });
        //----- end creating view(s) -----

        //add view(s) to sub layout(s)
        sub_layout_top.addView(address_textview);
        sub_layout_top.addView(address_edittext);
        sub_layout_top.addView(port_textview);
        sub_layout_top.addView(port_edittext);

        sub_layout_bot.addView(connect_button);

        //add view(s) to main layout
        main_layout.addView(sub_layout_top);
        main_layout.addView(sub_layout_bot);

        //hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //display main layout
        setContentView(main_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connect_button.requestFocus();
    }




    private void handle_button_click(Context context)
    {
        File file_path = new File(context.getFilesDir(), filename);
        HandleFileReadWrite handler = new HandleFileReadWrite();
        String info[] = new String[2];
        handler.readFromFile(file_path, info);

        String address = address_edittext.getText().toString();
        String port = port_edittext.getText().toString();
        int nport;

        char addr[] = address.toCharArray();
        if (addr.length > 0 && addr[0] == '@') {
            address = "192.168.0.";
            for (int i=1; i<addr.length; i++) {
                address += addr[i];
            }
        }

        if (address.equals(getString(R.string.default_string))) {
            address = info[0];
        }
        if (port.equals(getString(R.string.default_string))) {
            port = info[1];
        }

        try {
            nport = Integer.parseInt(port);
        } catch(NumberFormatException e) {
            nport = -1;
        }

        //switch activity
        if (address != null && !address.equals("") && nport != -1) {
            address_edittext.setText(R.string.default_string);
            port_edittext.setText(R.string.default_string);


            Intent next_activity = new Intent(context, DisplayFrameActivity.class);
            next_activity.putExtra("server_address", address);
            next_activity.putExtra("server_port", nport);
            next_activity.putExtra("file_path", file_path);
            startActivity(next_activity);
        }

    }

    private int dp2px(int dp) {
        int px;
        Resources r = getResources();
        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }
}
