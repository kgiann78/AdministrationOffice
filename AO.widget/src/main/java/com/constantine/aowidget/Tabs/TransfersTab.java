package com.constantine.aowidget.Tabs;

import com.constantine.aowidget.*;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import dao.PersonDAO;
import dao.TransferDAO;
import model.Person;
import model.Transfer;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransfersTab extends VerticalLayout implements TabElement {
    private static Logger logger = Logger.getLogger(TransfersTab.class);

    //    private HorizontalLayout layout = new HorizontalLayout();
    private Table transfersTable = new Table();
    private IndexedContainer transfersContainer = null;
    private TextField searchField = new TextField();

    //Additional helping elements
    Label blank = new Label();

    private FormLayout transferEditorLayout = new FormLayout();
    private FieldGroup transferEditorFields = new FieldGroup();
    private String person_id = null;

    private Button createTransferButton = new Button("Δημιουργία Μετακίνησης");
    private Button saveButton = new Button("Αποθήκευση");
    private Button deleteTransferButton = new Button("Διαγραφή Καρτέλας");

    private static final FieldName[] fieldNames = new FieldName[]{
            FieldName.ID, FieldName.SERVICE, FieldName.TRANSFER_TYPE, FieldName.ARRIVAL, FieldName.DEPARTURE};

    SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
    PersonDAO personDAO = helper.getPersonDAO(PersonDAO.class);
    TransferDAO transferDAO = helper.getTransferDAO(dao.TransferDAO.class);

    @Override
    public void initialize() {
        blank.setHeight("1em");

        /* Put a little margin around the fields in the right side editor */
        this.setMargin(false);
        this.setVisible(false);
        this.setHeight("100%");

        transfersTable.setHeight("300px");
        transfersTable.setWidth("100%");
        transfersTable.setSelectable(true);
        transfersTable.setImmediate(true);
        this.addComponent(transfersTable);


        initSearch();
        initEditor();
        initTransfersTable();
    }

    private void initSearch() {
        Label editorBlank = new Label();
        editorBlank.setHeight("2em");

		/*
         * We want to show a subtle prompt in the search field. We could also
		 * set a caption that would be shown above the field or description to
		 * be shown in a tooltip.
		 */
        searchField.setInputPrompt("Αναζήτηση μετακινήσης");


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
                transfersContainer.removeAllContainerFilters();
                transfersContainer.addContainerFilter(new TransfersFilter(event
                        .getText()));
            }
        });

        HorizontalLayout bottomRightLayout = new HorizontalLayout();
        bottomRightLayout.setMargin(false);
        bottomRightLayout.setSizeFull();

        searchField.setSizeFull();
        bottomRightLayout.addComponent(searchField);
        bottomRightLayout.addComponent(createTransferButton);
        this.addComponent(bottomRightLayout);
        this.addComponent(editorBlank);
    }

    private void initTransfersTable() {
        transfersTable.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                Object contactId = transfersTable.getValue();
                /*
                 * When a contact is selected from the list, we want to show
				 * that in our editor on the right. This is nicely done by the
				 * FieldGroup that binds all the fields to the corresponding
				 * Properties in our contact at once.
				 */
                if (contactId != null) {
                    transferEditorFields.setItemDataSource(transfersTable.getItem(contactId));
                }
                transferEditorLayout.setVisible(contactId != null);

            }
        });

        createTransferButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                /*
                 * Rows in the Container data model are called Item. Here we add
				 * a new row in the beginning of the list.
				 */
                transfersContainer.removeAllContainerFilters();
                Object contactId = transfersContainer.addItemAt(0);

				/* Lets choose the newly created contact to edit it. */
                transfersTable.select(contactId);
            }
        });
    }

    private void initEditor() {

         /* User interface can be created dynamically to reflect underlying data. */
        for (FieldName fieldName : fieldNames) {
            if (fieldName == FieldName.ID) continue;
            switch (fieldName) {
                case SERVICE:
                case TRANSFER_TYPE:
                    TextField field = new TextField(fieldName.getName());
                    transferEditorLayout.addComponent(field);
                    transferEditorFields.bind(field, fieldName);
                    break;
                case ARRIVAL:
                case DEPARTURE:
                    DateField dateField = new DateField(fieldName.getName());
                    dateField.setDateFormat("dd-MM-yyyy");
                    dateField.setConverter(new DateFieldConverter());
                    transferEditorLayout.addComponent(dateField);
                    transferEditorFields.bind(dateField, fieldName);
                    break;
            }
        }

		/*
         * Data can be buffered in the user interface. When doing so, commit()
		 * writes the changes to the data source. Here we choose to write the
		 * changes automatically without calling commit().
		 */
        transferEditorFields.setBuffered(false);
        transferEditorLayout.setMargin(true);

        HorizontalLayout horizontalLayout = new HorizontalLayout(saveButton, deleteTransferButton);
        horizontalLayout.setSizeFull();
        horizontalLayout.setMargin(true);
        transferEditorLayout.addComponent(horizontalLayout);

        transferEditorLayout.setVisible(false);
        this.addComponent(transferEditorLayout);

        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                Item item = transfersContainer.getItem(transfersTable.getValue());
                Transfer transfer = new Transfer();
                String id = (String) item.getItemProperty(FieldName.ID).getValue();
                try {
                    transfer.setService((String) item.getItemProperty(FieldName.SERVICE).getValue());
                    transfer.setType((String) item.getItemProperty(FieldName.TRANSFER_TYPE).getValue());

                    DateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat viewFormat = new SimpleDateFormat("dd-MM-yyyy");

                    String arrival = (String) item.getItemProperty(FieldName.ARRIVAL).getValue();
                    String departure = (String) item.getItemProperty(FieldName.DEPARTURE).getValue();

                    if (arrival != null && !arrival.isEmpty()) {
                        Date date = viewFormat.parse((String) item.getItemProperty(FieldName.ARRIVAL).getValue());
                        transfer.setArrival(date);
                    }

                    if (departure != null && !departure.isEmpty()) {
                        Date date = viewFormat.parse((String) item.getItemProperty(FieldName.DEPARTURE).getValue());
                        transfer.setDeparture(date);
                    }

                    if (!id.isEmpty()) {
                        transfer.setId(Integer.parseInt(id));
                        transferDAO.update(transfer);
                    } else {
                        transfer.setPerson(personDAO.getPerson(Integer.parseInt(person_id)));
                        transferDAO.create(transfer);
                    }
                } catch (ParseException e) {
                    logger.error(e);
                }
            }
        });
    }

    /*
     * A custom filter for searching names and companies in the
     * contactContainer.
     */
    private class TransfersFilter implements Container.Filter {
        private String needle;

        public TransfersFilter(String needle) {
            this.needle = needle.toLowerCase();
        }

        public boolean passesFilter(Object itemId, Item item) {
            String haystack = ("" + item.getItemProperty(FieldName.SERVICE).getValue()
                    + item.getItemProperty(FieldName.TRANSFER_TYPE).getValue() + item
                    .getItemProperty(FieldName.ARRIVAL).getValue() + item
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

                for (Transfer transfer : person.getTransfers()) {
                    Object id = ic.addItem();
                    ic.getContainerProperty(id, FieldName.ID).setValue(Integer.toString(transfer.getId()));
                    ic.getContainerProperty(id, FieldName.SERVICE).setValue(transfer.getService());
                    ic.getContainerProperty(id, FieldName.TRANSFER_TYPE).setValue(transfer.getType());

                    if (transfer.getArrival() != null) {
                        Date date = databaseFormat.parse(transfer.getArrival().toString());
                        ic.getContainerProperty(id, FieldName.ARRIVAL).setValue(viewFormat.format(date));
                    }

                    if (transfer.getDeparture() != null) {
                        Date date = databaseFormat.parse(transfer.getDeparture().toString());
                        ic.getContainerProperty(id, FieldName.DEPARTURE).setValue(viewFormat.format(date));
                    }
                }
                transfersContainer = ic;


                transfersTable.setContainerDataSource(transfersContainer);
            }

//            transfersContainer = dataProvider.getTransfers(fieldNames, Integer.parseInt(person_id));
            transfersTable.setContainerDataSource(transfersContainer);
            transfersTable.setVisibleColumns(new FieldName[]{FieldName.SERVICE, FieldName.TRANSFER_TYPE, FieldName.ARRIVAL, FieldName.DEPARTURE});


        } catch (NumberFormatException e) {
            logger.debug("Number format exception", e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void removeTransfer(final Container contactContainer, final Table contactList) {
        deleteTransferButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Item item = contactContainer.getItem(contactList.getValue());
                String id = (String) item.getItemProperty(FieldName.ID).getValue();
//                Person person = personDAO.getPerson(Integer.parseInt(id));

                item = transfersTable.getItem(transfersTable.getValue());
                id = (String) item.getItemProperty(FieldName.ID).getValue();
//                Transfer transfer = transferDAO.getTransfer(Integer.parseInt(id));
                transferDAO.delete(Integer.parseInt(id));
//                person.getTransfers().remove(transfer);

                transfersContainer.removeItem(transfersTable.getValue());
                transfersTable.removeItem(transfersTable.getValue());
            }
        });
    }
}
