package ui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

import static ui.NavigationUI.CATEGORY_VIEW;
import static ui.NavigationUI.HOTEL_VIEW;

public class Menu extends CustomComponent {
    private MenuItem previous = null;
    private final Label selection = new Label("-");

    public Menu() {
        HorizontalLayout layout = new HorizontalLayout();
        MenuBar menuBar = new MenuBar();
        menuBar.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
        layout.addComponent(menuBar);
        // A feedback component
        layout.addComponent(selection);

        // put menu items
        menuBar.addItem("Hotel", VaadinIcons.BUILDING, hotelBtn());
        menuBar.addItem("Category", VaadinIcons.ACADEMY_CAP, categoryBtn());

        layout.setSizeUndefined();
        setCompositionRoot(layout);
    }

    private Command hotelBtn() {

        return new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                highLightSelection(selection, selectedItem);
                getUI().getNavigator().navigateTo(HOTEL_VIEW);
            }
        };
    }

    private Command categoryBtn() {

        return new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                highLightSelection(selection, selectedItem);
                getUI().getNavigator().navigateTo(CATEGORY_VIEW);
            }
        };
    }

    private void highLightSelection(Label selection, MenuItem selectedItem) {
        selection.setValue("Ordered a " +
                selectedItem.getText() +
                " from menu.");

        if (previous != null)
            previous.setStyleName("unchecked" );

        selectedItem.setStyleName("checked");
        previous = selectedItem;
    }
}
