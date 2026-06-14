package com.zoom.loancalc.calculation;

import com.zoom.loancalc.LoanCalendar;


/**
 * Стратегия расчета для дифференцированного кредита
 *
 * @author Dmitry Bespalov dmitry@zoomlabs.co
 */
public final class GradedStrategy extends BaseCalculationStrategy {

    public GradedStrategy(LoanCalendar calendar) {

        super(calendar);
    }

    @Override
    public void refreshStateStaticPart(State state) {

        state.principal = state.balance / (state.term - state.index);
    }

    @Override
    public double termFromState(State state) {

        return state.balance / state.principal;
    }

    @Override
    public void refreshStateDynamicPart(State state) {

        if (state.principal >= state.balance) {
            state.principal = state.balance;
        }

        state.interest = calculateInterest(state);

        state.total = state.principal + state.interest;
    }

    @Override
    public double calculateInterest(State state) {

        double daysProportion = cal.daysProportionInYearBetweenDates(
                state.startDate, state.endDate,
                LoanCalendar.DO_NOT_IGNORE_NEW_YEAR);
        return state.balance * daysProportion * state.rate / 100.0;
    }

    @Override
    public double daysProportion(State state) {

        return cal.daysProportionInYearBetweenDates(state.startDate,
                state.endDate, LoanCalendar.IGNORE_NEW_YEAR);
    }
}
