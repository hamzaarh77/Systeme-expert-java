import java.util.*;

public class Fact {
    private String name;
    private Object value;
    Comparator comparator;
    int tour; //stocke le tour ou on a deduit ce fait

    public Fact(String name,Comparator c,   Object vv){
        this.name = name ;
        this.value = vv;
        this.comparator=c;
        this.tour=0;

    }
    public Fact(String name,Object vv){
        this.name = name ;
        this.value = vv;
        this.comparator=Comparator.EQUALS;
        this.tour=0;

    }
    public Fact(String name)
    {
        this.name=name;
        this.value = true;
        this.comparator= Comparator.EQUALS;
        this.tour=0;     
    }

    //methodes
    public boolean equals (Object obj){ //a revoir faux car 1 et non 0+
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fact fact = (Fact) obj;
        return name.equals(fact.name) && ( ( value != null && value.equals(fact.value))|| value == null && value.equals( null));
    }


    void updateTour(int tour){
        this.tour=tour;
    }

    int getTour(){
        return tour;
    }

    public boolean detecterIncoherence(List<Fact> facts){
        // detecter une incoherence entre le fait et notre base de faits
        // le probleme de cette implementation et que les arguments de deux faits egaux doivent etre dans le meme ordre pour pouvoir detecter qu'ils sont egaux
        for (Fact f : facts){
            if(f.name.equals(this.name) &&   f.value != this.value)
                return true;
        }
        return false ;

        //on peut faire ça alors ?
        //mais la valeur not elle est propre à chaque arguments non ?
/*
        boolean same=true;
        for (Fact f : facts){
            if(f.name.equals(this.name) && f.value != this.value) {
                same=true;
                for(String arg: f.arguments){
                    if(! arguments.contains(arg)){
                        same=false;
                        break;
                    }
                }
                if(same){
                    return true;
                }
            }
                
        }
        return false ;
        
         */
    }

    public String toString1(){
        StringBuilder db = new StringBuilder();
        if(this.value.equals(false))
            db.append("NOT ");
        db.append(this.name );
        
        if(valeurString().length()>0){
            db.append("=");
            db.append(valeurString());
        }
        return db.toString();
    }

    String valeurString(){
        if(!( value!=null && value  instanceof Boolean)){
            return value.toString();
        }else{
            return "";
        }}

    @Override
    public int hashCode(){
        return Objects.hash(name, value);
    }



    // getters and setters 
    public String getname() {
        return this.name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public boolean isValeurVerite() {
        return this.value.equals(true);
    }

    public Object getValue() {
        return this.value;
    }
    public String getName() {
        return this.name;
    }

    public void setValue(Object value) {
        this.value =  value;
    }
    public boolean evaluer(FactBase fb) {  // Chercher le fait correspondant dans la base de faits
        for (Fact fait : fb.getFacts()) {
            if (fait.getName().equals(getName())) {
                // Comparer la valeur du fait avec la valeur de la condition 
                return this.comparator.compare(fait.getValue(), getValue());
            }
        }
        // Si le fait n'existe pas dans la base, la condition est insatisfaite
        return false;
        
    }

    Comparator getComparator(){
        return this.comparator;
    }
    public String toString(){
        StringBuilder db = new StringBuilder();
        if(getValue().equals(false))
            db.append("NOT ");
        db.append(getName() );
        if(getValue().equals(true) || getValue().equals(false)){
            return db.toString();
        }
        db.append(comparator.getSymbol()).append(getValue().toString());
        return db.toString();}
}
