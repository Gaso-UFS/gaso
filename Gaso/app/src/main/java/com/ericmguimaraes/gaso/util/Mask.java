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
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.EditText;

import java.math.BigDecimal;

public abstract class Mask {

    public static String unmask(String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "")
                .replaceAll("[/]", "").replaceAll("[(]", "")
                .replaceAll("[)]", "").replaceAll("[R$,.]", "")
                .replaceAll("[$]", "");
    }

    /**
     * @param mask i.e: ###-###-##.##
     * @return TextWatcher
     */
    public static TextWatcher mask(final String mask) {

        return new TextWatcher() {

            boolean isUpdating;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isUpdating) {
                    isUpdating = true;
                    StringBuilder stringBuilder = new StringBuilder();
                    String original = s.toString();
                    for (int i = 0, j = 0; i < mask.length(); i++) {
                        if (j >= original.length())
                            break;
                        if (mask.charAt(i) == '#') {
                            stringBuilder.append(original.charAt(j));
                            j++;
                        } else {
                            if (mask.charAt(i) == original.charAt(j))
                                j++;
                            stringBuilder.append(mask.charAt(i));
                        }
                    }
                    s.clear();
                    String result = stringBuilder.toString();
                    InputFilter[] filters = s.getFilters();
                    InputFilter[] newFilters = new InputFilter[filters.length - 1];
                    int i = 0;
                    boolean thereWasDigitFilter = false;
                    for (InputFilter filter : filters) {
                        if (!(filter instanceof DigitsKeyListener)) {
                            if (i >= newFilters.length)
                                break;
                            newFilters[i] = filter;
                            i++;
                        } else {
                            thereWasDigitFilter = true;
                        }
                    }
                    if (thereWasDigitFilter)
                        s.setFilters(newFilters);
                    s.append(result);
                    isUpdating = false;
                }
            }
        };
    }

    public static TextWatcher moneyMask(final EditText editText) {
        return new TextWatcher() {

            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals(current)) {
                    try {

                        editText.removeTextChangedListener(this);

                        String cleanString = s.toString().replaceAll("[R$,.]",
                                "");
                        BigDecimal parsed = new BigDecimal(cleanString);
                        parsed = parsed.divide(new BigDecimal(100));

                        String formated = StringUtils.formatMoney(parsed
                                .toString());

                        current = formated;
                        editText.setText(formated);
                        editText.setSelection(formated.length());

                        editText.addTextChangedListener(this);
                    } catch (Exception e) {
                        Log.e("",e.getMessage(),e);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}
