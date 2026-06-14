package com.zoom.loancalc;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Payment {


    private int index;

    private Date date;

    private double total;

    private double interest;

    private double principal;

    private double endBalance;


    private double extras;

    private double rateExtra;


    private static DecimalFormat percentFormatter;

    private static DecimalFormat numberFormatter;

    private static DateFormat dateFormat;


    public static final double EPS = 0.009999;


    public static final double P_EPS = 0.0001;

    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');

        percentFormatter = new DecimalFormat("###,###.###%", symbols);
        numberFormatter = new DecimalFormat("###,###.##", symbols);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public Payment() {
        total = 0;
        this.date = new Date();
    }

    public Payment(int index, Date date, double total, double interest, double principal, double endBalance,
            double extras,
            double rateExtra) {

        this.index = index;
        this.date = date;
        this.total = total;
        this.interest = interest;
        this.principal = principal;
        this.endBalance = endBalance;
        this.extras = extras;
        this.rateExtra = rateExtra;
    }



    public String getExtraString() {

        StringBuilder sb = new StringBuilder();

        if (rateExtra > 0) {
            sb.append(percentFormatter.format(rateExtra / 100.0));
            sb.append(", ");
        }

        if (extras > 0) {
            sb.append(numberFormatter.format(extras));
        }

        return sb.toString();
    }



    public void setExtraByString(String extraAmount) throws ParseException {

        if (extraAmount != null && extraAmount.length() > 0) {
            extraAmount = extraAmount.replaceAll("\\s", "");

            if (extraAmount.contains("%")) {
                String[] parts = extraAmount.split("%,");

                if (parts.length > 0) {
                    rateExtra = Double.parseDouble(parts[0]);

                    if (parts.length > 1) {
                        extras = Double.parseDouble(parts[1]);
                    }
                }
            }
            else {
                extras = Double.parseDouble(extraAmount);
            }
        }
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("Payment [\nindex=");
        builder.append(index);
        builder.append("\n");
        if (date != null) {
            builder.append("date=");
            builder.append(dateFormat.format(date));
            builder.append("\n");
        }
        builder.append("\ttotal=");
        builder.append(numberFormatter.format(total));
        builder.append("\n\tprincipal=");
        builder.append(numberFormatter.format(principal));
        builder.append("\n\tinterest=");
        builder.append(numberFormatter.format(interest));
        builder.append("\nendBalance=");
        builder.append(numberFormatter.format(endBalance));
        if (extras > 0 || rateExtra > 0) {
            builder.append("\nextras=");
            builder.append(getExtraString());
        }
        builder.append("\n]");
        return builder.toString();
    }


    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Payment other = (Payment) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        }
        else if (!date.equals(other.date))
            return false;
        if (index != other.index)
            return false;

        if (Math.abs(endBalance - other.endBalance) >= EPS)
            return false;
        if (Math.abs(extras - other.extras) >= EPS)
            return false;
        if (Math.abs(interest - other.interest) >= EPS)
            return false;
        if (Math.abs(principal - other.principal) >= EPS)
            return false;
        if (Math.abs(rateExtra - other.rateExtra) >= P_EPS)
            return false;
        if (Math.abs(total - other.total) >= EPS)
            return false;
        return true;
    }


    public int getIndex() {

        return index;
    }


    public void setIndex(int index) {

        this.index = index;
    }


    public Date getDate() {

        return date;
    }


    public void setDate(Date date) {

        this.date = date;
    }


    public double getTotal() {

        return total;
    }


    public void setTotal(double total) {

        this.total = total;
    }


    public double getInterest() {

        return interest;
    }


    public void setInterest(double interest) {

        this.interest = interest;
    }


    public double getPrincipal() {

        return principal;
    }


    public void setPrincipal(double principal) {

        this.principal = principal;
    }


    public double getEndBalance() {

        return endBalance;
    }


    public void setEndBalance(double endBalance) {

        this.endBalance = endBalance;
    }


    public double getExtras() {

        return extras;
    }


    public void setExtras(double extras) {

        this.extras = extras;
    }


    public double getRateExtra() {

        return rateExtra;
    }


    public void setRateExtra(double rateExtra) {

        this.rateExtra = rateExtra;
    }
}
