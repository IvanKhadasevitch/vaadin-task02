package views.categoryveiw;

import backend.Category;
import backend.CategoryService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;
import ui.customcompanents.FilterWithClearBtn;

import java.util.List;
import java.util.Set;

public class CategoryView extends VerticalLayout implements View {

    final CategoryService categoryService = CategoryService.getInstance();

    private FilterWithClearBtn filterByName;

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
        filterByName = new FilterWithClearBtn("Filter by name...", e -> updateCategoryList());

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
            addCategoryBtn.setEnabled(true);       // switch on addNewCategory possibility
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
                // chosen only one category - can add & delete & edit
                addCategoryBtn.setEnabled(true);
                deleteCategoryBtn.setEnabled(true);
                editCategoryBtn.setEnabled(true);
            } else if (selectedCategories != null && selectedCategories.size() > 1) {
                // chosen more then one category - can delete & add
                categoryEditForm.setVisible(false);
                addCategoryBtn.setEnabled(true);
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
        Component[] controlComponents = {filterByName,
                addCategoryBtn, deleteCategoryBtn, editCategoryBtn};
        Component control = new TopCenterComposite(controlComponents);
        this.addComponent(control);
        this.setComponentAlignment(control, Alignment.TOP_CENTER);

        // content - categoryList & categoryEditForm
        Component[] categoryContentComponents = {categoryList, categoryEditForm};
        Component categoryContent = new TopCenterComposite(categoryContentComponents);
        this.addComponent(categoryContent);
        this.setComponentAlignment(categoryContent, Alignment.TOP_CENTER);


        // Compound view parts and allow resizing
        this.setSpacing(true);
        this.setMargin(false);
        this.setWidth("100%");
    }

    public void updateCategoryList() {
        List<Category> categoryList = categoryService.findAll(filterByName.getValue());
        this.categoryList.setItems(categoryList);
    }

    class TopCenterComposite extends CustomComponent {
        public TopCenterComposite(Component[] components) {
            HorizontalLayout layout = new HorizontalLayout(components);
            layout.setMargin(false);
            this.setSizeUndefined();
            this.setCompositionRoot(layout);
        }
    }
}
