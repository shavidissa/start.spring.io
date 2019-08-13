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

package io.spring.start.site.build.gradle;

import java.util.List;

import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.spring.build.gradle.DependencyManagementPluginVersionResolver;
import io.spring.initializr.generator.spring.code.kotlin.KotlinVersionResolver;

/**
 * {@link KotlinVersionResolver} that determines the Kotlin version by delegation.
 *
 * @author Andy Wilkinson
 */
public class CompositeDependencyManagementPluginVersionResolver implements DependencyManagementPluginVersionResolver {

	private final List<DependencyManagementPluginVersionResolver> delegates;

	public CompositeDependencyManagementPluginVersionResolver(
			List<DependencyManagementPluginVersionResolver> delegates) {
		this.delegates = delegates;
	}

	@Override
	public String resolveDependencyManagementPluginVersion(ResolvedProjectDescription description) {
		for (DependencyManagementPluginVersionResolver delegate : this.delegates) {
			String version = delegate.resolveDependencyManagementPluginVersion(description);
			if (version != null) {
				return version;
			}
		}
		return null;
	}

}
