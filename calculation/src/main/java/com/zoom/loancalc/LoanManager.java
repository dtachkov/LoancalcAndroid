package com.zoom.loancalc;

import com.zoom.loancalc.calculation.Calculator;

import java.awt.image.AreaAveragingScaleFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ru.loan.forecast.CalculatorForecast;


/**
 * Класс для загрузки, выгрузки кредитов, а также расчета платежей по кредиту. Этот класс является основным классом для
 * взаимодействия.Для того чтобы рассчитать платежи по кредиту, необходимо создать кредит, загрузить его в данный класс,
 * а затем рассчитать. Затем можно добавлять досрочные погашения. В этом случае, при добавлении каждого погашения
 * пересчитывается статистика и платежи по кредиту, чтобы выяснить экономию от доспрочного платежа. Пример работы с
 * классом:
 * <p/>
 * <pre>
 * LoanCalendar cal = new LoanCalendar();
 * LoanManager mgr = new LoanManager();
 *
 * Loan loan = new Loan(null, cal.date(2012, 12, 29), Loan.FIRST_PAYMENT_PLAIN,
 *         12, 11, 10000, Loan.ANNUITY);
 * // Подготовка к расчету mgr.setLoan(loan);
 * // Расчет платежей
 * mgr.calculate();
 *
 * // Каждое добавление досрочного погашения перерасчитывает все платежи.
 * double totalSavings = 0;
 * totalSavings += mgr.addExtra(new Extra(100, Extra.BALANCE, cal
 *         .date(2013, 1, 22)));
 * totalSavings += mgr.addExtra(new Extra(7, Extra.RATE, cal.date(2013, 1, 22)));
 * totalSavings += mgr
 *         .addExtra(new Extra(1000, Extra.TERM, cal.date(2013, 1, 22)));
 *
 * // Получение результатов
 * List&lt;Payments&gt; payments = mgr.getPayments();
 * Stats stats = mgr.getStats();
 * </pre>
 * <p/>
 * Если расчет экономии не нужен, то можно рассчитывать ежемесячные платежи только один раз. В этом случае менеджер не
 * нужен:
 * <p/>
 * <pre>
 * LoanCalendar cal = new LoanCalendar();
 * Calculator calc = new Calculator(cal);
 *
 * SortedSet&lt;Extra&gt; extras = new TreeSet&lt;Extra&gt;();
 *
 * extras.add(new Extra(100, Extra.BALANCE, cal.date(2013, 1, 22)));
 * extras.add(new Extra(7, Extra.RATE, cal.date(2013, 1, 22)));
 * extras.add(new Extra(1000, Extra.TERM, cal.date(2013, 1, 22)));
 *
 * Loan loan = new Loan(null, cal.date(2012, 12, 29), Loan.FIRST_PAYMENT_PLAIN,
 *         12, 11, 10000, Loan.ANNUITY, extras);
 *
 * List&lt;Payment&gt; payments = calc.calculate(loan);
 *
 * for (Payment payment : payments) {
 *     System.out.println(payment);
 * }
 * </pre>
 * <p/>
 * Однако этот способ не проверяет параметры кредита на верность. В случае отсутствия какого-либо из обязательных
 * параметров (суммы кредита, ставки или срока кредита) алгоритм расчета просто ничего не расчитает и вернет пустой
 * результат.
 *
 * @author Dmitry Bespalov dmitry@zoomlabs.co
 */
public class LoanManager  implements Cloneable {


    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /**
     * Разделитель полей CSV данных
     */
    private static final String CSV_SEPARATOR = "|";
    //максимальная сумма покупки с которой берется налоговый вычет
    private static  double MAX_OBJECT_PRICE_FOR_TAX = 2000000;
    private static  double MAX_INTEREST_CAN_RETURN = 390000;


    public static  double TAX_RATE = 0.13;
    /**
     * Календарь для расчета дат
     */
    private LoanCalendar cal;
    /**
     * Калькулятор кредитов для расчета ежемесячных платежей
     */
    private Calculator calc;
    //для прогноза досрочного погашения

    private CalculatorForecast forecast;

    /**
     * Форматировщик дат
     */
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * Текущий загруженный кредит
     */
    private Loan loan;
    /**
     * Текущая статистика (включая текущий платеж) по загруженному кредиту
     */
    private Stats stats;
    private SortedSet<Extra> extraList = new TreeSet<Extra>();

    //цена объекта пока не сериализуем чтоб ничего не поломалось
    private double objectPrice;

    //стоимость объекта
    public double getObjectPrice() {
        return objectPrice;
    }

    public void setObjectPrice(double objectPrice) {
        this.objectPrice = objectPrice;
    }


    /// вычисление налогового вычета
    private void computeTaxPayments() //float salary
    {
        ArrayList <TaxPayment> taxpays = new ArrayList<>();
        List<Payment> pays = loan.getPayments();
        Date first_date = pays.get(0).getDate();

        Calendar calendar = Calendar.getInstance();
        //получаем начальный год за который считать
        calendar.setTime(first_date);
        int currentyear = calendar.get(Calendar.YEAR);

        double m_currpay = 0;
        boolean flag = true;
        double  balance = stats.getPrincipialTax();

        for(int i = 0; i < pays.size(); i++)
        {
            Payment pay = pays.get(i);
            Date m_date = pay.getDate();

            calendar.setTime(m_date);

            int m_year =  calendar.get(Calendar.YEAR);

            if(m_year == currentyear)
            {
                m_currpay = m_currpay + pay.getInterest();
                flag = true;

            }  else {

                TaxPayment tax = new TaxPayment();
                tax.setYear(currentyear);
                tax.setPayment(m_currpay);
                tax.setReturnTax( TAX_RATE * m_currpay);
                balance = balance + tax.getReturnTax();
                tax.setRest(balance);
                m_currpay = pay.getInterest();
                currentyear = m_year;

                taxpays.add(tax);
            }
        }

        // echo 'Добавлии 2';
        TaxPayment tax = new TaxPayment();
        tax.setYear(currentyear);
        // $m_currpay = $pay->getInterest();
        tax.setPayment(m_currpay);
        tax.setReturnTax(TAX_RATE * m_currpay);
        balance = balance + tax.getReturnTax();
        tax.setRest(balance);
        taxpays.add(tax);
        //отправляем платежи в статистику
        stats.setTaxPayments(taxpays);
    }



    /// налоговый вычет по сумме кредита и процентам. Расчет
    /*
     * 1. Определяем максимум стоимости квартиры и кредита
     * 2. Вычисляем вычет с суммы
     * 3. Вычисляем вычет с процентов
     */
    private void computeTax()
    {
        // считаем по сумме 1. Вычисляем максимум из стоимости квартиры и суммы покупки
        double forcompare = Math.max(loan.getAmount(), getObjectPrice());
        if(forcompare <= MAX_OBJECT_PRICE_FOR_TAX)
        {
            stats.setPrincipialTax(forcompare * TAX_RATE);
        }
        else {
           stats.setPrincipialTax(MAX_OBJECT_PRICE_FOR_TAX * TAX_RATE);
        }
        // считаем по процентам. - 13 процентов от выплаченных процентов
        stats.setInterestTax(stats.getInterest() *  TAX_RATE);
        //с 2014 года можно получить вычет максимально с 3 млн.
        //2. В сумме фактически произведенных расходов на погашение процентов по ипотеке (подп. 4 п. 1 ст. 220 НК РФ), но не более 3 млн руб. (п. 4 ст. 220 НК РФ).

        if(stats.getInterestTax() > MAX_INTEREST_CAN_RETURN)
        {
            stats.setInterestTax(MAX_INTEREST_CAN_RETURN);
        }

    }


    public LoanManager(LoanCalendar calendar, Calculator calculator) {

        cal = calendar;
        calc = calculator;
        forecast = new CalculatorForecast(cal);
    }

    public double addExtra(Extra extra) throws Exception {

        if (extra != null) {
            if (loan != null && stats != null) {
                Stats statsBeforeExtra = stats;

                if (loan.getExtras() == null) {
                    loan.setExtras(new TreeSet<Extra>());
                }
                extraList.add(extra);
                loan.getExtras().add(extra);
                calculate(false);

                Stats statsAfterExtra = stats;

                extra.setSavings(
                        statsBeforeExtra.getInterest() - statsAfterExtra.getInterest());

                return extra.getSavings();
            }
            else {
                throw new LoanException("Данное действие возможно только для "
                                        + "рассчитанного кредита. Пожалуйста, "
                                        + "загрузите или рассчитайте кредит.");
            }
        }
        else {
            throw new InvalidExtraException(
                    "Некорректный дополнительный платеж");
        }
    }


    public void calculate(boolean is_forecast) throws ExtraForecastException, LoanException {

        if (loan != null) {

            if (loan.getPayments() == null) {
                if(!is_forecast) {
                    loan.setPayments(calc.calculate(loan));
                }else
                {
                    try {
                        loan.setPayments(forecast.calculate(loan));
                    }catch (ExtraForecastException e)
                    {
                        throw e;
                    }
                }

            }
            stats = computeStats();
            // статистика по налогам только после расчета всей статистики
            computeTax();
            computeTaxPayments();

        }
        else {
            throw new LoanException("Пожалуйста, загрузите кредит");
        }
    }

    public Loan getLoan() {

        return loan;
    }


    public List<Payment> getPayments() {

        if (loan != null) {
            return loan.getPayments();
        }
        else {
            return new ArrayList<Payment>();
        }
    }

    public Stats getStats() {

        return stats;
    }


    public void setLoan(Loan loan) throws LoanException {

        if (loan != null) {

            String error = null;
            if (loan.getAmount() < Payment.EPS) {
                error = "Введите сумму кредита";
            }
            else if (loan.getRate() < Payment.P_EPS) {
                error = "Введите процентную ставку";
            }
            else if (loan.getTerm() < 1) {
                error = "Задайте срок кредита";
            }

            if (error != null) {
                throw new LoanException(error);
            }
        }
        this.loan = loan;
    }


    public String toCSVString() {

        StringBuilder sb = new StringBuilder();

        // Заголовок
        sb.append(String
                .format("№%1$Дата платежа%1$Сумма платежа%1$Основной долг%1$Начисленные проценты%1$Остаток долга%1$Досрочное погашение\n",
                        CSV_SEPARATOR));

        if (loan != null && loan.getPayments() != null && stats != null) {
            // Статистика
            sb.append(CSV_SEPARATOR).append(CSV_SEPARATOR);
            sb.append(stats.getTotal());
            sb.append(CSV_SEPARATOR);
            sb.append(loan.getAmount());
            sb.append(CSV_SEPARATOR);
            sb.append(stats.getInterest());
            sb.append(CSV_SEPARATOR);
            sb.append(loan.getAmount());
            sb.append(CSV_SEPARATOR);
            sb.append(stats.getExtras());
            sb.append("\n");

            // Собственно платежи
            for (Payment p : loan.getPayments()) {
                sb.append(p.getIndex() + 1);
                sb.append(CSV_SEPARATOR);
                sb.append(dateFormat.format(p.getDate()));
                sb.append(CSV_SEPARATOR);
                sb.append(p.getTotal());
                sb.append(CSV_SEPARATOR);
                sb.append(p.getPrincipal());
                sb.append(CSV_SEPARATOR);
                sb.append(p.getInterest());
                sb.append(CSV_SEPARATOR);
                sb.append(p.getEndBalance());
                sb.append(CSV_SEPARATOR);
                sb.append(p.getExtraString());
                sb.append("\n");
            }
        }

        return sb.toString();
    }


    private Stats computeStats() {

        Stats stats = new Stats();
        Date currentDate = cal.truncateTime(new Date());
        // Статистика по платежам
        if (loan.getPayments() != null) {
            stats.setCurrentPayment(null);


            stats.setOwingAmount(loan.getAmount());

            for (int i = 0; i < loan.getPayments().size(); i++) {
                Payment p = loan.getPayments().get(i);

                stats.setTotal(stats.getTotal() + p.getTotal());
                stats.setInterest(stats.getInterest() + p.getInterest());
                stats.setExtras(stats.getExtras() + p.getExtras());
                stats.setPrincipal(stats.getPrincipal() + p.getPrincipal());

                if (stats.getCurrentPayment() == null && p.getDate().after(currentDate)) {
                    stats.setCurrentPayment(p);
                }

                if (p.getDate().before(currentDate)) {
                    stats.setOwingAmount(p.getEndBalance());
                    stats.setAlreadyPaidPrincipal(stats.getAlreadyPaidPrincipal() + p.getPrincipal());
                    stats.setAlreadyPaidInterest(stats.getAlreadyPaidInterest() + p.getInterest());
                }
            }
        }

        if (stats.getCurrentPayment() == null) {
            //если не нашли платеж который больше текущей даты то смотрим является ли текущая дата меньше даты первого платежа
            //если да то  берем ее если нет то 0
            if(loan.getPayments().get(0).getDate().after(currentDate)) {
                stats.setCurrentPayment(loan.getPayments().get(0));
            }else
            {
                stats.setCurrentPayment(new Payment());
            }
        }

        if (loan.isApplyExtrasImmediately()) {
            stats.setEndBalance(stats.getPrincipal());
        }
        else {
            stats.setEndBalance(stats.getPrincipal() + stats.getExtras());
        }

        computeExtrasStats(stats);

        // Проверка на вшивость
        if (stats.getEndBalance() - loan.getAmount() >= Payment.EPS) {
            System.err.println("Внимание: сумма платежей в счет "
                               + "основного долга расходится с суммой кредита");
        }




        return stats;
    }

    private void computeExtrasStats(Stats stats) {

        // Статистика по дополнительным платежам
        if (loan.getExtras() != null) {
            for (Extra extra : loan.getExtras()) {
                switch (extra.getType()) {
                    case Extra.FEE:
                        stats.setFees(stats.getFees() + extra.getValue());
                        break;

                    case Extra.INSURANCE:
                        stats.setInsurance(stats.getInsurance()
                                           + extra.getValue());
                        break;

                    default:
                        break;
                }
            }
        }
    }
}
