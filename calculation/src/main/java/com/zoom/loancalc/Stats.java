package com.zoom.loancalc;

import java.util.List;

/**
 * Статистика по кредиту.
 */
public class Stats {
    List<TaxPayment> taxPayments;
    /**
     * Сумма всех платежей по кредиту
     */
    private double total;
    /**
     * Суммарное значение выплаченных процентов
     */
    private double interest;
    /**
     * Сумма выплаченного основного долга
     */
    private double principal;

    /**
     * Сумма кредита
     */
    private double endBalance;

    /**
     * Сумма дополнительных платежей типов {@link Extra#BALANCE}, {@link Extra#TERM}
     */
    private double extras;
    /**
     * Сумма дополнительных платежей типа {@link Extra#FEE}
     */
    private double fees;
    /**
     * Сумма дополнительных платежей типа {@link Extra#INSURANCE}
     */
    private double insurance;

    /**
     * Текущий платеж по кредиту
     */
    private Payment currentPayment;

     //для расчета налогового вычета
    private double principialTax;
    private double interestTax;


    public double getTotalReturnTax(){
        return principialTax + interestTax;
    }

    /**
     * Сколько должны банку на текущий момент
     */
    private double owingAmount;

    /**
     * Сколько заплатили банку по основному долгу на текущий момент
     */
    private double alreadyPaidPrincipal;

    /**
     * Сколько заплатили банку процентов на текущий момент
     */
    private double alreadyPaidInterest;


    public void setTaxPayments(List<TaxPayment> taxPayments) {
        this.taxPayments = taxPayments;
    }

    public List<TaxPayment> getTaxPayments() {
        return taxPayments;
    }

    public double getInterestTax() {
        return interestTax;
    }

    public double getPrincipialTax() {
        return principialTax;
    }


    public void setInterestTax(double interestTax) {
        this.interestTax = interestTax;
    }

    public void setPrincipialTax(double principialTax) {
        this.principialTax = principialTax;
    }



    /**
     * Общая переплата банку
     *
     * @return
     */

    public double totalOverpay() {

        return interest + fees + insurance;
    }

    /**
     * Суммарные выплаты по кредиту.
     *
     * @return
     */
    public double getTotal() {

        return total;
    }

    /**
     * Устанавливает суммарные выплаты по кредиту
     *
     * @param total
     */
    public void setTotal(double total) {

        this.total = total;
    }

    /**
     * Суммарные выплаченные проценты по кредиту
     *
     * @return
     */
    public double getInterest() {

        return interest;
    }

    /**
     * Устанавливает суммарные проценты
     *
     * @param interest
     */
    public void setInterest(double interest) {

        this.interest = interest;
    }

    /**
     * Суммарный выплаченный долг по кредиту
     *
     * @return
     */
    public double getPrincipal() {

        return principal;
    }

    /**
     * Устанавливает сумму выплаченного долга
     *
     * @param principal
     */
    public void setPrincipal(double principal) {

        this.principal = principal;
    }

    /**
     * Сумма кредитования
     *
     * @return
     */
    public double getEndBalance() {

        return endBalance;
    }

    /**
     * Устанавливает сумму кредита
     *
     * @param endBalance
     */
    public void setEndBalance(double endBalance) {

        this.endBalance = endBalance;
    }

    /**
     * Сумма дополнительных платежей, повлиявших на расчет кредита (в счет погашения кредита или в счет изменения срока
     * кредитования)
     *
     * @return
     */
    public double getExtras() {

        return extras;
    }

    /**
     * Устанавливает сумму дополнительных платежей по кредиту
     *
     * @param extras
     */
    public void setExtras(double extras) {

        this.extras = extras;
    }

    /**
     * Сумма комиссий по кредиту
     *
     * @return
     */
    public double getFees() {

        return fees;
    }

    /**
     * Устанавливает сумму комиссий по кредиту
     *
     * @param fees
     */
    public void setFees(double fees) {

        this.fees = fees;
    }

    /**
     * Сумма страховых платежей к кредиту
     *
     * @return
     */
    public double getInsurance() {

        return insurance;
    }

    /**
     * Устанавливает сумму страховых платежей по кредиту
     *
     * @param insurance
     */
    public void setInsurance(double insurance) {

        this.insurance = insurance;
    }

    /**
     * Текущий платеж по кредиту (на момент расчета статистики)
     *
     * @return
     */
    public Payment getCurrentPayment() {

        return currentPayment;
    }

    /**
     * Устанавливает текущий платеж по кредиту
     *
     * @param currentPayment
     */
    public void setCurrentPayment(Payment currentPayment) {

        this.currentPayment = currentPayment;
    }

    public double getOwingAmount() {

        return owingAmount;
    }

    public void setOwingAmount(double owingAmount) {

        this.owingAmount = owingAmount;
    }

    public double getAlreadyPaidPrincipal() {

        return alreadyPaidPrincipal;
    }

    public void setAlreadyPaidPrincipal(double alreadyPaidPrincipal) {

        this.alreadyPaidPrincipal = alreadyPaidPrincipal;
    }

    public double getAlreadyPaidInterest() {

        return alreadyPaidInterest;
    }

    public void setAlreadyPaidInterest(double alreadyPaidInterest) {

        this.alreadyPaidInterest = alreadyPaidInterest;
    }
}
