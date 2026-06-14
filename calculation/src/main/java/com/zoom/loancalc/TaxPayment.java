package com.zoom.loancalc;

/**
 * Description of TaxPayment
 * Класс для расчета платежей налогового вычета по ипотеке.
 * @author Тачков Дмитрий
 */
public class TaxPayment {
    //ГОд за который нужно заплатить
    public  int year;
    // платеж
    public  double payment;
    //возвращаемый вычет
    public  double returntax;
    // остаток вычета
    public  double restvalue;


    public void setRest(double new_value)
    {
        this.restvalue = new_value;
    }
    public double  getRest()
    {
        return   this.restvalue;
    }

    public void setReturnTax(double taxvalue)
    {
        this.returntax = taxvalue;
    }
    public double  getReturnTax()
    {
        return  returntax;
    }


    public int  getYear()
    {
        return year;
    }

    public double  getPayment()
    {
        return payment;
    }

    public void  setPayment(double p_value)
    {
        this.payment = p_value;
    }

    public void setYear(int new_year){
        this.year = new_year;
    }

}

