package Utils;

public class GetCurrentTime {
    public static Long secondsSince1970()
    {
        return System.currentTimeMillis() / 1000L;
    }
}

