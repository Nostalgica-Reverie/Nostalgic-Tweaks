package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;

import java.time.Month;
import java.time.MonthDay;

public abstract class CalendarUtil {
    /**
     * Check if today matches the given month and day of the month.
     *
     * @param month      A {@link Month} enumeration.
     * @param dayOfMonth A day of the month, from 1 to 31.
     * @return Whether today matches the given arguments.
     */
    @PublicAPI
    public static boolean isToday(Month month, int dayOfMonth) {
        return MonthDay.now().equals(MonthDay.of(month, dayOfMonth));
    }

    /**
     * Check if today matches the given numerical month and day of the month.
     *
     * @param month      The month of the year, from 1 to 12.
     * @param dayOfMonth A day of the month, from 1 to 31.
     * @return Whether today matches the given arguments.
     */
    @PublicAPI
    public static boolean isToday(int month, int dayOfMonth) {
        return MonthDay.now().equals(MonthDay.of(month, dayOfMonth));
    }

    /**
     * This method returns the month as an int from 1 to 12.
     *
     * @return The month of the year, from 1 to 12.
     */
    @PublicAPI
    public static int getMonth() {
        return MonthDay.now().getMonthValue();
    }

    /**
     * This method returns the day as an int from 1 to 31.
     *
     * @return The day of the month, from 1 to 31.
     */
    @PublicAPI
    public static int getDay() {
        return MonthDay.now().getDayOfMonth();
    }

    /**
     * Check if the current day is within the given range. The range check is inclusive and the bounds are arranged from
     * min to max.
     *
     * @param start The starting value of the range.
     * @param end   The ending value of the range.
     * @return Whether the current day is within the given range.
     */
    @PublicAPI
    public static boolean isDayInRange(int start, int end) {
        int day = getDay();
        int min = Math.min(start, end);
        int max = Math.max(start, end);

        return day >= min && day <= max;
    }

    /**
     * Check if the current month is December and if the day is within the inclusive range of 24 to 26.
     *
     * @return Whether it is Christmastime!
     */
    @PublicAPI
    public static boolean isChristmasTime() {
        return getMonth() == 12 && isDayInRange(24, 26);
    }
}
