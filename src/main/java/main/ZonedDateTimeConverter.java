package main;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeConverter extends AbstractBeanField<ZonedDateTime, ZonedDateTime> {

    @Override
    protected ZonedDateTime convert(String value)
        throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(value, formatter);

        return ldt.atZone(ZoneId.of("UTC"));
    }
}
