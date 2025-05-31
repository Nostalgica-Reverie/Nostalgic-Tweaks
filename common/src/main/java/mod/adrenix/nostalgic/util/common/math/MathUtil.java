package mod.adrenix.nostalgic.util.common.math;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.util.Mth;

public abstract class MathUtil
{
    /**
     * Checks if the given number is an even number.
     *
     * @param number The number to check.
     * @return Whether the given number is evenly divisible by 2, if so, then it is even.
     */
    @PublicAPI
    public static boolean isEven(int number)
    {
        return number % 2 == 0;
    }

    /**
     * Checks if the given number is an odd number.
     *
     * @param number The number to check.
     * @return Whether the given number is not evenly divisible by 2, if so, then it is odd.
     */
    @PublicAPI
    public static boolean isOdd(int number)
    {
        return !isEven(number);
    }

    /**
     * Round the given {@code double} decimal to the given decimal place.
     *
     * @param value The value to round.
     * @param place The decimal place to round to.
     * @return A rounded {@code double} value.
     */
    @PublicAPI
    public static double roundTo(double value, int place)
    {
        int scale = (int) Math.pow(10, place);

        return (double) Math.round(value * scale) / scale;
    }

    /**
     * Round the given {@code float} decimal to the given decimal place.
     *
     * @param value The value to round.
     * @param place The decimal place to round to.
     * @return A rounded {@code float} value.
     */
    @PublicAPI
    public static float roundTo(float value, int place)
    {
        int scale = (int) Math.pow(10, place);

        return (float) Math.round(value * scale) / scale;
    }

    /**
     * Get a centered position within the defined enclosure.
     *
     * @param startPos A starting position that is offset from the origin.
     * @param size     The size of the entity being centered within the enclosure.
     * @param maxSize  The maximum size of the enclosure.
     * @return A centered position.
     */
    @PublicAPI
    public static float center(int startPos, int size, int maxSize)
    {
        return startPos + Math.abs((size / 2.0F) - (maxSize / 2.0F));
    }

    /**
     * Get a centered position within the defined enclosure at a starting position of zero.
     *
     * @param size    The size of the entity being centered within the enclosure.
     * @param maxSize The maximum size of the enclosure.
     * @return A centered position.
     */
    @PublicAPI
    public static float center(int size, int maxSize)
    {
        return center(0, size, maxSize);
    }

    /**
     * Get a random integer that includes the provided min/max bounds (i.e., [min, max]).
     *
     * @param min The min value.
     * @param max The max value.
     * @return A random integer.
     */
    @PublicAPI
    public static int randomInt(int min, int max)
    {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    /**
     * Turns given {@code bytes} into megabytes.
     *
     * @param bytes The bytes to turn into megabytes.
     * @return Bytes turned into megabytes.
     */
    @PublicAPI
    public static long bytesToMegabytes(long bytes)
    {
        return bytes / 1024L / 1024L;
    }

    /**
     * Checks if the given value is greater than or equal to the given start or less than or equal to the given end.
     *
     * @param value The integer to check.
     * @param start The starting integer.
     * @param end   The ending integer.
     * @return Whether the given value is within range of the given start/end.
     */
    @PublicAPI
    public static boolean isInRange(int value, int start, int end)
    {
        return value >= start && value <= end;
    }

    /**
     * Checks if the given value is greater than or equal to the given start or less than or equal to the given end.
     *
     * @param value The float to check.
     * @param start The starting float.
     * @param end   The ending float.
     * @return Whether the given value was within range of the given start/end.
     */
    public static boolean isInRange(float value, float start, float end)
    {
        return value >= start && value <= end;
    }

    /**
     * Determine if two integers are within a given {@code tolerance}.
     *
     * @param a         First integer.
     * @param b         Second integer.
     * @param tolerance The allowed distance between {@code a} and {@code b}.
     * @return If the two integers are within the given {@code tolerance}.
     */
    @PublicAPI
    public static boolean tolerance(int a, int b, int tolerance)
    {
        return Math.abs(a - b) < tolerance;
    }

    /**
     * This is an overload method for {@link MathUtil#tolerance(int, int, int)} with a default {@code tolerance} of 3.
     *
     * @param a First integer.
     * @param b Second integer.
     * @return If the two integers are within a {@code tolerance} of 3.
     */
    @PublicAPI
    public static boolean tolerance(int a, int b)
    {
        return tolerance(a, b, 3);
    }

    /**
     * Checks if the two floats are within a given {@code tolerance}.
     *
     * @param a         First float.
     * @param b         Second float.
     * @param tolerance The allowed distance between {@code a} and {@code b}.
     * @return If the two floats are within the given {@code tolerance}.
     */
    @PublicAPI
    public static boolean tolerance(float a, float b, float tolerance)
    {
        return Math.abs(a - b) < tolerance;
    }

    /**
     * Checks if the two doubles are within a given {@code tolerance}.
     *
     * @param a         First double.
     * @param b         Second double.
     * @param tolerance The allowed distance between {@code a} and {@code b}.
     * @return If the two doubles are within the given {@code tolerance}.
     */
    @PublicAPI
    public static boolean tolerance(double a, double b, double tolerance)
    {
        return Math.abs(a - b) < tolerance;
    }

    /**
     * Checks if {@code a}, {@code b}, and {@code c} are within the given {@code tolerance}.
     *
     * @param a         The first float to compare.
     * @param b         The second float to compare.
     * @param c         The third float to compare.
     * @param tolerance The maximum distance each float can be from each other.
     * @return Whether all three floats are within the given {@code tolerance}.
     */
    @PublicAPI
    public static boolean tolerance(float a, float b, float c, float tolerance)
    {
        return tolerance(a, b, tolerance) && tolerance(b, c, tolerance);
    }

    /**
     * Check if the two given arrays are within the given {@code tolerance}.
     *
     * @param a         The first array of floats to compare.
     * @param b         The second array of floats to compare.
     * @param tolerance The maximum distance each float can be from each other.
     * @return Whether all three floats in each array are within the given {@code tolerance}.
     */
    @PublicAPI
    public static boolean tolerance(float[] a, float[] b, float tolerance)
    {
        if (a.length != b.length)
            throw new AssertionError("Arrays must be the same length!");

        for (int i = 0; i < a.length; i++)
        {
            if (!tolerance(a[i], b[i], tolerance))
                return false;
        }

        return true;
    }

    /**
     * Checks if a point is within a bounding box.
     *
     * @param pointX The x-point to check against.
     * @param pointY The y-point to check against.
     * @param startX The startX of the box.
     * @param startY The startY of the box.
     * @param width  The width of the box. Do not include startX addition when providing a width.
     * @param height The height of the box. Do not include startY addition when providing a height.
     * @return Whether the point is within the box's boundaries.
     */
    @PublicAPI
    public static boolean isWithinBox(double pointX, double pointY, double startX, double startY, double width, double height)
    {
        return isWithinLine(pointX, startX, width) && isWithinLine(pointY, startY, height);
    }

    /**
     * Checks if a point is within a line.
     *
     * @param point The point number.
     * @param start The start of the line.
     * @param size  The size of the line.
     * @return Whether the point is within the line's boundaries.
     */
    @PublicAPI
    public static boolean isWithinLine(double point, double start, double size)
    {
        return point >= start && point < start + size;
    }

    /**
     * Gets the sign of the given input.
     *
     * @param input The input to check for its sign.
     * @return A {@code 1} when the input is positive or zero, {@code -1} when the input is negative.
     */
    @PublicAPI
    public static float sign(float input)
    {
        return input < 0.0F ? -1.0F : 1.0F;
    }

    /**
     * Overload method as {@code double} for {@link MathUtil#sign(float)}.
     */
    @PublicAPI
    public static double sign(double input)
    {
        return input < 0.0D ? -1.0D : 1.0D;
    }

    /**
     * Normalize the given value so that it is within the given range. It is assumed that the range wraps around
     * itself.
     *
     * @param value The value to normalize.
     * @param start The starting value of the range.
     * @param end   The ending value of the range.
     * @return A normalized value that is within the cyclical range.
     */
    @PublicAPI
    public static double normalizeInRange(double value, double start, double end)
    {
        double range = end - start;
        double offset = value - start;

        return (offset - (Math.floor(offset / range) * range)) + start;
    }

    /**
     * Normalize the given value so that it is within the given range. It is assumed that the range wraps around
     * itself.
     *
     * @param value The value to normalize.
     * @param start The starting value of the range.
     * @param end   The ending value of the range.
     * @return A normalized value that is within the cyclical range.
     */
    @PublicAPI
    public static float normalizeInRange(float value, float start, float end)
    {
        return (float) normalizeInRange((double) value, start, end);
    }

    /**
     * Normalize the given value so that it is within the given range. It is assumed that the range wraps around
     * itself.
     *
     * @param value The value to normalize.
     * @param start The starting value of the range.
     * @param end   The ending value of the range.
     * @return A normalized value that is within the cyclical range.
     */
    @PublicAPI
    public static long normalizeInRange(long value, long start, long end)
    {
        long range = end - start;
        long offset = value - start;

        return (offset - ((offset / range) * range)) + start;
    }

    /**
     * Normalize the given value so that it is within the given range. It is assumed that the range wraps around
     * itself.
     *
     * @param value The value to normalize.
     * @param start The starting value of the range.
     * @param end   The ending value of the range.
     * @return A normalized value that is within the cyclical range.
     */
    @PublicAPI
    public static int normalizeInRange(int value, int start, int end)
    {
        return (int) normalizeInRange((long) value, start, end);
    }

    /**
     * Checks if the distance between the {@code current} value and the {@code target} exceeds the given speed
     * ({@code delta}).
     *
     * @param current The current value.
     * @param target  The target value.
     * @param delta   The change in value, or speed of movement.
     * @return Checks if the distance between the given points is greater than the given speed.
     */
    private static boolean isTargetImmediate(float current, float target, float delta)
    {
        return Math.abs(target - current) <= delta;
    }

    /**
     * Moves {@code current} towards {@code target}. This is essentially
     * {@link Mth#lerp(float, float, float) Mth.lerp(delta, start, end)} but instead the method ensures that the speed
     * never exceeds the given {@code delta}. Negative values of {@code delta} pushes the value away from
     * {@code target}.
     *
     * @param current The current value.
     * @param target  The value to move towards.
     * @param delta   The change that should be applied to the value.
     * @return A point from the {@code current} value to the {@code target} value using the given {@code delta}.
     */
    @PublicAPI
    public static float moveTowards(float current, float target, float delta)
    {
        return Math.abs(target - current) <= delta ? target : current + sign(target - current) * delta;
    }

    /**
     * Overload method as {@code double} for {@link MathUtil#moveTowards(float, float, float)}}.
     */
    @PublicAPI
    public static double moveTowards(double current, double target, double delta)
    {
        return Math.abs(target - current) <= delta ? target : current + sign(target - current) * delta;
    }

    /**
     * Move {@code current} towards {@code target} while being clamped between the {@code min} and {@code max}. See
     * {@link MathUtil#moveTowards(float, float, float)} for more information on movement.
     *
     * @param current The current value.
     * @param target  The value to move towards.
     * @param delta   The change that should be applied to the value.
     * @param min     The minimum value allowed for this change.
     * @param max     The maximum value allowed for this change.
     * @return A point from {@code current} value to the {@code target} value using the given {@code delta} while being
     * clamped between the {@code min} and {@code max}.
     */
    @PublicAPI
    public static float moveClampTowards(float current, float target, float delta, float min, float max)
    {
        return Mth.clamp(moveTowards(current, target, delta), min, max);
    }

    /**
     * Overload method as {@code double} for {@link MathUtil#moveClampTowards(float, float, float, float, float)}.
     */
    @PublicAPI
    public static double moveClampTowards(double current, double target, double delta, double min, double max)
    {
        return Mth.clamp(moveTowards(current, target, delta), min, max);
    }

    /**
     * Moves the {@code CURRENT_RGB} array towards the {@code TARGET_RGB}.
     *
     * @param CURRENT_RGB The current color to move.
     * @param TARGET_RGB  The target color to move the current color towards.
     * @param SPEED       How quickly the current color moves towards the target color.
     */
    @PublicAPI
    public static void moveTowardsColor(final float[] CURRENT_RGB, final float[] TARGET_RGB, final float SPEED)
    {
        CURRENT_RGB[0] = moveClampTowards(CURRENT_RGB[0], TARGET_RGB[0], SPEED, 0.0F, 1.0F);
        CURRENT_RGB[1] = moveClampTowards(CURRENT_RGB[1], TARGET_RGB[1], SPEED, 0.0F, 1.0F);
        CURRENT_RGB[2] = moveClampTowards(CURRENT_RGB[2], TARGET_RGB[2], SPEED, 0.0F, 1.0F);
    }

    /**
     * Moves the {@code CURRENT_RGB} array towards <b>grayscale</b> before moving the {@code CURRENT_RGB} towards the
     * {@code TARGET_RGB} array with the given speed.
     *
     * @param CURRENT_RGB The current color to move.
     * @param TARGET_RGB  The target color to move the current color towards.
     * @param SPEED       How quickly the current color moves towards the target color.
     */
    @PublicAPI
    public static void moveTowardsGrayscale(final float[] CURRENT_RGB, final float[] TARGET_RGB, final float SPEED)
    {
        boolean isR = tolerance(CURRENT_RGB[0], TARGET_RGB[0], 0.1F);
        boolean isG = tolerance(CURRENT_RGB[1], TARGET_RGB[1], 0.1F);
        boolean isB = tolerance(CURRENT_RGB[2], TARGET_RGB[2], 0.1F);
        boolean isImmediate = isTargetImmediate(CURRENT_RGB[0], TARGET_RGB[0], SPEED) || (isR && isG && isB);

        if (isImmediate || tolerance(CURRENT_RGB[0], CURRENT_RGB[1], CURRENT_RGB[2], 0.05F))
            moveTowardsColor(CURRENT_RGB, TARGET_RGB, SPEED);
        else
        {
            final float AVERAGE = (CURRENT_RGB[0] + CURRENT_RGB[1] + CURRENT_RGB[2]) / 3.0F;
            CURRENT_RGB[0] = moveClampTowards(CURRENT_RGB[0], AVERAGE, SPEED, 0.0F, 1.0F);
            CURRENT_RGB[1] = moveClampTowards(CURRENT_RGB[1], AVERAGE, SPEED, 0.0F, 1.0F);
            CURRENT_RGB[2] = moveClampTowards(CURRENT_RGB[2], AVERAGE, SPEED, 0.0F, 1.0F);
        }
    }

    /**
     * Get the largest number within the given varargs.
     *
     * @param numbers The numbers to check.
     * @return The largest number.
     */
    @PublicAPI
    public static int getLargest(int... numbers)
    {
        int largest = Integer.MIN_VALUE;

        for (int number : numbers)
        {
            if (number > largest)
                largest = number;
        }

        return largest;
    }

    /**
     * Get the largest number within the given varargs.
     *
     * @param numbers The numbers to check.
     * @return The largest number.
     */
    @PublicAPI
    public static float getLargest(float... numbers)
    {
        float largest = Float.MIN_VALUE;

        for (float number : numbers)
        {
            if (number > largest)
                largest = number;
        }

        return largest;
    }

    /**
     * Get the smallest number within the given varargs.
     *
     * @param numbers The numbers to check.
     * @return The smallest number.
     */
    @PublicAPI
    public static int getSmallest(int... numbers)
    {
        int smallest = Integer.MAX_VALUE;

        for (int number : numbers)
        {
            if (number < smallest)
                smallest = number;
        }

        return smallest;
    }

    /**
     * Get the smallest number within the given varargs.
     *
     * @param numbers The numbers to check.
     * @return The smallest number.
     */
    @PublicAPI
    public static float getSmallest(float... numbers)
    {
        float smallest = Float.MAX_VALUE;

        for (float number : numbers)
        {
            if (number < smallest)
                smallest = number;
        }

        return smallest;
    }

    /**
     * Get a primitive number type (byte, short, int, long, float, or double) based on the given class type and number.
     * For example, if the class type is of an integer, then the number returned will be an integer.
     *
     * @param type   The class type check if the given is assignable from.
     * @param number A generic number.
     * @return A primitive number type.
     */
    @PublicAPI
    public static Number getNumberFromType(Class<?> type, Number number)
    {
        if (type.isAssignableFrom(Byte.class))
            return number.byteValue();
        else if (type.isAssignableFrom(Short.class))
            return number.shortValue();
        else if (type.isAssignableFrom(Integer.class))
            return number.intValue();
        else if (type.isAssignableFrom(Long.class))
            return number.longValue();
        else if (type.isAssignableFrom(Float.class))
            return number.floatValue();
        else if (type.isAssignableFrom(Double.class))
            return number.doubleValue();

        return number;
    }
}
