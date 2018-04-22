package backend;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@SuppressWarnings("serial")
public class Hotel implements Serializable, Cloneable {

    private Long id;

    private String name = "";

    private String address = "";

    private Integer rating = 0;

    private Long operatesFrom = LocalDate.now().toEpochDay();

//    private HotelCategory category;
    private Category category = new Category(null);

    private String url = "";

    private String description = "";

    public boolean isPersisted() {
        return id != null;
    }

    @Override
    public String toString() {
        return name + " " + rating +"stars " + address;
    }

    @Override
    protected Hotel clone() throws CloneNotSupportedException {
        return (Hotel) super.clone();
    }


}
