/**
 * Cerberus Copyright (C) 2013 - 2017 cerberustesting
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;

/**
 * @author bcivel
 */
@Configuration
@EnableAsync
@EnableScheduling
@ComponentScan("org.cerberus")
public class CerberusConfiguration {

    @Bean
    public PropertiesResolver properties() {
        PropertiesResolver pr = new PropertiesResolver();
        pr.setOrder(1);
        pr.setIgnoreUnresolvablePlaceholders(false);
        return pr;
    }

    @Bean
    public JndiObjectFactoryBean dataSource() {
        JndiObjectFactoryBean jpfb = new JndiObjectFactoryBean();
        jpfb.setJndiName("jdbc/cerberus" + System.getProperty(Property.ENVIRONMENT));
        jpfb.setResourceRef(true);
        return jpfb;
    }

    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
