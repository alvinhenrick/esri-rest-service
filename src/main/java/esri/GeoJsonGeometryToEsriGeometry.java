package esri;

import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.OperatorImportFromWkt;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.WktImportFlags;
import com.vividsolutions.jts.geom.*;
import org.mapfish.geo.MfGeometry;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ahenrick
 * Date: 1/31/14
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class GeoJsonGeometryToEsriGeometry implements Converter<MfGeometry, Geometry> {

    public Geometry convert(MfGeometry source) {

        final GeometryType geometryType = getGeometryType(source.getInternalGeometry().getClass());
        final String wktString = source.getInternalGeometry().toText();
        //source.getInternalGeometry().setSRID(4326);
        Geometry returnValue = null;

        switch (geometryType) {

            case POINT:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.Point, wktString, null);
                break;
            case LINESTRING:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.Line, wktString, null);
                break;

            case POLYGON:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.Polygon, wktString, null);
                break;

            case MULTIPOINT:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.MultiPoint, wktString, null);
                break;

            case MULTILINESTRING:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.Polyline, wktString, null);
                break;

            case MULTIPOLYGON:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.Polygon, wktString, null);
                break;

            case MULTIGEOMETRY:
                returnValue = handleMultiGeometry(source);
                break;

        }
        if (null != returnValue) {
        }
        return returnValue;
    }

    /**
     * @param source
     * @return
     */
    private Geometry handleMultiGeometry(MfGeometry source) {

        Geometry returnValue = null;

        final com.vividsolutions.jts.geom.Geometry firstGeometry = source.getInternalGeometry().getGeometryN(0);
        final List<com.vividsolutions.jts.geom.Geometry> filteredGeometries = new ArrayList<com.vividsolutions.jts.geom.Geometry>();

        for (int i = 0; i < source.getInternalGeometry().getNumGeometries(); i++) {
            com.vividsolutions.jts.geom.Geometry nextGeometry = source.getInternalGeometry().getGeometryN(i);
            if (isCompatible(firstGeometry, nextGeometry)) {
                filteredGeometries.add(nextGeometry);
            }
        }

        final com.vividsolutions.jts.geom.GeometryFactory geometryFactory = new com.vividsolutions.jts.geom.GeometryFactory();
        final com.vividsolutions.jts.geom.Geometry newGeometry = geometryFactory.buildGeometry(filteredGeometries);

        final MfGeometry mfGeometry = new MfGeometry(newGeometry);
        final GeometryType newGeometryType = getGeometryType(mfGeometry.getInternalGeometry().getClass());
        final String wktString = mfGeometry.getInternalGeometry().toText();

        switch (newGeometryType) {

            case POINT:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.Point, wktString, null);
                break;
            case LINESTRING:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.Line, wktString, null);
                break;

            case POLYGON:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.Polygon, wktString, null);
                break;

            case MULTIPOINT:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.MultiPoint, wktString, null);
                break;

            case MULTILINESTRING:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.Polyline, wktString, null);
                break;

            case MULTIPOLYGON:
                returnValue = OperatorImportFromWkt.local().execute(WktImportFlags.wktImportDefaults, Geometry.Type.Polygon, wktString, null);
                break;
        }
        return returnValue;
    }

    /**
     * @param firstGeometry
     * @param nextGeometry
     * @return
     */
    public boolean isCompatible(com.vividsolutions.jts.geom.Geometry firstGeometry, com.vividsolutions.jts.geom.Geometry nextGeometry) {
        boolean compatible = false;
        final String firstGeomType = firstGeometry.getGeometryType();
        final String nextGeomType = nextGeometry.getGeometryType();


        if ((firstGeomType.equals("Point") || firstGeomType.equals("MultiPoint)")) && (nextGeomType.equals("Point") || nextGeomType.equals("MultiPoint)"))) {
            compatible = true;
        } else if ((firstGeomType.equals("LineString") || firstGeomType.equals("MultiLineString")) && (nextGeomType.equals("LineString") || nextGeomType.equals("MultiLineString"))) {
            compatible = true;
        } else if ((firstGeomType.equals("Polygon") || firstGeomType.equals("MultiPolygon")) && (nextGeomType.equals("Polygon") || nextGeomType.equals("MultiPolygon"))) {
            compatible = true;
        }
        return compatible;

    }


    /**
     * Gets the internal representation for the given Geometry
     *
     * @param geomClass a Geometry  Class
     * @return int representation of Geometry
     */
    public GeometryType getGeometryType(final Class<?> geomClass) {
        //final Class<?> geomClass = geometry.getClass();
        final GeometryType returnValue;

        if (geomClass.equals(Point.class)) {
            returnValue = GeometryType.POINT;
        } else if (geomClass.equals(LineString.class)) {
            returnValue = GeometryType.LINESTRING;
        } else if (geomClass.equals(Polygon.class)) {
            returnValue = GeometryType.POLYGON;
        } else if (geomClass.equals(MultiPoint.class)) {
            returnValue = GeometryType.MULTIPOINT;
        } else if (geomClass.equals(MultiLineString.class)) {
            returnValue = GeometryType.MULTILINESTRING;
        } else if (geomClass.equals(MultiPolygon.class)) {
            returnValue = GeometryType.MULTIPOLYGON;
        } else if (geomClass.equals(GeometryCollection.class)) {
            returnValue = GeometryType.MULTIGEOMETRY;
        } else {
            returnValue = null;
        }

        return returnValue;
    }
}



