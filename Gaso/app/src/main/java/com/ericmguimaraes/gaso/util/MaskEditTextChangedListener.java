/*
 *     Gaso
 *
 *     Copyright (C) 2016  Eric Guimarães
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ericmguimaraes.gaso.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.util.HashSet;
import java.util.Set;

public class MaskEditTextChangedListener implements TextWatcher{

    private static MaskEditTextChangedListener instance;

    private String mMask;
    private EditText mEditText;
    private Set<String> symbolMask = new HashSet<String>();
    private boolean isUpdating;
    private String old = "";

    public MaskEditTextChangedListener(String mask, EditText editText) {
        mMask = mask;
        mEditText = editText;
        initSymbolMask();
    }

    private void initSymbolMask(){
        for (int i=0; i < mMask.length(); i++){
            char ch = mMask.charAt(i);
            if (ch != '#')
                symbolMask.add(String.valueOf(ch));
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String str = Utils.unmask(s.toString(), symbolMask);
        String mascara = "";

        if (isUpdating) {
            old = str;
            isUpdating = false;
            return;
        }

        if(str.length() > old.length())
            mascara = Utils.mask(mMask,str);
        else
            mascara = s.toString();

        isUpdating = true;

        mEditText.setText(mascara);
        mEditText.setSelection(mascara.length());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    @Override
    public void afterTextChanged(Editable s) {

    }
}