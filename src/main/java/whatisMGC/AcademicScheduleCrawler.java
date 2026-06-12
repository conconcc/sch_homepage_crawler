package whatisMGC;

import java.time.LocalDateTime;
import java.util.List;

import org.jsoup.nodes.Document;

/**
 * Crawls the SCH academic schedule page and stores parsed schedules.
 */
public class AcademicScheduleCrawler {
    private static final String ACADEMIC_SCHEDULE_URL = "https://home.sch.ac.kr/sch/05/010000.jsp";

    private final HtmlFetcher htmlFetcher;
    private final AcademicScheduleParser parser;
    private final DBManager dbManager;

    public AcademicScheduleCrawler(HtmlFetcher htmlFetcher, AcademicScheduleParser parser, DBManager dbManager) {
        this.htmlFetcher = htmlFetcher;
        this.parser = parser;
        this.dbManager = dbManager;
    }

    public boolean crawlAndSaveAcademicSchedule() {
        System.out.println("\n=== [Academic schedule crawl started] ===");
        System.out.println("Base URL: " + ACADEMIC_SCHEDULE_URL);

        try {
            Document doc = htmlFetcher.getHTMLDocument(ACADEMIC_SCHEDULE_URL);
            int targetYear = parser.extractYear(doc);
            String crawlUrl = ACADEMIC_SCHEDULE_URL;

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nov1 = LocalDateTime.of(now.getYear(), 11, 1, 0, 0, 0);
            if (now.isAfter(nov1)) {
                System.out.println("After November 1; trying next-year schedule page.");
                String nextYearUrl = parser.extractNextYearUrl(doc);
                if (nextYearUrl != null) {
                    crawlUrl = nextYearUrl;
                    doc = htmlFetcher.getHTMLDocument(nextYearUrl);
                    targetYear = parser.extractYear(doc);
                } else {
                    System.err.println("Warning: next-year schedule URL was not found; using current page.");
                }
            }

            List<AcademicScheduleInfo> schedules = parser.parseAcademicSchedule(doc, targetYear);
            System.out.println("Parsed schedule count: " + schedules.size());

            if (schedules.isEmpty()) {
                System.err.println("Warning: no academic schedule items were parsed.");
                return false;
            }

            System.out.println("Deleting existing academic schedules for " + targetYear + "...");
            dbManager.deleteAcademicSchedulesByYear(targetYear);

            System.out.println("Saving academic schedules...");
            dbManager.saveAcademicSchedules(schedules);

            int savedCount = dbManager.getAcademicSchedulesByYear(targetYear).size();
            if (savedCount < schedules.size()) {
                System.err.println("Academic schedule DB verification failed. Parsed: "
                        + schedules.size() + ", saved: " + savedCount);
                return false;
            }

            System.out.println("\nAcademic schedule crawl completed.");
            System.out.println("  - URL: " + crawlUrl);
            System.out.println("  - Year: " + targetYear);
            System.out.println("  - Saved items: " + savedCount);

            return true;
        } catch (Exception e) {
            System.err.println("Academic schedule crawl failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<AcademicScheduleInfo> getSchedulesByYear(int year) {
        return dbManager.getAcademicSchedulesByYear(year);
    }
}
