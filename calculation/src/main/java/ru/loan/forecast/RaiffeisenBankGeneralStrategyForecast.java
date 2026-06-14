package ru.loan.forecast;


import com.zoom.loancalc.Extra;
import com.zoom.loancalc.ExtraForecastException;
import com.zoom.loancalc.Loan;
import com.zoom.loancalc.LoanCalendar;
import com.zoom.loancalc.Payment;
import com.zoom.loancalc.calculation.BaseCalculationStrategy;
import com.zoom.loancalc.calculation.ExtrasFirstGeneralStrategy;
import com.zoom.loancalc.calculation.RaiffeisenBankGeneralStrategy;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class RaiffeisenBankGeneralStrategyForecast extends RaiffeisenBankGeneralStrategy {



    public RaiffeisenBankGeneralStrategyForecast(Loan loan, LoanCalendar cal, BaseCalculationStrategy calculationStrategy) {
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

                SortedSet<Extra>  extras = loan.getExtras();
                if (extras == null) {
                    extras = new TreeSet<Extra>();
                }
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
                //если вдруг процентов больше, чем досрочка, то она не сработает, в сумме
                //мы заплатим ежемесячный платеж, значит остаток от 80 тыс - останется на счете
                //$additional_extra = 0;
                //$x = 0;
                                     /*   if($interest > $for_extras)
                                        {

                                           // пока что это самый приблизительный вариант
                                          // $additional_extra =  $interest + ($monthlyPayment - $interest - $for_pay_to_principal_next) ;

                                          //   //  $x = ($monthlyPayment - $interest - $interest_next - $for_pay_to_principal_next )/(1 - $daysProportion_next * $this->state->rate / 100.0);
                                           // $additional_extra =  $interest + $x;
                                           // $extras[] = new Extra($additional_extra, $this->loan->getExtrasType(), $extraDate);
                                        }
                                        else
                                        {*/
                // extras.add(new Extra(for_extras, loan.getForecastExtraType(), extraDate));
                //}
                //теперь добавляем в текущие досрочки
                this.extras.add(new Extra(for_extras, loan.getForecastExtraType(), extraDate));

            }

        } else {
            throw new ExtraForecastException("forecast_monthly_Payment_error",
                     loan.getForecastMonthlyPayment(), state.annuity );
        }
    }

    @Override
    protected Date getPaymentsFromExtrasAndReturnLastExtraDate(Date startDate, Date endDate, List<Payment> payments) throws ExtraForecastException {

        // 03-04-2019 года if добавил расчет прогноза только если дата начала расчета больше стартовой даты учета досрочек
        boolean m_should_add_extraforecast = false;
        // <=
        if ((loan.getDateOfStartCalcExtras().before(startDate)) || loan.getDateOfStartCalcExtras().equals(startDate)) {
            //если за 0 дней, технология другая
            if (loan.getDaysBeforePayment() > 0) {
                addExtras(startDate);
            } else {
                //чтоб добавить прогнозный досрочный платеж мы должны пересчитать аннуитет если были досрочки
                //echo " adding extras for period ".$startDate->format('d.m.Y')."-".$endDate->format('d.m.Y')." \n";
                if (extras.isEmpty()) {
                   addExtras(startDate);
                } else {
                    m_should_add_extraforecast = true;
                }

            }
            // var_dump($this->loan->getExtras());
        }

       // Extra extra;
        Date lastExtraDate = startDate;
        state.notCoveredInterest = state.principalCoveredByExtras = 0.0;
        state.rateExtra = state.extras = 0;

        // Проходимся по всем внеплановым платежам, которые входят в текущий период
      //  while (!extras.isEmpty()
     //           && !(extra = extras.first()).getDate().before(startDate)
     //           && extra.getDate().before(endDate)) {

        SortedSet<Extra> local_extras = new TreeSet<>(extras);
        for (Extra extra: local_extras ) {
            System.out.println("Process extra: " + extra + " from " + lastExtraDate + " to " + endDate);
            // $extra->getDate() >= $lastExtraDate
            if (extra.getDate().before(endDate) &&
                    (extra.getDate().after(lastExtraDate) || extra.getDate().equals(lastExtraDate))) {

                switch (extra.getType()) {

                    case Extra.BALANCE:
                    case Extra.TERM: {

                        processExtraByAmount(extra.getType(), startDate, payments, extra);
                        lastExtraDate = extra.getDate();
                        startDate = extra.getDate();

                        break;
                    }
                    case Extra.RATE: {

                        processRateExtra(startDate, extra, payments);
                        startDate = extra.getDate();
                        lastExtraDate = extra.getDate();

                        break;
                    }
                }

                extras.remove(extra);
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

        System.out.println("RaifForecast state after getPaymentsFromExtrasAndReturnLastExtraDate: " + state);
        return startDate;

    }


    }
