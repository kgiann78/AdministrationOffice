package com.constantine.aowidget;

import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

public class ComboFieldConverter implements Converter<Object, Object> {
    @Override
    public Object convertToModel(Object object, Class<? extends Object> aClass, Locale locale) throws ConversionException {
        if (object == null)
            return "";

        return object;
    }

    @Override
    public Object convertToPresentation(Object s, Class<? extends Object> aClass, Locale locale) throws ConversionException {
        if (s == null || (s instanceof  String && ((String)s).isEmpty()))
            return null;
        return s;
    }

    @Override
    public Class<Object> getModelType() {
        return Object.class;
    }

    @Override
    public Class<Object> getPresentationType() {
        return Object.class;
    }
}
