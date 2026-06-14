package com.zoom.loancalc;

/**
 * Исключение, выбрасываемое при добавлении дополнительного платежа к кредиту, когда этот платеж null
 *
 * @author Dmitry Bespalov dmitry@zoomlabs.co
 */
public class InvalidExtraException extends Exception {

    /**
     * Сгенерированный номер сериализации
     */
    private static final long serialVersionUID = 2468027243134475722L;

    /**
     * Конструктор исключения
     *
     * @param message
     *         сообщение
     */
    public InvalidExtraException(String message) {

        super(message);
    }
}
