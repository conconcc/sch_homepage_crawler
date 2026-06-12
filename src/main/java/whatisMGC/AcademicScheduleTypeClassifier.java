package whatisMGC;

/**
 * Classifies SCH academic schedule titles into calendar event types.
 */
public final class AcademicScheduleTypeClassifier {
    private AcademicScheduleTypeClassifier() {
    }

    public static String classify(String eventName) {
        String name = eventName == null ? "" : eventName.replaceAll("\\s+", "");

        if (containsAny(name,
                "공휴일", "대체휴일", "연휴", "신정", "설연휴", "삼일절", "어린이날", "부처님오신날",
                "현충일", "광복절", "개천절", "추석", "한글날", "성탄절", "지방선거")) {
            return "holiday";
        }

        if (containsAny(name, "등록금납부", "수강료납부")) {
            return "tuition";
        }

        if (containsAny(name,
                "수강신청", "수강신청정정", "전공심화", "복수전공", "마이크로디그리", "소단위전공", "강좌수강")) {
            return "course_registration";
        }

        if (containsAny(name,
                "복학", "휴학", "전과", "재입학", "조기졸업", "졸업연장", "학사학위취득유예")) {
            return "registration";
        }

        if (containsAny(name,
                "성적", "수업평가", "강의평가", "중간평가", "기말평가", "중간고사", "기말고사", "보강주간", "학위수여식")) {
            return "grading";
        }

        return "general";
    }

    private static boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
