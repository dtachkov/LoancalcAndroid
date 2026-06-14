package com.zoom.loancalc;

/**
 * Created by rustamg on 15/11/16.
 */

public class InfiniteLoanException extends LoanException {

    /**
     * Конструктор исключения
     */
    public InfiniteLoanException() {

        super("С течением времени долг больше растет, чем уменьшается. Попробуйте уменьшить срок или ставку.");
    }
}
