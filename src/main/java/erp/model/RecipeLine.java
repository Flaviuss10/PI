package erp.model;

public class RecipeLine {
    private RawMaterial ingredient;
    private double cantitatePerUnitate;

    public RecipeLine(RawMaterial ingredient, double cantitatePerUnitate){
        this.ingredient = ingredient;
        this.cantitatePerUnitate = cantitatePerUnitate;
    }

    public RawMaterial getIngredient() {
        return ingredient;
    }

    public double getCantitatePerUnitate() {
        return cantitatePerUnitate;
    }

    @Override
    public String toString() {
        return ingredient.getName() + " -> " + cantitatePerUnitate + " " + ingredient.getUnit();
    }
}
