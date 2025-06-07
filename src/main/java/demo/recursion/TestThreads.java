package demo.recursion;

import demo.Link;

import java.util.LinkedHashSet;
import java.util.Set;

public class TestThreads {

public static void main(String[] args) {
    Set<Link> links = new LinkedHashSet<>();
    links.add(new Link("a","b"));
    links.add(new Link("a","b"));
    links.add(new Link("a","b"));
    links.add(new Link("a","b"));
    System.out.println(links);

    Set<String> strings = new LinkedHashSet<>();
    strings.add("a");
    strings.add("b");
    strings.add("c");
    strings.add("d");
    strings.add("a");
    System.out.println(strings);
}

//        Runnable runnableTask = () -> {
//            try {
//                TimeUnit.MILLISECONDS.sleep(300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        };
//
//        Callable<String> callableTask = () -> {
//            TimeUnit.MILLISECONDS.sleep(300);
//            lookForLinksIn(startingPoint);
//            return "Task's execution";
//        };
//
//        List<Callable<String>> callableTasks = new ArrayList<>();
//        callableTasks.add(callableTask);
//        callableTasks.add(callableTask);
//        callableTasks.add(callableTask);
//        ScheduledExecutorService executorService = null;
//        try {
//            executorService = Executors
//                    .newSingleThreadScheduledExecutor();
//
//            executorService.execute(runnableTask);
//            Future<String> future =
//                    executorService.submit(callableTask);
//            String result = future.get();
//
//            System.out.println(
//                    "Task's execution completed: " + result
//            );
//            List<Future<String>> futures = executorService.invokeAll(callableTasks);
//
//
//            List<Runnable> notExecutedTasks = executorService.shutdownNow();
//
//            executorService.shutdown();
//
//            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
//                executorService.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            executorService.shutdownNow();
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
}
