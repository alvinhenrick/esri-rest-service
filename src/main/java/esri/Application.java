package esri;

import org.json.JSONObject;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfGeoFactory;
import org.mapfish.geo.MfGeoJSONReader;
import org.mapfish.geo.MfGeometry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan
@EnableAutoConfiguration
@Configuration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public org.mapfish.geo.MfGeoFactory mfGeoFactory() {
        return new MfGeoFactory() {
            public MfFeature createFeature(String id, MfGeometry geometry, JSONObject properties) {
                return new CustomFeature(id, geometry, properties);
            }

        };
    }

    @Bean
    public MfGeoJSONReader mfGeoJSONReader() {
        return new MfGeoJSONReader(mfGeoFactory());
    }
}
