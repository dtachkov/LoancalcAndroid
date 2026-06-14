package com.zoom.loancalc.calculation;

import com.zoom.loancalc.LoanCalendar;


/**
 * Стратегия расчета для Аннуитентного кредита
 *
 * @author Dmitry Bespalov dmitry@zoomlabs.co
 */
public final class AnnuityStrategy extends BaseCalculationStrategy {

    public AnnuityStrategy(LoanCalendar calendar) {

        super(calendar);
    }

    @Override
    public void refreshStateStaticPart(State state) {

       // System.out.println("state old annuity =" + state.annuity);
       //System.out.println("Current Balance =" + state.balance);
        double m = Math.pow(1.0 + state.ratePerMonth,  state.term - state.index);
        if(m - 1 > 0) {
            state.total = state.ratePerMonth * state.balance * m / (m - 1);
           // System.out.println("calculated index  " + state.index + " m = " + m);
            double total_local = state.annuity * (state.balance / state.oldbalance);
            //если новый платеж больше чем старый...
            if ((state.annuity > 0.0) && (state.is_rateChanged == false) && (Double.compare(state.total, state.annuity) > 0)) {
                state.total = total_local;
            }
        }
        //если вдруг знаменатель меньше или равен нулю
        if (m - 1 <= 0)
        { //если вдруг коэффициент получился не тот, просто считаем как надо, при условии что не было изменения процентной ставки и старый баланс больше 0
            if((state.annuity > 0.0) && (!state.is_rateChanged) && (state.oldbalance > 0.0))
            {
                state.total = state.annuity * (state.balance/state.oldbalance);
            } //если вдруг было изменение процентной ставки, то ничего не считаем
            else {
                state.total = 0;
            }
        }

       // System.out.println("state balance =" + state.balance);
        //System.out.println("state oldBalance =" + state.oldbalance);

        //System.out.println("new annuity =" + state.total );

        state.annuity =  state.total;
    }

    @Override
    public double termFromState(State state) {

        double logX = Math.log(state.total / (state.total - state.ratePerMonth * state.balance));
        double logY = Math.log(1 + state.ratePerMonth);
        return logX / logY;
    }

    @Override
    public void refreshStateDynamicPart(State state) {

        state.interest = calculateInterest(state);

        if (state.total >= state.balance) {
            // последний платеж, включающий погашение процентов и остатка долга
            state.total = state.balance + state.interest;
        }

        state.principal = state.total - state.interest;

        //задаем текущий анунитетный платеж

    }

    @Override
    public double calculateInterest(State state) {

        double daysProportion = cal.daysProportionInYearBetweenDates(
                state.startDate, state.endDate,
                LoanCalendar.DO_NOT_IGNORE_NEW_YEAR);

        double interestForStatePeriod = state.balance * daysProportion * state.rate / 100.0;

        return interestForStatePeriod + state.notCoveredInterest;
    }
}
