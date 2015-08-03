<%--
  ~ Copyright (C) 2009-2015  Pivotal Software, Inc
  ~
  ~ This program is is free software; you can redistribute it and/or modify
  ~ it under the terms version 2 of the GNU General Public License as
  ~ published by the Free Software Foundation.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program; if not, write to the Free Software
  ~ Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/WEB-INF/layouts/top.jsp" />

<div id="secondary-navigation">
  <jsp:include page="local-navigation.jsp" />
</div><!-- /secondary-navigation -->

<div id="content">
  &nbsp;
  <div id="breadcrumb">
    <spring:url value="/app/{settingsId}/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Home</a>
    &gt;
    <spring:url value="/app/{settingsId}/configuration/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Configuration</a>
    &gt;
    <spring:url value="/app/{settingsId}/configuration/container/" var="url">
      <spring:param name="settingsId" value="${settings.humanId}" />
    </spring:url>
    <a href="${fn:escapeXml(url)}">Context Container</a>
  </div>
  
  <jsp:include page="/WEB-INF/layouts/saveChanges.jsp" />
  
  <h1>
    Context Container Configuration
    <a class="help-link allow-unsaved-changes" 
       href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-ContextContainer" 
       target="help">
       <img src="<c:url value="/resources/images/help.png" />">
    </a>
  </h1>
  <jsp:include page="/WEB-INF/layouts/messages.jsp" />
  
  <spring:url value="/app/{settingsId}/configuration/container/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
  </spring:url>
  <form:form action="${url}" modelAttribute="contextContainer" method="put">
    <table class="bordered-table">
      <tr>
        <th colspan="2">Static Resource Cache</th>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label><form:checkbox path="staticResourceCache.cachingAllowed" cssClass="spring-js-boolean" /> Allow Caching</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationcontainercachingAllowed" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="staticResourceCache.cachingAllowed" cssClass="error" />
          <ul class="recomendation">
            <li>Caching is recommended</li>
          </ul>
          <spring:bind path="staticResourceCache.cachingAllowed">
            <script type="text/javascript">
              ams.putId("id.staticResourceCache.cachingAllowed", "${fn:escapeXml(status.expression)}1");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="staticResourceCache.cacheMaxSize">Max Cache Size (KB)</form:label></td>
        <td>
          <form:input path="staticResourceCache.cacheMaxSize" cssClass="spring-js-numeric  spring-js-unit-KB" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationcontainercacheMaxSize" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="staticResourceCache.cacheMaxSize" cssClass="error" />
          <ul class="recomendation">
            <li>Under 102,400 KB (100 MB) recommended</li>
          </ul>
          <spring:bind path="staticResourceCache.cacheMaxSize">
            <script type="text/javascript">
              ams.putId("id.staticResourceCache.cacheMaxSize", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="staticResourceCache.cacheTTL">Cache TTL (ms)</form:label></td>
        <td>
          <form:input path="staticResourceCache.cacheTTL" cssClass="spring-js-numeric spring-js-unit-ms" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationcontainercacheTTL" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="staticResourceCache.cacheTTL" cssClass="error" />
          <ul class="recomendation">
            <li>5,000 ms - 30,000 ms recommended</li>
          </ul>
          <spring:bind path="staticResourceCache.cacheTTL">
            <script type="text/javascript">
            ams.putId("id.staticResourceCache.cacheTTL", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
    </table>
    <br/>
    <br/>
    <table class="bordered-table">
      <tr>
        <th>Web Application Logger</th>
      </tr>
      <tr>
        <td>
          <label><form:checkbox path="webApplicationLogger.swallowOutput" cssClass="spring-js-boolean" /> Swallow Output</label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationcontainerswallowOutput" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="webApplicationLogger.swallowOutput" cssClass="error" />
        </td>
      </tr>
    </table>
    <br/>
    <c:if test="${!readOnly}">
      <input type="submit" class="spring-js-submit" value="Save"/>
    </c:if>
  </form:form>
</div>

<script type="text/javascript">
  dojo.addOnLoad(function() {
    var cachingAllowed = ams.getWidgetByKey("id.staticResourceCache.cachingAllowed");
    var cacheMaxSize = ams.getWidgetByKey("id.staticResourceCache.cacheMaxSize");
    var cacheTTL = ams.getWidgetByKey("id.staticResourceCache.cacheTTL");

    cacheMaxSize.constraints.min = 1;
    cacheTTL.constraints.min = 1;

    cachingAllowed.connect(cachingAllowed, "onClick", function() {
      if (dojo.attr(cachingAllowed, "checked")) {
        ams.setReadOnly([cacheMaxSize, cacheTTL], false);
      }
      else {
        ams.setReadOnly([cacheMaxSize, cacheTTL], true);
      }
    });
    cachingAllowed.onClick();
  });
</script>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />
