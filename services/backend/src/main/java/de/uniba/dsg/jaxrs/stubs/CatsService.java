package de.uniba.dsg.jaxrs.stubs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.uniba.dsg.jaxrs.models.Cat;

public class CatsService {
    private static final List<Cat> CATS = new ArrayList<>();

    static {
        List<Cat> cats = Arrays.asList(
                new Cat(1,"Garfield", "http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-garfield-300x179.jpg", "Garfield"),
                new Cat(2,"Hello Kitty", "http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-hellokitty-300x261.jpg", ""),
                new Cat(3,"Sylvester", "http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-sylvester-1-200x300.jpg", "Looney Toons"),
                new Cat(4,"Tigger", "http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-tigger-300x238.jpg", "Winnie the Pooh"),
                new Cat(5,"Simba", "http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-simba-300x211.jpg", "The Lion King"),
                new Cat(6,"Nala", "http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-nala-2.jpg", "The Lion King"),
                new Cat(7,"Nermal", "http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-nermal-300x286.jpg", "Garfield"),
                new Cat(8,"Figaro", "http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-figaro-300x233.jpg", "Pinocchio"),
                new Cat(9,"Grumpy Cat", "http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-grumpy-cat-300x158.jpg", ""),
                new Cat(10,"Mr. Bigglesworth", "http://www.findcatnames.com/wp-content/uploads/2016/06/famous-cat-names-mrbigglesworth-300x198.jpg", "Austin Powers")
        );
        CATS.addAll(cats);
    }

    public static List<Cat> getFamousCats() {
        return CATS;
    }

    public static List<Cat> getFamousCats(String movie) {
        return CATS.stream().filter(c -> c.movie.equalsIgnoreCase(movie)).collect(Collectors.toList());
    }

    public static Cat getFamousCat(int id) {
        return CATS.stream().filter(c -> c.id == id).findFirst().orElse(null);
    }

    public static boolean deleteCat(int id) {
        Cat cat = getFamousCat(id);
        return CATS.remove(cat);
    }

    public static Cat addCat(Cat newCat) {
        if (newCat == null) {
            return null;
        }
        int highestId = CATS.stream().map(Cat::getId).max(Comparator.naturalOrder()).orElse(0);
        newCat.setId(highestId + 1);
        CATS.add(newCat);

        return newCat;
    }

    public static Cat updateCat(int id, Cat updatedCat) {
        Cat cat = getFamousCat(id);

        if (cat == null || updatedCat == null) {
            return null;
        }
        Optional.ofNullable(updatedCat.getName()).ifPresent(d -> cat.setName(d));
        Optional.ofNullable(updatedCat.getImageUrl()).ifPresent(d -> cat.setImageUrl(d));
        Optional.ofNullable(updatedCat.getMovie()).ifPresent(d -> cat.setMovie(d));

        return cat;
    }
}
