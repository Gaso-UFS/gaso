package com.ericmguimaraes.gaso.util;

import net.sourceforge.jFuzzyLogic.FIS;

/**
 * Created by adrianodias on 3/19/17.
 */
public class FuzzyManager {
    private static FuzzyManager ourInstance = new FuzzyManager();

    private final String filename = "gaso.fcl";
    private FIS fis;

    public static FuzzyManager getInstance() {
        return ourInstance;
    }

    private FuzzyManager() {
    }

}
