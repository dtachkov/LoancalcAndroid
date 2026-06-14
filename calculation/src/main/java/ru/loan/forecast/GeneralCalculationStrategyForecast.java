package ru.loan.forecast;

import com.zoom.loancalc.InfiniteLoanException;
import com.zoom.loancalc.Loan;
import com.zoom.loancalc.LoanCalendar;
import com.zoom.loancalc.Payment;
import com.zoom.loancalc.calculation.BaseCalculationStrategy;
import com.zoom.loancalc.calculation.GeneralCalculationStrategy;

import java.util.List;

public class GeneralCalculationStrategyForecast extends GeneralCalculationStrategy {
    protected GeneralCalculationStrategyForecast(Loan loan, LoanCalendar calendar, BaseCalculationStrategy strategy) {
        super(loan, calendar, strategy);

    }

    @Override
    public List<Payment> calculate() throws InfiniteLoanException {
        return null;
    }

}
