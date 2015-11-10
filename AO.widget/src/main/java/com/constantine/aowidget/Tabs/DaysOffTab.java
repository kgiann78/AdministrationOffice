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
import dao.DayOffDAO;
import dao.PersonDAO;
import model.DayOff;
import model.Person;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DaysOffTab extends VerticalLayout implements TabElement {
    private static Logger logger = Logger.getLogger(DaysOffTab.class);

    private String person_id = null;

    private Table daysOffTable = new Table();
    private IndexedContainer daysOffContainer = null;
    private TextField searchField = new TextField();

    private Button createDayOffButton = new Button("Δημιουργία Αδείας");
    private Button saveButton = new Button("Αποθήκευση");
    private Button deleteDayOffButton = new Button("Διαγραφή Καρτέλας");
    private Button hello = new Button("Hello");

    private FormLayout daysOffEditorLayout = new FormLayout();
    private FieldGroup daysOffEditorFields = new FieldGroup();

    private static final FieldName[] fieldNames = new FieldName[]{FieldName.ID, FieldName.DAYOFF_TYPE, FieldName.DEPARTURE, FieldName.DAYOFF_ARRIVAL};

    SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
    PersonDAO personDAO = helper.getPersonDAO(PersonDAO.class);
    DayOffDAO dayOffDAO = helper.getDayOffDAO(DayOffDAO.class);

    @Override
    public void initialize() {
        /* Put a little margin around the fields in the right side editor */
        this.setMargin(true);
        this.setVisible(false);
        daysOffTable.setHeight("300px");
        daysOffTable.setWidth("100%");
        this.addComponent(daysOffTable);
        initSearch();
        initDaysOffTable();
        initDaysOffEditor();
    }

    private void initSearch() {
        Label editorBlank = new Label();
        editorBlank.setHeight("2em");

		/*
         * We want to show a subtle prompt in the search field. We could also
		 * set a caption that would be shown above the field or description to
		 * be shown in a tooltip.
		 */
        searchField.setInputPrompt("Αναζήτηση άδειας");


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
                daysOffContainer.removeAllContainerFilters();
                daysOffContainer.addContainerFilter(new DaysOffFilter(event
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

    private void initDaysOffTable() {
        daysOffTable.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                Object contactId = daysOffTable.getValue();
                /*
                 * When a contact is selected from the list, we want to show
				 * that in our editor on the right. This is nicely done by the
				 * FieldGroup that binds all the fields to the corresponding
				 * Properties in our contact at once.
				 */
                if (contactId != null) {
                    daysOffEditorFields.setItemDataSource(daysOffTable.getItem(contactId));
                }
                daysOffEditorLayout.setVisible(contactId != null);

            }
        });

        createDayOffButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                /*
                 * Rows in the Container data model are called Item. Here we add
				 * a new row in the beginning of the list.
				 */
                daysOffContainer.removeAllContainerFilters();
                Object contactId = daysOffContainer.addItemAt(0);

				/* Lets choose the newly created contact to edit it. */
                daysOffTable.select(contactId);
            }
        });
    }

    private void initDaysOffEditor() {

         /* User interface can be created dynamically to reflect underlying data. */
        for (FieldName fieldName : fieldNames) {
            if (fieldName == FieldName.ID) continue;
            switch (fieldName) {
                case DAYOFF_TYPE:
                    TextField field = new TextField(fieldName.getName());
                    daysOffEditorLayout.addComponent(field);
                    daysOffEditorFields.bind(field, fieldName);
                    break;
                case DAYOFF_ARRIVAL:
                case DEPARTURE:
                    DateField dateField = new DateField(fieldName.getName());
                    dateField.setDateFormat("dd-MM-yyyy");
                    dateField.setConverter(new DateFieldConverter());
                    daysOffEditorLayout.addComponent(dateField);
                    daysOffEditorFields.bind(dateField, fieldName);
                    break;
            }
        }

		/*
         * Data can be buffered in the user interface. When doing so, commit()
		 * writes the changes to the data source. Here we choose to write the
		 * changes automatically without calling commit().
		 */
        daysOffEditorFields.setBuffered(false);
        daysOffEditorLayout.setMargin(true);

        HorizontalLayout horizontalLayout = new HorizontalLayout(saveButton, deleteDayOffButton);
        horizontalLayout.setSizeFull();
        horizontalLayout.setMargin(true);
        daysOffEditorLayout.addComponent(horizontalLayout);

        daysOffEditorLayout.setVisible(false);
        this.addComponent(daysOffEditorLayout);

        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                Item item = daysOffContainer.getItem(daysOffTable.getValue());
                DayOff dayOff = new DayOff();
                String id = (String) item.getItemProperty(FieldName.ID).getValue();

                try {
                    dayOff.setType((String) item.getItemProperty(FieldName.DAYOFF_TYPE).getValue());

                    DateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat viewFormat = new SimpleDateFormat("dd-MM-yyyy");

                    String arrival = (String) item.getItemProperty(FieldName.DAYOFF_ARRIVAL).getValue();
                    String departure = (String) item.getItemProperty(FieldName.DEPARTURE).getValue();

                    if (arrival != null && !arrival.isEmpty()) {
                        Date date = viewFormat.parse((String) item.getItemProperty(FieldName.DAYOFF_ARRIVAL).getValue());
                        dayOff.setArrival(date);
                    }

                    if (departure != null && !departure.isEmpty()) {
                        Date date = viewFormat.parse((String) item.getItemProperty(FieldName.DEPARTURE).getValue());
                        dayOff.setDeparture(date);
                    }

                    if (!id.isEmpty()) {
                        dayOff.setId(Integer.parseInt(id));
                        dayOffDAO.update(dayOff);
                    } else {
                        dayOff.setPerson(personDAO.getPerson(Integer.parseInt(person_id)));
                        dayOffDAO.create(dayOff);
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
                    .getItemProperty(FieldName.DAYOFF_ARRIVAL).getValue() + item
                    .getItemProperty(FieldName.DEPARTURE).getValue()).toLowerCase();
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

                for (DayOff dayOff : person.getDayOffs()) {
                    Object id = ic.addItem();
                    ic.getContainerProperty(id, FieldName.ID).setValue(Integer.toString(dayOff.getId()));
                    ic.getContainerProperty(id, FieldName.DAYOFF_TYPE).setValue(dayOff.getType());

                    if (dayOff.getDeparture() != null) {
                        Date date = databaseFormat.parse(dayOff.getDeparture().toString());
                        ic.getContainerProperty(id, FieldName.DEPARTURE).setValue(viewFormat.format(date));
                    }

                    if (dayOff.getArrival() != null) {
                        Date date = databaseFormat.parse(dayOff.getArrival().toString());
                        ic.getContainerProperty(id, FieldName.DAYOFF_ARRIVAL).setValue(viewFormat.format(date));
                    }
                }
                daysOffContainer = ic;


                daysOffTable.setContainerDataSource(daysOffContainer);
            }
            daysOffTable.setVisibleColumns(new FieldName[]{FieldName.DAYOFF_TYPE, FieldName.DEPARTURE, FieldName.DAYOFF_ARRIVAL});
            daysOffTable.setSelectable(true);
            daysOffTable.setImmediate(true);

        } catch (NumberFormatException e) {
            logger.debug("Number format exception", e);
        } catch (ParseException e) {
            logger.error(e);
        }
    }

    public void removeDayOff() {
        deleteDayOffButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Object objectId = daysOffTable.getValue();
                Item item = daysOffTable.getItem(objectId);
                logger.error("Item to be deleted " + item);
                String id = (String) item.getItemProperty(FieldName.ID).getValue();
                dayOffDAO.delete(Integer.parseInt(id));

                daysOffContainer.removeItem(daysOffTable.getValue());
                daysOffTable.removeItem(daysOffTable.getValue());
            }
        });
    }
}
