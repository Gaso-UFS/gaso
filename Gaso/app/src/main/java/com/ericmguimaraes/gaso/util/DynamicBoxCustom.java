package com.ericmguimaraes.gaso.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;

import mehdi.sakout.dynamicbox.DynamicBox;

/**
 * Created by ericmguimaraes on 17/04/17.
 */

public class DynamicBoxCustom extends DynamicBox {

    private Context mContext;

    public DynamicBoxCustom(Context context, View targetView) {
        super(context, targetView);
        this.mContext = context;
    }

    public DynamicBoxCustom(Context context, int viewID) {
        super(context, viewID);
        this.mContext = context;
    }

    public void showEmptyMessage(String msg) {
        View customView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.empty_layout, null, false);
        ((TextView) customView.findViewById(R.id.empty)).setText(msg);
        addCustomView(customView,"message");
        showCustomView("message");
    }

}
