package esri;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfGeometry;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ahenrick
 * Date: 1/31/14
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */

public class CustomFeature extends MfFeature {
    private final String id;
    private final MfGeometry geometry;
    private final JSONObject properties;

    public CustomFeature(String id, MfGeometry geometry, JSONObject properties) {
        this.id = id;
        this.geometry = geometry;
        this.properties = properties;
    }

    public String getFeatureId() {
        return id;
    }

    public MfGeometry getMfGeometry() {
        return geometry;
    }

    public JSONObject getProperties() {
        return properties;
    }

    public void toJSON(JSONWriter builder) throws JSONException {
        final Iterator iterator = properties.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = properties.get(key);
            builder.key(key).value(value);
        }
    }
}

