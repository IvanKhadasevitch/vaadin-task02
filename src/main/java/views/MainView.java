package views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class MainView extends VerticalLayout implements View {
    public MainView() {
        setSizeFull();
        setSpacing(true);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Notification.show("Choose any menu item, please.", Notification.Type.HUMANIZED_MESSAGE);
    }
}
