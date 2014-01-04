package nz.net.speakman.android.whatsshaking.db;

/**
 * Created by Adam on 4/01/14.
 */
public class EarthquakeDbContract {
    public static final String TableName = "earthquakes";

    public static class Columns {
        public static final String Place = "place";
        public static final String EventTime = "eventTime";
        public static final String Magnitude = "magnitude";
        public static final String PrimaryId = "primaryId";
        public static final String Id = "_id";
        public static final String Depth = "depth";
        public static final String Longitude = "longitude";
        public static final String Latitude = "latitude";
        public static final String CalculatedIntensity = "calculatedIntensity";
    }
}
