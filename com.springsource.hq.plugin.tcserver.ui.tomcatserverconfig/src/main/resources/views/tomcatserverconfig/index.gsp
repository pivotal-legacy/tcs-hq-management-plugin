%{--
  - Copyright (C) 2009-2015  Pivotal Software, Inc
  -
  - This program is is free software; you can redistribute it and/or modify
  - it under the terms version 2 of the GNU General Public License as
  - published by the Free Software Foundation.
  -
  - This program is distributed in the hope that it will be useful,
  - but WITHOUT ANY WARRANTY; without even the implied warranty of
  - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  - GNU General Public License for more details.
  -
  - You should have received a copy of the GNU General Public License
  - along with this program; if not, write to the Free Software
  - Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
  --}%
<html>
<head>
	
</head>
<body>
	<script>
		var openDialog = function(content, title) {
			var id = "tomcatserverconfig-dialog";
			if (dijit11.byId(id)) {
				dijit11.byId(id).destroyRecursive();
			}
			var dialog = new dijit11.Dialog({id: id, title: title}, content);
			dialog.startup();
			dialog.show();
		}
		
		var anchorTop = function() {
			var location = window.location.toString();
			if (location.indexOf("#") != -1) {
				location = location.substring(0, location.indexOf("#"));
			}		
			window.location = location + "#header";
		}
	</script>
	<iframe src="/tomcatserverconfig/app/?eid=

${eid}&amp;sessionId=${sessionId}&amp;readOnly=${readOnly}&amp;username=${username}&amp;csrfNonce=${csrfNonce}" width="100%" height="500" frameborder="0" id="tomcatserverconfig">
		This feature requires a browser that supports frames
	</iframe>
</body>
</html>
