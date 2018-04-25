package ui.customcompanents;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;


public class FilterWithClearBtn extends CustomField<String> {
    private final Button clearFilterBtn = new Button(VaadinIcons.CLOSE);
    private final TextField filterField = new TextField();

    public FilterWithClearBtn(String placeholder, ValueChangeListener<String> valueChangeListener) {
        super();
        this.filterField.setPlaceholder(placeholder);
        this.filterField.addValueChangeListener(valueChangeListener);
    }


    @Override
    protected Component initContent() {
        // filter field with clear button - configure components
        this.filterField.setValueChangeMode(ValueChangeMode.LAZY);
        this.clearFilterBtn.setDescription("Clear the current filter");
        this.clearFilterBtn.addClickListener( event -> this.filterField.clear());

        HorizontalLayout filtering = new HorizontalLayout(filterField, clearFilterBtn);
        filtering.setMargin(false);
        filtering.setSpacing(false);

        return filtering;
    }


    @Override
    public String getValue() {
        return filterField.getValue();
    }

    @Override
    protected void doSetValue(String stringValue) {
        this.filterField.setValue(stringValue);
    }
}
