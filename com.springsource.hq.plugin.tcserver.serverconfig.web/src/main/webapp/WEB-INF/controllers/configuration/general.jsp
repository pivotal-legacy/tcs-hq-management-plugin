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
  </div>
  
  <jsp:include page="/WEB-INF/layouts/saveChanges.jsp" />
  
  <h1>
    General Configuration
    <a class="help-link allow-unsaved-changes" 
       href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-GeneralConfiguration" 
       target="help">
       <img src="<c:url value="/resources/images/help.png" />">
    </a>
  </h1>
  <jsp:include page="/WEB-INF/layouts/messages.jsp" />
  
  <spring:url value="/app/{settingsId}/configuration/" var="url">
    <spring:param name="settingsId" value="${settings.humanId}" />
  </spring:url>
  <form:form action="${url}" modelAttribute="generalConfig" method="put">
    <table class="bordered-table">
      <tr>
        <th colspan="2">Server Properties</th>
      </tr>
      <tr>
        <td><form:label path="serverProperties.port" cssClass="required">Shutdown Port</form:label></td>
        <td>
          <form:input path="serverProperties.port" cssClass="spring-js-port spring-js-required" />
          <a class="help-link allow-unsaved-changes" 
	         href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurastiongeneralport" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="serverProperties.port" cssClass="error" />
          <ul class="recomendation">
            <li>Disabling the shutdown port is recommended (-1)</li>
          </ul>
          <spring:bind path="serverProperties.port">
            <script type="text/javascript">
              ams.putId("id.serverProperties.port", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="serverProperties.shutdown" cssClass="required">Shutdown Command</form:label></td>
        <td>
          <form:input path="serverProperties.shutdown" cssClass="spring-js-text spring-js-required" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationgeneralshutdown" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="serverProperties.shutdown" cssClass="error" />
          <ul class="recomendation">
            <li>Non-default value recommended for security.  Blank and 'SHUTDOWN' should be avoided as they are defaults.</li>
          </ul>
          <spring:bind path="serverProperties.shutdown">
            <script type="text/javascript">
              ams.putId("id.serverProperties.shutdown", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
    </table>
    <br />
    <table class="bordered-table">
      <tr>
        <th colspan="2">JMX Listener</th>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label>
            <form:checkbox path="jmxListener.enabled" cssClass="spring-js-boolean" />
            Enabled
          </label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxenabled" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.enabled" cssClass="error" />
          <ul class="recomendation">
            <li>Disabling the JMX listener is <strong>strongly discouraged</strong> as it is used internally.</li>
            <li>Please enable JMX via another mechanism if you disable the JMX listener</li>
          </ul>
          <spring:bind path="jmxListener.enabled">
            <script type="text/javascript">
              ams.putId("id.jmxListener.enabled", "${fn:escapeXml(status.expression)}1");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jmxListener.port" cssClass="required">Port</form:label></td>
        <td>
          <form:input path="jmxListener.port" cssClass="spring-js-port spring-js-required" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxport" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.port" cssClass="error" />
          <spring:bind path="jmxListener.port">
            <script type="text/javascript">
              ams.putId("id.jmxListener.port", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jmxListener.bind">IP Address</form:label></td>
        <td>
          <form:input path="jmxListener.bind" cssClass="spring-js-text" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxbind"
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.bind" cssClass="error" />
          <spring:bind path="jmxListener.bind">
            <script type="text/javascript">
              ams.putId("id.jmxListener.bind", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jmxListener.protocols">Protocols</form:label></td>
        <td>
          <form:input path="jmxListener.protocols" cssClass="spring-js-text" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxprotocols" target="help"><img src="<c:url value="/resources/images/help.png" />"></a>
          <form:errors path="jmxListener.protocols" cssClass="error" />
          <spring:bind path="jmxListener.protocols">
            <script type="text/javascript">
              ams.putId("id.jmxListener.protocols", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td colspan="2"><h3>Authentication</h3></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label>
            <form:checkbox path="jmxListener.authenticate" cssClass="spring-js-boolean" />
            Authenticate
          </label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxauthenticate" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.authenticate" cssClass="error" />
          <spring:bind path="jmxListener.authenticate">
            <script type="text/javascript">
              ams.putId("id.jmxListener.authenticate", "${fn:escapeXml(status.expression)}1");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jmxListener.accessFile">Access File</form:label></td>
        <td>
          <form:input path="jmxListener.accessFile" cssClass="spring-js-text spring-js-extra-wide" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxaccessFile" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.accessFile" cssClass="error" />
          <spring:bind path="jmxListener.accessFile">
            <script type="text/javascript">
              ams.putId("id.jmxListener.accessFile", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jmxListener.passwordFile">Password File</form:label></td>
        <td>
          <form:input path="jmxListener.passwordFile" cssClass="spring-js-text spring-js-extra-wide" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxpasswordFile" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.passwordFile" cssClass="error" />
          <spring:bind path="jmxListener.passwordFile">
            <script type="text/javascript">
              ams.putId("id.jmxListener.passwordFile", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td colspan="2"><h3>Security/SSL</h3></td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label>
            <form:checkbox path="jmxListener.useSSL" cssClass="spring-js-boolean" />
            Use SSL
          </label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxuseSSL" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.useSSL" cssClass="error" />
          <spring:bind path="jmxListener.useSSL">
            <script type="text/javascript">
              ams.putId("id.jmxListener.useSSL", "${fn:escapeXml(status.expression)}1");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label>
            <form:checkbox path="jmxListener.clientAuth" cssClass="spring-js-boolean" />
            Client Authentication
          </label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxclientAuth" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.clientAuth" cssClass="error" />
          <spring:bind path="jmxListener.clientAuth">
            <script type="text/javascript">
              ams.putId("id.jmxListener.clientAuth", "${fn:escapeXml(status.expression)}1");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <label>
            <form:checkbox path="jmxListener.useJdkClientFactory" cssClass="spring-js-boolean" />
            Use JDK Client Factory
          </label>
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxuseJdkClientFactory" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.useJdkClientFactory" cssClass="error" />
          <spring:bind path="jmxListener.useJdkClientFactory">
            <script type="text/javascript">
              ams.putId("id.jmxListener.useJdkClientFactory", "${fn:escapeXml(status.expression)}1");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jmxListener.cipherSuites">Cipher Suites</form:label></td>
        <td>
          <form:input path="jmxListener.cipherSuites" cssClass="spring-js-text spring-js-wide" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxcipherSuites" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.cipherSuites" cssClass="error" />
          <spring:bind path="jmxListener.cipherSuites">
            <script type="text/javascript">
              ams.putId("id.jmxListener.cipherSuites", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jmxListener.truststoreFile">Truststore File</form:label></td>
        <td>
          <form:input path="jmxListener.truststoreFile" cssClass="spring-js-text spring-js-extra-wide" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxtruststoreFile" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.truststoreFile" cssClass="error" />
          <spring:bind path="jmxListener.truststoreFile">
            <script type="text/javascript">
              ams.putId("id.jmxListener.truststoreFile", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jmxListener.truststorePass">Truststore Pass</form:label></td>
        <td>
          <form:input path="jmxListener.truststorePass" cssClass="spring-js-text" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxtruststorePass" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.truststorePass" cssClass="error" />
          <spring:bind path="jmxListener.truststorePass">
            <script type="text/javascript">
              ams.putId("id.jmxListener.truststorePass", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jmxListener.keystoreFile">Keystore File</form:label></td>
        <td>
          <form:input path="jmxListener.keystoreFile" cssClass="spring-js-text spring-js-extra-wide" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxkeystoreFile" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.keystoreFile" cssClass="error" />
          <spring:bind path="jmxListener.keystoreFile">
            <script type="text/javascript">
              ams.putId("id.jmxListener.keystoreFile", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
      <tr>
        <td><form:label path="jmxListener.keystorePass">Keystore Pass</form:label></td>
        <td>
          <form:input path="jmxListener.keystorePass" cssClass="spring-js-text" />
          <a class="help-link allow-unsaved-changes" 
             href="/ui_docs/DOC/ui-tcserver.ServerConfigurationRef.html#ui-tcserver.ServerConfigurationRef-configurationjmxkeystorePass" 
             target="help">
             <img src="<c:url value="/resources/images/help.png" />">
          </a>
          <form:errors path="jmxListener.keystorePass" cssClass="error" />
          <spring:bind path="jmxListener.keystorePass">
            <script type="text/javascript">
              ams.putId("id.jmxListener.keystorePass", "${fn:escapeXml(status.expression)}");
            </script>
          </spring:bind>
        </td>
      </tr>
    </table>
    <br />
    <c:if test="${!readOnly}">
      <input type="submit" class="spring-js-submit" value="Save"/>
    </c:if>
  </form:form>
</div>

<script type="text/javascript">
dojo.addOnLoad(function() {
	var port = ams.getWidgetByKey("id.serverProperties.port");
	var shutdown = ams.getWidgetByKey("id.serverProperties.shutdown");
	
	port.constraints.min = -1;
	port.intermediateChanges = true;
	port.validate();

	var portOnChange = function() {
		ams.setReadOnly(shutdown, port.getValue() == "-1");
	}
	
	port.connect(port, "onChange", portOnChange);
	portOnChange();
});

dojo.addOnLoad(function() {
	var enabled = ams.getWidgetByKey("id.jmxListener.enabled");
	var port = ams.getWidgetByKey("id.jmxListener.port");
	var authenticate = ams.getWidgetByKey("id.jmxListener.authenticate");
	var useSSL = ams.getWidgetByKey("id.jmxListener.useSSL");
	
	var requireKeystoreAndPass = function() {
		var keystoreFile = ams.getWidgetByKey("id.jmxListener.keystoreFile");
		var keystorePass = ams.getWidgetByKey("id.jmxListener.keystorePass");
		
		if (dojo.attr(useSSL, "checked")) {
			keystoreFile.attr("required", true);
			keystorePass.attr("required", true);
		} else {
			keystoreFile.attr("required", false);
			keystorePass.attr("required", false); 
		}
	}
	var jmxGroup = [
			port,
			ams.getWidgetByKey("id.jmxListener.bind"),
			ams.getWidgetByKey("id.jmxListener.protocols"),
			authenticate,
			useSSL
		];
	var jmxAuthGroup = [
			ams.getWidgetByKey("id.jmxListener.accessFile"),
			ams.getWidgetByKey("id.jmxListener.passwordFile")
		];
	var jmxSSLGroup = [
			ams.getWidgetByKey("id.jmxListener.clientAuth"),
			ams.getWidgetByKey("id.jmxListener.useJdkClientFactory"),
			ams.getWidgetByKey("id.jmxListener.cipherSuites"),
			ams.getWidgetByKey("id.jmxListener.truststoreFile"),
			ams.getWidgetByKey("id.jmxListener.truststorePass"),
			ams.getWidgetByKey("id.jmxListener.keystoreFile"),
			ams.getWidgetByKey("id.jmxListener.keystorePass")
		];

	var toggleJmxAuthReadOnly =  function() {
		if (dojo.attr(enabled, "checked") && dojo.attr(authenticate, "checked")) {
			ams.setReadOnly(jmxAuthGroup, false);
		}
		else {
			ams.setReadOnly(jmxAuthGroup, true);
		}
	}
	
	var toggleJmxSslReadOnly =  function() {
		if (dojo.attr(enabled, "checked") && dojo.attr(useSSL, "checked")) {
			ams.setReadOnly(jmxSSLGroup, false);
		}
		else {
			ams.setReadOnly(jmxSSLGroup, true);
		}
	}

	dojo.connect(authenticate, "onClick", authenticate, toggleJmxAuthReadOnly);
	dojo.connect(useSSL, "onClick", useSSL, toggleJmxSslReadOnly);
	dojo.connect(useSSL, "onClick", useSSL, function() { requireKeystoreAndPass(); });
	dojo.connect(enabled, "onClick", enabled, function() {
		if (dojo.attr(enabled, "checked")) {
			port.attr("required", true);
			ams.setReadOnly(jmxGroup, false);
		}
		else {
			port.attr("required", false);
			ams.setReadOnly(jmxGroup, true);
		}
		toggleJmxAuthReadOnly();
		toggleJmxSslReadOnly();
	});
	enabled.onClick();
});
</script>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />
