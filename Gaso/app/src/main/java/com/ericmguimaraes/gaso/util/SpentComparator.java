package com.ericmguimaraes.gaso.util;

import com.ericmguimaraes.gaso.model.Spent;

import java.util.Comparator;

/**
 * Created by ericm on 3/27/2016.
 */
public class SpentComparator implements Comparator<Spent> {

    @Override
    public int compare(Spent lhs, Spent rhs) {
        if(lhs.getDate().before(rhs.getDate()))
            return -1;
        else if (lhs.getDate().after(rhs.getDate()))
            return 1;
        else
            return 0;
    }

}
