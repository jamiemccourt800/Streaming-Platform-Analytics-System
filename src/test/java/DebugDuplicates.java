import org.example.model.Interaction;
import org.example.util.DataLoader;
import java.util.*;

public class DebugDuplicates {
    public static void main(String[] args) throws Exception {
        List<Interaction> d1 = DataLoader.loadFromFile("data/Dataset_1.txt");
        List<Interaction> d2 = DataLoader.loadFromFile("data/Dataset_2.txt");
        
        System.out.println("Checking for duplicates...\\n");
        
        int dupCount = 0;
        for (Interaction i1 : d1) {
            for (Interaction i2 : d2) {
                if (i1.equals(i2)) {
                    dupCount++;
                    System.out.println("DUPLICATE FOUND:");
                    System.out.println("  Dataset 1: " + i1);
                    System.out.println("  Dataset 2: " + i2);
                    System.out.println();
                }
            }
        }
        
        System.out.println("Total duplicates found: " + dupCount);
        System.out.println("Expected: 3 (I016=I053, I025=I055, I043=I081)");
        
        Set<Interaction> allUnique = new HashSet<>();
        allUnique.addAll(d1);
        allUnique.addAll(d2);
        
        Map<String, Integer> userCounts = new HashMap<>();
        for (Interaction i : allUnique) {
            userCounts.put(i.getUserId(), userCounts.getOrDefault(i.getUserId(), 0) + 1);
        }
        
        System.out.println("\\nUser interaction counts after deduplication:");
        List<String> users = new ArrayList<>(userCounts.keySet());
        Collections.sort(users);
        for (String user : users) {
            if (user.matches("U00[1-5]")) {
                System.out.println(user + ": " + userCounts.get(user));
            }
        }
    }
}
