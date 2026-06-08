package order;

import java.util.List;

public class Order {

    private List<String> ingredients;

    public Order(){    }

    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredient(List<String> ingredients) {
        this.ingredients = ingredients;
    }


}
