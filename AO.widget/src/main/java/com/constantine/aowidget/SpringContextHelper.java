package com.constantine.aowidget;

import dao.*;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

public class SpringContextHelper {

    private ApplicationContext context;
    public SpringContextHelper(ServletContext servletContext) {
        context = WebApplicationContextUtils.
                getRequiredWebApplicationContext(servletContext);
    }

    public Object getBean(final String beanRef) {
        return context.getBean(beanRef);
    }

    public UserDAO getUserDAO(final Class<dao.UserDAO> beanRef) { return  (UserDAO)context.getBean(beanRef); }

    public PersonDAO getPersonDAO(final Class<dao.PersonDAO> beanRef) {
        return (PersonDAO)context.getBean(beanRef);
    }

    public TransferDAO getTransferDAO(final Class<dao.TransferDAO> beanRef) { return (TransferDAO)context.getBean(beanRef); }

    public DayOffDAO getDayOffDAO(final Class<dao.DayOffDAO> beanRef) { return (DayOffDAO)context.getBean(beanRef); }

    public TrainingDAO getTrainingDAO(final Class<dao.TrainingDAO> beanRef) { return (TrainingDAO)context.getBean(beanRef); }
}
