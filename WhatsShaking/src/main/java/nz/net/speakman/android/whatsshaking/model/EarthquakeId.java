package nz.net.speakman.android.whatsshaking.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Because earthquake IDs "may change over time" as superior reports are received from varying stations, we must
 * keep a collection of IDs for a given event.
 * <p/>
 * When a quake comes in that we have potentially seen before (that is, the occurrence time of the quake is
 * <i>before</i> the last checked time), we should check and see if we have a stored quake whose ID matches the new one.
 * If we do, that's fine - update the stored one.
 * <p/>
 * If, however, we can't find a quake with a matching ID, we need to find a quake where allIDs contains the ID of the
 * new quake. If none exists, then this is (probably) a new event. If one does exist, then update that one.
 * <p/>
 * Created by Adam on 29/12/13.
 */
public class EarthquakeId {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String mReference;

    @DatabaseField(foreign = true)
    private Earthquake mEarthquake;

    public EarthquakeId(Earthquake earthquake, String reference) {
        this.mReference = reference;
        this.mEarthquake = earthquake;
    }

    public String getReference() {
        return mReference;
    }

    public Earthquake getEarthquake() {
        return mEarthquake;
    }
}
