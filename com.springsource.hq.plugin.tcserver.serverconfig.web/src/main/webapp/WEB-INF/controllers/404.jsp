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

<div id="content-no-nav">
  <h1>Not Found</h1>
  <p>The requested resource could not be found.</p>
  <p>Your session may have expired due to inactivity.  Refreshing the page may resolve this issue.</p>
</div>

<script type="text/javascript">
  dojo.addOnLoad(function() {
    var content = dojo.byId("content-no-nav");
    var refresh = document.createElement("div");
    var button = document.createElement("button");
    button.appendChild(document.createTextNode("Refresh Page"));
    dojo.connect(button, "onclick", function() {
      top.location = top.location;
    });
    refresh.appendChild(button);
    content.appendChild(refresh);
  });
</script>

<jsp:include page="/WEB-INF/layouts/bottom.jsp" />
