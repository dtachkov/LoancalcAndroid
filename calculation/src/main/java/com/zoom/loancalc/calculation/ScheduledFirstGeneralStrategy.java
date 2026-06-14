package com.zoom.loancalc.calculation;

import com.zoom.loancalc.Extra;
import com.zoom.loancalc.ExtraForecastException;
import com.zoom.loancalc.InfiniteLoanException;
import com.zoom.loancalc.Loan;
import com.zoom.loancalc.LoanCalendar;
import com.zoom.loancalc.Payment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by rustamg on 28/07/15. Стратегия рассчета для кредитов, в которых досрочные платежи должны учитываться в
 * день очередного планового платежа
 */
public class ScheduledFirstGeneralStrategy extends GeneralCalculationStrategy {
    Date startLoanDate;

    public ScheduledFirstGeneralStrategy(Loan loan, LoanCalendar calendar,
            BaseCalculationStrategy calculationStrategy) {

        super(loan, calendar, calculationStrategy);
    }

    @Override
    public List<Payment> calculate() throws InfiniteLoanException, ExtraForecastException {

        initState();

        ArrayList<Payment> payments = new ArrayList<>();

        if (loan.isFirstPaymentInterestOnly()) {
            double daysProportion = calculationStrategy.daysProportion(state);
            state.interest = state.balance * daysProportion * state.rate / 100.0;
            state.total = state.interest;
            state.principal = 0;
        }
        else {
            calculationStrategy.refreshStateStaticPart(state);
            calculationStrategy.refreshStateDynamicPart(state);
        }

        state.subtractFromBalance(state.principal);

        Payment payment = state.toPayment();

        if (loan.isFirstPaymentInterestOnly()) {
            state.index++;
            calculationStrategy.refreshStateStaticPart(state);
            state.index--;
        }

        try {

            processExtras(payments, payment);
        }catch (ExtraForecastException e)
        {
            throw e;
        }

        if (state.term == 1) {
            payment.setEndBalance(0.0);
        }
        else {
            payment.setEndBalance(state.balance);
        }

        payments.add(payment);

        state.startDate = state.endDate;
        startLoanDate = loan.getStartDate();

         for (int index = 1; index < state.term || state.balance > 0; index++) {
            state.index = index;

            state.endDate = cal.addMonthsToDate(startLoanDate, index);

            calculationStrategy.refreshStateDynamicPart(state);

            state.subtractFromBalance(state.principal);

            payment = state.toPayment();

            processExtras(payments, payment);



            payment.setEndBalance(state.balance);

            if (payment.getInterest() > payment.getTotal()) {
                throw new InfiniteLoanException();
            }

            payments.add(payment);

            state.startDate = state.endDate;

             if (state.balance <= 0) {
                 break;
             }

        }

        return payments;
    }


    /**
     * Обработать досрочные платежи
     *
     * @param payments
     * @param lastPayment
     */
    protected void processExtras(ArrayList<Payment> payments, Payment lastPayment) throws ExtraForecastException {

        Extra extra;
        state.index++;

        boolean is_last_payment = false;
        double last_principal = 0;



        if(extras.isEmpty())
        {
            return;
        }

        double new_balance = 0;

        // Проходимся по всем внеплановым платежам, которые входят в текущий период
        while (!extras.isEmpty()
               && !(extra = extras.get(0)).getDate().before(state.startDate)
               && extra.getDate().before(state.endDate)) {

            // в зависимости от типа внепланового платежа выполняются
            switch (extra.getType()) {

                case Extra.BALANCE:

                    new_balance  = state.balance + lastPayment.getPrincipal() - extra.getValue();

                    if(new_balance < 0)
                    {
                        new_balance = 0;
                    }

                    payments.add(new Payment(payments.size(), extra.getDate(), extra.getValue(), 0, extra.getValue(),
                            new_balance, extra.getValue(), 0));

                    state.subtractFromBalance(extra.getValue());

                    calculationStrategy.refreshStateStaticPart(state);
                    break;

                case Extra.RATE:

                    payments.add(new Payment(payments.size(), extra.getDate(), 0, 0, 0, 0, 0, extra.getValue()));

                    state.rate = extra.getValue();
                    state.updateRatePerMonth();

                    calculationStrategy.refreshStateStaticPart(state);
                    break;

                case Extra.TERM:

                    new_balance  = state.balance + lastPayment.getPrincipal() - extra.getValue();

                    if(new_balance < 0)
                    {
                        new_balance = 0;
                    }

                    payments.add(new Payment(payments.size(), extra.getDate(), extra.getValue(), 0, extra.getValue(),
                            new_balance, extra.getValue(), 0));

                    state.subtractFromBalance(extra.getValue());

                    double term = calculationStrategy.termFromState(state);

                    state.term = state.index + Math.ceil(term);
                    break;

                default:
                    break;
            }

            extras.remove(extra);
        }

        state.index--;
    }
}
