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

<form:hidden path="id" />
<table class="bordered-table">
  <tr>
    <th colspan="2">General</th>
  </tr>
  <tr>
    <td><form:label path="port" cssClass="required">Port</form:label></td>
    <td>
      <form:input path="port" cssClass="spring-js-port spring-js-required" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorport"
         target="help"><img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="port" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="address">IP Address</form:label></td>
    <td>
      <form:input path="address" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectoraddress"
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="address" cssClass="error" />
      <ul class="recomendation">
        <li>Use of a hostname, instead of IP address, is discouraged</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><form:label path="protocol">Protocol</form:label></td>
    <td>
      <form:select path="protocol" cssClass="spring-js-select">
        <form:option label="HTTP/Java" value="org.apache.coyote.http11.Http11Protocol" />
        <form:option label="HTTP/APR" value="org.apache.coyote.http11.Http11AprProtocol" />
        <form:option label="HTTP/Java NIO" value="org.apache.coyote.http11.Http11NioProtocol" />
      </form:select>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorprotocol"
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="protocol" cssClass="error" />
      <spring:bind path="protocol">
        <script type="text/javascript">
          ams.putId("id.http-connector.protocol", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="maxThreads">Max Threads</form:label></td>
    <td>
      <form:input path="maxThreads" cssClass="spring-js-numeric" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectormaxThreads"
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="maxThreads" cssClass="error" />
      <spring:bind path="protocol">
        <script type="text/javascript">
          ams.putId("id.http-connector.maxThreads", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="connectionTimeout">Connection Timeout (ms)</form:label></td>
    <td>
      <form:input path="connectionTimeout" cssClass="spring-js-numeric spring-js-unit-ms" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorconnectionTimeout"
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="connectionTimeout" cssClass="error" />
      <ul class="recomendation">
        <li>3,000 ms recommended</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td><form:label path="maxKeepAliveRequests">Max Keep Alive Requests</form:label></td>
    <td>
      <form:input path="maxKeepAliveRequests" cssClass="spring-js-numeric" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectormaxKeepAliveRequests" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="maxKeepAliveRequests" cssClass="error" />
      <spring:bind path="maxKeepAliveRequests">
        <script type="text/javascript">
          ams.putId("id.http-connector.maxKeepAliveRequests", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="acceptCount">Accept Count</form:label></td>
    <td>
      <form:input path="acceptCount" cssClass="spring-js-numeric" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectoracceptCount" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="acceptCount" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="scheme">Scheme</form:label></td>
    <td>
      <form:select path="scheme" cssClass="spring-js-select">
        <form:option value="http" label="http" />
        <form:option value="https" label="https" />
      </form:select>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorscheme" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="scheme" cssClass="error" />
      <spring:bind path="scheme">
        <script type="text/javascript">
          ams.putId("id.http-connector.scheme", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="proxyName">Proxy Host</form:label></td>
    <td>
      <form:input path="proxyName" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorproxyName" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="proxyName" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="proxyPort">Proxy Port</form:label></td>
    <td>
      <form:input path="proxyPort" cssClass="spring-js-port" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorproxyPort" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="proxyPort" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="redirectPort">Redirect Port</form:label></td>
    <td>
      <form:input path="redirectPort" cssClass="spring-js-port" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorredirectPort" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="redirectPort" cssClass="error" />
    </td>
  </tr>
</table>
<br />
<table class="bordered-table">
  <tr>
    <th colspan="2">Security/SSL</th>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <label><form:checkbox path="secure" cssClass="spring-js-boolean" /> Secure</label>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorsecure" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="secure" cssClass="error" />
      <spring:bind path="secure">
        <script type="text/javascript">
          ams.putId("id.http-connector.secure", "${fn:escapeXml(status.expression)}1");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>
      <label><form:checkbox path="sSLEnabled" cssClass="spring-js-boolean" /> Enable SSL</label>
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLEnabled" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="sSLEnabled" cssClass="error" />
      <spring:bind path="sSLEnabled">
        <script type="text/javascript">
          ams.putId("id.http-connector.SSLEnabled", "${fn:escapeXml(status.expression)}1");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="algorithm">Certificate Encoding Algorithm</form:label></td>
    <td>
      <form:input path="algorithm" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectoralgorithm" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="algorithm" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="keystoreFile">Keystore File</form:label></td>
    <td>
      <form:input path="keystoreFile" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorkeystoreFile" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="keystoreFile" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="keystorePass">Keystore Password</form:label></td>
    <td>
      <form:input path="keystorePass" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorkeystorePass" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="keystorePass" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="keyAlias">Key Alias</form:label></td>
    <td>
      <form:input path="keyAlias" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorkeyAlias" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="keyAlias" cssClass="error" />
    </td>
  </tr>
</table>
<div  id="apr-ssl-settings">
<br />
<!-- 
<table class="bordered-table">
  <tr>
    <th colspan="2">APR SSL Settings</th>
  </tr>
  <tr>
    <td colspan="2">These settings are only relevant for APR based connectors that also use SSL.</td>
  </tr>
  <tr>
    <td><form:label path="SSLCertificateFile" cssClass="required">SSL Certificate File</form:label></td>
    <td>
      <form:input path="SSLCertificateFile" cssClass="spring-js-text spring-js-extra-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLCertificateFile" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLCertificateFile" cssClass="error" />
      <spring:bind path="SSLCertificateFile">
        <script type="text/javascript">
          ams.putId("id.http-connector.SSLCertificateFile", "${fn:escapeXml(status.expression)}");
        </script>
      </spring:bind>
    </td>
  </tr>
  <tr>
    <td><form:label path="SSLProtocol">SSL Protocol</form:label></td>
    <td>
      <form:input path="SSLProtocol" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLProtocol" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLProtocol" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="SSLCipherSuite">SSL Cipher Suite</form:label></td>
    <td>
      <form:input path="SSLCipherSuite" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLCipherSuite" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLCipherSuite" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="SSLCertificateKeyFile">SSL Certificate Key File</form:label></td>
    <td>
      <form:input path="SSLCertificateKeyFile" cssClass="spring-js-text spring-js-extra-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLCertificateKeyFile" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLCertificateKeyFile" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="SSLPassword">SSL Password</form:label></td>
    <td>
      <form:input path="SSLPassword" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLPassword" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLPassword" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="SSLVerifyClient">SSL Verify Client</form:label></td>
    <td>
      <form:input path="SSLVerifyClient" cssClass="spring-js-text" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLVerifyClient" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLVerifyClient" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="SSLVerifyDepth">SSL Verify Depth</form:label></td>
    <td>
      <form:input path="SSLVerifyDepth" cssClass="spring-js-numeric" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLVerifyDepth" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLVerifyDepth" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="SSLCACertificateFile">SSL CA Certificate File</form:label></td>
    <td>
      <form:input path="SSLCACertificateFile" cssClass="spring-js-text spring-js-extra-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLCACertificateFile" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLCACertificateFile" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="SSLCACertificatePath">SSL CA Certificate Path</form:label></td>
    <td>
      <form:input path="SSLCACertificatePath" cssClass="spring-js-text spring-js-extra-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLCACertificatePath" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLCACertificatePath" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="SSLCertificateChainFile">SSL Certificate Chain File</form:label></td>
    <td>
      <form:input path="SSLCertificateChainFile" cssClass="spring-js-text spring-js-extra-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLCertificateChainFile" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLCertificateChainFile" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="SSLCARevocationFile">SSL CA Revocation File</form:label></td>
    <td>
      <form:input path="SSLCARevocationFile" cssClass="spring-js-text spring-js-extra-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLCARevocationFile" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLCARevocationFile" cssClass="error" />
    </td>
  </tr>
  <tr>
    <td><form:label path="SSLCARevocationPath">SSL CA Revocation Path</form:label></td>
    <td>
      <form:input path="SSLCARevocationPath" cssClass="spring-js-text spring-js-extra-wide" />
      <a class="help-link allow-unsaved-changes" 
         href="/ui_docs/DOC/ui-tcserver.ServerServicesRef.html#ui-tcserver.ServerServicesRef-servicesconnectorSSLCARevocationPath" 
         target="help">
         <img src="<c:url value="/resources/images/help.png" />">
      </a>
      <form:errors path="SSLCARevocationPath" cssClass="error" />
    </td>
  </tr>
</table>
-->
</div>

<script type="text/javascript">
// This should have been removed when TCS-58 was worked, might be added back it reworked.
//   var toggleAprSslSettings = function() {
// 	  var protocol = ams.getWidgetByKey("id.http-connector.protocol");
	  
// 	  var sslCertificateFile = ams.getWidgetByKey("id.http-connector.SSLCertificateFile");

//       if (protocol.getDisplayedValue() == "HTTP/APR" && sslEnabled.attr("checked")) {
//         sslCertificateFile.attr("required", true);
//         dojo.style("apr-ssl-settings", "display", "");
//       }
//       else {
//     	sslCertificateFile.attr("required", false);
//         dojo.style("apr-ssl-settings", "display", "none");
//       }
//       resizeFrame();
//   }

  dojo.addOnLoad(function() {
	var sslEnabled = ams.getWidgetByKey("id.http-connector.SSLEnabled");
    var protocol = ams.getWidgetByKey("id.http-connector.protocol");
    // Should have been pulled from TCS-58, might be added back.
    //protocol.connect(protocol, "onChange", toggleAprSslSettings);
    //sslEnabled.connect(sslEnabled, "onChange", toggleAprSslSettings);
    //toggleAprSslSettings();

    var secure = ams.getWidgetByKey("id.http-connector.secure");
    var scheme = ams.getWidgetByKey("id.http-connector.scheme");
    dojo.connect(sslEnabled, "onClick", sslEnabled, function() {
      if (dojo.attr(sslEnabled, "checked")) {
        // setChecked is deprecated, but attr for "checked" does not update the UI
        // dojo.attr(secure, "checked", true);
        secure.setChecked(true);
        scheme.attr("value", "https");
        ams.setReadOnly([secure, scheme], true);
      }
      else {
          ams.setReadOnly([secure, scheme], false);
      }
    });

    var maxKeepAliveRequests = ams.getWidgetByKey("id.http-connector.maxKeepAliveRequests");
    var maxThreads = ams.getWidgetByKey("id.http-connector.maxThreads");

    maxKeepAliveRequests.constraints.min = -1;
    maxThreads.constraints.min = 1;
  });
</script>
