package Utils;

public class GetCurrentTime {
    public static Long secondsSince1970()
    {
        long seconds = System.currentTimeMillis() / 1000l;
        return seconds;
    }
}

