package com.zoom.loancalc;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class Loan {

    public static final int ANNUITY = 0;

    public static final int GRADED = 1;


    public static final boolean FIRST_PAYMENT_PLAIN = false;

    public static final boolean FIRST_PAYMENT_INTEREST_ONLY = true;


    @SerializedName("payments")
    private List<Payment> payments;

    // loan params
    // required

    @SerializedName("amount")
    private double amount;

    @SerializedName("number")
    private String number;

    @SerializedName("rate")
    private double rate;

    @SerializedName("term")
    private int term;

    @SerializedName("type")
    private int type;

    @SerializedName("start_date")
    private Date startDate;
    /**

     */
    @SerializedName("is_first_payment_interest_only")
    private boolean isFirstPaymentInterestOnly;

    /**
     * После досрочного погашения, частично включающего погашение основного долга, в очередной платеж будут списаны
     * только проценты
     */
    @SerializedName("process_extras_by_balance_like_sberbank")
    private boolean processExtrasByBalanceLikeSberbank;

    @SerializedName("apply_extras_immediately")
    private boolean applyExtrasImmediately;

    @SerializedName("ignore_passed_periods_after_rate_change")
    private boolean ignorePassedPeriodsAfterRateChange;

    @SerializedName("issue_date")
    private Date issueDate;

    @SerializedName("is_raiffeisen")
    private boolean isRaiffeisen;

    @SerializedName("extras")
    private SortedSet<Extra> extras;

// для прогноза - число дней ежемес платеж дата начала прогноза
    private double forecastMonthlyPayment = 0;
    private int daysBeforePayment;
    private Date dateOfStartCalcExtras;
    //тип из
    private int forecastExtraType;

    public Date getDateOfStartCalcExtras() {
        return dateOfStartCalcExtras;
    }

    public int getDaysBeforePayment() {
        return daysBeforePayment;
    }

    public double getForecastMonthlyPayment() {
        return forecastMonthlyPayment;
    }

    public int getForecastExtraType() {
        return forecastExtraType;
    }

    public void setForecastMonthlyPayment(double forecastMonthlyPayment) {
        this.forecastMonthlyPayment = forecastMonthlyPayment;
    }
    public void setDateOfStartCalcExtras(Date dateOfStartCalcExtras) {
        this.dateOfStartCalcExtras = dateOfStartCalcExtras;
    }

    public void setDaysBeforePayment(int daysBeforePayment) {
        this.daysBeforePayment = daysBeforePayment;
    }

    public void setForecastExtraType(int forecastExtraType) {
        this.forecastExtraType = forecastExtraType;
    }



    public Loan(Date issueDate, Date startDate,
                boolean isRaiffeisen, boolean isFirstPaymentInterestOnly, boolean applyExtrasImmediately,
                boolean processExtrasByBalanceLikeSberbank, boolean ignorePassedPeriodsAfterRateChange,
                int term, double rate, double amount, int type,
                SortedSet<Extra> extras) {

        this.issueDate = issueDate;
        this.startDate = startDate;
        this.isFirstPaymentInterestOnly = isFirstPaymentInterestOnly;
        this.applyExtrasImmediately = applyExtrasImmediately;
        this.processExtrasByBalanceLikeSberbank = processExtrasByBalanceLikeSberbank;
        this.ignorePassedPeriodsAfterRateChange = ignorePassedPeriodsAfterRateChange;
        this.term = term;
        this.rate = rate;
        this.amount = amount;
        this.type = type;
        this.isRaiffeisen = isRaiffeisen;
        //this.extras = extras;

        LoanCalendar temp_cal = new LoanCalendar();
        if (!this.isFirstPaymentInterestOnly()) {

            this.setIssueDate(temp_cal.monthBeforeDate(this.getStartDate()));
        }
        else
        {
            this.setIssueDate(issueDate);
        }

        this.extras = deleteNotValidExtra(extras);
    }

    /**
     *
     */
    public Loan() {

    }

    public SortedSet<Extra> deleteNotValidExtra(SortedSet<Extra> extras) {

        SortedSet<Extra> validExtra = new TreeSet<Extra>(new Comparator<Extra>() {
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
        });

        Iterator<Extra> itr = extras.iterator();
        while (itr.hasNext()) {
            Extra e = itr.next();
            /// удаляем если досрочка меньше даты выдачи
            if (this.getIssueDate() != null) {
                if (e.getDate().before(this.getIssueDate())) {
                    System.out.println("Deleted nonvalid payment");
                    continue;
                }
            }

            if (this.getIssueDate() == null) {
                LoanCalendar cal = new LoanCalendar();
                if (e.getDate().before(cal.addMonthsToDate(this.getStartDate(), -1))) {
                    System.out.println("Удалили невалидный платеж" + e.getDate());
                    continue;
                }
            }
            validExtra.add(e);
        }

        return validExtra;
    }

    /**
     *
     *
     * @return
     */
    public List<Payment> getPayments() {

        return payments;
    }

    /**
     *
     *
     * @param payments
     */
    public void setPayments(List<Payment> payments) {

        this.payments = payments;
    }

    /**
     *
     *
     * @return
     */
    public double getAmount() {

        return amount;
    }

    /**
     *
     *
     * @param amount
     */
    public void setAmount(double amount) {

        this.amount = amount;
    }

    /**
     *
     *
     * @return
     */
    public String getNumber() {

        return number;
    }

    /**
     *
     *
     * @param number
     */
    public void setNumber(String number) {

        this.number = number;
    }


    /**
     *
     *
     * @return
     */
    public double getRate() {

        return rate;
    }

    /**
     *
     *
     * @param rate
     */
    public void setRate(double rate) {

        this.rate = rate;
    }

    /**
     *
     *
     * @return
     */
    public int getTerm() {

        return term;
    }

    /**
     *
     *
     * @param term
     */
    public void setTerm(int term) {

        this.term = term;
    }

    /**
     *
     *
     * @return
     */
    public int getType() {

        return type;
    }

    /**
     * РЈСЃС‚Р°РЅР°РІР»РёРІР°РµС‚ С‚РёРї РєСЂРµРґРёС‚Р°
     *
     * @param type
     *         {@link #ANNUITY} РёР»Рё {@link #GRADED}
     */
    public void setType(int type) {

        this.type = type;
    }

    /**
     *
     *
     * @return
     */
    public Date getStartDate() {

        return startDate;
    }

    /**
     *
     *
     * @param startDate
     */
    public void setStartDate(Date startDate) {

        this.startDate = startDate;
    }

    /**
     *
     *
     * @return
     */
    public boolean isFirstPaymentInterestOnly() {

        return isFirstPaymentInterestOnly;
    }

    /**
     * Р
     *
     * @param isFirstPaymentInterestOnly
     *         {@link #FIRST_PAYMENT_INTEREST_ONLY} РёР»Рё {@link #FIRST_PAYMENT_PLAIN}
     */
    public void setFirstPaymentInterestOnly(boolean isFirstPaymentInterestOnly) {

        this.isFirstPaymentInterestOnly = isFirstPaymentInterestOnly;
    }

    /**
   *
     * @return
     */
    public Date getIssueDate() {

        return issueDate;
    }

    /**
     *
     *
     * @param issueDate
     */
    public void setIssueDate(Date issueDate) {

        this.issueDate = issueDate;
    }

    /**
     *
     * @return
     */
    public SortedSet<Extra> getExtras() {

        return extras;
    }

    /**
     *
     *
     * @param extras
     */
    public void setExtras(SortedSet<Extra> extras) {

        this.extras = extras;
    }

    public boolean isApplyExtrasImmediately() {

        return applyExtrasImmediately;
    }

    public void setApplyExtrasImmediately(boolean applyExtrasImmediately) {

        this.applyExtrasImmediately = applyExtrasImmediately;
    }

    public boolean isProcessExtrasByBalanceLikeSberbank() {

        return processExtrasByBalanceLikeSberbank;
    }

    public void setProcessExtrasByBalanceLikeSberbank(boolean processExtrasByBalanceLikeSberbank) {

        this.processExtrasByBalanceLikeSberbank = processExtrasByBalanceLikeSberbank;
    }

    public boolean isIgnorePassedPeriodsAfterRateChange() {

        return ignorePassedPeriodsAfterRateChange;
    }

    public void setIgnorePassedPeriodsAfterRateChange(boolean ignorePassedPeriodsAfterRateChange) {

        this.ignorePassedPeriodsAfterRateChange = ignorePassedPeriodsAfterRateChange;
    }

    public boolean isRaiffeisen() {

        return isRaiffeisen;
    }

    public void setIsRaiffeisen(boolean isRaiffeisen) {

        this.isRaiffeisen = isRaiffeisen;
    }
}
