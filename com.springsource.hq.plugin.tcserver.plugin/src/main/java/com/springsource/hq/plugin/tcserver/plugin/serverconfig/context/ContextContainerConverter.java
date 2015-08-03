/*
 * Copyright (C) 2009-2015  Pivotal Software, Inc
 *
 * This program is is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.context;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.ContextContainer;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.StaticResourceCache;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.context.WebApplicationLogger;

public class ContextContainerConverter extends AbstractXmlElementConverter implements XmlElementConverter<ContextContainer> {

    private static final String CONTEXT = "Context";

    private static final String CACHING_ALLOWED = "cachingAllowed";

    private static final String CACHE_TTL = "cacheTTL";

    private static final String CACHE_MAX_SIZE = "cacheMaxSize";

    private static final String SWALLOW_OUTPUT = "swallowOutput";

    public ContextContainer convert(final Element context, final Properties catalinaProperties) {
        final ContextContainer contextContainer = new ContextContainer();
        contextContainer.setStaticResourceCache(createStaticResourceCache(context, catalinaProperties));
        contextContainer.setWebApplicationLogger(createWebApplicationLogger(context, catalinaProperties));
        return contextContainer;
    }

    private WebApplicationLogger createWebApplicationLogger(final Element context, final Properties catalinaProperties) {
        final WebApplicationLogger webApplicationLogger = new WebApplicationLogger();
        String swallowOutput = parseProperties(context.getAttribute(SWALLOW_OUTPUT), catalinaProperties);
        if (!(EMPTY_STRING.equals(swallowOutput))) {
            webApplicationLogger.setSwallowOutput(Boolean.valueOf(swallowOutput));
        }
        return webApplicationLogger;
    }

    private StaticResourceCache createStaticResourceCache(final Element context, final Properties catalinaProperties) {
        final StaticResourceCache staticResourceCache = new StaticResourceCache();
        String cacheMaxSize = parseProperties(context.getAttribute(CACHE_MAX_SIZE), catalinaProperties);
        if (!(EMPTY_STRING.equals(cacheMaxSize))) {
            try {
                staticResourceCache.setCacheMaxSize(Long.valueOf(cacheMaxSize));
            } catch (NumberFormatException e) {
                logNonNumericValue(CONTEXT, CACHE_MAX_SIZE, cacheMaxSize);
            }
        }
        String cacheTTL = parseProperties(context.getAttribute(CACHE_TTL), catalinaProperties);
        if (!(EMPTY_STRING.equals(cacheTTL))) {
            try {
                staticResourceCache.setCacheTTL(Long.valueOf(cacheTTL));
            } catch (NumberFormatException e) {
                logNonNumericValue(CONTEXT, CACHE_TTL, cacheTTL);
            }
        }
        String cachingAllowed = parseProperties(context.getAttribute(CACHING_ALLOWED), catalinaProperties);
        if (!(EMPTY_STRING.equals(cachingAllowed))) {
            staticResourceCache.setCachingAllowed(Boolean.valueOf(cachingAllowed));
        }
        return staticResourceCache;
    }

    public void convert(Document document, Element context, ContextContainer from, Properties catalinaProperties) {
        convertStaticResourceCache(context, from.getStaticResourceCache(), catalinaProperties);
        convertWebApplicationLogger(context, from.getWebApplicationLogger(), catalinaProperties);
    }

    private void convertStaticResourceCache(Element context, StaticResourceCache staticResourceCache, Properties catalinaProperties) {
        setAttribute(context, CACHE_MAX_SIZE, staticResourceCache.getCacheMaxSize(), catalinaProperties, false);
        setAttribute(context, CACHE_TTL, staticResourceCache.getCacheTTL(), catalinaProperties, false);
        setAttribute(context, CACHING_ALLOWED, staticResourceCache.getCachingAllowed(), catalinaProperties, false);
    }

    private void convertWebApplicationLogger(Element context, WebApplicationLogger webAppLogger, Properties catalinaProperties) {
        setAttribute(context, SWALLOW_OUTPUT, webAppLogger.getSwallowOutput(), catalinaProperties, false);
    }

}
