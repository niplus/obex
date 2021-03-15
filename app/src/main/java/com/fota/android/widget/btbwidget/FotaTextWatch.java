package com.fota.android.widget.btbwidget;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Dell on 2018/4/20.
 */

public abstract class FotaTextWatch implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String s = editable.toString();
        onTextChanged(s);
    }

    protected abstract void onTextChanged(String s);
}
