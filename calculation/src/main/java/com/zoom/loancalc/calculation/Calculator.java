package com.zoom.loancalc.calculation;

import com.zoom.loancalc.ExtraForecastException;
import com.zoom.loancalc.InfiniteLoanException;
import com.zoom.loancalc.Loan;
import com.zoom.loancalc.LoanCalendar;
import com.zoom.loancalc.Payment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Calculator {

    // Algorithm params
    /**
     * Календарь
     */
    protected LoanCalendar cal;

    protected GeneralCalculationStrategy generalStrategy;

    /**
     * Кредит
     */
    protected Loan loan;


    public Calculator(LoanCalendar calendar) {

        cal = calendar;
    }


    /**
     * Выбор стратегии расчета в зависимости от типа кредита
     */
    protected void chooseStrategies() {

        BaseCalculationStrategy calculationStrategy;

        switch (loan.getType()) {
            case Loan.GRADED:
                calculationStrategy = new GradedStrategy(cal);
                // TODO: loan.isApplyExtrasImmediately() will be implemented later
                generalStrategy = new ScheduledFirstGeneralStrategy(loan, cal, calculationStrategy);
                break;

            case Loan.ANNUITY:
            default:
                calculationStrategy = new AnnuityStrategy(cal);

                if (loan.isApplyExtrasImmediately()) {
                    if (cal.getExtraDayInMonth() != 0) {
                        generalStrategy = new RaiffeisenBankGeneralStrategy(loan, cal, calculationStrategy);
                    }   else {
                        generalStrategy = new ExtrasFirstGeneralStrategy(loan, cal, calculationStrategy);
                    }
                }
                else {
                    generalStrategy = new ScheduledFirstGeneralStrategy(loan, cal, calculationStrategy);
                }
                break;
        }
    }

    public List<Payment> calculate(Loan aLoan) throws InfiniteLoanException, ExtraForecastException {

        //удаляем невалидные платежи
        //aLoan.setExtras(aLoan.deleteNotValidExtra( aLoan.getExtras()));

        if (aLoan.getTerm() > 0 && aLoan.getRate() > 0 && aLoan.getAmount() > 0) {

            loan = aLoan;

            if (loan.getStartDate() == null) {
                loan.setStartDate(cal.truncateTime(new Date()));
            }
            //&& loan.getIssueDate() == null
            if (!loan.isFirstPaymentInterestOnly()) {
                loan.setIssueDate(cal.monthBeforeDate(loan.getStartDate()));
            }

            chooseStrategies();
            try {
                List<Payment> pays = generalStrategy.calculate();
                return pays;
            }catch (ExtraForecastException e)
            {
                throw e;
            }
        }

        return new ArrayList<>();
    }
}
