import java.util.List;

public class Trace {
    
    StringBuilder chaineDetaille=new StringBuilder();
    StringBuilder chaineAbrege=new StringBuilder();

    public Trace(FactBase fb,RuleBase rb){
        chaineAbrege.append("systeme expert : \n  \nla base de fait :\n").append(fb.toString()).append("\nla base de regle : \n ").append(rb.toString()).append("\n");
        chaineDetaille.append("systeme expert : \n  \nla base de fait :\n").append(fb.toString()).append("\nla base de regle : \n ").append(rb.toString()).append("\n");
    }

    String afficherSysteme(FactBase fb,RuleBase rb){
        return ("la base de fait :\n"+fb.toString()+"\nla base de regle : \n "+(rb.toString())+"\n");
    }

    void add(FactBase fb){
        chaineDetaille.append("\nla base de fait :\n").append(fb.toString());
    }

    void afficherBf(FactBase fb){
        System.out.println ("la base de fait :\n"+fb.toString());
    }    
    
    void afficherBr(RuleBase rb){
        System.out.println ("la base de fait :\n"+rb.toString());
    }

    void add(String txt,Boolean abrege){
        if(abrege){
        this.chaineAbrege.append("\n").append(txt);}
        this.chaineDetaille.append("\n").append(txt);
    }

     void add(Fact f){
        this.chaineDetaille.append("\nla base de fait a été étendu avec l'ajout du fait : ").append(f.toString());
    }
    void add(Rule r){
        this.chaineDetaille.append("\nla régle choisis est :\n").append(r.toString());
    }
    void tracerAbrege(){
        System.out.println(chaineAbrege.toString());
    } 
    void tracerDetaille(){
        System.out.println(chaineDetaille.toString());
    }
    void add(List<RulePacket> packets){
        StringBuilder s=new StringBuilder();
        s.append("\nvoici les paquets de groupement des regles : ");
        for(int i=0;i<packets.size();i++){
            s.append("\n    le paquet numéro "+i);
            for(int j=0;j<packets.get(i).size();j++){
                s.append("\n        la regle : ").append(packets.get(i).getRules().get(j));
            }
        }
        this.chaineAbrege.append(s.toString());
        this.chaineDetaille.append(s.toString());
    }

    void fin(FactBase fb,RuleBase rb){
        this.chaineAbrege.append("FIN.\n voicis les Base de Faits et de Regle:").append(afficherSysteme(fb,rb));
        this.chaineDetaille.append("\nles Base de Faits et de Regle:").append(afficherSysteme(fb,rb));
    }
    void error(){
        System.out.println("erreor \nvoicis le format à repecter pour créer le systéme :\nRegle :exemple: SI nom1>num ET nom2 ALORS nom3=false \nFait: exemple : nom1 \nnom2=false \nnom3=chaine      \nnom4=num");
        System.out.println("pour lancer le chainage ecrire 'chainge avant'/'chainage arriere' apres avoir creer le systeme");
        System.out.println("pour afficher la trace detaille ecrire tracer");
        System.out.println("pour afficher la trace  abrege ecrire tracer abrege");
    }
}
