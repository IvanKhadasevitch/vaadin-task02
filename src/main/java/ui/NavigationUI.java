package ui;

import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import lombok.Getter;
import views.categoryveiw.CategoryView;
import views.hotelveiw.HotelView;
import views.MainView;

import javax.servlet.annotation.WebServlet;

@PushStateNavigation                    // allow separate URL with "/"
@Title("vaadin: task02")
public class NavigationUI extends UI {

    private Component mainMenu = new Menu();
    @Getter
    private VerticalLayout contentPanel = new VerticalLayout();

    static final String HOTEL_VIEW = "hotel";
    static final String CATEGORY_VIEW = "category";

    @Override
    protected void init(VaadinRequest request) {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        Navigator navigator = new Navigator(this, contentPanel);

        navigator.addView("", MainView.class);
        navigator.addView(HOTEL_VIEW,  HotelView.class);
        navigator.addView(CATEGORY_VIEW, CategoryView.class);
    }

    private void buildLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        // mainMenu
        mainLayout.addComponent(mainMenu);

        // contentPanel - set full screan size 100% x 100% without margis for 4 sides
        contentPanel.setSizeFull();
        contentPanel.setMargin(false);
        mainLayout.addComponent(contentPanel);

        setContent(mainLayout);
    }

    /*
     * Deployed as a Servlet or Portlet.
     *
     * You can specify additional servlet parameters like the URI and UI class
     * name and turn on production mode when you have finished developing the
     * application.
     */
    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = NavigationUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
