package whatisMGC;

import java.util.Objects;

/**
 * Monthly academic schedule item.
 */
public class AcademicScheduleInfo {
    private int year;
    private int month;
    private String eventDate;
    private String eventName;
    private String eventType;
    private long createdAt;
    private long updatedAt;

    public AcademicScheduleInfo() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.eventType = "general";
    }

    public AcademicScheduleInfo(int year, int month, String eventDate, String eventName) {
        this.year = year;
        this.month = month;
        this.eventDate = eventDate;
        this.eventName = eventName;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.eventType = AcademicScheduleTypeClassifier.classify(eventName);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcademicScheduleInfo that = (AcademicScheduleInfo) o;
        return year == that.year &&
                month == that.month &&
                Objects.equals(eventDate, that.eventDate) &&
                Objects.equals(eventName, that.eventName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, eventDate, eventName);
    }

    @Override
    public String toString() {
        return "AcademicScheduleInfo{" +
                "year=" + year +
                ", month=" + month +
                ", eventDate='" + eventDate + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventType='" + eventType + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
