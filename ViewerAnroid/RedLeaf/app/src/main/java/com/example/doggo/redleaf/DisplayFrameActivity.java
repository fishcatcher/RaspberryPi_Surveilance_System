package com.example.doggo.redleaf;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

public class DisplayFrameActivity extends AppCompatActivity implements ConfirmPINFragment.ConfirmPINPositive {

    private static final String TAG = "DisplayFrameActivity";

    private boolean is_click;

    private static int port;
    private int image_view_current_count;
    private int image_view_max_count;


    private static String address;
    private String pin;
    private ReentrantLock lock;

    private File file_path;
    private ImageView display_view;
    private Bitmap bitmap;
    private ProgressDialog waiting_progress;
    private Context context;
    private Thread receive_data_thread;
    private ImageView iv[];

    //constant variables
    private static final int HEIGHT_PROPORTION = 12;
    private static final int READ_ALL_FROM_FILE = 3;

    private static final int INITIAL_CONNECTION = 0;
    private static final int SEND_PACKETS = 1;
    private static final int CLOSE_GARAGE = 3;

    //handler to update main GUI
    private Handler update_frame_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    display_view.setImageBitmap(bitmap);
                    break;
                case 1:
                    waiting_progress.show();
                    waiting_progress.cancel();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //


        //get information from last activity
        Bundle foreignData = getIntent().getExtras();
        if (foreignData != null) {
            if (getIntent().hasExtra("server_address"))
                address = foreignData.getString("server_address");
            if (getIntent().hasExtra("server_port"))
                port = foreignData.getInt("server_port");
            file_path = (File) foreignData.get("file_path");
        }

        //get context
        context = DisplayFrameActivity.this;


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
        //create array of imageView



        //create image view
        RelativeLayout.LayoutParams imageview_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        imageview_params.setMargins(dp2px(0), dp2px(0), dp2px(0), dp2px(0));

        display_view = new ImageView(context);
        display_view.setId(R.id.frame_imageview_id);
        display_view.setLayoutParams(imageview_params);
        display_view.setImageResource(R.drawable.error_loading_image);
        display_view.setBackgroundColor(Color.GRAY);

        //create button(s)
        int button_width;
        int button_height;
        //create operate button

        RelativeLayout.LayoutParams button_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (screen_width >= screen_height) {
            //landscape
            button_width = sub_layout_bot_width / 2;
            button_params.setMargins(sub_layout_bot_width/7, sub_layout_bot_height/4, dp2px(8), dp2px(8));

            //button_params.addRule(RelativeLayout.CENTER_VERTICAL);
        } else {
            //portrait
            button_width = sub_layout_bot_width / 3;
            button_params.setMargins(dp2px(8), dp2px(8), dp2px(8), dp2px(8));
            button_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }
        Button operate_button = new Button(context);
        operate_button.setId(R.id.operate_button_id);
        operate_button.setText(R.string.operate_string);
        operate_button.setWidth(button_width);
        //operate_button.setHeight(screen_height / HEIGHT_PROPORTION);
        operate_button.getBackground().setColorFilter(Color.rgb(124,252,0), PorterDuff.Mode.MULTIPLY);
        operate_button.setLayoutParams(button_params);
        operate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_operate_button_click(context, v);
            }
        });


        //create setting button button
        button_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (screen_width >= screen_height) {
            //landscape
            button_params.setMargins(sub_layout_bot_width/7, dp2px(8), dp2px(8), dp2px(8));
            //button_params.addRule(RelativeLayout.CENTER_VERTICAL);
        } else {
            //portrait
            button_params.setMargins(dp2px(8), dp2px(8), dp2px(8), dp2px(8));
            button_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        }
        button_params.addRule(RelativeLayout.BELOW, operate_button.getId());

        Button setting_button = new Button(context);
        setting_button.setId(R.id.setting_button_id);
        setting_button.setText(R.string.setting_string);
        setting_button.setWidth(button_width);
        //setting_button.setHeight(screen_height / HEIGHT_PROPORTION);
        setting_button.setLayoutParams(button_params);
        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_setting_button_click(context, v);
            }
        });

        //set progress dialog
        waiting_progress = new ProgressDialog(this);
        waiting_progress.setMessage(getString(R.string.connecting_string));
        waiting_progress.setCancelable(false);
        waiting_progress.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel_confirm_PIN_string), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                waiting_progress.cancel();
            }
        });
        waiting_progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                final AlertDialog.Builder simpleDialog = new AlertDialog.Builder(DisplayFrameActivity.this);
                simpleDialog.create();
                simpleDialog.setMessage(getString(R.string.failed_to_connect_string));
                simpleDialog.setPositiveButton(getString(R.string.ok_string), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switch_home_activity();
                    }
                });
                simpleDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        switch_home_activity();
                    }
                });
                simpleDialog.show();
            }
        });

        //set lock
        lock = new ReentrantLock();

        //add view(s) to sub layout(s)
        sub_layout_top.addView(display_view);

        sub_layout_bot.addView(operate_button);
        sub_layout_bot.addView(setting_button);

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

        //show loading screen
        waiting_progress.show();

        //create new thread
        Runnable running_thread = new Runnable() {
            @Override
            public void run() {
                //create local data
                byte request[];
                byte data[];
                boolean flag = false;
                boolean is_close = false;

                int r = 1, size, bread;
                int holder_width, holder_height;
                //connecting to server
                ClientService service_handler = new ClientService();
                flag = service_handler.ConnectToServer(address, port);

                if (!flag) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            waiting_progress.cancel();
                        }
                    });
                    return;
                }

                //dismiss loading screen
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        waiting_progress.dismiss();
                    }
                });

                //save address and port
                String info[] = new String[READ_ALL_FROM_FILE];
                String write_line = "";
                HandleFileReadWrite handler = new HandleFileReadWrite();
                handler.readFromFile(file_path, info);
                write_line = address + "\n" + port + "\n" + info[2] + "\n";
                handler.writeToFile(file_path, write_line);

                //get image holder size
                holder_height = holder_width = 0;
                do {
                    if (display_view == null)
                        continue;
                    holder_width = display_view.getWidth();
                    holder_height = display_view.getHeight();
                } while (holder_width <= 0 || holder_height <=0);

                //send request to server
                request = new byte[1];
                request[0] = INITIAL_CONNECTION;
                do {
                    flag = service_handler.Send(request, 1);
                    switch (request[0]) {
                        case INITIAL_CONNECTION:
                            data = new byte[16];
                            r = service_handler.Recv(data, 0, 16, false);
                            request[0] = SEND_PACKETS;
                            break;
                        case SEND_PACKETS:
                            data = new byte[4];
                            r = service_handler.Recv(data, 0, 4, true);
                            if (r <= 0) break;
                            size = ByteBuffer.wrap(data, 0, 4).getInt();
                            if (size <=0) break;
                            data = new byte[size];

                            for (int i=0; i<size; i+=bread) {
                                bread = service_handler.Recv(data, i, size-i, false);
                                if (bread <= 0)
                                    break;
                            }

                            //decompress bitmap from data array
                            Bitmap original_bitmap = BitmapFactory.decodeByteArray(data, 0, size);

                            //flip bitmap
                            //Bitmap bInput/*your input bitmap*/, bOutput;
                            Matrix matrix = new Matrix();
                            matrix.preScale(-1.0f, 1.0f);
                            original_bitmap = Bitmap.createBitmap(original_bitmap, 0, 0, original_bitmap.getWidth(), original_bitmap.getHeight(), matrix, true);

                            //resize bitmap to fit holder
                            bitmap = Bitmap.createScaledBitmap(original_bitmap, holder_width, holder_height, true);

                            //update to image view (main thread) from this thread
                            update_frame_handler.sendEmptyMessage(0);

                            lock.lock();
                            try {
                                is_close = is_click;
                                is_click = false;
                            } finally {
                                lock.unlock();
                            }

                            if (is_close)
                                request[0] = CLOSE_GARAGE;
                            break;
                        case CLOSE_GARAGE:

                            request[0] = SEND_PACKETS;
                            break;
                    }

                } while (flag && r>0 && !Thread.currentThread().isInterrupted());
                service_handler.Close();

                if (!flag || r<=0)
                    update_frame_handler.sendEmptyMessage(1);

            }

        };

        receive_data_thread = new Thread(running_thread);
        receive_data_thread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //interupt thread when activity is out of focus
        receive_data_thread.interrupt();
        //receive_data_thread = null;
        if (waiting_progress.isShowing())
            waiting_progress.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void handlePositiveButtonFromConfirmPIN(String typedPin) {
        SimpleEncDec simpleEnc = new SimpleEncDec(5,6);
        typedPin = simpleEnc.simpleEnc(typedPin);

        if (pin.equals(typedPin)) {
            lock.lock();
            try {
                is_click = true;
            } finally {
                lock.unlock();
            }
            displaySimpleDialog(getString(R.string.request_sent_string));
        } else {
            displaySimpleDialog(getString(R.string.incorrect_PIN_string));
        }
    }


    private void handle_operate_button_click(Context context, final View v)
    {
        String info[] = new String[1];
        HandleFileReadWrite handler = new HandleFileReadWrite();
        handler.readFromFile(file_path, info, 3);
        pin = info[0];

        if (pin == null || pin.equals("") || pin.equals("null")) {
            displaySimpleDialog(getString(R.string.unsetting_PIN_string));
            return;
        }

        v.getBackground().setColorFilter(Color.rgb(0, 100, 0), PorterDuff.Mode.MULTIPLY);
        v.setClickable(false);
        DialogFragment newFragment = new ConfirmPINFragment();
        //newFragment.show(getSupportFragmentManager(), "missiles");
        newFragment.show(getFragmentManager(), "");

        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.getBackground().setColorFilter(Color.rgb(124, 252, 0), PorterDuff.Mode.MULTIPLY);
                v.setClickable(true);
            }
        }, 5000);
    }

    private void handle_add_feed_button_click()
    {
    }

    private void handle_setting_button_click(Context context, View v)
    {
        Intent next_activity = new Intent(this, SettingActivity.class);
        //next_activity.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        next_activity.putExtra("file_path", file_path);
        startActivity(next_activity);
    }




    private void switch_home_activity()
    {
        Intent home_activity = new Intent(this, MainActivity.class);
        startActivity(home_activity);
    }

    private void displaySimpleDialog(String msg) {
        final AlertDialog.Builder simpleDialog = new AlertDialog.Builder(this);
        simpleDialog.create();
        simpleDialog.setMessage(msg);
        simpleDialog.setPositiveButton(getString(R.string.ok_string), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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