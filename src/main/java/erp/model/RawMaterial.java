package erp.model;

public class RawMaterial {

    private int id;
    private String name;
    private String unit;

    public RawMaterial(int id, String name, String unit){
        this.id = id;
        this.name = name;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public String afisare(){
        return id + "-" + name;
    }

    public String getName() {
        return name;
    }

    public String getUnit(){ return unit;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Materie Prima-").append(name).append("-");
        sb.append(unit);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RawMaterial)) return false;
        RawMaterial that = (RawMaterial) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
