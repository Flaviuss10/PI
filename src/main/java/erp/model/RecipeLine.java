package erp.model;

/**
 * Clasa pentru modelarea unei linii din reteta
 * O linie din reteta este de tipul Ingredient - Cantitate
 * O reteta contine mai multe linii de reteta
 */
public class RecipeLine {

    /** Ingredientul folosit(materia prima) */
    private RawMaterial ingredient;

    /** Cantitatea folosita */
    private double cantitatePerUnitate;

    /**
     * Constructorul
     */
    public RecipeLine(RawMaterial ingredient, double cantitatePerUnitate){
        this.ingredient = ingredient;
        this.cantitatePerUnitate = cantitatePerUnitate;
    }

    /**
     * Getter pentru unitatea de masura a ingredientului folosit
     * Apeleaza getter-ul din Clasa RawMaterial
     * @return unitatea de masura
     */
    public String getUnit(){
        return ingredient.getUnit();
    }

    /**
     * Getter pentru materia prima folosita
     * @return RawMaterial
     */
    public RawMaterial getIngredient() {
        return ingredient;
    }

    /**
     * Getter pentru cantitatea folosita
     * @return numar real - cantitatea
     */
    public double getCantitatePerUnitate() {
        return cantitatePerUnitate;
    }

    /**
     * Suprascrierea metodei de afisare
     * @return String
     */
    @Override
    public String toString() {
        return ingredient.getName() + " -> " + cantitatePerUnitate + " " + ingredient.getUnit();
    }
}
