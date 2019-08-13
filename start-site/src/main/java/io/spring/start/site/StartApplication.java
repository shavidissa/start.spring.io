/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.start.site;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.spring.build.gradle.DependencyManagementPluginVersionResolver;
import io.spring.initializr.generator.spring.build.gradle.InitializrDependencyManagementPluginVersionResolver;
import io.spring.initializr.generator.spring.code.kotlin.InitializrMetadataKotlinVersionResolver;
import io.spring.initializr.generator.spring.code.kotlin.KotlinVersionResolver;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;
import io.spring.start.site.build.gradle.CompositeDependencyManagementPluginVersionResolver;
import io.spring.start.site.build.gradle.ManagedDependenciesDependencyManagementPluginVersionResolver;
import io.spring.start.site.extension.ProjectDescriptionCustomizerConfiguration;
import io.spring.start.site.kotlin.CompositeKotlinVersionResolver;
import io.spring.start.site.kotlin.ManagedDependenciesKotlinVersionResolver;
import io.spring.start.site.support.StartInitializrMetadataUpdateStrategy;
import io.spring.start.site.web.HomeController;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Initializr website application.
 *
 * @author Stephane Nicoll
 */
@EnableAutoConfiguration
@SpringBootConfiguration
@Import(ProjectDescriptionCustomizerConfiguration.class)
@EnableCaching
@EnableAsync
public class StartApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}

	@Bean
	public InitializrMetadataUpdateStrategy startMetadataUpdateStrategy(RestTemplateBuilder restTemplateBuilder,
			ObjectMapper objectMapper) {
		return new StartInitializrMetadataUpdateStrategy(restTemplateBuilder.build(), objectMapper);
	}

	@Bean
	public HomeController homeController() {
		return new HomeController();
	}

	@Bean
	public ErrorPageRegistrar notFound() {
		return (registry) -> registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404.html"));
	}

	@Bean
	public DependencyManagementVersionResolver dependencyManagementVersionResolver() throws IOException {
		return DependencyManagementVersionResolver
				.withCacheLocation(Files.createTempDirectory("version-resolver-cache-"));
	}

	@Bean
	public KotlinVersionResolver kotlinVersionResolver(DependencyManagementVersionResolver versionResolver,
			InitializrMetadataProvider metadataProvider) {
		return new CompositeKotlinVersionResolver(
				Arrays.asList(new ManagedDependenciesKotlinVersionResolver(versionResolver),
						new InitializrMetadataKotlinVersionResolver(metadataProvider)));
	}

	@Bean
	public DependencyManagementPluginVersionResolver dependencyManagementPluginVersionResolver(
			DependencyManagementVersionResolver versionResolver, InitializrMetadataProvider metadataProvider) {
		return new CompositeDependencyManagementPluginVersionResolver(
				Arrays.asList(new ManagedDependenciesDependencyManagementPluginVersionResolver(versionResolver),
						new InitializrDependencyManagementPluginVersionResolver(metadataProvider)));
	}

}
