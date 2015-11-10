package com.constantine.aowidget;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

public class DateFieldConverterFactory extends DefaultConverterFactory {
    @Override
    public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL>
    createConverter(Class<PRESENTATION> presentationType,
                    Class<MODEL> modelType) {
// Handle one particular type conversion
        if (String.class == modelType &&
                DateFieldConverter.class == presentationType)
            return (Converter<PRESENTATION, MODEL>)
                    new DateFieldConverter();

        // Default to the supertype
        return super.createConverter(presentationType,
                modelType);
    }
}
