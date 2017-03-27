package com.ericmguimaraes.gaso.util;

/**
 * Created by adrianodias on 3/19/17.
 */
public class FuzzyManager {
    private static FuzzyManager ourInstance = new FuzzyManager();

    public static FuzzyManager getInstance() {
        return ourInstance;
    }

    private FuzzyManager() {
    }
}
