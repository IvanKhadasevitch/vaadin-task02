package views.hotelveiw;

import backend.Category;
import backend.CategoryService;
import backend.Hotel;
import backend.HotelService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;
import ui.customcompanents.FilterWithClearBtn;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class HotelView extends VerticalLayout implements View {

    final HotelService hotelService = HotelService.getInstance();

    private FilterWithClearBtn filterByName;
    private FilterWithClearBtn filterByAddress;
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
        // filter fields with clear button
        filterByName = new FilterWithClearBtn("Filter by name...",
                e -> updateHotelList());
        filterByAddress = new FilterWithClearBtn("Filter by address...",
                e -> updateHotelList());

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
            addHotelBtn.setEnabled(true);       // switch on addNewHotel possibility
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
                // chosen only one hotel - can add & delete & edit
                addHotelBtn.setEnabled(true);
                deleteHotelBtn.setEnabled(true);
                editHotelBtn.setEnabled(true);
            } else if (selectedHotels != null && selectedHotels.size() > 1) {
                // chosen more then one hotel - can delete & add
                hotelEditForm.setVisible(false);
                addHotelBtn.setEnabled(true);
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
        // tools bar - filters & buttons
        HorizontalLayout control = new HorizontalLayout(filterByName, filterByAddress,
                addHotelBtn, deleteHotelBtn, editHotelBtn);
        control.setMargin(false);
        control.setWidth("100%");
        // divide free space between filterByName (50%) & filterByAddress (50%)
        control.setExpandRatio(filterByName, 1);
        control.setExpandRatio(filterByAddress, 1);

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
