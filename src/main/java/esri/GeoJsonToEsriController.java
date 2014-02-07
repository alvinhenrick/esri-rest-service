package esri;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.OperatorExportToJson;
import com.esri.core.geometry.SpatialReference;
import com.esri.json.EsriFeature;
import com.esri.json.EsriFeatureClass;
import com.esri.json.EsriJsonFactory;
import org.mapfish.geo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
public class GeoJsonToEsriController {

    private MfGeoJSONReader mfGeoJSONReader;
    private GeoJsonFeatureToEsriFeatue geoJsonFeatureToEsriFeatue;
    private GeoJsonGeometryToEsriGeometry geoJsonGeometryToEsriGeometry;

    @Autowired
    public void setMfGeoJSONReader(MfGeoJSONReader mfGeoJSONReader) {
        this.mfGeoJSONReader = mfGeoJSONReader;
    }

    @Autowired
    public void setGeoJsonFeatureToEsriFeatue(GeoJsonFeatureToEsriFeatue geoJsonFeatureToEsriFeatue) {
        this.geoJsonFeatureToEsriFeatue = geoJsonFeatureToEsriFeatue;
    }

    @Autowired
    public void setGeoJsonGeometryToEsriGeometry(GeoJsonGeometryToEsriGeometry geoJsonGeometryToEsriGeometry) {
        this.geoJsonGeometryToEsriGeometry = geoJsonGeometryToEsriGeometry;
    }


    @RequestMapping(value = "/convert", method = RequestMethod.POST)
    public
    @ResponseBody
    String convert(@RequestBody String geoJson) {
        String returnValue = "";

        try {

            final MfGeo mfGeo = mfGeoJSONReader.decode(geoJson);

            switch (mfGeo.getGeoType()) {

                case FEATURECOLLECTION:
                    EsriFeatureClass esriFeatureClass = handleFeatureCollection(mfGeo);
                    returnValue = EsriJsonFactory.JsonFromFeatureClass(esriFeatureClass);
                    break;

                case FEATURE:
                    final CustomFeature mfFeature = (CustomFeature) mfGeo;
                    final EsriFeature esriFeature = geoJsonFeatureToEsriFeatue.convert(mfFeature);
                    returnValue = EsriJsonFactory.JsonFromFeature(esriFeature);
                    break;

                case GEOMETRY:
                    final MfGeometry mfGeometry = (MfGeometry) mfGeo;
                    final Geometry geometry = geoJsonGeometryToEsriGeometry.convert(mfGeometry);
                    returnValue = OperatorExportToJson.local().execute(SpatialReference.create("4326"), geometry);
                    break;

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return returnValue;
    }

    private EsriFeatureClass handleFeatureCollection(MfGeo mfGeo) {

        final MfFeatureCollection mfFeatureCollection = (MfFeatureCollection) mfGeo;
        final ArrayList<EsriFeature> features = new ArrayList<EsriFeature>(mfFeatureCollection.getCollection().size());

        for (MfFeature currentFeature : mfFeatureCollection.getCollection()) {
            features.add(geoJsonFeatureToEsriFeatue.convert((CustomFeature) currentFeature));
        }

        final EsriFeatureClass esriFeatureClass = new EsriFeatureClass();
        esriFeatureClass.spatialReference = SpatialReference.create("4326");
        esriFeatureClass.features = features.toArray(new EsriFeature[features.size()]);

        return esriFeatureClass;

    }
}