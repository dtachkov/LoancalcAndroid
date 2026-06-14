package com.zoom.loancalc;

import com.google.gson.annotations.SerializedName;

import java.util.Date;


public  class Extra implements  Comparable<Extra>{


    public static final int BALANCE = 1;

    public static final int RATE = 2;

    public static final int TERM = 3;

    public static final int INSURANCE = 4;

    public static final int FEE = 5;
    //там еще есть ежемесячные типы поэтому с запасом 8
    public static final int DATE = 8;

    @SerializedName("number")
    private String number;


    @SerializedName("value")
    private double value;

    @SerializedName("type")
    private int type;

    @SerializedName("date")
    private Date date;

    // this is calculated and tied to the loan

    @SerializedName("savings")
    private double savings;


    public Extra() {

    }


    public Extra(double value, int type, Date date) {

        this.value = value;
        this.type = type;
        this.date = date;
    }


    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + type;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Extra other = (Extra) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        }
        else if (!date.equals(other.date))
            return false;
        if (type != other.type)
            return false;
        if ((type != RATE && value - other.value >= Payment.EPS)
            || (type == RATE && value - other.value >= Payment.P_EPS))
            return false;
        return true;
    }


    public int compareTo(Extra o) {

        int result;
        if ((result = date.compareTo(o.date)) == 0) {
            result = type - o.type;
        }
        return result;
    }

    public String getNumber() {

        return number;
    }


    public void setNumber(String number) {

        this.number = number;
    }


    public double getValue() {

        return value;
    }


    public void setValue(double value) {

        this.value = value;
    }


    public int getType() {

        return type;
    }


    public void setType(int type) {

        this.type = type;
    }


    public Date getDate() {

        return date;
    }


    public void setDate(Date date) {

        this.date = date;
    }


    public double getSavings() {

        return savings;
    }


    public void setSavings(double savings) {

        this.savings = savings;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("type: ").append(getTypeString()).append("; ");
        sb.append("date: ").append(getDate()).append("; ");
        sb.append("value: ").append(getValue()).append("; ");

        return sb.toString();
    }

    private String getTypeString() {

        switch (getType()) {

            case FEE: return "[FEE]";
            case INSURANCE: return "[INSURANCE]";
            case TERM: return "[TERM]";
            case RATE: return "[RATE]";
            case BALANCE: return "[BALANCE]";
            case DATE: return "[DATE]";
            default: return "[unknown]";
        }
    }
}
