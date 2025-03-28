package com.myflavor.myflavor.common.configuration.enviroment;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

@Configuration
public class Envconfig implements PropertySourceFactory {
	@Override
	public org.springframework.core.env.PropertySource<?> createPropertySource(String name,
		EncodedResource resource) throws IOException {
		YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
		factoryBean.setResources(resource.getResource());
		Properties properties = factoryBean.getObject();
		assert properties != null;
		return new PropertiesPropertySource(Objects.requireNonNull(resource.getResource().getFilename()), properties);
	}
}

