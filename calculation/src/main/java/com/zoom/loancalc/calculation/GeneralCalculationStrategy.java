package com.zoom.loancalc.calculation;

import com.zoom.loancalc.Extra;
import com.zoom.loancalc.ExtraForecastException;
import com.zoom.loancalc.InfiniteLoanException;
import com.zoom.loancalc.Loan;
import com.zoom.loancalc.LoanCalendar;
import com.zoom.loancalc.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Date;

public abstract class GeneralCalculationStrategy {

    protected BaseCalculationStrategy calculationStrategy;

    protected LoanCalendar cal;

    protected final Loan loan;
    //тут лучше простой список
    protected ArrayList<Extra> extras;

    protected State state;


    protected GeneralCalculationStrategy(Loan loan, LoanCalendar calendar, BaseCalculationStrategy strategy) {

        this.calculationStrategy = strategy;
        this.cal = calendar;

        this.loan = loan;
        this.extras = new ArrayList<>();
        if (loan.getExtras() != null) {
            for (Extra extra : loan.getExtras()) {
                if (extra.getType() != Extra.INSURANCE && extra.getType() != Extra.FEE) {
                    this.extras.add(extra);
                }
            }
        }
    }

    public double daysProportionDays(Date startDate, Date endDate)
    {
        return this.cal.daysProportionInYearBetweenDates(startDate, endDate,
                LoanCalendar.DO_NOT_IGNORE_NEW_YEAR);
    }

    protected void initState() {

        state = new State(this.loan.getAmount(), this.loan.getTerm(), this.loan.getRate(), this.loan.getIssueDate(), this.loan.getStartDate());
    }

    public abstract List<Payment> calculate() throws InfiniteLoanException, ExtraForecastException;
}
