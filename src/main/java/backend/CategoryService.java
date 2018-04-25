package backend;

import backend.enums.HotelCategory;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CategoryService {
    private static CategoryService instance;
    private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());

    private final HashMap<Long, Category> categories = new HashMap<>();
    private long nextId = 0;

    private CategoryService() {
    }

    public static CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
            instance.ensureTestData();
        }
        return instance;
    }

    public synchronized List<Category> findAll() {
        return findAll(null);
    }

    public synchronized List<Category> findAll(String stringFilter) {
        List<Category> categoryList = new ArrayList<>();
        for (Category category : categories.values()) {
            try {
                boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
                        || category.toString().toLowerCase().contains(stringFilter.toLowerCase());
                if (passesFilter) {
                    categoryList.add(category.clone());
                }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(CategoryService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        categoryList.sort(new Comparator<Category>() {

            @Override
            public int compare(Category o1, Category o2) {
                return (int) (o2.getId() - o1.getId());
            }
        });
        return categoryList;
    }

    public synchronized long count() {
        return categories.size();
    }

    public synchronized void delete(Category value) {
        categories.remove(value.getId());
    }

    public synchronized boolean save(Category entry) {
        if (entry == null) {
            LOGGER.log(Level.SEVERE, "backend.Category is null.");
            return false;
        }

        // check if category name is already in saved List
        if (isCategoryNameInList(entry)) {
            return false;
        }

        // category is new - can save
        if (entry.getId() == null) {
            entry.setId(nextId++);
        } else {
            // category was edited - refresh category in the hotels Map
            Map<Long, Hotel> hotelMap = HotelService.getInstance().getHotels();
            for (Map.Entry<Long, Hotel> mapEntry : hotelMap.entrySet()) {
                if (mapEntry.getValue().getCategory() != null
                        && entry.getId().equals(mapEntry.getValue().getCategory().getId()) ) {
                    mapEntry.getValue().setCategory(entry);
                }
            }
        }
        try {
            entry = (Category) entry.clone();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        categories.put(entry.getId(), entry);
        return true;
    }

    public synchronized boolean isCategoryNameInList(Category category) {
        if (category == null) {
            return false;
        }

        // check if category name is already in saved List
        List<String> categoryNameList = this.findAll()
                                            .stream().map( e -> e.getName().toLowerCase())
                                            .collect(Collectors.toList());
        String incomeEntryName = category.getName() != null
                ? category.getName().toLowerCase()
                : "";
        return categoryNameList.contains(incomeEntryName);
    }

    private void ensureTestData() {
        if (findAll().isEmpty()) {
            Arrays.stream(HotelCategory.values())
                  .forEach(enumItem -> {
                Category category = new Category(enumItem.name());
                this.save(category);
            });
        }
    }
}
