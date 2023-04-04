import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        final List<Callable<Boolean>> threads = new ArrayList<>();
        int numberOfThreads = 1000;

        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(() -> {
                String str = generateRoute("RLRFR", 100);
                int number = str.length() - str.replaceAll("R", "").length();
                //счетоводство символов через поток, как альтернатива счетоводству через реплейс
//                int number = (int) (str
//                        .chars()
//                        .filter(s -> s == 'R')
//                        .count());
                synchronized (sizeToFreq) {
                    if (!sizeToFreq.containsKey(number)) {
                        sizeToFreq.compute(number, (k, v) -> v = 1);
                    } else {
                        sizeToFreq.computeIfPresent(number, (k, v) -> v + 1);
                    }
                }
                return true;
            });
        }
        threadPool.invokeAll(threads);
        threadPool.shutdown();
        System.out.println(sizeToFreq);

        int maxValue = Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getValue();
        final int[] maxKey = new int[1];    //выглядит жутко, но по-другому не работает
        sizeToFreq.entrySet().forEach(s -> {
            if (s.getValue() == maxValue) {   //не пойму почему сравнить значение с интом можно, а присвоить интовой переменной - нельзя
                System.out.println(s.getKey());
                maxKey[0] = s.getKey();
            }
        });

        System.out.print("Самое частое количество повторений " + maxKey[0] + " (встретилось " + maxValue + " раз)");
        sizeToFreq.remove(maxKey[0]);
        System.out.println("\nДругие размеры:");
        sizeToFreq.entrySet().forEach(s -> {
            System.out.println("- " + s.getKey() + " (" + s.getValue() + " раз)");
        });
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}