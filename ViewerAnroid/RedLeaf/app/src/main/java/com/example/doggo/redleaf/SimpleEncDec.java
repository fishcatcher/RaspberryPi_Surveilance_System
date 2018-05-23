package com.example.doggo.redleaf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SimpleEncDec {

    private int RAW_MAX_LENGTH;
    private int ROTATE_BASE;

    public SimpleEncDec()
    {
        this(5, 4);
    }

    public SimpleEncDec(int base, int mlen)
    {
        ROTATE_BASE = base;
        RAW_MAX_LENGTH = mlen;
    }

    public String simpleEnc(String raw)
    {
        String result = "";
        if (raw.length() != RAW_MAX_LENGTH) {
            return null;
        }
        char rawArray[] = raw.toCharArray();
        int numArray[] = new int[raw.length()];
        int sum = 0;
        int length = raw.length();

        for (int i=0; i<length; i++) {
            try {
                int n = Character.getNumericValue(rawArray[i]);
                numArray[i] = n;
                sum += n;
            } catch (NumberFormatException e) {
                //
            }
        }
        for (int i=0; i<length; i++) {
            int n = numArray[i];
            n = ((n + ROTATE_BASE * (i+1)) + sum )% 10;
            result += n;
        }

        return result;
    }

    public String simpleDec(String enc)
    {
        return null;
    }

}
