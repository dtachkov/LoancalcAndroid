package com.zoom.loancalc.calculation;


import com.zoom.loancalc.Extra;
import com.zoom.loancalc.ExtraForecastException;
import com.zoom.loancalc.Loan;
import com.zoom.loancalc.LoanCalendar;
import com.zoom.loancalc.Payment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * При досрочном погашении сумма вычитается полностью из остатка долга. Проценты для ежемесячного платежа считаются до
 * досрочки + после досрочки
 */
public class RaiffeisenBankGeneralStrategy extends GeneralCalculationStrategy {

    private int indexSinceLastTermRecalculation;
    protected Double interest_old = 0.0;
    protected Double interest_last_pay = 0.0;
    private Date      startLoanDate ;
    Boolean should_add_old_percent;
    public RaiffeisenBankGeneralStrategy(Loan loan, LoanCalendar cal, BaseCalculationStrategy calculationStrategy) {

        super(loan, cal, calculationStrategy);
    }

    @Override
    protected void initState() {

        super.initState();

        indexSinceLastTermRecalculation = 0;
    }

    @Override
    public List<Payment> calculate() throws ExtraForecastException {

        initState();
        should_add_old_percent = false;
        ArrayList<Payment> payments = new ArrayList<>();

        int indexCorrection = 0;

        double annuity = 0;

        if (loan.isFirstPaymentInterestOnly()) {

            indexCorrection = 1;

            recalculateAnnuity(indexCorrection);

            // Обрабатываем досрочные платежи, добавляя их в payments.
            Date lastExtraDate = getPaymentsFromExtrasAndReturnLastExtraDate(state.startDate,
                    state.endDate, payments);

            if (lastExtraDate.getTime() != state.startDate.getTime()) {
                state.startDate = lastExtraDate;
                annuity = state.total;
            }

            // Считаем, сколько процентов осталось в первом плановом платеже
            handleInterestOnlyPeriod();
            state.extras = 0;
            putFirstScheduledPayment(payments);

            state.startDate = state.endDate;
        }

        if (annuity != 0) {
            state.total = annuity;
        }
        else {
            recalculateAnnuity(indexCorrection);
        }


        recalculateAnnuity(indexCorrection);

        Date startDate = state.startDate;
        //для изменени даты ежемес платежа
        startLoanDate = startDate;
        this.interest_old = 0.0;
        Payment payment;
        int oldSize, newSize = 0;
        for (int index = 0; index < state.term - indexCorrection || state.balance > 0; index++) {

            state.index = index;
            indexSinceLastTermRecalculation++;
            //для досрочки по изменению даты ежемес платежа
            state.endDate = cal.addMonthsToDate(startLoanDate, index + 1);

            State stateBeforeExtras = new State(state.balance, state.startDate, state.endDate, state.total, state.term,
                    state.rate, state.ratePerMonth, state.index);
            //нам нужно вычислить последнюю часть для уплаты процентов чтобы в случае досрочки добавить к ней проценты и получит
            //меньший платеж
            double daysProportion_old = this.daysProportionDays(state.startDate, state.endDate);

            interest_last_pay = this.state.balance * daysProportion_old * this.state.rate / 100.0;
            this.state.oldprincipal = this.state.annuity - interest_last_pay;

            oldSize = payments.size();
            state.startDate = getPaymentsFromExtrasAndReturnLastExtraDate(state.startDate, state.endDate, payments);
            newSize = payments.size();
            //добавили досрочку
            if(oldSize != newSize)
            {
                should_add_old_percent = true;
            }

            if (state.balance <= 0) {
                break;
            }

            state.extras = 0;
            calculationStrategy.refreshStateDynamicPart(state);


            payment = addPaymentFromState(payments);

            if(should_add_old_percent)
            {
                if(this.state.annuity <  this.state.interest + this.interest_old) {
                    // старые и новые проценты  = сумма ежемес платежа
                    payment.setTotal(payment.getInterest() + interest_old);
                    payment.setPrincipal(0);
                    payment.setInterest(payment.getInterest() + interest_old);
                }else {
                    payment.setPrincipal(this.state.annuity
                            - this.state.interest
                            - this.interest_old  );
                    payment.setInterest(this.state.interest + this.interest_old);

                }
                should_add_old_percent = false;
                interest_old = 0.0;
                // var_dump($payment);
            }else {
                state.subtractFromBalance(state.principal);
                payment.setEndBalance(state.balance);
            }



            System.out.println(payment);

            state.startDate = state.endDate;

            if (state.balance <= 0) {
                break;
            }
        }

        return payments;
    }

    private void recalculateAnnuity(int indexCorrection) {

        state.index += indexCorrection;
        calculationStrategy.refreshStateStaticPart(state);
        state.index -= indexCorrection;
    }

    private Payment addPaymentFromState(ArrayList<Payment> payments) {

        Payment payment = state.toPayment();

        payment.setIndex(payments.size());
        payments.add(payment);
        return payment;
    }

    private void handleInterestOnlyPeriod() {

        double daysProportion = calculationStrategy.daysProportion(state);
        state.interest = state.notCoveredInterest + state.balance * daysProportion * state.rate / 100.0;
        state.total = state.interest;
        state.principal = 0;
    }

    private void putFirstScheduledPayment(ArrayList<Payment> payments) {

        Payment payment = state.toPayment();

        if (state.term == 1) {
            payment.setEndBalance(0.0);
        }
        else {
            payment.setEndBalance(state.balance);
        }

        payment.setIndex(payments.size());
        payments.add(payment);

        System.out.println(payment);
    }

    /**
     * @param startDate
     *         Начало периода
     * @param endDate
     *         Конец периода
     * @param payments
     *         куда складывать обработанные платежи
     *
     * @return Дата последнего обработанного платежа
     */

    protected Date getPaymentsFromExtrasAndReturnLastExtraDate(Date startDate, Date endDate, List<Payment> payments) throws ExtraForecastException {

        Extra extra;
        state.rateExtra = state.extras = 0;


        // Проходимся по всем внеплановым платежам, которые входят в текущий период
        while (!extras.isEmpty()
                && !(extra = extras.get(0)).getDate().before(startDate)
                && extra.getDate().before(endDate)) {

            System.out.println("Process extra: " + extra);

            switch (extra.getType()) {

                case Extra.BALANCE:
                case Extra.TERM: {

                    processExtraByAmount(extra.getType(), startDate, payments, extra);
                    startDate = extra.getDate();

                    break;
                }
                case Extra.RATE: {

                    processRateExtra(startDate, extra, payments);
                    startDate = extra.getDate();

                    break;
                }
                //если в графике было изменение даты ежемесячного платежа
                case Extra.DATE: {
                    Payment payment = processExtraByDate(startDate, payments, extra);
                    startDate =  extra.getDate();   //cal.date(cal_local.get(Calendar.YEAR),cal_local.get(Calendar.MONTH), cal_local.get(Calendar.DAY_OF_MONTH));
                    extras.remove(extra);
                    //тут же получается что потом она переопределится при новой ветке цикла
                    state.endDate =  cal.addMonthsToDate(startDate, 1);
                    break;
                }

            }

            extras.remove(extra);
        }

        System.out.println("state after getPaymentsFromExtrasAndReturnLastExtraDate: " + state);

        return startDate;
    }


    protected Payment processExtraByDate(Date startDate, List<Payment> payments, Extra extra) {

        //задаем новую дату платежа
        Calendar cal_local = Calendar.getInstance();
        cal_local.setTime(extra.getDate());
        Date new_date = cal_local.getTime();


        //считаем проценты до даты последного погашения до изменения даты
        double daysProportion =  daysProportionDays(startDate,  new_date);

        double interest =  state.balance *  daysProportion * state.rate / 100.0;

        Payment payment = new Payment();
        //дата - это дата на которую переносим https://trello.com/c/rELfNDx6/145-%D0%BA%D1%80%D0%B5%D0%B4%D0%B8%D1%82%D0%BD%D1%8B%D0%B5-%D0%BA%D0%B0%D0%BD%D0%B8%D0%BA%D1%83%D0%BB%D1%8B
        payment.setDate(new_date);
        // делаем так, чтоб 1 месяц теперь добавлялся к новой дате ежемесячного платежа
        //такого дня может не быть в данном месяце?

        Calendar cal_start = Calendar.getInstance();
        cal_start.setTime(startLoanDate);
        cal_start.set(Calendar.DAY_OF_MONTH, cal_local.get(Calendar.DAY_OF_MONTH));
        //а если для февраля и хотим задать 31 - задаем ноую дату
        //временно меняем дату
        startLoanDate = cal_start.getTime();
        // this.cal.date(
        //cal_start.get(Calendar.YEAR),
        //cal_start.get(Calendar.MONTH),
        //cal_local.get(Calendar.DAY_OF_MONTH));

        //нужно платить только проценты
        //var_dump( $this->startLoanDate);
        payment.setTotal(interest);
        payment.setInterest(interest);
        payment.setPrincipal(0);
        //баланс неизменен
        payment.setEndBalance(state.balance);
        payment.setIndex(payments.size());
        //добавляем в массив платежей
        payments.add(payment);
        return payment;

    }

    protected void processExtraByAmount(int type, Date startDate, List<Payment> payments, Extra extra) {

        boolean is_last_payment = false;
        double  last_principal = 0;

        double daysProportion = calculationStrategy.daysProportion(startDate, extra.getDate());
        double interest = state.balance * daysProportion * state.rate / 100.0;

        state.extras += extra.getValue();

        Payment payment = new Payment();
        payment.setDate(extra.getDate());
        payment.setPrincipal(extra.getValue());
        payment.setTotal(extra.getValue());


        if (payment.getPrincipal() > state.balance) {
            payment.setTotal(state.balance + interest);
            last_principal = state.balance;
            is_last_payment = true;
        }

        state.subtractFromBalance(extra.getValue());

        switch (type) {
            case Extra.BALANCE: {
                int indexBackup = state.index;
                state.index = indexSinceLastTermRecalculation;
                calculationStrategy.refreshStateStaticPart(state);
                state.index = indexBackup;
                break;
            }
            case Extra.TERM: {
                double newTerm = calculationStrategy.termFromState(state);
                state.term = (int) Math.ceil(newTerm);
                break;
            }
        }



        // payment.setInterest(payment.getTotal() - payment.getPrincipal());
        //начало поддержки последнего платежа
        if(!is_last_payment){
            payment.setInterest(0);
            interest_old =  interest_old + interest;
        }
        else
        {
            payment.setInterest(interest);
            payment.setPrincipal(last_principal);
        }


        payment.setEndBalance(state.balance);
        payment.setExtras(extra.getValue());
        payment.setIndex(payments.size());

        payments.add(payment);

        System.out.println(payment);
    }

    protected void processRateExtra(Date startDate, Extra extra, List<Payment> payments) {

        Payment rateChangePayment = new Payment();
        rateChangePayment.setRateExtra(extra.getValue());
        rateChangePayment.setIndex(payments.size());
        rateChangePayment.setDate(extra.getDate());
        rateChangePayment.setEndBalance(state.balance);
        payments.add(rateChangePayment);

        double daysProportion = calculationStrategy.daysProportion(startDate, extra.getDate());
        double interest = state.balance * daysProportion * state.rate / 100.0;

        state.notCoveredInterest += interest;

        state.rate = extra.getValue();
        state.updateRatePerMonth();

        int indexCorrection;

        if (loan.isIgnorePassedPeriodsAfterRateChange()) {
            // Вот это должно правильно рассчитывать аннуитет для ВТБ24
            indexCorrection = -state.index;
        }
        else {
            indexCorrection = loan.isFirstPaymentInterestOnly() ? 1 : 0;
        }

        recalculateAnnuity(indexCorrection);

        state.extras = 0;
    }
}
