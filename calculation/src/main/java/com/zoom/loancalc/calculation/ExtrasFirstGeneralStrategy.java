package com.zoom.loancalc.calculation;

import com.zoom.loancalc.Extra;
import com.zoom.loancalc.ExtraForecastException;
import com.zoom.loancalc.InfiniteLoanException;
import com.zoom.loancalc.Loan;
import com.zoom.loancalc.LoanCalendar;
import com.zoom.loancalc.Payment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Стратегия рассчета для кредитов, в которых досрочные платежи должны учитываться в день, когда они были внесены, но не
 * в дату очередного планового платежа Расчет для Сбера и ВТБ
 */
public class ExtrasFirstGeneralStrategy extends GeneralCalculationStrategy {

    private int mIndexSinceLastTermRecalculation;
    private Date startLoanDate;
    protected boolean mShouldRecalculateAnnuity;
    protected  boolean is_term_changed = false;
    /**
     * Для сбера, если досрочный платеж был в дату очередного платежа, следующий очередной платеж не должен быть только
     * проценты
     */
    protected boolean mNextPaymentInterestOnly;

    public ExtrasFirstGeneralStrategy(Loan loan, LoanCalendar cal, BaseCalculationStrategy calculationStrategy) {

        super(loan, cal, calculationStrategy);
    }

    @Override
    protected void initState() {

        super.initState();

        mIndexSinceLastTermRecalculation = 0;
        mNextPaymentInterestOnly = false;
        mShouldRecalculateAnnuity = true;
    }

    @Override
    public List<Payment> calculate() throws InfiniteLoanException, ExtraForecastException {

        initState();

        ArrayList<Payment> payments = new ArrayList<>();

        int indexCorrection = 0;

        double total = 0;
        double monthlyPayment = 0;
        if (loan.isFirstPaymentInterestOnly()) {

            indexCorrection = 1;

            recalculateTotal(indexCorrection);
            monthlyPayment = state.total;
            // Обрабатываем досрочные платежи, добавляя их в payments.
            Date lastExtraDate = getPaymentsFromExtrasAndReturnLastExtraDate(state.startDate,
                    state.endDate, payments);

            if (lastExtraDate.getTime() != state.startDate.getTime()) {
                state.startDate = lastExtraDate;
                total = state.total;

            }

            // Считаем, сколько процентов осталось в первом плановом платеже
            handleInterestOnlyPeriod();
            state.extras = 0;
            putFirstScheduledPayment(payments);

            state.startDate = state.endDate;
            indexCorrection = 1;
        }

        if (total != 0) {
            state.total = total;
        }
        else {
                state.total = monthlyPayment;
        }

        if (mShouldRecalculateAnnuity == true) {
            recalculateTotal(indexCorrection);
        }

        Date startDate = state.startDate;
        //дата начала кредита необходима если будет перенос платежа
        startLoanDate = startDate;

        Payment payment;
        this.is_term_changed = false;
        //основной цикл расчета платежей
        for (int index = 0; index < state.term - indexCorrection || state.balance > 0; index++) {

            state.index = index;
            mIndexSinceLastTermRecalculation++;
            //здесь учитываем что дата ежемесячного платежа может меняться
            state.endDate = cal.addMonthsToDate(startLoanDate, index + 1);

            State stateBeforeExtras = new State(state.balance, state.startDate, state.endDate, state.total, state.term,
                    state.rate, state.ratePerMonth, state.index);
            //добвлено 07-11-2019 Тачков Д.Е.
            //Сделано для досрочного погашения в Сбербанке по отношению новый остаток долга и старый остаток долга
            //вычисляем число дней, которые должны были пройти между двумя плановыми платежами
            double daysProportion_old = this.daysProportionDays(state.startDate, state.endDate);
            //вычисляем проценты которые нужно было начислить если бы не было досрочек.
            double interest_old = this.state.balance * daysProportion_old * this.state.rate / 100.0;
            // cумма которая пошла бы в погашение ОД, если бы не было досрочек
            /*Если досрочка не в дату платежа нам нужно получить старый платеж в погашение ОД*/
            this.state.oldprincipal = this.state.annuity - interest_old;


            if(cal.truncateTime(state.startDate).compareTo(cal.truncateTime(state.endDate)) == 0 )
            {
                //если вдруг получился пустой период досрочек
                continue;
            }
            state.startDate = getPaymentsFromExtrasAndReturnLastExtraDate(state.startDate, state.endDate, payments);

            if (state.balance <= 0) {
                break;
            }

            if (loan.isProcessExtrasByBalanceLikeSberbank() && state.principalCoveredByExtras > 0 && mNextPaymentInterestOnly) {

                 monthlyPayment = state.total;

                handleInterestOnlyPeriod();
                stateBeforeExtras.index ++;
                calculationStrategy.refreshStateDynamicPart(stateBeforeExtras);
                double subtractedPrincipal = state.principal;
                state.principal = Math.max(0,
                        stateBeforeExtras.total - stateBeforeExtras.interest - state.principalCoveredByExtras);
                state.total = state.principal + state.interest;
                state.balance = Math.max(0, state.balance - Math.max(0, state.principal - subtractedPrincipal));

                state.extras = 0;
                payment = addPaymentFromState(payments);
                state.index ++;
                if (mShouldRecalculateAnnuity) {
                    int indexBackup = state.index;

                    // Если было изменение срока, а потом ежемесячного платежа в новом периоде, то новый платеж считался
                    // по сроку, уменьшенному на mindexSinceLastTermRecalculation, что было неверно
                    //если было изменение срока, То индекс mindexSinceLastTermRecalculation насчитал +2, один в начале цикла, второй при обработки дсорочки
                    //поэтому нужно

                    if(!this.is_term_changed) {
                        this.state.index = mIndexSinceLastTermRecalculation;
                    }  else {
                        mIndexSinceLastTermRecalculation = mIndexSinceLastTermRecalculation - 2;
                        this.state.index =mIndexSinceLastTermRecalculation;
                        this.is_term_changed = false;

                    }

                    //state.index = mIndexSinceLastTermRecalculation; //пока убрали и заменили условиями выше
                    //System.out.println("recalc annuity 129 line");
                    calculationStrategy.refreshStateStaticPart(state);
                    state.index = indexBackup;
                }
                else {
                    state.total = monthlyPayment;
                }
            } //если это не сбербанк
            else {

                 monthlyPayment = state.total;
                //доработка по письму когда ВТБ и изменение срока
                if ((state.notCoveredInterest > 0) && (loan.isProcessExtrasByBalanceLikeSberbank())) {
                    // Когда досрочные платежи не покрывали проценты, уменьшаем общую сумму очередного платежа на
                    // сумму досрочных погашений
                    state.total -= state.extras;
                    state.extras = 0;
                    calculationStrategy.refreshStateDynamicPart(state);
                    state.subtractFromBalance(state.principal);
                    payment = addPaymentFromState(payments);
                    state.total = monthlyPayment;
                }
                else {
                    if (mShouldRecalculateAnnuity) {
                        calculationStrategy.refreshStateStaticPart(state);
                    }
                    state.extras = 0;
                    calculationStrategy.refreshStateDynamicPart(state);
                    state.subtractFromBalance(state.principal);
                    payment = addPaymentFromState(payments);
                }
            }

            System.out.println(payment);

            state.startDate = state.endDate;

            if (state.balance <= 0) {
                break;
            }
            System.out.println("---End iteration with index--" + state.index );
        }

        return payments;
    }

    private void recalculateTotal(int indexCorrection) {

        state.index += indexCorrection;
        calculationStrategy.refreshStateStaticPart(state);
        state.index -= indexCorrection;
    }

    private Payment addPaymentFromState(ArrayList<Payment> payments) throws InfiniteLoanException {

        Payment payment = state.toPayment();

        payment.setIndex(payments.size());
        payments.add(payment);

        if (payment.getInterest() > payment.getTotal()) {
            throw new InfiniteLoanException();
        }

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

        Date lastExtraDate = startDate;
        Extra extra;
        int  extra_counter  = 0;

        mShouldRecalculateAnnuity = false;
        state.notCoveredInterest = state.principalCoveredByExtras = 0.0;
        state.rateExtra = state.extras = 0;
        mNextPaymentInterestOnly = false;

        // Проходимся по всем внеплановым платежам, которые входят в текущий период  && !(extra = extras.first()).getDate().before(lastExtraDate)
        while (!extras.isEmpty()
               &&  !(extra = extras.get(0)).getDate().before(lastExtraDate)
               && extra.getDate().before(endDate) )
        {
            System.out.println("Process extra: " + extra + " from " + lastExtraDate + " to " + endDate);



            //если досрочное погашение в дату ежемес платежа то отнимать платеж в погашение ОД не нужно Тачков Д.Е. 17-05-2019
            //см. аннуитетная стратегия сбербанка
            if(extra.getDate().getTime()== lastExtraDate.getTime()){
                this.state.oldprincipal = 0;
            }

            switch (extra.getType()) {

                case Extra.BALANCE:
                case Extra.TERM: {

                    processExtraByAmount(extra.getType(), lastExtraDate, payments, extra);
                    lastExtraDate = extra.getDate();

                    //а если выставлен флаг первый платеж только проценты то как
                    if (loan.isProcessExtrasByBalanceLikeSberbank() && state.principalCoveredByExtras > 0
                        && lastExtraDate.getTime() != startDate.getTime()) {

                        mNextPaymentInterestOnly = true;
                    }
                    extras.remove(extra);
                    extra_counter ++;
                    break;
                }
                case Extra.RATE: {
                    processRateExtra(lastExtraDate, extra, payments);
                    lastExtraDate = extra.getDate();
                    extras.remove(extra);
                    break;
                }
                //если в графике было изменение даты ежемесячного платежа
                case Extra.DATE: {
                    Payment payment = processExtraByDate(lastExtraDate, payments, extra, extra_counter);
                     lastExtraDate =  extra.getDate();   //cal.date(cal_local.get(Calendar.YEAR),cal_local.get(Calendar.MONTH), cal_local.get(Calendar.DAY_OF_MONTH));
                     extra_counter ++;
                     extras.remove(extra);
                     //тут же получается что потом она переопределится при новой ветке цикла
                     state.endDate =  cal.addMonthsToDate(lastExtraDate, 1);
                    break;
                }

            }


        }
        System.out.println("state after getPaymentsFromExtrasAndReturnLastExtraDate: " + state);
        return lastExtraDate;
    }

    protected void processExtraByAmount(int type, Date startDate, List<Payment> payments, Extra extra) {


        boolean is_last_payment = false;
        double last_principal = 0;

        double daysProportion = calculationStrategy.daysProportion(startDate, extra.getDate());
        double interest = state.balance * daysProportion * state.rate / 100.0;

        state.notCoveredInterest += interest - extra.getValue();
        if(!extra.getDate().equals(startDate)) {
            state.extras += extra.getValue();
        }
        else
        {
           int i1=1;
        }

        Payment payment = new Payment();
        payment.setDate(extra.getDate());
        payment.setTotal(extra.getValue());
        //если досрочка покрывает полностью проценты - то пересчитываем ежемесячный платеж
        if (state.notCoveredInterest < 0) {

            payment.setPrincipal(-state.notCoveredInterest);
            //поддержка последних платежей
            if(payment.getPrincipal() >  state.balance )
            {
                 payment.setTotal(state.balance + interest);
                 last_principal = state.balance;
                 is_last_payment = true;
                // echo $this->state->balance.' vs '.$payment->getPrincipal();
            }
            //конец поддержки последних платежей

            state.subtractFromBalance(payment.getPrincipal());
            state.principalCoveredByExtras += payment.getPrincipal();
            state.notCoveredInterest = 0;

            switch (type) {
               case Extra.BALANCE: {

                    int indexBackup = state.index;
                    if(!loan.isProcessExtrasByBalanceLikeSberbank()) {
                        state.index = mIndexSinceLastTermRecalculation;
                        calculationStrategy.refreshStateStaticPart(state);
                        state.index = indexBackup;
                    }
                    //если досрочное погашение введено не в дату очередного платежа, то пересчитываем аннуитет
                    //  && (extra.getDate().compareTo(startDate) != 0)
                    if (loan.isProcessExtrasByBalanceLikeSberbank()
                            && state.principalCoveredByExtras > 0
                            && state.oldprincipal <  state.principalCoveredByExtras ) {
                        mShouldRecalculateAnnuity = true;
                    }
                    break;
                }
                case Extra.TERM: {
                    double newTerm = calculationStrategy.termFromState(state);
                    state.term = (int) (newTerm + 0.5);
                    this.is_term_changed = true;
                    mIndexSinceLastTermRecalculation = 1;
                    break;
                }
            }
        } else
        {
            state.principal = 0;
            state.principalCoveredByExtras = 0;
        }

        //end if state.notCoveredInterest < 0

       // payment.setInterest(payment.getTotal() - payment.getPrincipal());
        //начало поддержки последнего платежа
        if(!is_last_payment){
           payment.setInterest(payment.getTotal() - payment.getPrincipal());
        }
        else
        {
            payment.setInterest(interest);
            payment.setPrincipal(last_principal);
        }
        //конец поддержки последнего платежа

        payment.setEndBalance(state.balance);
        payment.setExtras(extra.getValue());
        payment.setIndex(payments.size() );

        payments.add(payment);

        System.out.println(payment);
    }


    protected Payment processExtraByDate(Date startDate, List<Payment> payments, Extra extra, int extrainperiodcount) {

        //задаем новую дату платежа
        Calendar cal_local = Calendar.getInstance();
        cal_local.setTime(extra.getDate());
        Date new_date = cal_local.getTime();
        //date_parse($extra->getValue());

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
        payment.setIndex(payments.size() + extrainperiodcount);
        //добавляем в массив платежей
        payments.add(payment);
        return payment;

    }

    protected void processRateExtra(Date startDate, Extra extra, List<Payment> payments) {

        Payment rateChangePayment = new Payment();
        rateChangePayment.setRateExtra(extra.getValue());
        rateChangePayment.setIndex(payments.size());
        rateChangePayment.setDate(extra.getDate());
        //здесь это нужно чтоб работало определение начисленных процентов на дату на карточке добавления досрочки
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
        //говорим что при перерасчете аннуитета ставка изменилась
        this.state.is_rateChanged = true;
        recalculateTotal(indexCorrection);
        this.state.is_rateChanged = false;
        state.extras = 0;
    }
}
