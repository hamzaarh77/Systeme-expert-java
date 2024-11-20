import java.io.IOException;

public class testParser {
    public static void main(String[] args) {
        try {
            // Parse the data from a file
            Moteur moteur = Parser.parseFromFile("src/testVarDemandable.txt");
            

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
    }
}
