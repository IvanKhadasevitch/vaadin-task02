package views.categoryveiw;

import backend.Category;
import backend.CategoryService;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;

public class CategoryEditForm extends FormLayout {
    private CategoryView categoryView;

    private CategoryService categoryService = CategoryService.getInstance();
    @Getter
    private Category category;
    private Binder<Category> categoryBinder = new Binder<>(Category.class);

    private TextField name = new TextField("Category name:");

    private Button saveCategoryBtn = new Button("Save");
    private Button closeFormBtn = new Button("Close");


    public CategoryEditForm(CategoryView categoryView){
        this.categoryView = categoryView;

        this.setMargin(true);       // Enable layout margins. Affects all four sides of the layout
        this.setVisible(false);

        // form tools - buttons
        HorizontalLayout buttons = new HorizontalLayout(saveCategoryBtn, closeFormBtn);
        buttons.setSpacing(true);

        // collect form components - form fields & buttons
        this.addComponents(name, buttons);

        // add ToolTip to the forms fields
        name.setDescription("Category name");

        // connect entity fields with form fields
        name.setRequiredIndicatorVisible(true);         // Required field
        categoryBinder.forField(name)
                      .asRequired("Every category must have name")
                      .bind(Category::getName, Category::setName);

        // buttons
        saveCategoryBtn.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        saveCategoryBtn.addClickListener(e -> saveCategory());
        closeFormBtn.addClickListener(e -> closeCategoryEditForm());
    }

    public void saveCategory() {
        // This will make all current validation errors visible
        BinderValidationStatus<Category> status = categoryBinder.validate();
        if (status.hasErrors()) {
            Notification.show("Validation error count: "
                    + status.getValidationErrors().size(), Notification.Type.WARNING_MESSAGE);
        }

        // save validated Category with not empty fields
        if ( !status.hasErrors() ) {
            boolean isSaved = categoryService.save(getCategory());
            if (isSaved) {
                categoryView.updateCategoryList();
                this.setVisible(false);
                Notification.show("Saved category with name: " + this.getCategory().getName(),
                        Notification.Type.HUMANIZED_MESSAGE);
            } else {
                Notification.show("Can't save category with name: " + getCategory().getName(),
                        Notification.Type.ERROR_MESSAGE);
            }
        }
        categoryView.getAddCategoryBtn().setEnabled(true);
    }

    public void setCategory(Category category) {
        this.setVisible(true);
        this.category = category;

        // connect entity fields with form fields
        categoryBinder.setBean(category);
    }

    public void closeCategoryEditForm() {
        this.setVisible(false);
        categoryView.getAddCategoryBtn().setEnabled(true);
        categoryView.getCategoryList().deselectAll();
        categoryView.updateCategoryList();
    }
}
