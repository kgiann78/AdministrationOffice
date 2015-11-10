package com.constantine.aowidget.Tabs;

import com.constantine.aowidget.DateFieldConverter;
import com.constantine.aowidget.FieldName;
import com.constantine.aowidget.SpringContextHelper;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import dao.PersonDAO;
import dao.TrainingDAO;
import model.Person;
import model.Training;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrainingTab extends VerticalLayout implements TabElement {
    private static Logger logger = Logger.getLogger(TrainingTab.class);

    private String person_id = null;
    private Table trainingTable = new Table();
    private IndexedContainer trainingContainer = null;
    private TextField searchField = new TextField();

    private Button createDayOffButton = new Button("Δημιουργία Εκπαίδευσης");
    private Button saveButton = new Button("Αποθήκευση");
    private Button deleteTrainingButton = new Button("Διαγραφή Καρτέλας");

    private FormLayout trainingEditorLayout = new FormLayout();
    private FieldGroup trainingEditorFields = new FieldGroup();

    private static final FieldName[] fieldNames = new FieldName[]{FieldName.ID, FieldName.SCHOOL_NAME, FieldName.START, FieldName.END};

    SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
    PersonDAO personDAO = helper.getPersonDAO(PersonDAO.class);;
    TrainingDAO trainingDAO = helper.getTrainingDAO(TrainingDAO.class);

    @Override
    public void initialize() {
        /* Put a little margin around the fields in the right side editor */
        this.setMargin(true);
        this.setVisible(false);
        trainingTable.setHeight("300px");
        trainingTable.setWidth("100%");
        this.addComponent(trainingTable);
        initSearch();
        initTrainingTable();
        initTrainingEditor();
    }

    private void initSearch() {
        Label editorBlank = new Label();
        editorBlank.setHeight("2em");

		/*
         * We want to show a subtle prompt in the search field. We could also
		 * set a caption that would be shown above the field or description to
		 * be shown in a tooltip.
		 */
        searchField.setInputPrompt("Αναζήτηση εκπαίδευσης");


		/*
         * Granularity for sending events over the wire can be controlled. By
		 * default simple changes like writing a text in TextField are sent to
		 * server with the next Ajax call. You can set your component to be
		 * immediate to send the changes to server immediately after focus
		 * leaves the field. Here we choose to send the text over the wire as
		 * soon as user stops writing for a moment.
		 */
        searchField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.LAZY);

		/*
         * When the event happens, we handle it in the anonymous inner class.
		 * You may choose to use separate controllers (in MVC) or presenters (in
		 * MVP) instead. In the end, the preferred application architecture is
		 * up to you.
		 */
        searchField.addTextChangeListener(new FieldEvents.TextChangeListener() {
            public void textChange(final FieldEvents.TextChangeEvent event) {

				/* Reset the filter for the contactContainer. */
                trainingContainer.removeAllContainerFilters();
                trainingContainer.addContainerFilter(new DaysOffFilter(event
                        .getText()));
            }
        });

        HorizontalLayout bottomRightLayout = new HorizontalLayout();
        bottomRightLayout.setMargin(false);
        bottomRightLayout.setSizeFull();

        searchField.setSizeFull();
        bottomRightLayout.addComponent(searchField);
        bottomRightLayout.addComponent(createDayOffButton);
        this.addComponent(bottomRightLayout);
        this.addComponent(editorBlank);
    }

    private void initTrainingTable() {
        trainingTable.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                Object contactId = trainingTable.getValue();
                /*
                 * When a contact is selected from the list, we want to show
				 * that in our editor on the right. This is nicely done by the
				 * FieldGroup that binds all the fields to the corresponding
				 * Properties in our contact at once.
				 */
                if (contactId != null) {
                    trainingEditorFields.setItemDataSource(trainingTable.getItem(contactId));
                }
                trainingEditorLayout.setVisible(contactId != null);

            }
        });

        createDayOffButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                /*
                 * Rows in the Container data model are called Item. Here we add
				 * a new row in the beginning of the list.
				 */
                trainingContainer.removeAllContainerFilters();
                Object contactId = trainingContainer.addItemAt(0);

				/* Lets choose the newly created contact to edit it. */
                trainingTable.select(contactId);
            }
        });
    }

    private void initTrainingEditor() {

         /* User interface can be created dynamically to reflect underlying data. */
        for (FieldName fieldName : fieldNames) {
            if (fieldName == FieldName.ID) continue;
            switch (fieldName) {
                case SCHOOL_NAME:
                    TextField field = new TextField(fieldName.getName());
                    trainingEditorLayout.addComponent(field);
                    trainingEditorFields.bind(field, fieldName);
                    break;
                case START:
                case END:
                    DateField dateField = new DateField(fieldName.getName());
                    dateField.setDateFormat("dd-MM-yyyy");
                    dateField.setConverter(new DateFieldConverter());
                    trainingEditorLayout.addComponent(dateField);
                    trainingEditorFields.bind(dateField, fieldName);
                    break;
            }
        }

		/*
         * Data can be buffered in the user interface. When doing so, commit()
		 * writes the changes to the data source. Here we choose to write the
		 * changes automatically without calling commit().
		 */
        trainingEditorFields.setBuffered(false);
        trainingEditorLayout.setMargin(true);

        HorizontalLayout horizontalLayout = new HorizontalLayout(saveButton, deleteTrainingButton);
        horizontalLayout.setSizeFull();
        horizontalLayout.setMargin(true);
        trainingEditorLayout.addComponent(horizontalLayout);

        trainingEditorLayout.setVisible(false);
        this.addComponent(trainingEditorLayout);

        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                Item item = trainingContainer.getItem(trainingTable.getValue());
                Training training = new Training();
                String id = (String) item.getItemProperty(FieldName.ID).getValue();
                try {
                    training.setName((String) item.getItemProperty(FieldName.SCHOOL_NAME).getValue());

                    DateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat viewFormat = new SimpleDateFormat("dd-MM-yyyy");

                    String arrival = (String) item.getItemProperty(FieldName.START).getValue();
                    String departure = (String) item.getItemProperty(FieldName.END).getValue();

                    if (arrival != null && !arrival.isEmpty()) {
                        Date date = viewFormat.parse((String) item.getItemProperty(FieldName.START).getValue());
                        training.setArrival(date);
                    }

                    if (departure != null && !departure.isEmpty()) {
                        Date date = viewFormat.parse((String) item.getItemProperty(FieldName.END).getValue());
                        training.setDeparture(date);
                    }

                    if (!id.isEmpty()) {
                        training.setId(Integer.parseInt(id));
                        trainingDAO.update(training);
                    } else {
                        training.setPerson(personDAO.getPerson(Integer.parseInt(person_id)));
                        trainingDAO.create(training);
                    }
                } catch (ParseException e) {
                    logger.error(e);
                }
            }
        });
    }

    private class DaysOffFilter implements Container.Filter {
        private String needle;

        public DaysOffFilter(String needle) {
            this.needle = needle.toLowerCase();
        }

        public boolean passesFilter(Object itemId, Item item) {
            String haystack = ("" + item.getItemProperty(FieldName.DAYOFF_TYPE).getValue() + item
                    .getItemProperty(FieldName.START).getValue() + item
                    .getItemProperty(FieldName.END).getValue()).toLowerCase();
            return haystack.contains(needle);
        }

        public boolean appliesToProperty(Object id) {
            return true;
        }
    }

    public void setVisibility(boolean visible) {
        this.setVisible(visible);
    }

    public void setUserId(Property itemProperty) {
        person_id = itemProperty.getValue().toString();

        try {
            IndexedContainer ic = new IndexedContainer();
            for (FieldName fieldName : fieldNames) {
                ic.addContainerProperty(fieldName, String.class, "");
            }

            Person person = personDAO.getPerson(Integer.parseInt(person_id));
            if (person != null) {
                DateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat viewFormat = new SimpleDateFormat("dd-MM-yyyy");

                for (Training training : person.getTrainings()) {
                    Object id = ic.addItem();
                    ic.getContainerProperty(id, FieldName.ID).setValue(Integer.toString(training.getId()));
                    ic.getContainerProperty(id, FieldName.SCHOOL_NAME).setValue(training.getName());

                    if (training.getArrival() != null) {
                        Date date = databaseFormat.parse(training.getArrival().toString());
                        ic.getContainerProperty(id, FieldName.START).setValue(viewFormat.format(date));
                    }

                    if (training.getDeparture() != null) {
                        Date date = databaseFormat.parse(training.getDeparture().toString());
                        ic.getContainerProperty(id, FieldName.END).setValue(viewFormat.format(date));
                    }
                }
                trainingContainer = ic;


                trainingTable.setContainerDataSource(trainingContainer);
            }
            trainingTable.setContainerDataSource(trainingContainer);
            trainingTable.setVisibleColumns(new FieldName[]{FieldName.SCHOOL_NAME, FieldName.START, FieldName.END});
            trainingTable.setSelectable(true);
            trainingTable.setImmediate(true);
        } catch (NumberFormatException e) {
            logger.debug("Number format exception", e);
        } catch (ParseException e) {
            logger.error(e);
        }
    }

    public void removeTraining() {
        deleteTrainingButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Object objectId = trainingTable.getValue();
                Item item = trainingTable.getItem(objectId);
                logger.error("Item to be deleted " + item);
                String id = (String) item.getItemProperty(FieldName.ID).getValue();
                trainingDAO.delete(Integer.parseInt(id));

                trainingContainer.removeItem(trainingTable.getValue());
                trainingTable.removeItem(trainingTable.getValue());
            }
        });
    }
}
