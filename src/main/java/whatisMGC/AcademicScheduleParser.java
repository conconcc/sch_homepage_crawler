package whatisMGC;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Parses monthly academic schedule items from the SCH academic calendar page.
 */
public class AcademicScheduleParser {

    public List<AcademicScheduleInfo> parseAcademicSchedule(Document doc) {
        return parseAcademicSchedule(doc, extractYear(doc));
    }

    public List<AcademicScheduleInfo> parseAcademicSchedule(Document doc, int year) {
        List<AcademicScheduleInfo> schedules = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            Element monthSection = doc.selectFirst("div.section.month_" + month);
            if (monthSection == null) {
                System.out.println("Warning: month section not found: " + month);
                continue;
            }

            Element listDiv = monthSection.selectFirst("div.list");
            if (listDiv == null) {
                continue;
            }

            for (Element dt : listDiv.select("dl dt")) {
                String eventDate = dt.text().trim();
                Element currentElement = dt.nextElementSibling();

                while (currentElement != null && "dd".equals(currentElement.tagName())) {
                    String eventName = currentElement.text().trim();
                    if (!eventDate.isEmpty() && !eventName.isEmpty()) {
                        schedules.add(new AcademicScheduleInfo(year, month, eventDate, eventName));
                    }
                    currentElement = currentElement.nextElementSibling();
                }
            }
        }

        System.out.println("Parsed academic schedule items: " + schedules.size());
        return schedules;
    }

    public int extractYear(Document doc) {
        Element yearElement = doc.selectFirst("div.year h4");
        if (yearElement != null) {
            try {
                return Integer.parseInt(yearElement.text().trim().replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse academic schedule year: " + yearElement.text());
            }
        }
        return LocalDateTime.now().getYear();
    }

    public String extractNextYearUrl(Document doc) {
        Element nextButton = doc.selectFirst("a.next");
        if (nextButton != null) {
            String href = nextButton.attr("href");
            if (href != null && !href.isEmpty()) {
                if (!href.startsWith("http")) {
                    href = "https://home.sch.ac.kr" + (href.startsWith("/") ? "" : "/") + href;
                }
                System.out.println("Next academic schedule URL: " + href);
                return href;
            }
        }
        return null;
    }
}
