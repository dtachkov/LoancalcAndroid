package com.zoom.loancalc;

import java.util.ArrayList;

public class ExtraForecastException extends Exception {

     ArrayList<String> numbers_list;

    public ArrayList<String> getNumbers() {
        return numbers_list;
    }

    public ExtraForecastException() {

    }

    public ExtraForecastException(String detailMessage) {

        super(detailMessage);
    }


    public ExtraForecastException(String message_code, double... numbers) {

        super(message_code);
        numbers_list = new ArrayList<String>();
        for (Double number: numbers
             ) {
                this.numbers_list.add(String.valueOf(number));
        }
    }

    public ExtraForecastException(String detailMessage, Throwable throwable) {

        super(detailMessage, throwable);
    }

    public ExtraForecastException(Throwable throwable) {

        super(throwable);
    }
}