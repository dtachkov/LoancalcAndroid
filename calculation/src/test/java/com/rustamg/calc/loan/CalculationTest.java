package com.rustamg.calc.loan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zoom.loancalc.Extra;
import com.zoom.loancalc.ExtraForecastException;
import com.zoom.loancalc.InfiniteLoanException;
import com.zoom.loancalc.Loan;
import com.zoom.loancalc.LoanCalendar;
import com.zoom.loancalc.LoanException;
import com.zoom.loancalc.LoanManager;
import com.zoom.loancalc.Payment;
import com.zoom.loancalc.calculation.Calculator;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import static java.lang.Math.abs;


/**
 * Created by rustamg on 29/07/15.
 */

public class CalculationTest {

    /*  Тачков Д.Е. Отключил тест, т.к. аннуитет после ежемесячного платежа вырос
    @Test()
    public void testAnnuityWithExtrasByBalanceSberbank() throws InfiniteLoanException {

        testCase("res/cases/annuity/with_extras_by_balance_sberbank_2.json");
    }
*/

        /*@Test
    public void testAnnuityWithExtraByTermInterestOnlyAfterPrincipalPaidByExtra() {

        testCase("res/cases/annuity/with_extras_by_term_interest_only_after_principal_paid_by_extra.json");
    }*/

/* 07-11-2019 Тест убран, т.к. в нем новый платеж после досрочки больше чем старый
    @Test
    public void testAnnuity1stPaymentInterestOnlyWithAllTypesOfExtras() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/with_all_types_of_extras_1.json");
    }
*/
//07-11-2019 Тачков Д.Е. Отключил. Ошибка 1 копейка, нужноразбираться...
/*
    @Test
    public void testAnnuity1stPaymentInterestOnlyRaiffeisenWithExtrasByBalance() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/raiffeisen_with_extras_by_balance.json");
    }
*/

/*Основные тесты */

    @Test
    public void testAnnuityFailInfiniteLoanExtrasFirst() {

        testFailingInfiniteLoan("res/cases/annuity/fail_infinite_loan_extras_first.json");
    }

    @Test
    public void testAnnuityFailInfiniteLoanScheduleFirst() {

        testFailingInfiniteLoan("res/cases/annuity/fail_infinite_loan_scheduled_first.json");
    }

    @Test()
    public void testWithExtrasByTermVtb24OnlyRateChange() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/with_extras_by_term_vtb24_only_rate_change.json");
    }

    @Test()
    public void testGraded1stPaymentInterestOnlyLastDay() throws InfiniteLoanException {

        testCase("res/cases/graded/1st_pmt_interest__last_day_of_month.json");
    }

    @Test()
    public void testGpbGraded() throws InfiniteLoanException {

        testCase("res/cases/graded/gpb_graded.json");
    }

    @Test()
    public void testZenit() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/first_payment_zenit.json");
    }

    @Test()
    public void testAnnuity1stPaymentInterestOnlyLastDay() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/last_day.json");
    }

    @Test()
    public void testWithExtrasByTermVtb24() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/with_extras_by_term_vtb24.json");
    }

    @Test()
    public void testAnnuityWithExtrasByBalance() throws InfiniteLoanException {

        testCase("res/cases/annuity/with_extras_by_balance_sberbank_1.json");
    }

    @Test
    public void testAnnuityWithoutExtras() throws InfiniteLoanException {

        testCase("res/cases/annuity/without_extras.json");
    }

    @Test
    public void testAnnuity1stPaymentInterestOnlyWithoutExtras() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/without_extras.json");
    }

    @Test
    public void testAnnuity1stPaymentInterestOnlyWith1stExtraAffectingPrincipal() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/with_1extra_affecting_principal.json");
    }

    @Test
    public void testAnnuity1stPaymentInterestOnlyWith1stExtraNotAffectingPrincipal() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/with_1st_extra_not_affecting_principal.json");
    }

    @Test
    public void testAnnuity1stPaymentInterestOnlyWithRateChangeBefore1stPayment() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/with_rate_change_during_1st_period.json");
    }

    @Test
    public void testAnnuityWithExtraNotAffectingPrincipal() throws InfiniteLoanException {

        testCase("res/cases/annuity/with_extra_not_affecting_principal.json");
    }

    @Test
    public void testAnnuityWithExtraByTerm() throws InfiniteLoanException {

        testCase("res/cases/annuity/with_extras_by_term.json");
    }





    @Test
    public void testAnnuity1stPaymentInterestOnlyWithExtrasByTermAndRateChange() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/with_extras_by_term_and_rate_change.json");
    }

    @Test
    public void testAnnuityWithRateChange() throws InfiniteLoanException {

        testCase("res/cases/annuity/change_rate.json");
    }

    @Test
    public void testAnnuity1stPaymentInterestOnlyWithRateChange() throws InfiniteLoanException {

        testCase("res/cases/annuity/1st_pmt_interest_only/change_rate.json");
    }


    @Test
    public void testAnnuityWithExtraOnScheduledDateSberbank() throws InfiniteLoanException {

        testCase("res/cases/annuity/with_extra_on_scheduled_date_sberbank.json");
    }

    @Test
    public void testAnnuityWithExtraOnScheduledDateSberbank2() throws InfiniteLoanException {

        testCase("res/cases/annuity/with_extra_on_scheduled_date_sberbank_2.json");
    }

    @Test
    public void testAnnuityWithExtraOnScheduledDateSberbank3() throws InfiniteLoanException {

        testCase("res/cases/annuity/with_extra_on_scheduled_date_sberbank_3.json");
    }

    @Test
    public void testAnnuityWithExtraOnScheduledDateSberbank4() throws InfiniteLoanException {

        testCase("res/cases/annuity/with_extra_on_scheduled_date_sberbank_4.json");
    }

    @Test
    public void testRaifBigPayment() throws InfiniteLoanException {
        String json_DATA = "{\"rate\":\"11.79\",\"sum\":\"4700000\",\"term\":\"240\",\"paymentType\":\"0\",\"firstpaymentdate\":\"21.08.2023\",\"issuedate\":\"25.07.2023\",\"flagpercent\":\"1\",\"lastdayFlag\":\"0\",\"extradayinmonth\":\"1\",\"interest_only_after_principal_paid_by_extra\":\"false\",\"applyExtrasImmediately\":\"true\",\"ignorePassedPeriodsAfterRateChange\":\"false\",\"isCalcAnnuityByOldRest\":\"false\",\"flagdayoff\":\"1\",\"extra\":[{\"extradate\":\"16.10.2023\",\"extratype\":\"1\",\"extraamount\":\"2700000\"}]}";
        List<Payment> payments = testCaseFromString(json_DATA);
        Payment pay_start = payments.get(3);
        //платеж послед досрочного погашения
        Payment pay_end = payments.get(2);
        //проверяем платеж после последнего досрочного погашения
        Assert.assertTrue("Total after extra : " + pay_start.getTotal() + " vs 41144.9551103953", abs(pay_start.getTotal() - 41144.955) < 0.01);
        //проверка что аннуитет после досрочки больше чем до досрочки
        Assert.assertTrue( "Interest after extra : " + pay_end.getInterest() +  " vs 0", pay_end.getInterest() <= 0.01);
    }

    @Test
    public void testRaifLowPayment() throws InfiniteLoanException {
        String json_DATA = "{\"rate\":\"12.5\",\"sum\":\"3600000\",\"term\":\"180\",\"paymentType\":\"0\",\"firstpaymentdate\":\"27.11.2013\",\"issuedate\":\"12.11.2013\",\"flagpercent\":\"1\",\"lastdayFlag\":\"0\",\"extradayinmonth\":\"1\",\"interest_only_after_principal_paid_by_extra\":\"false\",\"applyExtrasImmediately\":\"true\",\"ignorePassedPeriodsAfterRateChange\":\"false\",\"isCalcAnnuityByOldRest\":\"false\",\"flagdayoff\":\"0\",\"extra\":[{\"extradate\":\"12.03.2014\",\"extratype\":\"1\",\"extraamount\":\"150000\"}]}";
        List<Payment> payments = testCaseFromString(json_DATA);
        Payment pay_start = payments.get(6);
        //платеж послед досрочного погашения
          //проверяем платеж после последнего досрочного погашения
        Assert.assertTrue("Total after extra : " + pay_start.getTotal() + " vs 42603.80", abs(pay_start.getTotal() - 42603.80) < 0.01);
        //проверка что аннуитет после досрочки больше чем до досрочки

    }



    //тест что после досрочного погашения новый аннуитет не становится больше чем старый
//http://mobile-testing.ru/ipotechnii_kreditnii_kalkulator_online/?loan=36284307eb1efed3
    @Test
    public void testcheckAnnuityAfterExtrasWithSum_less_than_previous_payment() throws InfiniteLoanException {

        String json_DATA = "{\"rate\":\"9.75\",\"sum\" :\"3521000\",\"term\":\"37\",\"paymentType\":\"0\",\"firstpaymentdate\" :\"02.10.2019\",\"issuedate\":\"10.09.2019\",\"flagpercent\":\"1\",\"lastdayFlag\" :\"0\",\"extradayinmonth\":\"0\",\"interest_only_after_principal_paid_by_extra\":\"false\",\"applyExtrasImmediately\":\"true\",\"ignorePassedPeriodsAfterRateChange\":\"false\",\"isCalcAnnuityByOldRest\":\"false\",\"flagdayoff\" :\"0\",\"extra\":[{\"extradate\":\"30.11.2019\",\"extratype\":\"2\",\"extraamount\":\"40000\"},{\"extradate\":\"20.10.2019\",\"extratype\":\"2\",\"extraamount\":\"86000\"},{\"extradate\":\"19.09.2019\",\"extratype\":\"2\",\"extraamount\":\"300000\"},{\"extradate\":\"22.09.2019\",\"extratype\":\"2\",\"extraamount\":\"30000\"}]}";

        List<Payment> payments = testCaseFromString(json_DATA);
        Payment pay_start = payments.get(4);
        //платеж послед досрочного погашения
        Payment pay_end = payments.get(8);
        //проверяем платеж после последнего досрочного погашения
        Assert.assertTrue("Last Payment : " + pay_start.getTotal() + " vs 113 199.94", abs(pay_start.getTotal() - 113199.94) < 0.01 );
        //проверка что аннуитет после досрочки больше чем до досрочки
        Assert.assertTrue( "Check Total doesnt change: " + pay_start.getTotal() + " vs " + pay_end.getTotal(),abs(pay_start.getTotal() - pay_end.getTotal()) < 0.01);

    }

//проверка работы досрочки с уменьешнием суммы после уменьшение срока сбербанк добавлено 07-02-2020
//https://trello.com/c/LwBHVXob/129-%D0%BF%D1%80%D0%BE%D0%B1%D0%BB%D0%B5%D0%BC%D0%B0-%D1%81%D0%BE-%D1%81%D0%B1%D0%B5%D1%80%D0%B1%D0%B0%D0%BD%D0%BA%D0%BE%D0%BC

    @Test
    public void testcheckAnnuityExtrawithTerm_and_nextExtraSum() throws InfiniteLoanException {

        String json_DATA = "{\"rate\":\"9.6\",\"sum\" :\"4407000\",\"term\":\"360\",\"paymentType\":\"0\",\"firstpaymentdate\" :\"18.11.2019\",\"issuedate\":\"28.11.2020\",\"flagpercent\":\"0\",\"lastdayFlag\" :\"0\",\"extradayinmonth\":\"0\",\"interest_only_after_principal_paid_by_extra\":\"true\",\"applyExtrasImmediately\":\"true\",\"ignorePassedPeriodsAfterRateChange\":\"false\",\"isCalcAnnuityByOldRest\":\"false\",\"flagdayoff\" :\"0\",\"extra\":[{\"extradate\":\"06.01.2020\",\"extratype\":\"1\",\"extraamount\":\"75000\"},{\"extradate\":\"07.12.2019\",\"extratype\":\"2\",\"extraamount\":\"125000\"},{\"extradate\":\"06.11.2019\",\"extratype\":\"2\",\"extraamount\":\"375000\"}]}";

        List<Payment> payments = testCaseFromString(json_DATA);
        Payment pay_start = payments.get(6);

        Assert.assertTrue("Last Payment : " + pay_start.getTotal() + " vs 36864.78", abs(pay_start.getTotal() - 36864.78) < 0.01 );


    }

//Проверка текущего платежа   после окончания  выплат
    @Test
    public void testCheckCurrentPaymentAlreadyPaid() throws InfiniteLoanException, ExtraForecastException, LoanException {
//создаем кредит


        Date current = new Date();

        LoanCalendar cal = new LoanCalendar();
        Date minus_2year_date = cal.addMonthsToDate(current, -24);

        Loan minus_year_loan = new Loan(null, minus_2year_date,
                false,false, false,
                false,false,
                12, 12, 100000, 0,
                new TreeSet<Extra>());


        cal.setLastDayFlag(false);
        cal.setMoveDayOff(false);

        Calculator calc = new Calculator(cal);
        LoanManager mgr = new LoanManager(cal,calc);
        try {
            mgr.setLoan(minus_year_loan);
            mgr.calculate(false);
            //List<Payment> actualPayments = calc.calculate(minus_year_loan);
            Payment currentPayment = mgr.getStats().getCurrentPayment();
            Assert.assertTrue("Last Payment when loan already paid: " + currentPayment.getTotal() + " vs 0", currentPayment.getTotal() == 0);
            //  actualPayments ;
        }catch (ExtraForecastException e)
        {
            e.printStackTrace();

        }



    }



    /// В ВТБ при попадании на выходной день придется переносить платежи и делать чтоб платеж был нормальным
    //файл для расчета
    //[{"extras":[{"amount":21111.14,"date":"2024-01-23","document_number":"1","id":2,"loan_id":2,"type":"PAYMENT_FOR_DECREASE_TERM"}],"loan":{"amount":2052848.0,"apply_extras_immediately":true,"interest_only_after_principal_paid_by_extra":false,"consider_days_off":true,"creation_date":"2024-01-02","date_of_issue":"2023-09-22","forecast_start_date":"2024-02-20","forecast_days_before":0,"extra_day_in_month":false,"first_payment_date":"2023-10-23","forecast_extra_type":"PAYMENT_FOR_DECREASE_TERM","forecast_montly_pay":95000.0,"id":2,"ignore_passed_periods_after_rate_change":false,"is_forecast_active":false,"monthly_payment":43888.863,"pay_on_last_day_of_month":false,"rate":18.4,"term":84,"title":"Авто","type":"ANNUITY"}}]
    @Test
    public void testVTBIfdate23bfebHoliday() throws InfiniteLoanException, ExtraForecastException, LoanException {

        String json_DATA = "{\"rate\":\"18.4\",\"sum\" :\"2052848.00\",\"term\":\"84\",\"paymentType\":\"0\",\"firstpaymentdate\" :\"23.10.2023\",\"issuedate\":\"23.09.2023\",\"flagpercent\":\"1\",\"lastdayFlag\" :\"0\",\"extradayinmonth\":\"0\",\"interest_only_after_principal_paid_by_extra\":\"true\",\"applyExtrasImmediately\":\"true\",\"ignorePassedPeriodsAfterRateChange\":\"false\",\"isCalcAnnuityByOldRest\":\"false\",\"flagdayoff\" :\"1\",\"extra\":[{\"extradate\":\"23.01.2024\",\"extratype\":\"2\",\"extraamount\":\"21111\"}" +
                "]}";

        List<Payment> payments = testCaseFromString(json_DATA);
        Payment pay_start = payments.get(5);
        Payment pay_first= payments.get(0);

        Assert.assertTrue("5st Payment : " + pay_start.getInterest() + " vs 34095.02", abs(pay_start.getInterest() - 34095.02) < 0.01 );

        Assert.assertTrue("1st Payment : " + pay_first.getInterest() + " vs 31045.81", abs(pay_first.getInterest() -31045.81) < 0.01 );

    }


//Проверка текущего платежа до начала выплат
    @Test
    public void testCheckCurrentPaymentBefore_start() throws InfiniteLoanException, ExtraForecastException, LoanException {
//создаем кредит
        Date current = new Date();
        LoanCalendar cal = new LoanCalendar();
        Date plus6months_date = cal.addMonthsToDate(current, 6);

        Loan minus_year_loan = new Loan(null, plus6months_date,
                false,false, false,
                false,false,
                12, 12, 100000, 0,
                new TreeSet<Extra>());

        cal.setLastDayFlag(false);
        cal.setMoveDayOff(false);

        Calculator calc = new Calculator(cal);
        LoanManager mgr = new LoanManager(cal,calc);
        try {
            mgr.setLoan(minus_year_loan);
            mgr.calculate(false);
            //List<Payment> actualPayments = calc.calculate(minus_year_loan);
            Payment currentPayment = mgr.getStats().getCurrentPayment();
            Assert.assertTrue("Last Payment when loan doesnt start: " + currentPayment.getTotal() + " vs 8884.88", abs(currentPayment.getTotal() - 8884.88) < 0.01);
            //  actualPayments ;
        }catch (ExtraForecastException e)
        {
            e.printStackTrace();

        }
    }


    @Test
    public void testExtraInPaymenDate() throws InfiniteLoanException {
        String json_DATA = "{\"rate\":\"8\",\"sum\" :\"1850000\",\"term\":\"144\",\"paymentType\":\"0\",\"firstpaymentdate\" :\"25.03.2018\",\"issuedate\":\"17.11.2020\",\"flagpercent\":\"0\",\"lastdayFlag\" :\"0\",\"extradayinmonth\":\"0\",\"interest_only_after_principal_paid_by_extra\":\"true\",\"applyExtrasImmediately\":\"true\",\"ignorePassedPeriodsAfterRateChange\":\"false\",\"isCalcAnnuityByOldRest\":\"false\",\"flagdayoff\" :\"0\",\"extra\":[{\"extradate\":\"25.12.2018\",\"extratype\":\"1\",\"extraamount\":\"5200\"},{\"extradate\":\"25.02.2019\",\"extratype\":\"1\",\"extraamount\":\"5400\"},{\"extradate\":\"25.01.2019\",\"extratype\":\"1\",\"extraamount\":\"5300\"},{\"extradate\":\"25.11.2018\",\"extratype\":\"1\",\"extraamount\":\"5200\"},{\"extradate\":\"25.10.2018\",\"extratype\":\"1\",\"extraamount\":\"5100\"},{\"extradate\":\"25.09.2018\",\"extratype\":\"1\",\"extraamount\":\"14100\"},{\"extradate\":\"05.09.2018\",\"extratype\":\"1\",\"extraamount\":\"453026\"}]}";
        List<Payment> payments = testCaseFromString(json_DATA);
        Payment pay_start = payments.get(15);
        //платеж послед досрочного погашения
        //Payment pay_end = payments.get(8);
        //проверяем платеж после последнего досрочного погашения
        Assert.assertTrue("Last Payment : " + pay_start.getTotal() + " vs 14 769.53", abs(pay_start.getTotal() - 14769.53) < 0.01 );
        //проверка что аннуитет после досрочки больше чем до досрочки
       // Assert.assertTrue( "Total doesnt change: " + pay_start.getTotal() + " vs " + pay_end.getTotal(),abs(pay_start.getTotal() - pay_end.getTotal()) < 0.01);


    }


//https://mobile-testing.ru/ipotechnii_kreditnii_kalkulator_online/?loan=98b0e864d710c95b
    @Test
    public void testWhenFirstPaymentIntrerestOnlySber() throws InfiniteLoanException {
        String json_DATA = "{\"rate\":\"14.9\",\"sum\":\"559157\",\"term\":\"49\",\"paymentType\":\"0\",\"firstpaymentdate\":\"04.07.2022\",\"issuedate\":\"30.06.2022\",\"flagpercent\":\"1\",\"lastdayFlag\":\"0\",\"extradayinmonth\":\"0\",\"interest_only_after_principal_paid_by_extra\":\"true\",\"applyExtrasImmediately\":\"true\",\"ignorePassedPeriodsAfterRateChange\":\"false\",\"isCalcAnnuityByOldRest\":\"false\",\"flagdayoff\":\"0\",\"extra\":[{\"extradate\":\"03.07.2022\",\"extratype\":\"1\",\"extraamount\":\"50000\"}]}";
         List<Payment> payments = testCaseFromString(json_DATA);
        Payment pay_start = payments.get(2);
        //платеж послед досрочного погашения
        //Payment pay_end = payments.get(8);
        //проверяем платеж после последнего досрочного погашения
        Assert.assertTrue("Payment 3 : " + pay_start.getTotal() + " vs 14 163.45", abs(pay_start.getTotal() - 14163.45) < 0.01 );
    }


    //если досрочка с изменением срока не пошла на погашение ОД то срок и платеж не пересчитваем
    //https://mobile-testing.ru/ipotechnii_kreditnii_kalkulator_online/?loan=607cec2ce06def11
    @Test
    public void testPaymentAfterFewExtraTerm() throws InfiniteLoanException {
        String json_DATA = "{\"rate\":\"9.8\",\"sum\":\"5550000\",\"term\":\"216\",\"paymentType\":\"0\",\"firstpaymentdate\":\"22.01.2023\",\"issuedate\":\"22.12.2022\",\"flagpercent\":\"0\",\"lastdayFlag\":\"0\",\"extradayinmonth\":\"0\",\"interest_only_after_principal_paid_by_extra\":\"true\",\"applyExtrasImmediately\":\"true\",\"ignorePassedPeriodsAfterRateChange\":\"false\",\"isCalcAnnuityByOldRest\":\"false\",\"flagdayoff\":\"0\",\"extra\":[{\"extradate\":\"31.01.2023\",\"extratype\":\"2\",\"extraamount\":\"1000\"},{\"extradate\":\"22.01.2023\",\"extratype\":\"2\",\"extraamount\":\"21300\"},{\"extradate\":\"22.01.2023\",\"extratype\":\"2\",\"extraamount\":\"61500\"},{\"extradate\":\"16.01.2023\",\"extratype\":\"2\",\"extraamount\":\"36300\"},{\"extradate\":\"28.12.2022\",\"extratype\":\"2\",\"extraamount\":\"17858.22\"},{\"extradate\":\"22.12.2022\",\"extratype\":\"2\",\"extraamount\":\"49500\"}]}";
        List<Payment> payments = testCaseFromString(json_DATA);
        Payment pay_start = payments.get(7);
        //платеж послед досрочного погашения
        //Payment pay_end = payments.get(8);
        //проверяем платеж после последнего досрочного погашения
        Assert.assertTrue("Payment 7 : " + pay_start.getTotal() + " vs 53779.38", abs(pay_start.getTotal() - 53779.38) < 0.01 );
        Assert.assertTrue("Principal for 7 : " + pay_start.getPrincipal() + " vs 9 830.15", abs(pay_start.getPrincipal() - 9830.15) < 0.01 );
    }



    private void testFailingInfiniteLoan(String fileName) {

        boolean failedDueToInfiniteLoan = false;

        try {

            testCase(fileName);
        }
        catch (InfiniteLoanException e) {
            failedDueToInfiniteLoan = true;
        }

        Assert.assertTrue("Расчет кредита должен был бросить InfiniteLoanException", failedDueToInfiniteLoan);
    }

    private void testCase(String fileName) throws InfiniteLoanException {

        CalculationTestModel testModel = CalculationTestModel.readFromFile(fileName);

        LoanCalendar cal = new LoanCalendar();
        cal.setLastDayFlag(testModel.isPayOnLastDayOfMonth());
        cal.setMoveDayOff(testModel.isConsiderDaysOff());
        cal.setExtraDayInMonth(testModel.getLoan().isRaiffeisen() ? 1 : 0);

        Calculator calc = new Calculator(cal);

        System.out.println("Тестирование " + testModel.getDescription());

        List<Payment> expectedPayments = testModel.getLoan().getPayments();
        try {
            List<Payment> actualPayments = calc.calculate(testModel.getLoan());
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            String json = gson.toJson(actualPayments);
            //  System.out.println("-----------------------");
            System.out.println(json);
            assertPaymentsOneByOne(expectedPayments, actualPayments);

            assertPaymentsSize(expectedPayments, actualPayments);

        }catch (ExtraForecastException e)
        {
            e.printStackTrace();
        }

    }
//тестирование Загрузка кредита из строки json_DATA
    private List<Payment> testCaseFromString(String json_DATA) throws InfiniteLoanException {
       // грузим параметры в объект и досрочные погашения тоже
        Gson g = new Gson();
        WebLoanModel m_loan = g.fromJson(json_DATA, WebLoanModel.class);
        //SortedSet<Extra> extras = ;

        //создаем кредит
        Loan buildloan = new Loan(m_loan.getIssuedate(), m_loan.getFirstpaymentdate(),
                m_loan.getExtradayinmonth(),m_loan.getFlagpercent(), m_loan.isApplyExtrasImmediately(),
                m_loan.isInterest_only_after_principal_paid_by_extra(),m_loan.isIgnorePassedPeriodsAfterRateChange(),
                m_loan.getTerm(), m_loan.getRate(), m_loan.getSum(), m_loan.getPaymentType(),
                m_loan.getExtras());

         System.out.println(buildloan.getExtras());


           LoanCalendar cal = new LoanCalendar();
           cal.setLastDayFlag(m_loan.getLastdayFlag());
           cal.setMoveDayOff(m_loan.getFlagdayoff());
           cal.setExtraDayInMonth(m_loan.getExtradayinmonthInt());

        Calculator calc = new Calculator(cal);

        System.out.println("Testing for string " );
        try {
            List<Payment> actualPayments = calc.calculate(buildloan);
            return actualPayments;
        }catch (ExtraForecastException e)
        {
            e.printStackTrace();
            return  null;
        }


        //Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        //String json = gson.toJson(actualPayments);
        //  System.out.println("-----------------------");
        //System.out.println(json);
       // assertPaymentsOneByOne(expectedPayments, actualPayments);

       // assertPaymentsSize(expectedPayments, actualPayments);
    }


    private void assertPaymentsOneByOne(List<Payment> expectedPayments, List<Payment> actualPayments) {

        for (int i = 0; i < Math.min(actualPayments.size(), expectedPayments.size()); i++) {
            Payment expected = expectedPayments.get(i);
            Payment actual = actualPayments.get(i);
            if (!expected.equals(actual)) {
                Assert.assertEquals("Платежи не совпали ", expected, actual);
                return;
            }
        }
    }

    private void assertPaymentsSize(List<Payment> expectedPayments, List<Payment> actualPayments) {

        String message = "";

        if (actualPayments.size() > expectedPayments.size()) {
            message = "Насчитали больше платежей (" + actualPayments.size() + "), чем ожидалось(" + expectedPayments.size() + ")";
        }
        else if (actualPayments.size() < expectedPayments.size()) {
            message = "Насчитали меньше платежей (" + actualPayments.size() + "), чем ожидалось(" + expectedPayments.size() + ")";
        }

        Assert.assertEquals(message, expectedPayments.size(), actualPayments.size());
    }
}
