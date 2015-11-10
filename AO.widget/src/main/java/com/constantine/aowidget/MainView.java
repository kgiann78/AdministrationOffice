package com.constantine.aowidget;

import com.constantine.aowidget.Tabs.DaysOffTab;
import com.constantine.aowidget.Tabs.EditorTab;
import com.constantine.aowidget.Tabs.TrainingTab;
import com.constantine.aowidget.Tabs.TransfersTab;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import dao.PersonDAO;
import model.Person;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainView extends CustomComponent implements View {
    private static Logger logger = Logger.getLogger(MainView.class);

    public static final String USERNAME = "";

    /* Root of the user interface component tree is set */
    VerticalLayout verticalPanel = new VerticalLayout();
//    HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
    HorizontalLayout horizontalSplitPanel = new HorizontalLayout();
    HorizontalLayout logoutPanel = new HorizontalLayout();
    VerticalLayout leftLayout = new VerticalLayout();

    private Table contactList = new Table();
    private TextField searchField = new TextField();
    private Button addNewContactButton = new Button("Δημιουργία Καρτέλας Προσωπικού");

    // Tabs
    private TabSheet tabsheet = new TabSheet();
    private EditorTab editorTab = new EditorTab();
    private TransfersTab transfersTab = new TransfersTab();
    private DaysOffTab daysOffTab = new DaysOffTab();
    private TrainingTab trainingTab = new TrainingTab();

    private Button saveButton = new Button("Αποθήκευση");

    /*
     * Any component can be bound to an external data source. This example uses
     * just a dummy in-memory list, but there are many more practical
     * implementations.
     */
    IndexedContainer contactContainer = null;

    Label text = new Label();
    Button logout = new Button("Έξοδος");

    public MainView() {
        setCompositionRoot(verticalPanel);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        SpringContextHelper context = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
        final PersonDAO personDAO = context.getPersonDAO(PersonDAO.class);
        IndexedContainer ic = new IndexedContainer();

        ic.addContainerProperty(FieldName.ID, String.class, "");
        ic.addContainerProperty(FieldName.NAME, String.class, "");
        ic.addContainerProperty(FieldName.SURNAME, String.class, "");
        ic.addContainerProperty(FieldName.BIRTHDAY, String.class, "");
        ic.addContainerProperty(FieldName.AGM, String.class, "");
        ic.addContainerProperty(FieldName.ADDRESS, String.class, "");
        ic.addContainerProperty(FieldName.PHONE, String.class, "");
        ic.addContainerProperty(FieldName.MOBILE, String.class, "");
        ic.addContainerProperty(FieldName.EMAIL, String.class, "");


        List<Person> personnel = new ArrayList<>(personDAO.personnel());
        try {
            for (Person person : personnel) {

                Object id = ic.addItem();
                ic.getContainerProperty(id, FieldName.ID).setValue(Integer.toString(person.getId()));
                ic.getContainerProperty(id, FieldName.NAME).setValue(person.getName());
                ic.getContainerProperty(id, FieldName.SURNAME).setValue(person.getSurname());
                DateFormat databaseFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat viewFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = databaseFormat.parse(person.getBirthday().toString());
                ic.getContainerProperty(id, FieldName.BIRTHDAY).setValue(viewFormat.format(date));
                ic.getContainerProperty(id, FieldName.AGM).setValue(person.getAgm());
                ic.getContainerProperty(id, FieldName.ADDRESS).setValue(person.getPersonData().getAddress());
                ic.getContainerProperty(id, FieldName.PHONE).setValue(person.getPersonData().getPhone());
                ic.getContainerProperty(id, FieldName.MOBILE).setValue(person.getPersonData().getMobile());
                ic.getContainerProperty(id, FieldName.EMAIL).setValue(person.getPersonData().getEmail());
            }
        } catch (ParseException e) {
            logger.error(e);
        }

        contactContainer = ic;
        initLayout();
        initContactList();
        initSearch();
        initButtons();
    }

    /*
 * In this example layouts are programmed in Java. You may choose use a
 * visual editor, CSS or HTML templates for layout instead.
 */
    private void initLayout() {
        // Get the user name from the session
        String username = String.valueOf(getSession().getAttribute("username"));

        verticalPanel.setHeight("100%");

        // And show the username
        text.setValue("Είστε συνδεδεμένος σαν " + username + "... ");

        logout.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {

                // "Logout" the user
                getSession().setAttribute("username", null);

                // Refresh this view, should redirect to login view
                getUI().getNavigator().navigateTo(USERNAME);
            }
        });

        logoutPanel.setSizeFull();
        logoutPanel.setMargin(true);
        logoutPanel.setSpacing(true);
        logoutPanel.addComponents(text, logout);
        verticalPanel.addComponent(logoutPanel);
        verticalPanel.addComponent(horizontalSplitPanel);

        horizontalSplitPanel.setSizeFull();
        horizontalSplitPanel.addComponent(leftLayout);

        // Tabs
        editorTab.initialize();
        transfersTab.initialize();
        daysOffTab.initialize();
        trainingTab.initialize();

        tabsheet.addTab(editorTab, "Ατομικά Στοιχεία");
        tabsheet.addTab(transfersTab, "Μετακινήσεις");
        tabsheet.addTab(daysOffTab, "Άδειες");
        tabsheet.addTab(trainingTab, "Εκπαίδευση");

        tabsheet.setVisible(false);
        horizontalSplitPanel.addComponent(tabsheet);

        leftLayout.setHeight("100%");
        leftLayout.addComponent(contactList);
        HorizontalLayout bottomLeftLayout = new HorizontalLayout();
        leftLayout.addComponent(bottomLeftLayout);
        bottomLeftLayout.addComponent(searchField);
        bottomLeftLayout.addComponent(addNewContactButton);

		/* Set the contents in the left of the split panel to use all the space */
        leftLayout.setSizeFull();

		/*
         * On the left side, expand the size of the contactList so that it uses
		 * all the space left after from bottomLeftLayout
		 */
        leftLayout.setExpandRatio(contactList, 1);
        contactList.setSizeFull();

		/*
         * In the bottomLeftLayout, searchField takes all the width there is
		 * after adding addNewContactButton. The height of the layout is defined
		 * by the tallest component.
		 */
        bottomLeftLayout.setWidth("100%");
        searchField.setWidth("100%");
        bottomLeftLayout.setExpandRatio(searchField, 1);
    }

    private void initSearch() {

		/*
		 * We want to show a subtle prompt in the search field. We could also
		 * set a caption that would be shown above the field or description to
		 * be shown in a tooltip.
		 */
        searchField.setInputPrompt("Αναζήτηση προσωπικού");

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
                contactContainer.removeAllContainerFilters();
                contactContainer.addContainerFilter(new ContactFilter(event
                        .getText()));
            }
        });
    }

    private void initButtons() {
        addNewContactButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {

				/*
				 * Rows in the Container data model are called Item. Here we add
				 * a new row in the beginning of the list.
				 */
                contactContainer.removeAllContainerFilters();
                Object contactId = contactContainer.addItemAt(0);

				/* Lets choose the newly created contact to edit it. */
                contactList.select(contactId);
            }
        });

        editorTab.removePerson(contactContainer, contactList);
        transfersTab.removeTransfer(contactContainer, contactList);
        daysOffTab.removeDayOff();
        trainingTab.removeTraining();

        editorTab.savePerson(transfersTab, daysOffTab, trainingTab);
    }

    private void initContactList() {
        contactList.setContainerDataSource(contactContainer);
        contactList.setVisibleColumns(new FieldName[]{FieldName.NAME, FieldName.SURNAME, FieldName.AGM});
        contactList.setSelectable(true);
        contactList.setImmediate(true);

        contactList.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                Object contactId = contactList.getValue();
				/*
				 * When a contact is selected from the list, we want to show
				 * that in our editor on the right. This is nicely done by the
				 * FieldGroup that binds all the fields to the corresponding
				 * Properties in our contact at once.
				 */
                if (contactId != null) {
                    editorTab.getFields().setItemDataSource(contactList.getItem(contactId));
                }

//                editorTab.getLayout().setVisible(contactId != null);
                tabsheet.setVisible(contactId != null);

                if (contactId != null && !((String) contactList.getItem(contactId).getItemProperty(FieldName.ID).getValue()).isEmpty()) {
                    transfersTab.setUserId(contactList.getItem(contactId).getItemProperty(FieldName.ID));
                    daysOffTab.setUserId(contactList.getItem(contactId).getItemProperty(FieldName.ID));
                    trainingTab.setUserId(contactList.getItem(contactId).getItemProperty(FieldName.ID));

                    transfersTab.setVisibility(true);
                    daysOffTab.setVisibility(true);
                    trainingTab.setVisibility(true);
                }
            }
        });
    }

    /*
     * A custom filter for searching names and companies in the
     * contactContainer.
     */
    private class ContactFilter implements Container.Filter {
        private String needle;

        public ContactFilter(String needle) {
            this.needle = needle.toLowerCase();
        }

        public boolean passesFilter(Object itemId, Item item) {
            String haystack = ("" + item.getItemProperty(FieldName.NAME).getValue()
                    + item.getItemProperty(FieldName.SURNAME).getValue() + item
                    .getItemProperty(FieldName.AGM).getValue()).toLowerCase();
            return haystack.contains(needle);
        }

        public boolean appliesToProperty(Object id) {
            return true;
        }
    }
}
