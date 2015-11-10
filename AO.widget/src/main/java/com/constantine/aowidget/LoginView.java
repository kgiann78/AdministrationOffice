package com.constantine.aowidget;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import dao.UserDAO;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

public class LoginView extends CustomComponent implements View,
        Button.ClickListener {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(LoginView.class);
    public static final String NAME = "login";

    private TextField username;

    private PasswordField password;

    private final Button loginButton;

    public LoginView() {
        setSizeFull();

        // Create the username input field
        username = new TextField("Χρήστης:");
        username.setWidth("300px");
        username.setRequired(true);
        username.setInputPrompt("Το username (πχ. username@domain)");
        username.addValidator(new EmailValidator(
                "Το username πρέπει να είναι μια ηλεκτρονική διεύθυνση"));
        username.setInvalidAllowed(false);

        // Create the password input field
        password = new PasswordField("Password:");
        password.setWidth("300px");
        password.addValidator(new PasswordValidator("Το password δεν είναι σωστό. \nΠρέπει να είναι τουλάχιστον 8 χαρακτήρων και να περιέχει τουλάχιστον έναν αριθμό."));
        password.setRequired(true);
        password.setValue("");
        password.setNullRepresentation("");

        // Create login button
        loginButton = new Button("Login", this);

        password.addShortcutListener(new Button.ClickShortcut(loginButton, 13));

        // Add both to a panel
        VerticalLayout fields = new VerticalLayout(username, password, loginButton);
        fields.setCaption("Παρακαλώ συνδεθείτε με το λογαριασμό σας.");
        fields.setSpacing(true);
        fields.setMargin(new MarginInfo(true, true, true, false));
        fields.setSizeUndefined();

        // The view root layout
        VerticalLayout viewLayout = new VerticalLayout(fields);
        viewLayout.setSizeFull();
        viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
        viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);
        setCompositionRoot(viewLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // focus the username field when username arrives to the login view
        username.focus();
    }

    // Validator for validating the passwords
    private static final class PasswordValidator extends
            AbstractValidator<String> implements Validator {

        public PasswordValidator(String message) {
            super(message);
        }

        @Override
        protected boolean isValidValue(String value) {
            //
            // Password must be at least 8 characters long and contain at least
            // one number
            //
            return !(value != null
                    && (value.length() < 8 || !value.matches(".*\\d.*")));
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        //
        // Validate the fields using the navigator. By using validators for the
        // fields we reduce the amount of queries we have to use to the database
        // for wrongly entered passwords
        //
        if (!this.username.isValid() || !this.password.isValid()) {
            return;
        }

        String username = this.username.getValue();
        String password = this.password.getValue();
        //
        // Validate username and password with database here.
        //
        try {
            SpringContextHelper context = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
            final UserDAO userDAO = context.getUserDAO(UserDAO.class);
            if (userDAO.validate(username, password)) {
                // Store the current username in the service session
                getSession().setAttribute("username", username);

                // Navigate to main view
                getUI().getNavigator().navigateTo(MainView.USERNAME);
            } else {
                this.username.focus();
                throw new Validator.InvalidValueException("Το όνομα χρήστη ή το password είναι λανθασμένα.");
            }
        } catch (HibernateException e) {
            this.password.setValue(null);
            this.password.focus();
            throw new Validator.InvalidValueException("Δεν υπάρχει πρόσβαση με τη Βάση Δεδομένων. Ελέξτε τη σύνδεσή σας.");
        }
    }

}
