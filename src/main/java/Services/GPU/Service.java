package Services.GPU;

import java.util.Properties;

import lombok.Getter;

@Getter
public abstract class Service {

    private Properties properties;

    public Service(Properties properties) {

        this.properties = properties;
    }
}
