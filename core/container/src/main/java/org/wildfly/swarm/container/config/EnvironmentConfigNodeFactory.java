/**
 * Copyright 2015-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.swarm.container.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.inject.Vetoed;

import org.wildfly.swarm.spi.api.config.ConfigKey;

/**
 * Factory capable ofr building a {@code ConfigNode} tree from a {@link Properties} object.
 *
 * @author Bob McWhirter
 */
@Vetoed
public class EnvironmentConfigNodeFactory {

    private EnvironmentConfigNodeFactory() {

    }

    /**
     * Load a given {@link Properties} into a {@link ConfigNode}.
     *
     * @param input The properties to load.
     * @return The loaded {@code ConfigNode}.
     */
    public static ConfigNode load(Map<String, String> input) {
        ConfigNode config = new ConfigNode();

        load(config, input);

        return config;
    }

    protected static void load(ConfigNode config, Map<String, String> input) {
        Map<String, String> releaxed = adopt(input);
        Set<String> names = releaxed.keySet();

        for (String name : names) {
            if (name.startsWith("swarm.")) {
                String value = releaxed.get(name);
                ConfigKey key = ConfigKey.parse(name);
                config.recursiveChild(key, value);
            }
        }
    }

    /**
     * Before adding environment variables adopt from env syntax - . is disallowed.
     * We are changing from _ to . and Capital letter to -
     * @param environment
     * @return
     */
    private static Map<String, String> adopt(Map<String, String> environment) {
        Map<String, String> releaxed = new HashMap<>();
        environment.keySet().forEach(s -> releaxed.put(relax(s), environment.get(s)));
        return releaxed;
    }

    private static String relax(String s) {
        String result = s.replace('_', '.');
        StringBuilder builder = new StringBuilder();
        for (char ch : result.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                builder.append('-');
            }
            builder.append(ch);
        }
        return builder.toString().toLowerCase();
    }

}


