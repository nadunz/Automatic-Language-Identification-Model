
/**
 * Main class for this system
 */
public class Main {

    public static void main(String[] args) {

        // create the learning model
        LearningModel model = new LearningModel();
        model.createLanguageModels();

        IdentificationModel im = new IdentificationModel();

        for (int i = 0; i < 7; i++) {
            String name = "Unknown" + (i + 1);
            System.out.printf("%s -> %s\n", name, im.predictLanguage(name));
        }

    }
}
