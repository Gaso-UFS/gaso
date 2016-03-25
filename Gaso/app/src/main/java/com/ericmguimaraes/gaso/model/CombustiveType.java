package com.ericmguimaraes.gaso.model;

/**
 * Created by ericm on 3/15/2016.
 */
public enum CombustiveType {

    COMMON_GAS(0), PREMIUM_GAS(1), ALCHOOL(2), DIESEL(3), OTHER(4);

    private int value;

    CombustiveType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CombustiveType fromInteger(int x) {
        switch(x) {
            case 0:
                return COMMON_GAS;
            case 1:
                return PREMIUM_GAS;
            case 2:
                return ALCHOOL;
            case 3:
                return DIESEL;
            case 4:
                return OTHER;
        }
        return null;
    }

}
