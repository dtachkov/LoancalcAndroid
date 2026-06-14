package ru.loan.forecast;


import com.zoom.loancalc.Extra;
import com.zoom.loancalc.ExtraForecastException;
import com.zoom.loancalc.Loan;
import com.zoom.loancalc.LoanCalendar;
import com.zoom.loancalc.LoanException;
import com.zoom.loancalc.Payment;
import com.zoom.loancalc.calculation.BaseCalculationStrategy;
import com.zoom.loancalc.calculation.ExtrasFirstGeneralStrategy;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;



public class ExtrasFirstGeneralStrategyForecast extends ExtrasFirstGeneralStrategy {



    public ExtrasFirstGeneralStrategyForecast(Loan loan, LoanCalendar cal, BaseCalculationStrategy calculationStrategy) {
        super(loan, cal, calculationStrategy);
    }

    private void addExtras(Date fromdate) throws ExtraForecastException {
        double monthlyPayment = loan.getForecastMonthlyPayment();
        int daysBeforePayment = loan.getDaysBeforePayment();

        //if ($monthlyPayment >= $this->state->total) { //заменил на аннуити как текущий платеж

        Date extraDate;
        if (monthlyPayment >= state.annuity) {
            if (daysBeforePayment != 0){
              extraDate = cal.addDaysToDate( state.endDate, -1 * daysBeforePayment );
            }
            else{
                extraDate = state.endDate;
            }
            //если дата досрочки больше даты начала учета досрочек, то считаем сумму
            // <=
            if (loan.getDateOfStartCalcExtras().before(extraDate) || loan.getDateOfStartCalcExtras().equals(extraDate)) {

                double for_extras = monthlyPayment - state.annuity;
                //echo $this->state->startDate->format('d.m.Y') . '- ' .$this->state->endDate->format('d.m.Y').'- ' .$extraDate->format('d.m.Y'). '- ' .$this->state->annuity. "-".$for_extras."\r\n";


                double daysProportion_total =  daysProportionDays(fromdate, state.endDate);
                //вычисляем проценты которыеначислились за данный период
                double interest_total = state.balance * daysProportion_total * state.rate / 100.0;

                double for_pay_to_principal_next = state.annuity - interest_total;
                // $for_pay_to_principal_next = $this->state->annuity - $interest_total;
                //проценты после досрочного погашения по плану
                double daysProportion_next = daysProportionDays(extraDate, state.endDate);
                //вычисляем проценты которыеначислились за данный период
                double interest_next = state.balance * daysProportion_next * state.rate / 100.0;

                //у нас есть начальна дата периода и дата досрочки, задача определить проценты
                //и понять, больше ли они той суммы, на которую наша досрочка
                double daysProportion = daysProportionDays(fromdate, extraDate);
                //вычисляем проценты которыеначислились до даты досрочки
                double interest = state.balance * daysProportion *  state.rate / 100.0;

                //теперь добавляем в текущие досрочки
                this.extras.add(new Extra(for_extras, loan.getForecastExtraType(), extraDate));
                //}

                 //loan.setExtras(extras);

            }

        } else { //если вдруг платеж меньше чем текущий платеж

            throw new ExtraForecastException("forecast_monthly_Payment_error",
                    loan.getForecastMonthlyPayment(), state.annuity );

          //  throw new ExtraForecastException("forecast_monthly_Payment_error");
        }
    }

    @Override
    protected Date getPaymentsFromExtrasAndReturnLastExtraDate(Date startDate, Date endDate, List<Payment> payments) throws ExtraForecastException {

        // 03-04-2019 года if добавил расчет прогноза только если дата начала расчета больше стартовой даты учета досрочек
        boolean m_should_add_extraforecast = false;
        if ((loan.getDateOfStartCalcExtras().before(startDate)) || loan.getDateOfStartCalcExtras().equals(startDate)) {
            //если за 0 дней, технология другая
            if (loan.getDaysBeforePayment() > 0) {
                addExtras(startDate);
            } else {
                //чтоб добавить прогнозный досрочный платеж мы должны пересчитать аннуитет если были досрочки
                //echo " adding extras for period ".$startDate->format('d.m.Y')."-".$endDate->format('d.m.Y')." \n";
                if (this.extras.isEmpty()) {
                   addExtras(startDate);
                } else {
                    m_should_add_extraforecast = true;
                }

            }
            // var_dump($this->loan->getExtras());
        }

        //теперь начинается основной алгоритм расчета
        Date lastExtraDate = startDate;
        //Extra extra;
        int extra_counter = 0;

        mShouldRecalculateAnnuity = false;
        state.notCoveredInterest = state.principalCoveredByExtras = 0.0;
        state.rateExtra = state.extras = 0;
        mNextPaymentInterestOnly = false;


        // Проходимся по всем внеплановым платежам, которые входят в текущий период  && !(extra = extras.first()).getDate().before(lastExtraDate)
       // while (!extras.isEmpty()
      //          && !(extra = extras.first()).getDate().before(lastExtraDate)
       //         && extra.getDate().before(endDate)) {
        SortedSet<Extra> local_extras = new TreeSet<>(extras);

        for (Extra extra: local_extras ) {
            System.out.println("Process extra: " + extra + " from " + lastExtraDate + " to " + endDate);
           // $extra->getDate() >= $lastExtraDate
            if(extra.getDate().before(endDate) &&
                    (extra.getDate().after(lastExtraDate ) || extra.getDate().equals(lastExtraDate ) )) {

                //если досрочное погашение в дату ежемес платежа то отнимать платеж в погашение ОД не нужно Тачков Д.Е. 17-05-2019
                //см. аннуитетная стратегия сбербанка
                if (extra.getDate().getTime() == lastExtraDate.getTime()) {
                    this.state.oldprincipal = 0;
                }

                switch (extra.getType()) {
                    case Extra.BALANCE:
                    case Extra.TERM: {

                        processExtraByAmount(extra.getType(), lastExtraDate, payments, extra);
                        lastExtraDate = extra.getDate();

                        if (loan.isProcessExtrasByBalanceLikeSberbank() && state.principalCoveredByExtras > 0
                                && lastExtraDate.getTime() != startDate.getTime()) {

                            mNextPaymentInterestOnly = true;
                        }
                        extras.remove(extra);
                        extra_counter++;
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
                        lastExtraDate = extra.getDate();   //cal.date(cal_local.get(Calendar.YEAR),cal_local.get(Calendar.MONTH), cal_local.get(Calendar.DAY_OF_MONTH));
                        extra_counter++;
                        extras.remove(extra);
                        //тут же получается что потом она переопределится при новой ветке цикла
                        state.endDate = cal.addMonthsToDate(lastExtraDate, 1);
                        break;
                    }

                }

            }

        }

        //если это был период в котором были досрочки, то нужно сначала нужно прогнать все досрочки, а потом уже посчитать что пойдет в досрочку из нового аннуитета
        //но это только для периода где были досрочки помимо досрочки из прогноза

        //if(($this->loan->getDateOfStartCalcExtras() > $startDate) && ($this->loan->getDateOfStartCalcExtras() <= $endDate) && ($endDate > $startDate) )
        //
        if((loan.getDateOfStartCalcExtras().after(startDate)) &&
                (loan.getDateOfStartCalcExtras().before(endDate) || loan.getDateOfStartCalcExtras().equals(endDate))
                &&  endDate.after(startDate) )
        {

            addExtras(startDate);
            //если уж добавили, то повторно в конце добавлять не надо
            m_should_add_extraforecast = false;
        }

        //после того как мы обработали все досрочки и пересчитался аннуитет можно добавить прогнозный
        if(m_should_add_extraforecast){

            addExtras(startDate);
        }

        System.out.println("Forecast state after getPaymentsFromExtrasAndReturnLastExtraDate: " + state);
        return lastExtraDate;

    }


    }
