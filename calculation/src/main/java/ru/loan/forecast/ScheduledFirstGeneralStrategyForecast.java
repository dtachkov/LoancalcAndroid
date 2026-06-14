package ru.loan.forecast;

import com.zoom.loancalc.Extra;
import com.zoom.loancalc.ExtraForecastException;
import com.zoom.loancalc.Loan;
import com.zoom.loancalc.LoanCalendar;
import com.zoom.loancalc.Payment;
import com.zoom.loancalc.calculation.BaseCalculationStrategy;
import com.zoom.loancalc.calculation.ScheduledFirstGeneralStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

public class ScheduledFirstGeneralStrategyForecast extends ScheduledFirstGeneralStrategy {
    public ScheduledFirstGeneralStrategyForecast(Loan loan, LoanCalendar calendar, BaseCalculationStrategy calculationStrategy) {
        super(loan, calendar, calculationStrategy);
    }


    @Override
    protected void processExtras(ArrayList<Payment> payments, Payment lastPayment) throws ExtraForecastException {

       try {
           addExtras();
       }catch (ExtraForecastException e)
       {
           throw e;
       }
        super.processExtras(payments, lastPayment);
    }

    private void addExtras() throws ExtraForecastException {
        double monthlyPayment = loan.getForecastMonthlyPayment();
        int daysBeforePayment = loan.getDaysBeforePayment();
        Date extraDate;

        // if ($monthlyPayment >= $this->state->annuity) {
        if (monthlyPayment >= state.annuity) {

            if (daysBeforePayment > 0 )
                extraDate = cal.addDaysToDate( state.endDate, -1 * daysBeforePayment);
            else
                extraDate =  state.endDate;

            if (loan.getDateOfStartCalcExtras().before(extraDate) || loan.getDateOfStartCalcExtras().equals(extraDate) ) {
                double for_extras = monthlyPayment - state.annuity;

                if (loan.getForecastExtraType() == Extra.TERM) { // Уменьшение срока
                    if (loan.getDaysBeforePayment() == 0) {
                        if (monthlyPayment < state.balance) {
                            addExtra(for_extras);
                        }
                    } else {
                        if ( monthlyPayment < state.balance || monthlyPayment < state.balance + state.principal) {
                            addExtra(for_extras);
                        } else
                            if (monthlyPayment >state.balance + state.principal
                                    && state.total < state.balance + state.principal) {
                            for_extras = state.balance + state.principal - state.total + state.interest;
                             addExtra(for_extras);
                        }
                    }

                } else
                    if (loan.getForecastExtraType()  == Extra.BALANCE) { // Уменьшение суммы
                    if (loan.getDaysBeforePayment() == 0) {
                        if (for_extras > (state.balance + state.principal - state.extras - state.principal)) {
                            // Уменьшение суммы с 0 днями до платежа.
                            // Спрогнозировать следующий платеж и высчитать правильную сумму досрочки
                        }

                        addExtra(for_extras);
                    } else {
                        if (for_extras > state.balance + state.principal)
                            for_extras = state.balance;

                        addExtra(for_extras);
                    }
                }
            }
        } else {
          //  System.out.println("Платеж по кредиту больше суммы ежемесячного платежа. Сделайте платеж больше "
           //         + state.annuity + " vs " + loan.getForecastMonthlyPayment());
            throw new ExtraForecastException("forecast_monthly_Payment_error",
                  loan.getForecastMonthlyPayment(), state.annuity );

           // throw new ExtraForecastException("forecast_monthly_Payment_error");
        }
    }


    //добавление досрочки
    private void addExtra(double amount) {
        // SortedSet<Extra> extras = loan.getExtras();
        //if ( extras == null)
        //    extras = new TreeSet<Extra>();

        int daysBeforePayment = loan.getDaysBeforePayment();
        Date extraDate;
        if (daysBeforePayment >= 0 )
             extraDate = cal.addDaysToDate(state.endDate, -1 * daysBeforePayment);
        else
            extraDate = state.endDate;

        //extras.add(new Extra(amount, loan.getForecastExtraType(), extraDate));
        //здесь нужно задавать для самой стратегии так как первоначально выгружаем из кредита
        this.extras.add(new Extra(amount, loan.getForecastExtraType(), extraDate));
        //loan.setExtras(extras);
    }
}
