import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountWords {
    public static void countWords(String str) {
        if (str == null || str.trim().isEmpty()) {
            System.out.println("No words found.");
            return;
        }

        Map<String, Integer> wordCount = new HashMap<>();
        String[] words = str.trim().split("\\s+");

        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(wordCount.entrySet());
        sortedList.sort(
                Comparator.<Map.Entry<String, Integer>>comparingInt(entry -> entry.getValue())
                        .reversed()
                        .thenComparing(Map.Entry::getKey)
        );

        System.out.println("Top 3 most frequent words:");
        for (int i = 0; i < Math.min(3, sortedList.size()); i++) {
            Map.Entry<String, Integer> entry = sortedList.get(i);
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        System.out.println("Case 1: sample sentence");
        countWords("the cat and the dog and the bird");

        System.out.println("\nCase 2: different order but same tie");
        countWords("dog bird cat");

        System.out.println("\nCase 3: empty string");
        countWords("");

        System.out.println("\nCase 4: single word");
        countWords("hello");
    }
}