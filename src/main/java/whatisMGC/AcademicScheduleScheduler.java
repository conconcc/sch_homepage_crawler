package whatisMGC;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Runs the academic schedule crawler every year on November 1 at midnight.
 */
public class AcademicScheduleScheduler {
    private static final String SCHEDULER_THREAD_NAME = "AcademicScheduleScheduler";

    private final AcademicScheduleCrawler crawler;
    private ScheduledExecutorService scheduler;

    public AcademicScheduleScheduler(AcademicScheduleCrawler crawler) {
        this.crawler = crawler;
    }

    public void start() {
        System.out.println("\n=== [Academic schedule scheduler started] ===");

        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("Academic schedule scheduler is already running.");
            return;
        }

        scheduler = Executors.newScheduledThreadPool(1, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(SCHEDULER_THREAD_NAME);
            thread.setDaemon(false);
            return thread;
        });

        scheduleNextRun();
        System.out.println("Academic schedule scheduler is ready.");
    }

    private void scheduleNextRun() {
        LocalDateTime nextRun = nextNovember1();
        long delaySeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), nextRun);
        if (delaySeconds <= 0) {
            delaySeconds = 1;
        }

        System.out.println("Current time: " + LocalDateTime.now());
        System.out.println("Next run time: " + nextRun);
        System.out.println("Wait time: " + formatDuration(delaySeconds));

        scheduler.schedule(() -> {
            System.out.println("\n[Academic schedule scheduled crawl @ " + LocalDateTime.now() + "]");
            try {
                crawler.crawlAndSaveAcademicSchedule();
            } catch (Exception e) {
                System.err.println("Academic schedule crawl failed: " + e.getMessage());
                e.printStackTrace();
            }
            scheduleNextRun();
        }, delaySeconds, TimeUnit.SECONDS);
    }

    private LocalDateTime nextNovember1() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextNov1 = LocalDateTime.of(now.getYear(), 11, 1, 0, 0, 0);
        if (!now.isBefore(nextNov1)) {
            nextNov1 = nextNov1.plusYears(1);
        }
        return nextNov1;
    }

    private String formatDuration(long seconds) {
        long days = seconds / (24 * 3600);
        long hours = (seconds % (24 * 3600)) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%d days %d hours %d minutes %d seconds", days, hours, minutes, secs);
    }

    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("\n[Stopping academic schedule scheduler]");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            System.out.println("Academic schedule scheduler stopped.");
        }
    }

    public boolean isRunning() {
        return scheduler != null && !scheduler.isShutdown();
    }
}
