
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;

public class IdentificationModel {

    private int numOfLanguages;

    private final String[] languageNames = {"English", "French", "German", "Italian", "Spanish"};
    private final Locale[] languages = {Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN,
        Locale.ITALIAN, new Locale("ES")};

    private double[] scrores;

    private Hashtable<String, Double>[] hashtables;

    public IdentificationModel() {
        numOfLanguages = 5;
        hashtables = new Hashtable[numOfLanguages];

        scrores = new double[numOfLanguages];

        for (int i = 0; i < numOfLanguages; i++) {
            hashtables[i] = readModelFile(i);
        }
    }

    public String predictLanguage(String fileName) {
        String content = readTestFile(fileName);
        ArrayList<String> extractWords = extractWords(content);
        ArrayList<String> trigrams = getTrigrams(extractWords);

        for (int i = 0; i < numOfLanguages; i++) {
            
            double score = 0;
            // calculate 5 scores
            for (String trigram : trigrams) {
                score += hashtables[i].containsKey(trigram) ? hashtables[i].get(trigram) : 0;
            }
            
            scrores[i] = score;
        }
        return languageNames[maxScore()];

    }
    
    private int maxScore() {
        double max = Double.MIN_VALUE;
        int index = 0;
        for (int i = 0; i < numOfLanguages; i++) {
            if(scrores[i] > max) {
                max = scrores[i];
                index = i;
            }
        }
        return index;
    }

    private Hashtable<String, Double> readModelFile(int languageIndex) {

        Hashtable<String, Double> hashtable = new Hashtable<>();

        String filePath = "Models/" + languageNames[languageIndex] + "Model.txt";

        try {
            Reader reader = new InputStreamReader(new FileInputStream(filePath),
                    StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                String[] split = s.trim().split(" ");
                String trigram = split[0].trim();
                double prob = Double.parseDouble(split[1].trim());
                hashtable.put(trigram, prob);
            }
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashtable;
    }

    public static ArrayList<String> getTrigrams(ArrayList wList) {
        ArrayList<String> trigrams = new ArrayList<>();
        for (int i = 0; i < wList.size(); i++) {
            String word = wList.get(i).toString().trim();
            for (int m = 0; m < word.length() - 2; m++) {
                trigrams.add(word.substring(m, m + 3));
            }
        }
        return trigrams;
    }

    public static ArrayList<String> extractWords(String inputText) {

        ArrayList wList = new ArrayList();
        BreakIterator wordIterator = BreakIterator.getWordInstance(Locale.ENGLISH);
        wordIterator.setText(inputText);
        int start = wordIterator.first();
        int end = wordIterator.next();
        while (end != BreakIterator.DONE) {
            String word = inputText.substring(start, end);
            word = word.toLowerCase();
            if (Character.isLetter(word.charAt(0)) && word.length() > 1) {
                wList.add(word);
            }
            start = end;
            end = wordIterator.next();
        }
        return wList;

    }

    public String readTestFile(String fileName) {

        String filePath = "Testing/" + fileName + ".txt";

        StringBuilder file = new StringBuilder();
        try {
            Reader reader = new InputStreamReader(new FileInputStream(filePath),
                    StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                file.append(s + "\n");
            }
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.toString();
    }

}
