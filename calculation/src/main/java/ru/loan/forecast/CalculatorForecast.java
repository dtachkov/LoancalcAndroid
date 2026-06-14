package ru.loan.forecast;

import com.zoom.loancalc.Loan;
import com.zoom.loancalc.LoanCalendar;
import com.zoom.loancalc.calculation.AnnuityStrategy;
import com.zoom.loancalc.calculation.BaseCalculationStrategy;
import com.zoom.loancalc.calculation.Calculator;
import com.zoom.loancalc.calculation.ExtrasFirstGeneralStrategy;
import com.zoom.loancalc.calculation.GradedStrategy;
import com.zoom.loancalc.calculation.RaiffeisenBankGeneralStrategy;
import com.zoom.loancalc.calculation.ScheduledFirstGeneralStrategy;

public class CalculatorForecast extends Calculator {

    public CalculatorForecast(LoanCalendar calendar) {
        super(calendar);
    }

    @Override
    protected void chooseStrategies() {

        BaseCalculationStrategy calculationStrategy;

        switch (loan.getType()) {
            case Loan.GRADED:
                calculationStrategy = new GradedStrategy(cal);
                // TODO: loan.isApplyExtrasImmediately() will be implemented later
                generalStrategy = new ScheduledFirstGeneralStrategyForecast(loan, cal, calculationStrategy);
                break;

            case Loan.ANNUITY:
            default:
                calculationStrategy = new AnnuityStrategy(cal);

                if (loan.isApplyExtrasImmediately()) {
                    if (cal.getExtraDayInMonth() != 0) {
                        generalStrategy = new RaiffeisenBankGeneralStrategyForecast(loan, cal, calculationStrategy);
                    }   else {
                        generalStrategy = new ExtrasFirstGeneralStrategyForecast(loan, cal, calculationStrategy);
                    }
                }
                else {
                    generalStrategy = new ScheduledFirstGeneralStrategyForecast(loan, cal, calculationStrategy);
                }
                break;
        }
    }

}
