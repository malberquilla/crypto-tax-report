package main;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class TransactionTypeConverter extends
    AbstractBeanField<EnumTransactionType, EnumTransactionType> {

    @Override
    protected EnumTransactionType convert(String value)
        throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        return EnumTransactionType.fromString(value);
    }
}
