package nz.net.speakman.android.whatsshaking.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Locale;

/**
 * Created by Adam on 28/12/13.
 */
@DatabaseTable
public class Earthquake {

    public static final int TSUNAMI_UNLIKELY = 0;
    public static final int TSUNAMI_POSSIBLE = 1;

    public static final String ALERT_LEVEL_RED = "red";
    public static final String ALERT_LEVEL_ORANGE = "orange";
    public static final String ALERT_LEVEL_YELLOW = "yellow";
    public static final String ALERT_LEVEL_GREEN = "green";

    public static final String STATUS_AUTOMATIC = "automatic";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_REVIEWED = "reviewed";

    /**
     * This event was an earthquake.
     */
    public static final String EVENT_TYPE_EARTHQUAKE = "earthquake";
    /**
     * This event was a quarry blast.
     */
    public static final String EVENT_TYPE_QUARRY = "quarry";

    @DatabaseField(generatedId = true)
    private int _id;

    @DatabaseField
    private double magnitude;

    @DatabaseField
    private String place;

    @DatabaseField
    private long eventTime;

    @DatabaseField
    private long updatedTime;

    @DatabaseField
    private int timeZoneOffsetMinutes;

    @DatabaseField
    private String url;

    @DatabaseField
    private String detailDataUrl;

    @DatabaseField
    private int feltCount;

    /**
     * Returned as 'CDI' in an earthquake from USGS. Uses the MMI scale, is computed by DYFI from user reports.
     * http://earthquake.usgs.gov/research/dyfi/
     */
    @DatabaseField
    private double reportedIntensity;

    /**
     * Returned as 'MMI' in an earthquake from USGS. Is computed automatically by ShakeMap.
     * http://earthquake.usgs.gov/research/shakemap/
     */
    @DatabaseField
    private double calculatedIntensity;

    @DatabaseField
    private String alertLevel;

    @DatabaseField
    private String status;

    @DatabaseField
    private int possibleTsunami;

    /**
     * A number describing how significant the event is. Larger numbers indicate a more significant event.
     * This value is determined on a number of factors, including: magnitude, maximum MMI, felt reports, and estimated
     * impact.
     */
    @DatabaseField
    private int significance;

    @DatabaseField
    private String primaryReportingNetwork;

    @DatabaseField
    private String primaryReportingNetworkEventId;

    @DatabaseField(foreign = true)
    private EarthquakeId primaryId;

    @ForeignCollectionField()
    private ForeignCollection<EarthquakeId> allIds;

    /**
     * },
     * geometry: {
     * type: "Point",
     * coordinates: [
     * longitude,
     * latitude,
     * depth
     * ]
     * },
     */

    @DatabaseField
    private String sources;

    @DatabaseField
    private String types;

    @DatabaseField
    private int numberOfReportingStations;

    @DatabaseField
    private double nearestReportingStationDegrees;

    @DatabaseField
    private double rms;

    @DatabaseField
    private double gapBetweenAdjacentStationsDegrees;

    @DatabaseField
    private String magnitudeType;

    @DatabaseField
    private String eventType;

    @DatabaseField
    private double longitude;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private double depth;

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public int getTimeZoneOffsetMinutes() {
        return timeZoneOffsetMinutes;
    }

    public void setTimeZoneOffsetMinutes(int timeZoneOffsetMinutes) {
        this.timeZoneOffsetMinutes = timeZoneOffsetMinutes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDetailDataUrl() {
        return detailDataUrl;
    }

    public void setDetailDataUrl(String detailDataUrl) {
        this.detailDataUrl = detailDataUrl;
    }

    public int getFeltCount() {
        return feltCount;
    }

    public void setFeltCount(int feltCount) {
        this.feltCount = feltCount;
    }

    /**
     * Uses the MMI scale, is computed by DYFI from user reports.
     * http://earthquake.usgs.gov/research/dyfi/
     */
    public double getReportedIntensity() {
        return reportedIntensity;
    }

    public void setReportedIntensity(double reportedIntensity) {
        this.reportedIntensity = reportedIntensity;
    }

    /**
     * Uses the MMI scale, is computed automatically by ShakeMap.
     * http://earthquake.usgs.gov/research/shakemap/
     */
    public double getCalculatedIntensity() {
        return calculatedIntensity;
    }

    public void setCalculatedIntensity(double calculatedIntensity) {
        this.calculatedIntensity = calculatedIntensity;
    }

    /**
     * The alert level from the PAGER earthquake impact scale. One of:
     * <ul>
     *     <li>{@code ALERT_LEVEL_GREEN}</li>
     *     <li>{@code ALERT_LEVEL_YELLOW}</li>
     *     <li>{@code ALERT_LEVEL_ORANGE}</li>
     *     <li>{@code ALERT_LEVEL_RED}</li>
     * </ul>
     * http://earthquake.usgs.gov/research/pager/
     * @return
     */
    public String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    /**
     * Indicates whether the event has been reviewed by a human. One of:
     * <ul>
     *     <li>{@code STATUS_AUTOMATIC}</li>
     *     <li>{@code STATUS_REVIEWED}</li>
     *     <li>{@code STATUS_PUBLISHED}</li>
     * </ul>
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Set to {@code TSUNAMI_POSSIBLE} for large events in oceanic regions. Set to {@code TSUNAMI_UNLIKELY otherwise.
     * Note that this does not indicate that a tsunami exists; see http://www.tsunami.gov/ instead.
     */
    public int getPossibleTsunami() {
        return possibleTsunami;
    }

    public void setPossibleTsunami(int possibleTsunami) {
        this.possibleTsunami = possibleTsunami;
    }

    /**
     * A number describing how significant the event is. Larger numbers indicate a more significant event.
     * This value is determined on a number of factors, including: magnitude, maximum MMI, felt reports, and estimated
     * impact.
     */
    public int getSignificance() {
        return significance;
    }

    public void setSignificance(int significance) {
        this.significance = significance;
    }

    public String getPrimaryReportingNetwork() {
        return primaryReportingNetwork;
    }

    public void setPrimaryReportingNetwork(String primaryReportingNetwork) {
        this.primaryReportingNetwork = primaryReportingNetwork;
    }

    public String getPrimaryReportingNetworkEventId() {
        return primaryReportingNetworkEventId;
    }

    public void setPrimaryReportingNetworkEventId(String primaryReportingNetworkEventId) {
        this.primaryReportingNetworkEventId = primaryReportingNetworkEventId;
    }

    /**
     * A unique identifier for the event. This is the current preferred id for the event, and may change over time.
     */
    public EarthquakeId getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(EarthquakeId primaryId) {
        this.primaryId = primaryId;
    }

    /**
     * All potential unique IDs from all reporting networks. One of these is used as the {@code primaryId}.
     */
    public ForeignCollection<EarthquakeId> getAllIds() {
        return allIds;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public int getNumberOfReportingStations() {
        return numberOfReportingStations;
    }

    public void setNumberOfReportingStations(int numberOfReportingStations) {
        this.numberOfReportingStations = numberOfReportingStations;
    }

    /**
     * The closer the reporting station, the more accurate the reported depth. 1 degree ~= 111.2 kilometres.
     */
    public double getNearestReportingStationDegrees() {
        return nearestReportingStationDegrees;
    }

    public void setNearestReportingStationDegrees(double nearestReportingStationDegrees) {
        this.nearestReportingStationDegrees = nearestReportingStationDegrees;
    }

    public double getRms() {
        return rms;
    }

    public void setRms(double rms) {
        this.rms = rms;
    }

    /**
     * The largest azimuthal gap between azimuthally adjacent stations (in degrees). In general, the smaller this
     * number, the more reliable is the calculated horizontal position of the earthquake.
     */
    public double getGapBetweenAdjacentStationsDegrees() {
        return gapBetweenAdjacentStationsDegrees;
    }

    public void setGapBetweenAdjacentStationsDegrees(double gapBetweenAdjacentStationsDegrees) {
        this.gapBetweenAdjacentStationsDegrees = gapBetweenAdjacentStationsDegrees;
    }

    /**
     * The method or algorithm used to calculate the preferred magnitude for the event.
     */
    public String getMagnitudeType() {
        return magnitudeType;
    }

    public void setMagnitudeType(String magnitudeType) {
        this.magnitudeType = magnitudeType;
    }

    /**
     * The type of this event. One of:
     * <ul>
     *     <li>{@code EVENT_TYPE_EARTHQUAKE}</li>
     *     <li>{@code EVENT_TYPE_QUARRY}</li>
     * </ul>
     */
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }
}

