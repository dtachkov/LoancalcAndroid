package com.zoom.loancalc;

/**
 * Сигнализирует об ошибке
 *
 * @author Dmitry Bespalov dmitry@zoomlabs.co
 */
public class LoanException extends Exception {

    /**
     * Сгенерированный номер сериализации
     */
    private static final long serialVersionUID = 4153346044272918261L;

    /**
     * Конструктор исключения
     *
     * @param message
     *         сообщение
     */
    public LoanException(String message) {

        super(message);
    }
}
