package views.hotelveiw;

import backend.Category;
import backend.CategoryService;
import backend.Hotel;
import backend.HotelService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class HotelView extends VerticalLayout implements View {

    final HotelService hotelService = HotelService.getInstance();

    final TextField filterByName = new TextField();
    final Button clearFilterByNameBtn = new Button(VaadinIcons.CLOSE);
    final TextField filterByAddress = new TextField();
    final Button clearFilterByAddressBtn = new Button(VaadinIcons.CLOSE);
    @Getter
    final Button addHotelBtn = new Button("Add hotel");
    final Button deleteHotelBtn = new Button("Delete hotel");
    final Button editHotelBtn = new Button("Edit hotel");

    @Getter
    final Grid<Hotel> hotelList = new Grid<>();

    private HotelEditForm hotelEditForm = new HotelEditForm(this);

    public HotelView() {
        // UI Configuration
        configureComponents();
        buildLayout();

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

    private void configureComponents() {
        // filterByName field with clear button
        filterByName.setPlaceholder("Filter by name...");
        filterByName.addValueChangeListener(e -> updateHotelList());
        filterByName.setValueChangeMode(ValueChangeMode.LAZY);
        clearFilterByNameBtn.setDescription("Clear the current filter");
        clearFilterByNameBtn.addClickListener(e -> filterByName.clear());

        // filterByAddress field with clear button
        filterByAddress.setPlaceholder("Filter by address...");
        filterByAddress.addValueChangeListener(e -> updateHotelList());
        filterByAddress.setValueChangeMode(ValueChangeMode.LAZY);
        clearFilterByAddressBtn.setDescription("Clear the current filter");
        clearFilterByAddressBtn.addClickListener(e -> filterByAddress.clear());

        // add Hotel Button
        addHotelBtn.addClickListener(e -> {
            addHotelBtn.setEnabled(false);
            hotelEditForm.setHotel(new Hotel());
        } );

        // delete Hotel Button
        deleteHotelBtn.setStyleName(ValoTheme.BUTTON_DANGER);
        deleteHotelBtn.setEnabled(false);
        deleteHotelBtn.addClickListener(e -> {
            int deletedHotelsCount = hotelList.getSelectedItems().size();
            hotelList.getSelectedItems().forEach(hotelService::delete);
            deleteHotelBtn.setEnabled(false);
            addHotelBtn.setEnabled(true);
            updateHotelList();
            Notification.show(String.format("Were deleted [%d] hotels.", deletedHotelsCount),
                    Notification.Type.WARNING_MESSAGE);
        });

        // edit Hotel Button (can edit only if one hotel was chosen)
        editHotelBtn.setEnabled(false);
        editHotelBtn.addClickListener(e -> {
            Hotel editCandidate = hotelList.getSelectedItems().iterator().next();
            hotelEditForm.setHotel(editCandidate);
        });

        // Hotel list (Grid)
        hotelList.addColumn(Hotel::getName).setCaption("Name");
        hotelList.setFrozenColumnCount(1);              // froze "name" column
        hotelList.addColumn(Hotel::getAddress).setCaption("Address");
        hotelList.addColumn(Hotel::getRating).setCaption("Rating");
        hotelList.addColumn(hotel -> LocalDate.ofEpochDay(hotel.getOperatesFrom()))
                 .setCaption("Operates from");
        hotelList.addColumn( e -> {
            List<Category> categoryList = CategoryService.getInstance().findAll();
            return categoryList.contains(e.getCategory())
                    ? e.getCategory().getName()
                    : Category.NULL_CATEGORY_REPRESENTATION;
        }).setCaption("Category");

        Grid.Column<Hotel, String> htmlColumn = hotelList.addColumn(hotel ->
                        "<a href='" + hotel.getUrl() + "' target='_blank'>more info</a>",
                new HtmlRenderer()).setCaption("Url");
        hotelList.addColumn(Hotel::getDescription).setCaption("Description");
        hotelList.setSelectionMode(Grid.SelectionMode.MULTI);           // multi select possible
        // delete and edit selected Hotel
        hotelList.addSelectionListener(e -> {
            // when Hotel is chosen - can delete or edit
            Set<Hotel> selectedHotels = e.getAllSelectedItems();
            if (selectedHotels != null && selectedHotels.size() == 1) {
                // chosen only one hotel - can delete & edit
                deleteHotelBtn.setEnabled(true);
                editHotelBtn.setEnabled(true);
            } else if (selectedHotels != null && selectedHotels.size() > 1) {
                // chosen more then one hotel - can delete only
                deleteHotelBtn.setEnabled(true);
                editHotelBtn.setEnabled(false);
            } else {
                // no any hotel chosen - can't delete & edit
                deleteHotelBtn.setEnabled(false);
                editHotelBtn.setEnabled(false);
                hotelEditForm.setVisible(false);
            }
        });

        this.updateHotelList();
    }

    private void buildLayout() {
        // filters with close button
        CssLayout filteringByName = new CssLayout();
        filteringByName.addComponents(filterByName, clearFilterByNameBtn);
        filteringByName.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        CssLayout filteringByAddress = new CssLayout();
        filteringByAddress.addComponents(filterByAddress, clearFilterByAddressBtn);
        filteringByAddress.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        // tools bar - filters & buttons
        HorizontalLayout control = new HorizontalLayout(filteringByName, filteringByAddress,
                addHotelBtn, deleteHotelBtn, editHotelBtn);
        control.setMargin(false);
        control.setWidth("100%");
        control.setExpandRatio(filteringByAddress, 1);

        // content - HotelList & hotelEditForm
        HorizontalLayout hotelContent = new HorizontalLayout(hotelList, hotelEditForm);
        hotelList.setSizeFull();            // size 100% x 100%
        hotelEditForm.setSizeFull();
        hotelContent.setMargin(false);
        hotelContent.setWidth("100%");
        hotelContent.setHeight(32, Unit.REM);
        hotelContent.setExpandRatio(hotelList, 229);
        hotelContent.setExpandRatio(hotelEditForm, 92);

        // Compound view parts and allow resizing
        this.addComponents(control, hotelContent);
        this.setSpacing(true);
        this.setMargin(false);
        this.setWidth("100%");

    }

    public void updateHotelList() {
        List<Hotel> hotelList = hotelService.findAll(filterByName.getValue(),
                filterByAddress.getValue());
        this.hotelList.setItems(hotelList);
    }

}
