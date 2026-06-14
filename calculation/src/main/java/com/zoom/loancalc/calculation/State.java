package com.zoom.loancalc.calculation;

import com.zoom.loancalc.Payment;

import java.util.Date;


public final class State {

    // loan fields
    /**
     * Остаток долга на конец периода
     */
    public double balance;

    /**
     * используется при расчете аннуитета как новый и старый долг - отношение
     */
    public double oldprincipal;


    /**
     * Остаток долга до его уменьшения
     */
    public double oldbalance;
    /**
     * Сколько всего месяцев по текущим рассчетам
     */
    public double term;
    /**
     * Текущая ставка
     */
    public double rate;
    /**
     * Процентная ставка за месяц
     */
    public double ratePerMonth;

    // period fields
    /**
     * Номер текущего месяца
     */
    public int index;
    /**
     * Дата начала периода
     */
    public Date startDate;
    /**
     * Дата окончания периода
     */
    public Date endDate;

    // payment fields
     /*  Текущий аннуитетный платеж, рассчитанный по формуле аннуитета
     */
    public double annuity;
    /**
     * Общая сумма текущего платежа
     */
    public double total;
    /**
     * Сумма за пользование кредитом
     */
    public double interest;
    /**
     * Сумма в погашение основного долга
     */
    public double principal;

    /**
     * Общая сумма внеплановых платежей за текущий период
     */
    public double extras;
    /**
     * Новая процентная ставка, установленная при досрочном платеже
     */
    public double rateExtra;

    /**
     * Сумма начисленных процентов, неоплаченных досрочными платежами
     */
    public double notCoveredInterest;

    /* Флаг что меняется ставка */
    public boolean is_rateChanged = false;

    /**
     * Сумма основного долга, погашенная досрочными платежами в текущем периоде
     */
    public double principalCoveredByExtras;



    public State(double balance, double term, double rate, Date startDate, Date endDate) {

        this.balance = balance;
        this.term = term;
        this.rate = rate;
        this.startDate = startDate;
        this.endDate = endDate;

        updateRatePerMonth();
    }

    public State(double balance, Date startDate, Date endDate, double total, double term, double rate,
            double ratePerMonth,
            int index) {

        this.balance = balance;
        this.startDate = startDate;
        this.endDate = endDate;
        this.total = total;
        this.term = term;
        this.rate = rate;
        this.ratePerMonth = ratePerMonth;
        this.index = index;
    }

    /**
     * Уменьшить остаток долга на заданную сумму
     *
     * @param value
     *         Сумма, на которую уменьшается остаток долга
     */
    public void subtractFromBalance(double value) {
        //старый остаток долга до его изменинения
        this.oldbalance = balance;
        if (value > balance) {
            balance = 0;
        }
        else {
            balance -= value;
        }
    }

    /**
     * Обновить ежемесячную ставку в соответствии с годовой
     */
    public void updateRatePerMonth() {

        ratePerMonth = rate / 1200.0;
    }

    /**
     * Сконструировать платеж по текущему периоду
     *
     * @return Р В Р вЂ¦Р В РЎвЂўР В Р вЂ Р РЋРІР‚в„–Р В РІвЂћвЂ“ Р В РЎвЂ”Р В Р’В»Р В Р’В°Р РЋРІР‚С™Р В Р’ВµР В Р’В¶
     */
    public Payment toPayment() {

        Payment payment = new Payment();
        payment.setIndex(index);
        payment.setDate(endDate);
        payment.setTotal(total);
        payment.setInterest(interest);
        payment.setPrincipal(principal);
        payment.setEndBalance(balance);
        payment.setExtras(extras);
        payment.setRateExtra(rateExtra);
        return payment;
    }

    @Override
    public String toString() {

        return "State [(balance="
               + String.format("%.2f", balance)
               + ", term="
               + String.format("%.0f", term)
               + ", rate="
               + String.format("%.3f", rate)
               + ", ratePerMonth="
               + String.format("%.3f", ratePerMonth)
               + "), notCoveredInterest="
               + String.format("%.2f", notCoveredInterest)
               + "\n\t(index="
               + index
               + ", "
               + (startDate != null ? "startDate=" + startDate + ", " : "")
               + (endDate != null ? "endDate=" + endDate + "), " : ")")
               + "\n\t(total=" + String.format("%.2f", total)
               + ", interest=" + String.format("%.3f", interest)
               + ", principal=" + String.format("%.2f", principal) + ")]";
    }
}
