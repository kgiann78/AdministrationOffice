package com.constantine.aowidget.Tabs;

import com.constantine.aowidget.DateFieldConverter;
import com.constantine.aowidget.FieldName;
import com.constantine.aowidget.Helper;
import com.constantine.aowidget.SpringContextHelper;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import dao.PersonDAO;
import model.Person;
import model.PersonData;
import model.Training;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class EditorTab extends VerticalLayout implements TabElement {
    private static Logger logger = Logger.getLogger(EditorTab.class);

    private Button removePersonButton = new Button("Διαγραφή Καρτέλας");
    private Button savePersonButton = new Button("Αποθήκευση");

    private FormLayout editorLayout = new FormLayout();
    private FieldGroup editorFields = new FieldGroup();
    private static final FieldName[] fieldNames = new FieldName[]{FieldName.ID, FieldName.NAME, FieldName.SURNAME,
            FieldName.BIRTHDAY, FieldName.AGM, FieldName.MOBILE, FieldName.PHONE, FieldName.EMAIL, FieldName.ADDRESS};

    SpringContextHelper context = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
    final PersonDAO personDAO = context.getPersonDAO(PersonDAO.class);

    @Override
    public void initialize() {
        SpringContextHelper context = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
        final PersonDAO personDAO = context.getPersonDAO(PersonDAO.class);

        editorLayout.addComponent(removePersonButton);

		/* User interface can be created dynamically to reflect underlying data. */
        for (FieldName fieldName : fieldNames) {
            if (fieldName == FieldName.ID) continue;

            if (fieldName != FieldName.BIRTHDAY) {
                TextField field = new TextField(fieldName.toString());
                editorLayout.addComponent(field);
                field.setSizeFull();
                editorFields.bind(field, fieldName);

            } else {
                DateField dateField = new DateField(fieldName.getName());
                dateField.setDateFormat("dd-MM-yyyy");
                dateField.setConverter(new DateFieldConverter());
                dateField.setSizeFull();
                editorLayout.addComponent(dateField);
                editorFields.bind(dateField, fieldName);
            }

			/*
             * We use a FieldGroup to connect multiple components to a data
			 * source at once.
			 */
        }

		/*
         * Data can be buffered in the user interface. When doing so, commit()
		 * writes the changes to the data source. Here we choose to write the
		 * changes automatically without calling commit().
		 */
        editorFields.setBuffered(false);

        HorizontalLayout horizontalLayout = new HorizontalLayout(savePersonButton, removePersonButton);
        horizontalLayout.setMargin(true);
        horizontalLayout.setSizeFull();
        editorLayout.addComponent(horizontalLayout);

        this.addComponent(editorLayout);

    }

    public FormLayout getLayout() {
        return editorLayout;
    }

    public FieldGroup getFields() {
        return editorFields;
    }

    public void savePerson(final TransfersTab transfersTab, final DaysOffTab daysOffTab, final TrainingTab trainingTab) {
        savePersonButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Item item = editorFields.getItemDataSource();
                logger.error("ITEM " + item);
                Person person = new Person();
                PersonData personData = new PersonData();

                String id = (String) item.getItemProperty(FieldName.ID).getValue();

                person.setName((String) item.getItemProperty(FieldName.NAME).getValue());
                person.setSurname((String) item.getItemProperty(FieldName.SURNAME).getValue());
                person.setAgm((String) item.getItemProperty(FieldName.AGM).getValue());

                Date date = Helper.tryParse((String) item.getItemProperty(FieldName.BIRTHDAY).getValue());
                person.setBirthday(date);

                personData.setMobile((String) item.getItemProperty(FieldName.MOBILE).getValue());
                personData.setPhone((String) item.getItemProperty(FieldName.PHONE).getValue());
                personData.setEmail((String) item.getItemProperty(FieldName.EMAIL).getValue());
                personData.setAddress((String) item.getItemProperty(FieldName.ADDRESS).getValue());

                person.setPersonData(personData);

                if (id.isEmpty()) {
                    logger.error("Insert new person");
                    item.getItemProperty(FieldName.ID).setValue(Integer.toString(personDAO.create(person)));
                } else {
                    person.setId(Integer.parseInt((String) item.getItemProperty(FieldName.ID).getValue()));
                    logger.error("Update current person");
                    personDAO.update(person);
                }
                editorFields.setItemDataSource(item);
                transfersTab.setUserId(item.getItemProperty(FieldName.ID));
                daysOffTab.setUserId(item.getItemProperty(FieldName.ID));
                trainingTab.setUserId(item.getItemProperty(FieldName.ID));

                transfersTab.setVisibility(true);
                daysOffTab.setVisibility(true);
                trainingTab.setVisibility(true);
            }
        });
    }

    public void removePerson(final Container contactContainer, final Table contactList) {
        removePersonButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Item item = editorFields.getItemDataSource();
                logger.error("Item to be deleted " + contactContainer.getItem(contactList.getValue()));
                String id = (String) item.getItemProperty(FieldName.ID).getValue();
                personDAO.delete(Integer.parseInt(id));

                contactContainer.removeItem(contactList.getValue());
                contactList.removeItem(contactList.getValue());
            }
        });
    }
}
