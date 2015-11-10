package com.constantine.aowidget;

import com.vaadin.data.util.converter.Converter;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFieldConverter implements Converter<Date, String> {
    private static Logger logger = Logger.getLogger(DateFieldConverter.class);

    @Override
    public String convertToModel(Date date, Class<? extends String> aClass, Locale locale) throws ConversionException {
        if (date == null)
            return "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(date);
    }

    @Override
    public Date convertToPresentation(String s, Class<? extends Date> aClass, Locale locale) throws ConversionException {
        if (s == null || s.isEmpty())
            return null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            logger.error("ConverterToPresentation simpleDateFormat Parse Exception", e);
        }
        return date;
    }

    @Override
    public Class<String> getModelType() {
        return String.class;
    }

    @Override
    public Class<Date> getPresentationType() {
        return Date.class;
    }
}
