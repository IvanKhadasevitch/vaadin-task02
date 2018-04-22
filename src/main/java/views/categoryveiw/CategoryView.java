package views.categoryveiw;

import backend.Category;
import backend.CategoryService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;

import java.util.List;
import java.util.Set;

public class CategoryView extends VerticalLayout implements View {

    final CategoryService categoryService = CategoryService.getInstance();

    final TextField filterByName = new TextField();
    final Button clearFilterByNameBtn = new Button(VaadinIcons.CLOSE);
    @Getter
    final Button addCategoryBtn = new Button("Add category");
    final Button deleteCategoryBtn = new Button("Delete category");
    final Button editCategoryBtn = new Button("Edit category");

    @Getter
    final Grid<Category> categoryList = new Grid<>();

    private CategoryEditForm categoryEditForm = new CategoryEditForm(this);

    public CategoryView() {
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
        filterByName.addValueChangeListener(e -> updateCategoryList());
        filterByName.setValueChangeMode(ValueChangeMode.LAZY);
        clearFilterByNameBtn.setDescription("Clear the current filter");
        clearFilterByNameBtn.addClickListener(e -> filterByName.clear());

        // add Category Button
        addCategoryBtn.addClickListener(e -> {
            addCategoryBtn.setEnabled(false);
            categoryEditForm.setCategory(new Category(null));
        } );

        // delete Hotel Button
        deleteCategoryBtn.setStyleName(ValoTheme.BUTTON_DANGER);
        deleteCategoryBtn.setEnabled(false);
        deleteCategoryBtn.addClickListener(e -> {
            int deletedCategoriesCount = categoryList.getSelectedItems().size();
            categoryList.getSelectedItems().forEach(categoryService::delete);
            deleteCategoryBtn.setEnabled(false);
            addCategoryBtn.setEnabled(true);
            updateCategoryList();
            Notification.show(String.format("Were deleted [%d] categories.", deletedCategoriesCount),
                    Notification.Type.WARNING_MESSAGE);
        });

        // edit Category Button (can edit only if one category was chosen)
        editCategoryBtn.setEnabled(false);
        editCategoryBtn.addClickListener(e -> {
            Category editCandidate = categoryList.getSelectedItems().iterator().next();
            categoryEditForm.setCategory(editCandidate);
        });

        // Category list (Grid)
        categoryList.addColumn(Category::getName).setCaption("Name");
        categoryList.setSelectionMode(Grid.SelectionMode.MULTI);
        // delete and edit selected Category
        categoryList.addSelectionListener(e -> {
            // when Category is chosen - can delete or edit
            Set<Category> selectedCategories = e.getAllSelectedItems();
            if (selectedCategories != null && selectedCategories.size() == 1) {
                // chosen only one category - can delete & edit
                deleteCategoryBtn.setEnabled(true);
                editCategoryBtn.setEnabled(true);
            } else if (selectedCategories != null && selectedCategories.size() > 1) {
                // chosen more then one category - can delete only
                deleteCategoryBtn.setEnabled(true);
                editCategoryBtn.setEnabled(false);
            } else {
                // no any category chosen - can't delete & edit
                deleteCategoryBtn.setEnabled(false);
                editCategoryBtn.setEnabled(false);
                categoryEditForm.setVisible(false);
            }
        });
        // refresh Grid state
        this.updateCategoryList();
    }

    private void buildLayout() {
        // filters with close button
        CssLayout filteringByName = new CssLayout();
        filteringByName.addComponents(filterByName, clearFilterByNameBtn);
        filteringByName.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        // tools bar - filters & buttons
        HorizontalLayout control = new HorizontalLayout(filteringByName,
                addCategoryBtn, deleteCategoryBtn, editCategoryBtn);
        control.setMargin(false);
        control.setWidth("100%");
        control.setExpandRatio(filteringByName, 1);

        // content - categoryList & categoryEditForm
        HorizontalLayout categoryContent = new HorizontalLayout(categoryList, categoryEditForm);
        categoryList.setSizeFull();             // size 100% x 100%
        categoryEditForm.setSizeFull();
        categoryContent.setMargin(false);
        categoryContent.setWidth("100%");
        categoryContent.setExpandRatio(categoryList, 2);
        categoryContent.setExpandRatio(categoryEditForm, 1);

        // Compound view parts and allow resizing
        this.addComponents(control, categoryContent);
        this.setSpacing(true);
        this.setMargin(false);
        this.setWidth("100%");
    }

    public void updateCategoryList() {
        List<Category> categoryList = categoryService.findAll(filterByName.getValue());
        this.categoryList.setItems(categoryList);
    }
}
