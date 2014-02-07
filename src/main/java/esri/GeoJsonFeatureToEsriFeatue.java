package esri;

import com.esri.json.EsriFeature;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ahenrick
 * Date: 1/31/14
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class GeoJsonFeatureToEsriFeatue implements Converter<CustomFeature, EsriFeature> {

    private GeoJsonGeometryToEsriGeometry geoJsonGeometryToEsriGeometry;

    @Autowired
    public void setGeoJsonGeometryToEsriGeometry(GeoJsonGeometryToEsriGeometry geoJsonGeometryToEsriGeometry) {
        this.geoJsonGeometryToEsriGeometry = geoJsonGeometryToEsriGeometry;
    }

    public EsriFeature convert(CustomFeature source) {

        EsriFeature feature = new EsriFeature();

        feature.geometry = geoJsonGeometryToEsriGeometry.convert(source.getMfGeometry());

        Map<String, Object> mfProperties = new HashMap<String, Object>();
        try {
            Iterator iterator = source.getProperties().keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Object value = source.getProperties().get(key);
                mfProperties.put(key, value);
            }
        } catch (JSONException ex) {
        }

        feature.attributes = mfProperties;

        return feature;
    }
}
