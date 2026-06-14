package com.rustamg.calc.loan;

import com.google.gson.annotations.SerializedName;
import com.zoom.loancalc.Extra;
import com.zoom.loancalc.Loan;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class WebLoanModel {
//    $jsonDATA='{"rate":"9.75","sum" :"3521000","term":"37","paymentType":"0","firstpaymentdate" :"02.10.2019",
//    "issuedate":"10.09.2019","flagpercent":"1","lastdayFlag" :"0",
//    "extradayinmonth":"0","interest_only_after_principal_paid_by_extra":"false",
//    "applyExtrasImmediately":"true","ignorePassedPeriodsAfterRateChange":
//    "false","isCalcAnnuityByOldRest":"false","flagdayoff" :
//    "0","extra":[{"extradate":"30.11.2019","extratype":"1","extraamount":"40000"},{"extradate":"20.10.2019","extratype":"2","extraamount":"86000"},{"extradate":"19.09.2019","extratype":"2","extraamount":"300000"},{"extradate":"22.09.2019","extratype":"2","extraamount":"30000"}]}';
//

    private Loan mLoan;
    private class ExtraOne{

        @SerializedName("extradate")
        public  String extradate;
        @SerializedName("extratype")
        public int extratype;
        @SerializedName("extraamount")
        public double extravalue;
        public Date getExtradate()
        {
            Date idate = new Date();
            try {
                idate = new SimpleDateFormat("dd.MM.yyyy").parse(this.extradate);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return  idate;
        }


    }

    @SerializedName("extra")
    private ExtraOne[] extras;

    public SortedSet<Extra> getExtras()
    {
        SortedSet<Extra> local_extras = new TreeSet<Extra>(new Comparator<Extra>() {
            @Override
            public int compare(Extra o1, Extra o2) {
                if (o1.getDate().equals(o2.getDate())
                        && o1.getValue() == o2.getValue()
                        && o1.getType() == o2.getType()) {
                    return 0;
                } else if (o1.getDate().before(o2.getDate())) {
                    return -1;
                } else {
                    return 1;
                }

            }
        }
            );

        //здесь идет разница типов для веб и мобильного калькулятора, поэтому пересчет
        for (ExtraOne extraOne: this.extras)  {
            int extratype = extraOne.extratype;
            switch (extraOne.extratype){
                case 2:{
                    extratype = 3;
                    break;
                }
                case 3:{
                    extratype = 2;
                    break;
                }
                default:
                    break;
            }

            System.out.println(Double.valueOf(extraOne.extravalue));
            boolean result = local_extras.add(
                    new Extra( Double.valueOf(extraOne.extravalue),
                    extratype,
                    extraOne.getExtradate()));

            if(result == false)
            {
                result = result;
            }
        }

        return local_extras;
    }

    @SerializedName("rate")
    private float rate;

    @SerializedName("sum")
    private float sum;

    public float getSum() {
        return this.sum;
    }

    @SerializedName("term")
    private int term;

    public int getTerm() {
        return this.term;
    }

    public float getRate() {
        System.out.println();
        return this.rate;

    }
    /*firstpaymentdate
    public WebLoanModel(Loan loan, boolean considerDaysOff, boolean payOnLastDayOfMonth) {

        this.mLoan = new Loan(this.getIssuedate(), this.getFirstpaymentdate(),
                this.getExtradayinmonth(), this.getFlagpercent(), this.isApplyExtrasImmediately(),
                this.isInterest_only_after_principal_paid_by_extra(),this.isIgnorePassedPeriodsAfterRateChange(),
                this.getTerm(), this.getRate(), this.getSum(),this.getPaymentType(),
                this.extras);

    }

*/


    @SerializedName("paymentType")
    private int paymentType;

    public int getPaymentType() {
        return this.paymentType;
    }

    @SerializedName("firstpaymentdate")
    private String firstpaymentdate;

    public Date getFirstpaymentdate() {

        Date idate = new Date();
        try {
            idate = new SimpleDateFormat("dd.MM.yyyy").parse(this.firstpaymentdate);
            System.out.println("firstpayment = "+idate.toString());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return  idate;

    }

    @SerializedName("issuedate")
    private String issuedate;

    public Date getIssuedate() {
        Date idate = new Date();
        try {
            idate = new SimpleDateFormat("dd.MM.yyyy").parse(this.issuedate);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return  idate;

    }

    @SerializedName("flagpercent")
    private int flagpercent;

    public boolean getFlagpercent()
    {
        return (this.flagpercent == 0)?false:true;
    }


    @SerializedName("lastdayFlag")
    private int lastdayFlag;

    @SerializedName("extradayinmonth")
    private int extradayinmonth;

    @SerializedName("interest_only_after_principal_paid_by_extra")
    private boolean interest_only_after_principal_paid_by_extra;

    public boolean isInterest_only_after_principal_paid_by_extra()
    {
        return this.interest_only_after_principal_paid_by_extra;
    }


    @SerializedName("applyExtrasImmediately")
    private boolean  applyExtrasImmediately;

    public boolean isApplyExtrasImmediately() {
        return this.applyExtrasImmediately;
    }

    @SerializedName("ignorePassedPeriodsAfterRateChange")
    private boolean  ignorePassedPeriodsAfterRateChange;

    public boolean isIgnorePassedPeriodsAfterRateChange() {
        return this.ignorePassedPeriodsAfterRateChange;
    }

    @SerializedName("isCalcAnnuityByOldRest")
    private boolean  isCalcAnnuityByOldRest;

    @SerializedName("flagdayoff")
    private int flagdayoff;



    public boolean getLastdayFlag() {

        return (this.lastdayFlag == 0)?false:true;
    }

    public boolean getFlagdayoff()
    {
        return (this.flagdayoff == 0)?false:true;
    }

    public boolean getExtradayinmonth()
    {
        return (this.extradayinmonth == 0)?false:true;
    }

    public int getExtradayinmonthInt()
    {
        return this.extradayinmonth;
    }

    public Loan getLoan()
    {
        //возвращаем кредит для расчета
        return this.mLoan;
    }



}




