package main;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class CurrencyConverter extends AbstractBeanField {

    private static final Pattern CRYPTO_CURRENCY_PATTERN = Pattern.compile("(\\d+\\.\\d+)([A-Z]*)");

    @Override
    protected Currency convert(String value)
        throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        var cryptoCurrency = new Currency();
        var matcher = CRYPTO_CURRENCY_PATTERN.matcher(value);
        cryptoCurrency.setAmount(new BigDecimal(matcher.find() ? matcher.group(1) : "0"));
        cryptoCurrency.setCoin(matcher.group(2));
        return cryptoCurrency;
    }
}
