package sample.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ReloadableProperties {
	
	@Autowired
	protected StandardEnvironment environment;
	
	private long			  lastModTime			  = 0L;
	private Path			  configPath			  = null;
	private PropertySource<?> appConfigPropertySource = null;
	
	@PostConstruct
	private void stopIfProblemsCreatingContext() {
		System.out.println("reloading");
		MutablePropertySources propertySources = environment.getPropertySources();
		Optional<PropertySource<?>> appConfigPsOp = StreamSupport.stream(propertySources.spliterator(), false)
		        .filter(ps -> ps.getName().matches("^.*applicationConfig.*file:.*$"))
		        .findFirst();
		if (!appConfigPsOp.isPresent()) {
			// this will stop context initialization 
			// (i.e. kill the spring boot program before it initializes)
			throw new RuntimeException("Unable to find property Source as file");
		}
		appConfigPropertySource = appConfigPsOp.get();
		
		String filename = appConfigPropertySource.getName();
		filename = filename
		        .replace("applicationConfig: [file:", "")
		        .replaceAll("\\]$", "");
		
		configPath = Paths.get(filename);
		
	}
	
	@Scheduled(fixedDelay = 30000)
	private void reload() throws IOException {
		log.debug("<Configuration> Reloading configyration...");
		long currentModTs = Files.getLastModifiedTime(configPath).toMillis();
		if (currentModTs > lastModTime) {
			lastModTime = currentModTs;
			Properties properties = new Properties();
			@Cleanup
			InputStream inputStream = Files.newInputStream(configPath);
			properties.load(inputStream);
			environment.getPropertySources()
			        .replace(appConfigPropertySource.getName(), new PropertiesPropertySource(appConfigPropertySource.getName(), properties));
			log.debug("<Configuration> Reload configyration done");
			propertiesReloaded();
		}
	}
	
	protected abstract void propertiesReloaded();
}