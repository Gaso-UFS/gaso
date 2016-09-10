package com.ericmguimaraes.gaso.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

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

}