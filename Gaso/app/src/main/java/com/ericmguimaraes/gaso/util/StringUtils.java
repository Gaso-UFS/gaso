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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StringUtils {

    public static String formatMoney(String vlr) {

        NumberFormat moneyFormat = NumberFormat.getCurrencyInstance(new Locale(
                "pt", "BR"));
        DecimalFormat formatter = (DecimalFormat) moneyFormat
                .getCurrencyInstance();
        String symbol = formatter.getCurrency().getSymbol();
        formatter.setNegativePrefix("-" + symbol);
        formatter.setNegativeSuffix("");

        BigDecimal result = new BigDecimal(vlr);
        return formatter.format(result);
    }

    public static String formatMoney(double value) {
        return formatMoney(Double.toString(value));
    }
}