package com.constantine.aowidget;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
    private static Logger logger = Logger.getLogger(Helper.class);

    public static Date tryParse(String dateString) {
        String[] formatStrings = {"d-M-y", "d/M/y"};

        for (String formatString : formatStrings) {
            try {
                return new SimpleDateFormat(formatString).parse(dateString);
            } catch (ParseException e) {
                logger.error("Error parsing date format " + formatString);
            }
        }
        return null;
    }
}
