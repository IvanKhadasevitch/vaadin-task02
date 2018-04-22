package backend;

import lombok.Data;

import java.io.Serializable;

@Data
public class Category implements Serializable, Cloneable {

    public final static String NULL_CATEGORY_REPRESENTATION = "No category";

    private Long id;

    private String name = "";

    public Category (String name) {
        this.name = name == null
                ? ""
                : name;
    }

    public boolean isPersisted() {
        return id != null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected Category clone() throws CloneNotSupportedException {
        return (Category) super.clone();
    }
}
