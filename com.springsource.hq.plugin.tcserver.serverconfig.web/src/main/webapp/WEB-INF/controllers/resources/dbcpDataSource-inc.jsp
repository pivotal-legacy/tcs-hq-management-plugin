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

<jsp:include page="dataSource-inc.jsp" />
<br />
<div id="dbcp-connection-pool" class="spring-titlePane bordered-table">
  <h2>DBCP Connection Pool</h2>
  <table>
    <tr>
      <td colspan="2"><h3>Pool Size</h3></td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.initialSize">Initial Number of Connections</form:label></td>
      <td>
        <form:input path="connectionPool.initialSize" cssClass="spring-js-numeric" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionPoolinitialSize" 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.initialSize" cssClass="error" />
        <ul class="recomendation">
          <li>10 connections recommended</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.maxActive">Max Active Connections</form:label></td>
      <td>
        <form:input path="connectionPool.maxActive" cssClass="spring-js-numeric" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionPoolmaxActive" 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.maxActive" cssClass="error" />
        <ul class="recomendation">
          <li>100 connections recommended</li>
        </ul>
        <spring:bind path="connectionPool.maxActive">
          <script type="text/javascript">
            ams.putId("id.connectionPool.maxActive", "${fn:escapeXml(status.expression)}");
          </script>
        </spring:bind>
      </td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.minIdle">Min Idle Connections</form:label></td>
      <td>
        <form:input path="connectionPool.minIdle" cssClass="spring-js-numeric" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionPoolminIdle" 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.minIdle" cssClass="error" />
        <ul class="recomendation">
          <li>10 connections recommended</li>
        </ul>
        <spring:bind path="connectionPool.minIdle">
          <script type="text/javascript">
            ams.putId("id.connectionPool.minIdle", "${fn:escapeXml(status.expression)}");
          </script>
        </spring:bind>
      </td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.maxIdle">Max Idle Connections</form:label></td>
      <td>
        <form:input path="connectionPool.maxIdle"  cssClass="spring-js-numeric" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionPoolmaxIdle" 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.maxIdle" cssClass="error" />
        <ul class="recomendation">
          <li>10 connections recommended</li>
        </ul>
        <spring:bind path="connectionPool.maxIdle">
          <script type="text/javascript">
            ams.putId("id.connectionPool.maxIdle", "${fn:escapeXml(status.expression)}");
          </script>
        </spring:bind>
      </td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.maxWait">Max Wait Time For Connection Borrow (ms)</form:label></td>
      <td>
        <form:input path="connectionPool.maxWait" cssClass="spring-js-numeric spring-js-unit-ms" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionPoolmaxWait" 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.maxWait" cssClass="error" />
        <ul class="recomendation">
          <li>10,000 ms recommended</li>
        </ul>
        <spring:bind path="connectionPool.maxWait">
          <script type="text/javascript">
            ams.putId("id.connectionPool.maxWait", "${fn:escapeXml(status.expression)}");
          </script>
        </spring:bind>
      </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
       <label><form:checkbox path="connectionPool.poolPreparedStatements" cssClass="spring-js-boolean" /> Pool Prepared Statements</label>
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPoolpoolPrepa..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.poolPreparedStatements" cssClass="error" />
      </td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.maxOpenPreparedStatements">Max Open Prepared Statements</form:label></td>
      <td>
        <form:input path="connectionPool.maxOpenPreparedStatements" cssClass="spring-js-numeric" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPoolmaxOpenPr..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.maxOpenPreparedStatements" cssClass="error" />
      </td>
    </tr>
    <tr>
      <td colspan="2"><h3>Connection Validation</h3></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <label><form:checkbox path="connectionPool.testOnBorrow" cssClass="spring-js-boolean" /> Test on Borrow</label>
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionPooltestOnBorrow" 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.testOnBorrow" cssClass="error" />
        <ul class="recomendation">
          <li>Testing on borrow recommended</li>
        </ul>
        <spring:bind path="connectionPool.testOnBorrow">
          <script type="text/javascript">
            ams.putId("id.connectionPool.testOnBorrow", "${fn:escapeXml(status.expression)}1");
          </script>
        </spring:bind>
      </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <label><form:checkbox path="connectionPool.testOnReturn" cssClass="spring-js-boolean" /> Test on Return</label>
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionPooltestOnReturn" 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.testOnReturn" cssClass="error" />
        <ul class="recomendation">
          <li>Testing on return not recommended</li>
        </ul>
        <spring:bind path="connectionPool.testOnReturn">
          <script type="text/javascript">
            ams.putId("id.connectionPool.testOnReturn", "${fn:escapeXml(status.expression)}1");
          </script>
        </spring:bind>
      </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <label><form:checkbox path="connectionPool.testWhileIdle" cssClass="spring-js-boolean" /> Test While Idle</label>
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionPooltestWhileIdle" 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.testWhileIdle" cssClass="error" />
        <ul class="recomendation">
          <li>Testing when idle not recommended</li>
        </ul>
        <spring:bind path="connectionPool.testWhileIdle">
          <script type="text/javascript">
            ams.putId("id.connectionPool.testWhileIdle", "${fn:escapeXml(status.expression)}1");
          </script>
        </spring:bind>
      </td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.validationQuery">Validation Query</form:label></td>
      <td>
        <form:textarea path="connectionPool.validationQuery" cssClass="spring-js-textarea spring-js-wide" rows="3" cols="40" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPoolvalidatio..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.validationQuery" cssClass="error" />
        <spring:bind path="connectionPool.validationQuery">
          <script type="text/javascript">
            ams.putId("id.connectionPool.validationQuery", "${fn:escapeXml(status.expression)}");
          </script>
        </spring:bind>
      </td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.timeBetweenEvictionRunsMillis">Time Between Eviction Runs (ms)</form:label></td>
      <td>
        <form:input path="connectionPool.timeBetweenEvictionRunsMillis" cssClass="spring-js-numeric spring-js-unit-ms" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPooltimeBetwe..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.timeBetweenEvictionRunsMillis" cssClass="error" />
        <ul class="recomendation">
          <li>10,000 ms recommended</li>
        </ul>
        <spring:bind path="connectionPool.timeBetweenEvictionRunsMillis">
          <script type="text/javascript">
            ams.putId("id.connectionPool.timeBetweenEvictionRunsMillis", "${fn:escapeXml(status.expression)}");
          </script>
        </spring:bind>
      </td>
    </tr>
    <tr>
     <td><form:label path="connectionPool.numTestsPerEvictionRun">Tests Per Eviction Run</form:label></td>
      <td>
        <form:input path="connectionPool.numTestsPerEvictionRun" cssClass="spring-js-numeric" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPoolnumTestsP..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.numTestsPerEvictionRun" cssClass="error" />
      </td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.minEvictableIdleTimeMillis">Min Evictable Idle Time (ms)</form:label></td>
      <td>
        <form:input path="connectionPool.minEvictableIdleTimeMillis" cssClass="spring-js-numeric spring-js-unit-ms" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPoolminEvicta..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.minEvictableIdleTimeMillis" cssClass="error" />
        <ul class="recomendation">
          <li>10,000 ms recommended</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <label><form:checkbox path="connectionPool.removeAbandoned" cssClass="spring-js-boolean" /> Remove Abandoned Connections</label>
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPoolremoveAba..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.removeAbandoned" cssClass="error" />
        <ul class="recomendation">
          <li>Removing abandoned connections recommended</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <label><form:checkbox path="connectionPool.logAbandoned" cssClass="spring-js-boolean" /> Log Abandoned Statements and Connections</label>
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-resourcesjdbcdataSourceconnectionPoollogAbandoned" 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.logAbandoned" cssClass="error" />
        <ul class="recomendation">
          <li>Logging abandonment recommended</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.removeAbandonedTimeout">Remove Abandoned Timeout (s)</form:label></td>
      <td>
        <form:input path="connectionPool.removeAbandonedTimeout" cssClass="spring-js-numeric  spring-js-unit-s" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPoolremoveAba..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.removeAbandonedTimeout" cssClass="error" />
        <ul class="recomendation">
          <li>60 s recommended</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td colspan="2"><h3>Transaction Management</h3></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <label><form:checkbox path="connectionPool.defaultAutoCommit" cssClass="spring-js-boolean" /> Default Auto Commit</label>
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPooldefaultAu..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.defaultAutoCommit" cssClass="error" />
      </td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <label><form:checkbox path="connectionPool.defaultReadOnly" cssClass="spring-js-boolean" /> Default Read Only</label>
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPooldefaultRe..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.defaultReadOnly" cssClass="error" />
      </td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.defaultTransactionIsolation">Default Transaction Isolation</form:label></td>
      <td>
        <form:select path="connectionPool.defaultTransactionIsolation" cssClass="spring-js-select">
          <form:option value="DEFAULT">JDBC Driver Default</form:option>
          <form:option value="NONE">None</form:option>
          <form:option value="READ_COMMITTED">Read Committed</form:option>
          <form:option value="READ_UNCOMMITTED">Read Uncommitted</form:option>
          <form:option value="REPEATABLE_READ">Repeatable Read</form:option>
          <form:option value="SERIALIZABLE">Serializable</form:option>
        </form:select>
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPooldefaultTr..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.defaultTransactionIsolation" cssClass="error" />
      </td>
    </tr>
    <tr>
      <td colspan="2"><h3>Miscellaneous</h3></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td>
        <label><form:checkbox path="connectionPool.accessToUnderlyingConnectionAllowed" cssClass="spring-js-boolean" /> Allow Access to Underlying Connection</label>
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPoolaccessToU..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.accessToUnderlyingConnectionAllowed" cssClass="error" />
      </td>
    </tr>
    <tr>
      <td><form:label path="connectionPool.defaultCatalog">Default Catalog</form:label></td>
      <td>
        <form:input path="connectionPool.defaultCatalog" cssClass="spring-js-text" />
        <a class="help-link allow-unsaved-changes" 
           href="/ui_docs/DOC/ui-tcserver.ServerJDBCRef.html#ui-tcserver.ServerJDBCRef-esourcesjdbcdataSourceconnectionPooldefaultCa..." 
           target="help">
           <img src="<c:url value="/resources/images/help.png" />">
        </a>
        <form:errors path="connectionPool.defaultCatalog" cssClass="error" />
      </td>
    </tr>
  </table>
</div>

<script type="text/javascript">
  dojo.addOnLoad(function() {
    var maxActive = ams.getWidgetByKey("id.connectionPool.maxActive");
    maxActive.constraints.min = -1;
    maxActive.validate();
    
    var maxIdle = ams.getWidgetByKey("id.connectionPool.maxIdle");
    maxIdle.constraints.min = -1;
    maxIdle.validate();
    
    var maxWait = ams.getWidgetByKey("id.connectionPool.maxWait");
    maxWait.constraints.min = -1;
    maxWait.validate();
    
    var timeBetweenEvictionRunsMillis = ams.getWidgetByKey("id.connectionPool.timeBetweenEvictionRunsMillis");
    timeBetweenEvictionRunsMillis.constraints.min = -1;
    timeBetweenEvictionRunsMillis.validate();
    
    var testOnBorrow = ams.getWidgetByKey("id.connectionPool.testOnBorrow");
    var testOnReturn = ams.getWidgetByKey("id.connectionPool.testOnReturn");
    var testWhileIdle = ams.getWidgetByKey("id.connectionPool.testWhileIdle");
    var validationQuery = ams.getWidgetByKey("id.connectionPool.validationQuery");
    var toggleValidationQueryRequired = function() {
      // unfortunately there is no validating text area
      if (testOnBorrow.attr("checked") || testOnReturn.attr("checked") || testWhileIdle.attr("checked")) {
        //validationQuery.attr("required", true);
        ams.setReadOnly(validationQuery, false);
        dojo.query("label[for=" + ams.getId("id.connectionPool.validationQuery") + "]").addClass("required");
      }
      else {
        //validationQuery.attr("required", false);
        ams.setReadOnly(validationQuery, true);
        dojo.query("label[for=" + ams.getId("id.connectionPool.validationQuery") + "]").removeClass("required");
      }
    }
    testOnBorrow.connect(testOnBorrow, "onClick", toggleValidationQueryRequired);
    testOnReturn.connect(testOnReturn, "onClick", toggleValidationQueryRequired);
    testWhileIdle.connect(testWhileIdle, "onClick", toggleValidationQueryRequired);
    toggleValidationQueryRequired();
    
    ams.createMinMaxConstraint(
      ams.getWidgetByKey("id.connectionPool.minIdle"),
      ams.getWidgetByKey("id.connectionPool.maxIdle")
    );
  });
</script>
