<!--
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
-->
<html>
<head>
	<link rel=stylesheet href="/hqu/tomcatappmgmt/public/css/table.css" type="text/css">
	<style>
	/* Start collapsible listing */
	.trigger{
		cursor: pointer;
		cursor: hand;
	}
	.branch{
		display: none;
		margin-left: 16px;
	}
	/* End collapsible listing */
	
	/* Start of Services tree lines */
	ul.tree, ul.tree ul { 
		list-style-type: none; 
		background: url(/hqu/tomcatappmgmt/public/images/vline.png) repeat-y;
		margin: 0; 
		padding: 0; 
	} 
	ul.tree ul { 
		margin-left: 10px; 
	} 
	ul.tree li { 
		margin: 0; 
		padding: 0 12px; 
		line-height: 20px; 
		background: url(/hqu/tomcatappmgmt/public/images/node.png) no-repeat; 
	} 
	ul.tree li.last { 
		background: #fff url(/hqu/tomcatappmgmt/public/images/lastnode.png) no-repeat; 
	} 
	/* End of Services tree lines */
	
	</style>

<link rel="stylesheet" href="/hqu/tomcatappmgmt/public/css/dhtmlwindow.css" type="text/css" />

<script type="text/javascript" src="/hqu/tomcatappmgmt/public/js/dhtmlwindow.js">
</script>


<script type="text/javascript"><!--

hqDojo.addOnLoad(function() {
	var hasErrors = showResourceListing();
	createServiceListing();	
	populateDeployedAppsTable();
});

//global values
var _serviceName;
var _hostName;
var _hasErrors;
var _globalCumulativeResults;

// initialize images
var openImg = new Image();
openImg.src = "/hqu/tomcatappmgmt/public/images/expand-minussign-dark.png";
var closedImg = new Image();
closedImg.src = "/hqu/tomcatappmgmt/public/images/expand-plussign-dark.png";
var progressImage = new Image();
progressImage.src = "/hqu/tomcatappmgmt/public/images/progress-feedback.gif";

// Retrieve all selected check boxes.
function getSelectedApplications(){
	var i=0;
	var checkboxFound = hqDojo.byId('checkbox'+i);
	var selectedCheckboxes = [];
	while(checkboxFound) {	
		if (checkboxFound.checked){
			selectedCheckboxes.push(checkboxFound.value);
			checkboxFound.checked = false;
		}
		i++;
		checkboxFound = hqDojo.byId('checkbox'+i);
	}
	return selectedCheckboxes;
}

// Return whether there is an error on any resource in array.
function hasEntryErrorsOnAnyResource(errorsArray){
	if (errorsArray){
		for (var i=0; i < errorsArray.length; i++){
			if (errorsArray[i].hasEntryErrors){
				return true;
			}
		}
	}
	return false;
}

// Return whether there is an error on a specific resource
function hasEntryErrorsOnResource(resource, errorsArray){
	if (errorsArray){
		for (var i=0; i < errorsArray.length; i++){
			if (errorsArray[i].resource == resource && errorsArray[i].hasEntryErrors){
				return true;
			}
		}
	}
	return false;
}

// Creates and displays the collapsible result listing from the last command.
function createResourceListing(resultStatuses, hasErrorsArray){
	if (resultStatuses.result){
		var results = "";
		var resources = resultStatuses.result; 
		var count = 1;
		var topLevelText = "";
		if (hasEntryErrorsOnAnyResource(hasErrorsArray)){
			topLevelText = "<font color='red'>Failure occurred - Click for details.</font>";
		} else {
			topLevelText = "Successful - Click for details.";
		}
		results = "<div class='trigger' onClick='showBranch(\\"topLevel\\");swapFolder(\\"topFolder\\")'>" +
		"<img src='/hqu/tomcatappmgmt/public/images/expand-plussign-dark.png' border='0' id='topFolder'>&nbsp;" + topLevelText + "</div>" +
		"<span class='branch' id='topLevel'>";
		for (var y=0; y < resources.length; y++){
			var resource = resources[y].key;
			if (hasEntryErrorsOnResource(resource, hasErrorsArray)){
				resource = "<font color='red'>" + resource + " had failure(s).</font>";
			} else {
				resource = resource + " was successful.";
			}
			var resultValues = resources[y].value;
			var branchNumber = "branch" + count;
			var folderNumber = "folder"+count;
			results = results + "<div class='trigger' onClick='showBranch(\\"";
			results = results + branchNumber + "\\");swapFolder(\\""+folderNumber+"\\")'>"+
					"<img src='/hqu/tomcatappmgmt/public/images/expand-plussign-dark.png' border='0' id='"
						+folderNumber+"'>&nbsp;" + resource + "</div>";
			results = results + "<span class='branch' id='" + branchNumber + "'>";
			for (var x=0; x<resultValues.length; x++){
				results = results + "<img src='/images/icon_right_arrow.gif' border='0'>" + resultValues[x].value + "<br>";
			}
			results = results + "</span>";
			count++;
		}
		results = results + "</span>";
	}
	return results;
}

// If an error is in the error messages, this will format and create the error messages.
function showErrorResourceListing(errorMessages){
	var results = "";
	if (errorMessages){
		for (var y=0; y < errorMessages.length; y++){
			results = results + "<img src='/images/icon_right_arrow.gif' border='0'>" + errorMessages[y].key + " had an error: <font color='red'>" + errorMessages[y].value + "</font><br>";
		}
	}
	return results;
}

// Decides which output to display in the "recently invoked command" field. 
function showResourceListing(resultStatuses, hasEntryErrorsMap, errorMessages){
	var results;
	var hasResults = false;
	var globalError = false;
	_hasErrors=false;
	if (!errorMessages){
		errorMessages = %{--
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

${errorMessages};
	}
	if (errorMessages.length > 0){
		results = showErrorResourceListing(errorMessages);
		_hasErrors = true;
	}else {
		results = "";
	}
	
	if (!resultStatuses){
		resultStatuses = ${resultStatuses};
	}
	
	if (resultStatuses.result && resultStatuses.result.length > 0){
		hasResults = true;
		if (!_hasErrors){
			if (!hasEntryErrorsMap){
				hasEntryErrorsMap = ${resourceEntryErrors};
			}
			results = createResourceListing(resultStatuses, hasEntryErrorsMap);
		}
	}
		
	if (typeof results != "undefined"){
		hqDojo.byId("invokeResult").innerHTML = results;
	}
	return _hasErrors;
}

// Shows or hides the progress bar and sets the cursor on the page. 
function statusBar(option){
	if (!_hasErrors){
		if (option == "show"){
			document.body.style.cursor = "wait";
			window.status = "Processing command...";
			hqDojo.byId("invokeResult").innerHTML = "<p><img src='/hqu/tomcatappmgmt/public/images/progress-feedback.gif'></p>";
		} else {
			hqDojo.byId("invokeResult").innerHTML = "";
			document.body.style.cursor = "default";
			window.status = "";
		}
	}
}

// Displays an error message to the user when the page currently "hasErrors"
function displayErrorMessage(){
	hqDojo.byId('clearErrorsMessage').innerHTML = "<font color='red' size='+1'>The error(s) must be resolved before commands are functional.</font>";
}

// Invokes the specified method on the controller and handles the return values.
function invoke(method) { 
	var selectedApplications = getSelectedApplications();
	if (method == 'deployApp' || method == 'listApps' || selectedApplications.length > 0){
		var contentParameters = {eid:"${eid}", 
								APPLICATIONS: selectedApplications,
								METHOD: method,
								SERVICE_NAME:_serviceName,
								HOST_NAME:_hostName
							}
		if (method == 'deployApp'){
			var isValid = checkForIssues('remoteFileName');
			if (isValid){
				contentParameters.WAR_FILE_LOCATION = hqDojo.byId('remoteFileName').value;	
				contentParameters.DEPLOY_PATH = hqDojo.byId('remoteContextPath').value;			
				
				if (hqDojo.byId('remoteColdDeploy').checked) {
					contentParameters.REMOTE_COLD_DEPLOY = hqDojo.byId('remoteColdDeploy').value;		
				}
			}else {
				return;
			}
		}	
		if (method != 'listApps'){		
			if (_hasErrors){
				return displayErrorMessage();
			}
			statusBar("show");	
		}			
  		hqDojo.xhrPost({
    		url:  '/<%= urlFor(action:"invokeCommand", encodeUrl:true) %>',
			error: errorHandler,
			timeoutSeconds: 3600,
			timeout: timeoutHandler,
    		mimetype:  "text/json-comment-filtered",
			handleAs: "json-comment-filtered",
			content: contentParameters,
    		load:  function(data, ioargs) {
				statusBar("hide");
				if (ioargs.xhr.getResponseHeader("Content-Type") == "text/html;charset=UTF-8"){
					hqDojo.byId("invokeResult").innerHTML = "<font color='red'>Your session has ended. Please <a href='/'>logon</a> again.</font>";
				} else {
					showResourceListing(data.resultStatuses, data.resourceEntryErrors, data.errorMessages);
					_globalCumulativeResults = data.cumulativeResults;
					populateDeployedAppsTable(data.statusChanges);
					hqDojo.byId('remoteFileName').value = "";
					hqDojo.byId('remoteContextPath').value = "";
					hqDojo.byId('remoteColdDeploy').checked = false;
					hqDojo.byId('clearErrorsMessage').innerHTML = "";
				}
    		}
  		});
	}
}

// The error handler for the invoked command.
function errorHandler(type, data, evt) {
    var message = "An error has occurred: <font color='red'>" + data.message + "</font>";
    hqDojo.byId("invokeResult").innerHTML = message;
	statusBar("hide");
}

// The timeout handler for the invoked command
function timeoutHandler(type, data, evt){
	var message = "<font color='red'>The last operation has timed out.</font>";
    hqDojo.byId("invokeResult").innerHTML = message;
	statusBar("hide");
}

// Populates the application table with the current applications and running statuses.
function populateDeployedAppsTable(applicationStatus){
	if (!applicationStatus){
		applicationStatus = ${appListing};
	}
	if (applicationStatus.result && applicationStatus.result[0] && applicationStatus.result[0].value.length >0){
        // Use the property from TomcatappmgmtController.groovy 
        var isReadOnly = $readOnly;
		
		var multiRevisionCapable = ${multiRevisionCapable};
		
		var table = "<table class='bordered-table' width='100%'><tr align='left'>";
		if (!isReadOnly) {
			table = table + "<th>Select</th>"; 
		}
		
		table = table + "<th>Name</th>";
		
		if (multiRevisionCapable) {
		    table = table + "<th>Revision</th>";
		}
		
		table = table + "<th>Status</th>";
		
		var appListing = applicationStatus.result[0].value;
		// only add this if the values returned have a sessionCount( i.e. group listing will not have a sessionCount)
		if (appListing.length >0 && appListing[0].value[1] != null){
			table = table + "<th>Sessions&nbsp;</th>";
		}
		
		table = table + "</tr>";
		for (var j=0; j < appListing.length ; j++){
			var key = appListing[j].key;
			var status = appListing[j].value[0];
			var sessionCount = appListing[j].value[1];			
			var cssClass;
			if (j%2 ==0){
				cssClass = 'sublevel1-even';
			}else {
				cssClass = 'sublevel1-odd';
			}
			table = table + "<tr> ";
            if (!isReadOnly) {
                var checkBoxValue;
                if (multiRevisionCapable) {
                    checkBoxValue = key + "##" + appListing[j].value[2];
                } else {
                    checkBoxValue = key + "##";
                }
                table = table + "<td width='50px' class='"+ cssClass + "'>" +
	                "<input type='checkbox' id='checkbox"+j+"' name='checkbox"+j+"' value='" + checkBoxValue + "' >" +
		            "</td>";
            } 
           table = table + "<td class='"+ cssClass + "'>"+key+"</td>";
           
           if (multiRevisionCapable) {
               table = table + "<td class='" + cssClass + "'>" + appListing[j].value[2] + "</td>";
           }
           
           table = table + "<td class='"+ cssClass + "'>"+
		       "<a href='#' onClick=\\"openDetails(event, '"+ key +"', " + appListing[j].value[2] + ", " + multiRevisionCapable + ")\\">"+status+
			   "</a></td>";
			
			if (sessionCount!=null){
				table = table + "<td width='50px' class='" + cssClass + "'>"+sessionCount+"</td>"; 
			}
			
			table  = table + "</tr>";
		}
		table = table + "</table>";
		hqDojo.byId("deployedApps").innerHTML = table;
	} else {
		hqDojo.byId("deployedApps").innerHTML = "<h2>No data available.</h2>";
	}
}

// Checks the current fields for issues.
function checkForIssues(fileName){
	if (_hasErrors){
		displayErrorMessage();
		return false;
	}
	var fileValue = hqDojo.byId(fileName).value;
	var index = fileValue.lastIndexOf(".war");
	var isWarFile = false;
	if (fileValue.length > 0){
		if (index > 0 &&  index == fileValue.length - 4){
			hqDojo.byId("validFormatText").innerHTML = "";
			isWarFile = true;
		} else {
			hqDojo.byId("validFormatText").innerHTML = 
				"<font size ='+1' color='red'>Invalid file type selected. Must be of type <em>war</em>.</font>";
			isWarFile = false;
		}
	}else {
		hqDojo.byId("validFormatText").innerHTML = 
			"<font size ='+1' color='red'>No application file was specified.</font>";
	}
	if (!isWarFile){
		statusBar("hide");
	}
	return isWarFile;
}

// Method to show the next level of branches for the collapsible listing.
function showBranch(branch){
	var objBranch = document.getElementById(branch).style;
	if(objBranch.display=="block")
		objBranch.display="none";
	else
		objBranch.display="block";
}

// Switches out the images depending on what is select in the collapsible listing.
function swapFolder(img){
	objImg = document.getElementById(img);
	if(objImg.src.indexOf(closedImg.src)>-1)
		objImg.src = openImg.src;
	else
		objImg.src = closedImg.src;
}

// Used for the DHTML pop-up window to prepare the results inside the window.
function prepareResultData(applicationName, applicationRevision, resultData) {
	var content = "<table cell-spacing='20%' cell-padding='20%' width='100%' class='bordered-table'>" +
	    "<TR align=left><TH>Name</TH><TH>Status</TH><TH>Sessions</TH></TR>";
	
	for (var i=0; i <  resultData.length; i++){
		var key = resultData[i].key;
		if (i%2 ==0){
			cssClass = 'sublevel1-even';
		}else {
			cssClass = 'sublevel1-odd';
		}
		content = content + "<tr><td class='"+ cssClass + "'>"+ key + "</td>";
		var value = resultData[i].value;
		var childValue = "Not Deployed";
		for (var j=0; j < value.length; j++){
			var childKey = value[j].key;
			var revision = value[j].value[2]
			
			if (applicationName === childKey && applicationRevision === revision){
				childValue = value[j].value;
				break;
			}
		}				
		
		content = content + "<td class='"+ cssClass + "'>" + childValue[0];
		content = content + "<td class='"+ cssClass + "'>" + childValue[1] + "</td></tr>";
	}
	content = content + "</table>";
	return content;
}
// Gets the current coordinates of the last selected event and returns the adjusted coords to the DTHML window pop-up.
function getCoordinates(e, xoffset, yoffset){
	var posx = 0;
	var posy = 0;
	if (!e) var e = window.event;
	if (e.clientX || e.clientY){
		posx = e.clientX;// + document.body.scrollLeft;
		posy = e.clientY;// + document.body.scrollTop;
	}
	return Array(posx+xoffset, posy- yoffset);
}


// The method that pops up the detailed status window.
function openDetails(event, applicationName, applicationRevision, multiRevisionCapable){
	var resultData = _globalCumulativeResults;
	if (!resultData){
		resultData = ${cumulativeResults};
	}	
	var coords = getCoordinates(event, 150, 100);
	var content = prepareResultData(applicationName, applicationRevision, resultData);
	
	var title
	
	if (multiRevisionCapable) {
		title = applicationName + " revision " + applicationRevision
	} else {
	    title = applicationName
	}
	
	dhtmlwindow.open(applicationName, "inline", content, "Application: " + title, 
		"width=420,height=" + (resultData.length *20) + ",left="+coords[0]+",top="+coords[1]+",resize=1,scrolling=1", "recal")
}

// Processes the service listing and handles the layout of the services.
function createServiceListing(serviceListing){
	if (!serviceListing){
		serviceListing = ${serviceListing};
	}
	if (serviceListing){
		var table = "<table><tr><td><ul class='tree' id='tree'>";
		setServiceAndHostName('${selectedService}', '${selectedHost}');
		for (var j=0; j < serviceListing.length ; j++){
			var key = serviceListing[j].key;
			var value = serviceListing[j].value;
			if ((j+1) == serviceListing.length){
				table = table + "<li class='last'>";
			} else {
				table = table + "<li>";
			}
			table = table + key + "<ul>";
			for (var i=0; i < value.length; i++){
				if ((i+1) == value.length){
					table = table + "<li class='last'>";
				} else {
					table = table + "<li>";
				}
				var underline = "";
				if (_serviceName == key && _hostName == value[i]){
					underline = "style='text-decoration:underline'";
				}
				table = table + "<a href='#'" + underline + " id='"+ key+value[i]+ "' onClick=\\"getAppListing('"+ key+"', '"+ value[i] +"')\\">"+value[i]+
				"</a></li>";
			}
			table = table + "</ul></li>";
		}
		table = table + "</ul></td</tr></table>";
		hqDojo.byId("serviceNavigation").innerHTML = table;
	} else {
		hqDojo.byId("serviceNavigation").innerHTML = "<b>No Services Found.</b>";
	}	
}

// Handles the updating of the service and host name, depending on the user selection.
function setServiceAndHostName(service, host){
    // Use the property from TomcatappmgmtController.groovy 
    var isReadOnly = $readOnly;
    if (!isReadOnly) {
        if (service != "" && host != ""){
            var tempServiceName = _serviceName;
            var tempHostName = _hostName;
            _serviceName = service;
            _hostName = host;
            hqDojo.byId('serviceHostInput').innerHTML = "<input name='serviceName' type='hidden' value='"+_serviceName+"'><input name='hostName' type='hidden' value='"+_hostName+"'>";
            if (tempServiceName != null){
                hqDojo.byId(tempServiceName+tempHostName).style.textDecoration="none";
                hqDojo.byId(service+host).style.textDecoration="underline";
            }
        }
    }
}

// Retrieves the app listing for a specific service selection.
function getAppListing(service, host){
	setServiceAndHostName(service, host);
	invoke('listApps');
}


--></script>

</head>
<body>
	<div class="pageContent">
	<table border='0' width='100%'>
		<tr>
			<td width="20%" rowspan="2" valign="top" style='padding-right:1em;'>
				<h1>Choose Service</h1>
				<div id="serviceNavigation"></div>
			</td>
			<td width="60%">
				<h1>Manage Deployed Applications</h1>
				<% if (!readOnly) { %>
					<p>Result(s) of the last operation: </p>
				<% } %>
				<blockquote>
					<div id='clearErrorsMessage'></div>
					<div id="invokeResult">
					</div>
				</blockquote>
			</td>
			<td rowspan="2">&nbsp;</td>
		</tr>
	
		<tr style='vertical-align:top;' >		
			<td>
				<div id="deployedApps">
					<span>Loading applications...</span>
				</div>
				<% if (!readOnly) { %>
				<div class="buttonContainer">
					<button class="buttonStyle" id='start' onclick="invoke('startApps')">Start</button>&nbsp;
					<button class="buttonStyle" id='stop' onclick="invoke('stopApps')">Stop</button>&nbsp;
					<button class="buttonStyle" id='reload' onclick="invoke('reloadApps')">Reload</button>&nbsp;
					<button class="buttonStyle" id='undeploy' onclick="invoke('undeployApps')">Undeploy</button>&nbsp;
				</div>
				<h1>Deploy an Application</h1>
				<p>
					Locate an application to upload and deploy to the server. 
					<div id="validFormatText"></div>
			    </p>
				<form id="uploadFormLocal"  method="POST" enctype="multipart/form-data" onSubmit="return checkForIssues('fileName')">
					<div id="serviceHostInput"></div>
					<table class="bordered-table" width="100%">
			   			<tr align="left">
			   				<th colspan="2">Deploy Application from Local Machine</th>
			   			</tr>
			   			<tr>
							<td nowrap="true" align="right">Local Application File</td>
							<td width="80%" style="padding-right:2px"><input style="width:100%;" type="file" name="fileName" id="fileName" size="65" /></td>
						</tr>
						<tr>
							<td nowrap="true" align="right">Context path (optional)</td>
							<td><input type="text" name="contextPath" id="contextPath" /></td>
						</tr>
						<tr>
							<td nowrap="true" align="right">Use cold deployment strategy</td>
							<td><input id="coldDeploy" name="coldDeploy" type="checkbox" /></td>
						</tr>
					</table>
					<div class="buttonContainer">
						<input class="buttonStyle" id="localDeploy" type="submit"  value="Upload and Deploy" onClick="statusBar('show')" />
					</div>
				</form>	
				<br />
				<p>
					Identify an application to deploy that already resides on the server. 
			    </p>
				<table class="bordered-table" width="100%">
			   		<tr>
			   			<th colspan="2">Deploy Application from Server Machine</th>
			   		</tr>
			   		<tr>
						<td nowrap="true" align="right">Remote Machine Application File</td>
						<td width="80%" style="padding-right:2px"><input style="font-size:10px; width:100%;" type="text" id="remoteFileName" name="remoteFileName" /></td>
					</tr>
					<tr>
						<td nowrap="true" align="right">Context path (optional)</td>
						<td><input type="text" name="contextPath" id="remoteContextPath" /></td>
					</tr>
					<tr>
						<td nowrap="true" align="right">Use cold deployment strategy</td>
						<td><input id="remoteColdDeploy" name="remoteColdDeploy" type="checkbox" /></td>
					</tr>
				</table>
				<div class="buttonContainer">
					<button id="remoteDeploy" class="buttonStyle" onclick="invoke('deployApp')">Deploy</button>
				</div>
			</td>
			<% } %>
		</tr>
 
		</table>
		</div>
	</body>
	</html>
