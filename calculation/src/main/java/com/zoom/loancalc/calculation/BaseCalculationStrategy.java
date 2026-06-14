package com.zoom.loancalc.calculation;

import com.zoom.loancalc.LoanCalendar;

import java.util.Date;

/**
 * Базовый класс стратегии рассчета
 *
 *
 */
public abstract class BaseCalculationStrategy {

    protected final LoanCalendar cal;

    BaseCalculationStrategy(LoanCalendar calendar) {

        cal = calendar;
    }

     public abstract void refreshStateStaticPart(State state);

       public abstract void refreshStateDynamicPart(State state);

    /**
     * Вычислить общий срок кредита по текущему состоянию
     */
    public abstract double termFromState(State state);

    public abstract double calculateInterest(State state);

    /**
     * Вычислить, какую часть от года занимает период
     */
    public double daysProportion(State state) {

        return daysProportion(state.startDate, state.endDate);
    }

    public double daysProportion(Date startDate, Date endDate) {

        return cal.daysProportionInYearBetweenDates(startDate, endDate,
                LoanCalendar.DO_NOT_IGNORE_NEW_YEAR);
    }
}
