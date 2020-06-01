
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Locale;

public class LearningModel {

    private int numOfLanguages;

    private final String[] languageNames = {"English", "French", "German", "Italian", "Spanish"};
    private final Locale[] languages = {Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN,
        Locale.ITALIAN, new Locale("ES")};

    public LearningModel() {
        numOfLanguages = 5;
    }

    public void createLanguageModels() {

        for (int i = 0; i < numOfLanguages; i++) {
            String content = readLanguageFile(i);
            ArrayList<String> extractWords = extractWords(content, languages[i]);
            ArrayList<String> trigrams = getTrigrams(extractWords);
            String out = createModel(trigrams);
            writeModel(i, out);
        }

    }

    private static ArrayList<String> getTrigrams(ArrayList wList) {
        ArrayList<String> trigrams = new ArrayList<>();
        for (int i = 0; i < wList.size(); i++) {
            String word = wList.get(i).toString().trim();
            for (int m = 0; m < word.length() - 2; m++) {
                trigrams.add(word.substring(m, m + 3));
            }
        }
        return trigrams;
    }

    private static ArrayList<String> extractWords(String inputText, Locale currentLocale) {

        ArrayList wList = new ArrayList();
        BreakIterator wordIterator = BreakIterator.getWordInstance(currentLocale);
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

    private void writeModel(int languageIndex, String content) {

        String filePath = "Models/" + languageNames[languageIndex] + "Model.txt";
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(filePath),
                    StandardCharsets.UTF_8);
            PrintWriter printWriter = new PrintWriter(writer);
            printWriter.write(content);
            writer.close();
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readLanguageFile(int languageIndex) {

        String filePath = "Learning/" + languageNames[languageIndex] + ".txt";

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

    private static String createModel(ArrayList<String> trigrams) {

        // create hash table to count frequencies
        Hashtable<String, Integer> hashtable = new Hashtable<>();

        for (String trigram : trigrams) {
            if (hashtable.get(trigram) != null) {
                int f = hashtable.get(trigram);
                hashtable.replace(trigram, f + 1);
            } else {
                hashtable.put(trigram, 1);
            }
        }

        StringBuilder theEnd = new StringBuilder();

        double total = trigrams.size();

        ArrayList<String> keys = new ArrayList<>(hashtable.keySet());

        // sort triagrams
        Collections.sort(keys);

        for (String triagram : keys) {
            int freq = hashtable.get(triagram);
            theEnd.append(triagram).append(" ").append((double) (freq / total));
            theEnd.append("\n");
        }
        return theEnd.toString();
    }

}
