package sample.configuration;

import org.springframework.stereotype.Component;

@Component
public class AppProperties extends ReloadableProperties {
	
	public String getProperty(String propertyName) {
		return environment.getProperty(propertyName);
	}
	
	public String anotherDynamicProperty() {
		return environment.getProperty("another.dynamic.prop");
	}
	
	@Override
	protected void propertiesReloaded() {
		// do something after a change in property values was done
	}
}