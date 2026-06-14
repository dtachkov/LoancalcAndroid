package com.zoom.loancalc;

import java.util.Calendar;
import java.util.Date;


public final class LoanCalendar {

    public static final boolean DO_NOT_IGNORE_NEW_YEAR = false;
    public static final boolean IGNORE_NEW_YEAR = true;

    private Calendar cal = Calendar.getInstance();
    /**
     * флаг последний день недели и перенос с выходных
     */
    private boolean moveDayOff;

    /**
     * флаг ежемесячного платежа в последний день месяца
     */
    private boolean lastDayFlag;

    /**
     * флаг Считать, как Райффайзен
     */
    private int mExtraDayInMonth;

    public int getExtraDayInMonth() {

        return mExtraDayInMonth;
    }

    public void setExtraDayInMonth(int extraDayInMonth) {

        mExtraDayInMonth = extraDayInMonth;
    }

    public void setMoveDayOff(boolean newvalue) {

        moveDayOff = newvalue;
    }

    public boolean getMoveDayOff() {

        return moveDayOff;
    }

    /**
     * флаг ежемесячного платежа в последний день месяца
     *
     * @param newvalue
     */
    public void setLastDayFlag(boolean newvalue) {

        lastDayFlag = newvalue;
    }

    /**
     * флаг ежемесячного платежа в последний день месяца
     */
    public boolean getLastDayFlag() {

        return lastDayFlag;
    }


    public Date truncateTime(Date date) {

        if (date != null) {
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            date = cal.getTime();
        }

        return date;
    }

    /**
     * добавление к дате х месяцев и указание последнего дня месяца
     *
     * @param dateValue
     * @param x
     *
     * @return
     */
    public Date lastDayXMonth(Date dateValue, int x) {

        cal.setTime(dateValue);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, x);
        // получаем последний день месяца
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        return cal.getTime();
    }

    public Date  addDaysToDate(Date date, int days)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return  c.getTime();
    }

    private Date addMonthsToDateHoliday(Date date, int months) {

        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        int[][] holidaysRF = {{23,2}, {8,3},{1,5}, {9,5},{12,6}};
        //если вдруг приходится на выходной в РФ то делаем так чтоб добавлялся 1 день
        for (int[] day : holidaysRF
             ) {
            if(cal.get(Calendar.DAY_OF_MONTH) ==  day[0] &&  (day[1]-1)  == cal.get(Calendar.MONTH) )
            {
                cal.add(Calendar.DATE, 1);
                break;
            }

        }

        //есть еще случай вТБ там учитываются российские праздники то есть если пятница 23 февраля то добавляется 2 дня к дате платежа
        //если день недели выпадает на праздник то добавляем единицу


        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // в случае райффайзен суббота не выходной день, добавлять ничего не нужно
        //поэтому просто добавляем месяц
        if ((dayOfWeek == Calendar.SATURDAY) && (mExtraDayInMonth < 1)) {
            cal.add(Calendar.DATE, 2);
        } else  if (dayOfWeek == Calendar.SUNDAY) {
            cal.add(Calendar.DATE, 1);
        }




        return cal.getTime();
    }


    public Date monthBeforeDate(Date date) {

        return addMonthsToDate(date, -1);
    }


    public Date addMonthsToDate(Date date, int months) {
        date = truncateTime(date);
        Date resultdate = null;
        if (date != null) {

            if (getMoveDayOff()) {
                resultdate = addMonthsToDateHoliday(date, months);
            }
            else {
                //проверяем флаг последний день месяца
                if (getLastDayFlag()) {
                    resultdate = lastDayXMonth(date, months);
                }
                else {
                    // если флаг переноса на последний день месяца не установлен
                    cal.setTime(date);
                    int day_of_month_start = cal.get(Calendar.DAY_OF_MONTH);
                    cal.add(Calendar.MONTH, months);
                    //добавляем число месяцев
                    resultdate = cal.getTime();
                    int day_of_month_end = cal.get(Calendar.DAY_OF_MONTH);
                    //если это 30 января то перенос даты на 28 или 29
                    // тут по идее должно быть еще условие что считаем только для Сбербанка, но пока его нет
                    if( (day_of_month_start == 30) && ((day_of_month_end == 1) || (day_of_month_end == 2)) )
                    {
                        //добавляем 1 месяц получаем первое
                        //$resultdate = $this->dateAdd('m',$months,$date);
                        //минус 1 день чтоб получить 29 или 28 февраля
                        int minus_day = day_of_month_end;
                        //отнимаем нужное число дней
                        cal.add(Calendar.DATE, -minus_day);
                        // и снова задаем дату
                        resultdate = cal.getTime();
                        //echo $resultdate;
                        // echo $resultdate->format('Y-m-d');
                    }


                }
            }
        }

        return resultdate;
    }


    public void setDayToDate(Date dstDate, Date srcDate) {

        if (srcDate != null && dstDate != null) {
            cal.setTime(srcDate);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            cal.setTime(dstDate);
            cal.set(Calendar.DAY_OF_MONTH, day);
            dstDate.setTime(cal.getTimeInMillis());
        }
    }



    public double daysProportionInYearBetweenDates(Date fromDate, Date toDate, boolean ignoreNewYear) {

        double timeDifference = toDate.getTime() - fromDate.getTime();
        Calendar dateToCalendar = Calendar.getInstance();
        dateToCalendar.setTime(toDate);
        timeDifference /= 60 * 60 * 24 * 1000;

        double daysDifference = Math.floor(timeDifference + 0.5f);

        // ASSUMING THAT DATES DIFFER ONLY FOR 1 MONTH!!!
        double daysInCurrentDateYear, daysInPreviousDateYear, proportion;

        cal.setTime(fromDate);
        daysInPreviousDateYear = cal.getActualMaximum(Calendar.DAY_OF_YEAR);

        if ((ignoreNewYear || cal.get(Calendar.MONTH) != Calendar.DECEMBER)
            || ((cal.get(Calendar.MONTH) == dateToCalendar.get(Calendar.MONTH))
                && (cal.get(Calendar.MONTH) == Calendar.DECEMBER))) {

            proportion = daysDifference / daysInPreviousDateYear;
        }
        else {

            double daysPrevious = 31 - cal.get(Calendar.DAY_OF_MONTH) + mExtraDayInMonth;

            cal.setTime(toDate);
            daysInCurrentDateYear = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
            double daysCurrent = cal.get(Calendar.DAY_OF_MONTH) - mExtraDayInMonth;

            proportion = daysCurrent / daysInCurrentDateYear + daysPrevious / daysInPreviousDateYear;
        }

        return proportion;
    }

    /*
     * @param year

     * @param month

     * @param day

     */
    public Date date(int year, int month, int day) {

        cal.set(year, month - 1, day);

        return truncateTime(cal.getTime());
    }
}
