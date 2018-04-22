package views.hotelveiw;

import backend.*;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HotelEditForm extends FormLayout {
    private HotelView hotelView;

    private HotelService hotelService = HotelService.getInstance();
    private CategoryService categoryService = CategoryService.getInstance();
    @Getter
    private Hotel hotel;
    private Binder<Hotel> hotelBinder = new Binder<>(Hotel.class);

    private TextField name = new TextField("Name:");
    private TextField address = new TextField("Address:");
    private TextField rating = new TextField("Rating:");
    private DateField operatesFrom = new DateField("Operates from:");
    private NativeSelect<String> category = new NativeSelect<>("Category:");
    private TextField url = new TextField("URL:");
    private TextArea description = new TextArea("Description:");

    private Button saveHotelBtn = new Button("Save");
    private Button closeFormBtn = new Button("Close");

    public HotelEditForm(HotelView hotelView) {

        this.hotelView = hotelView;

        this.setMargin(true);       // Enable layout margins. Affects all four sides of the layout
        this.setVisible(false);     // hide form at start

        HorizontalLayout buttons = new HorizontalLayout(saveHotelBtn, closeFormBtn);
        buttons.setSpacing(true);

        this.addComponents(name, address, rating, operatesFrom, category,
                url, description, buttons);

        // add ToolTip to the forms fields
        name.setDescription("Hotel name");
        address.setDescription("Hotel address");
        rating.setDescription("Hotel rating from 0 to 5 stars");
        operatesFrom.setDescription("Date of the beginning of the operating " +
                "of the hotel must be in the past");
        category.setDescription("Hotel category");
        url.setDescription("Info about hotel on the booking.com");
        description.setDescription("Hotel description");

        // connect entity fields with form fields
        name.setRequiredIndicatorVisible(true);         // Required field
        hotelBinder.forField(name)
                   // Shorthand for requiring the field to be non-empty
                   .asRequired("Every hotel must have a name")
                   .bind(Hotel::getName, Hotel::setName);

        address.setRequiredIndicatorVisible(true);
        hotelBinder.forField(address)
                   .asRequired("Every hotel must have a address")
                   .bind(Hotel::getName, Hotel::setName);

        rating.setRequiredIndicatorVisible(true);
        hotelBinder.forField(rating)
                   .asRequired("Every hotel must have a rating")
                   .withConverter(new StringToIntegerConverter("Enter an integer, please"))
                   .withValidator(rating -> rating >= 0 && rating <= 5,
                "Rating must be between 0 and 5")
                   .bind(Hotel::getRating, Hotel::setRating);

        operatesFrom.setRequiredIndicatorVisible(true);
        hotelBinder.forField(operatesFrom)
                   .asRequired("Every hotel must operates from a certain date")
                   .withValidator( new DateRangeValidator("Date must be in the past",
                           null, LocalDate.now().minusDays(1)))
                   .withConverter(LocalDate::toEpochDay, LocalDate::ofEpochDay,
                           "Don't look like a date")
                   .bind(Hotel::getOperatesFrom, Hotel::setOperatesFrom);

        category.setRequiredIndicatorVisible(true);
        SerializableFunction<String,Category> toModel = ( e -> {
             return CategoryService.getInstance().findAll().stream()
                                                 .filter( f -> f.getName().equalsIgnoreCase(e))
                                                 .findAny().orElse(new Category(e));
        });
        SerializableFunction<Category,String> toPresentation = (Category::getName);
        SerializablePredicate<? super String> noCategoryNameInList = ( e -> {
            return categoryService.isCategoryNameInList(new Category(e));
        });
        hotelBinder.forField(category)
                   .asRequired("Every hotel must have a category")
                   .withValidator(noCategoryNameInList, "Define category, please")
                   .withConverter(toModel, toPresentation, "No such category")
                   .bind(Hotel::getCategory, Hotel::setCategory);

        url.setRequiredIndicatorVisible(true);
        hotelBinder.forField(url)
                   .asRequired("Every hotel must have a link to booking.com")
                   .bind(Hotel::getUrl, Hotel::setUrl);

        hotelBinder.forField(description).bind(Hotel::getDescription, Hotel::setDescription);

        // fill categories items
        List<String> categoryItems = CategoryService.getInstance().findAll()
                                                    .stream()
                                                    .map(Category::getName)
                                                    .collect(Collectors.toList());
        category.setItems(categoryItems);

        // buttons
        saveHotelBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        saveHotelBtn.addClickListener(e -> saveHotel());
        closeFormBtn.addClickListener(e -> closeHotelEditForm());

    }

    public void saveHotel() {
        // This will make all current validation errors visible
        BinderValidationStatus<Hotel> status = hotelBinder.validate();
        if (status.hasErrors()) {
            Notification.show("Validation error count: "
                    + status.getValidationErrors().size(), Notification.Type.WARNING_MESSAGE);
        }

        // save validated hotel with not empty fields (exclude description)
        if ( !status.hasErrors() ) {
            boolean isSaved = hotelService.save(getHotel());
            if (isSaved) {
                hotelService.save(getHotel());
                hotelView.updateHotelList();
                this.setVisible(false);
                Notification.show("Saved hotel with name: " + this.getHotel().getName(),
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show("Can't save hotel with name: " + getHotel().getName(),
                        Notification.Type.ERROR_MESSAGE);
            }
        }
        hotelView.getAddHotelBtn().setEnabled(true);
    }

    public void setHotel(Hotel hotel) {
        this.setVisible(true);
        this.hotel = hotel;

        // connect entity fields with form fields
        hotelBinder.setBean(hotel);
    }

    public void closeHotelEditForm() {
        this.setVisible(false);
        hotelView.getAddHotelBtn().setEnabled(true);
        hotelView.getHotelList().deselectAll();
        hotelView.updateHotelList();
    }

}
