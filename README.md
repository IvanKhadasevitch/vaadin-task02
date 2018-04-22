# vaadin-task02
Vaadin. Menu with navigation. Realesed CRUD operation with list items, filtering. Work with entery form for entety - validation &amp; convertion fields.

0. ѕол€ rating, operatesFrom класса Hotel сделать Interger, Long и добавить к ним валидацию.
     о всем пол€м на форме добавить ToolTip с описанием значени€ пол€.
    ¬се пол€ кроме описани€ отел€ дожны быть об€зательными к заполнению.
    Rating может быть от 0 до 5. OperatesFrom должно быть в прошлом.

1. ƒобавить сервис дл€ редактировани€ списка категорий. ƒобавить отдельную страничку дл€ операций со списком категорий.
    ќперации: add category, delete category, edit category
    Ќа старте приложени€ уже должны быть категории, которые раньше были в enum.

2. ќбновить HotelService и HotelForm, так чтобы они использовали новый список категорий.
    ѕри удалении категории, на списке отелей (удаленную) категорию отображать как No category (NullRepresentation)

3. ¬се списки HotelList и CategoryList должны быть MultiSelect. ƒл€ редактировани€ добавить кнопку Edit (Edit hotel, Edit category).
    ≈сли выбрано несколько записей, доступны только кнопки add и delete. ≈сли выбрана одна запись, доступны все кнопки.
    ≈сли не выбрано ничего, доступна только add.

4. ƒобавить меню с 2 пунктами Hotel и Category. ѕри клике на соответствующий пункт отображаетс€ страница со списком (либо отелей, либо категорий).

