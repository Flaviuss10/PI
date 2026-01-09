package erp.model;

/**
 * Clasa care modeleaza o materie prima din viata reala
 */
public class RawMaterial {

    /** id-ul unic al materiei prime */
    private int id;

    /** Numele materiei prime */
    private String name;

    /** Unitatea de masura (kg, litri etc.)*/
    private String unit;

    /**
     * Constructor pentru materia prima
     */
    public RawMaterial(int id, String name, String unit){
        this.id = id;
        this.name = name;
        this.unit = unit;
    }

    /**
     * Getter pentru id-ul materiei prime
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter pentru numele materiei prime
     * @return String - nume materie prima
     */
    public String getName() {
        return name;
    }

    /**
     * Getter pentru unitatea de masura
     * @return String
     */
    public String getUnit(){ return unit;}

    /**
     * Suprascrierea metodei de afisare
     * @return String - descrierea materiei prime
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Materie Prima-").append(name).append("-");
        sb.append(unit);
        return sb.toString();
    }

    /**
     * Suprascrierea metodei de comparare cu un alt obiect
     * Necesara pentru folosirea Map, List
     * @param o  Obiectul cu care se compara instanta RawMaterial
     * @return True - obiectele sunt egale, False - altfel
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RawMaterial)) return false;
        RawMaterial that = (RawMaterial) o;
        return id == that.id;
    }

    /**
     * Suprascrierea metodei pentru hashcode
     * @return hashcode-ul calculat folosind id-ul materiei prime
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
