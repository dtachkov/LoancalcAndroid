package com.rustamg.calc.loan.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by rustamg on 29/07/15.
 */
public class GsonFactory {

    private static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static Gson create() {

        return new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat(YYYY_MM_DD)
                .create();
    }
}
