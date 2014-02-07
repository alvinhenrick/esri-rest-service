package esri;

/**
 * Created with IntelliJ IDEA.
 * User: ahenrick
 * Date: 1/31/14
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public enum GeometryType {
    POINT("Point"),
    LINESTRING("LineString"),
    POLYGON("Polygon"),
    MULTIPOINT("MultiPoint"),
    MULTILINESTRING("MultiLineString"),
    MULTIPOLYGON("MultiPolygon"),
    MULTIGEOMETRY("GeometryCollection");

    private final String name;

    private GeometryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
