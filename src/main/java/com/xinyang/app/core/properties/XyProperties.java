package com.xinyang.app.core.properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "xy.config")
public class XyProperties {

    private FileProperties fileConfig = new FileProperties();

    private WxProperties wxConfig = new WxProperties();

}
