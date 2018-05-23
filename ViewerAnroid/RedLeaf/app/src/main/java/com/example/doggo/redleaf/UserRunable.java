package com.example.doggo.redleaf;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.widget.ImageView;

public class UserRunable implements Runnable {

    private Context context;
    private Activity act;

    private Handler handler;

    public UserRunable() {
        this.context = context;



    }

    @Override
    public void run() {

    }

    public void updateGUI(int code, ImageView iv, Bitmap bm)
    {
        Message msg = new Message();
        msg.what = code;

        //create bundle
        Bundle b = new Bundle(4);
        b.putParcelable("bitmap", bm);

        //get
        // Bitmap bitmapimage = getIntent().getExtras().getParcelable("BitmapImage");

        handler.sendEmptyMessage(code);
        handler.sendMessage(msg);
    }
}
