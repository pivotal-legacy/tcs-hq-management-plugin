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

dojo.addOnLoad(function() {

  dojo.query('input.spring-js-text').forEach(function(inputElement){
    dojo.require("dijit.form.ValidationTextBox");
    Spring.addDecoration(new Spring.ElementDecoration({
      elementId : inputElement.id,
      widgetType : 'dijit.form.ValidationTextBox',
      widgetAttrs : {
        required : dojo.hasClass(inputElement, 'spring-js-required'),
        promptMessage : dojo.attr(inputElement, "alt"),
        "class" : inputElement.className
      }
    }));
    var widget = dijit.byId(inputElement.id);
    ams.registerWidget("text", widget);
    ams.registerWidget("form", widget);
  });

  dojo.query('input.spring-js-hostname').forEach(function(inputElement){
	    dojo.require("dijit.form.ValidationTextBox");
	    Spring.addDecoration(new Spring.ElementDecoration({
	      elementId : inputElement.id,
	      widgetType : 'dijit.form.ValidationTextBox',
	      widgetAttrs : {
	        required : dojo.hasClass(inputElement, 'spring-js-required'),
	        promptMessage : dojo.attr(inputElement, "alt"),
	        "class" : inputElement.className,
	        regExp: '[-a-zA-Z0-9._]+',
	        invalidMessage: 'Invalid hostname (only use letters, numbers, dots, underscores, and dashes)'
	      }
	    }));
	    var widget = dijit.byId(inputElement.id);
	    ams.registerWidget("text", widget);
	    ams.registerWidget("form", widget);
	  });

  dojo.query('input.spring-js-numeric').forEach(function(inputElement){
    dojo.require("com.springsource.hq.plugin.tcserver.serverconfig.dijit.form.UnitAwareNumberSpinner");
    var unitPrefix = "spring-js-unit-";
    var unit = null;
    var classes = inputElement["className"].split(" ");
    for (var i=0; i<classes.length; i++) {
      if (classes[i].indexOf(unitPrefix) == 0) {
        unit = classes[i].substr(unitPrefix.length);
      }
    }
    var invalidMessage = "A numeric value is expected";
    if (unit) {
    	invalidMessage = invalidMessage + " in " + unit;
    }
    Spring.addDecoration(new Spring.ElementDecoration({
      elementId : inputElement.id,
      widgetType : 'com.springsource.hq.plugin.tcserver.serverconfig.dijit.form.UnitAwareNumberSpinner',
      widgetAttrs : { 
        invalidMessage : invalidMessage,
        constraints : {
      	  min : 0,
      	  max : 100000000
        },
        unit : unit,
        required : dojo.hasClass(inputElement, 'spring-js-required'),
        promptMessage : dojo.attr(inputElement, "alt"),
        "class" : inputElement.className
      }
    }));
    var widget = dijit.byId(inputElement.id);
    widget.set("value", widget.get("value"), false);
    ams.registerWidget("numeric", widget);
    ams.registerWidget("form", widget);
    if (unit) {
    	ams.registerWidget("unit", widget);
    }
  });

  dojo.query('input.spring-js-port').forEach(function(inputElement){
    dojo.require("com.springsource.hq.plugin.tcserver.serverconfig.dijit.form.Port");
    Spring.addDecoration(new Spring.ElementDecoration({
      elementId : inputElement.id,
      widgetType : 'com.springsource.hq.plugin.tcserver.serverconfig.dijit.form.Port',
      widgetAttrs : { 
        invalidMessage : 'A numeric value is expected',
        constraints : {
    	  min : 0,
    	  max : 65535
        },
        rangeMessage : "A port is between 0 and 65535",
        required : dojo.hasClass(inputElement, 'spring-js-required'),
        promptMessage : dojo.attr(inputElement, "alt"),
        "class" : inputElement.className
      }
    }));
    var widget = dijit.byId(inputElement.id);
    widget.set("value", widget.get("value"), false);
    ams.registerWidget("port", widget);
    ams.registerWidget("numeric", widget);
    ams.registerWidget("form", widget);
  });

  dojo.query('input.spring-js-boolean').forEach(function(inputElement){
    dojo.require("dijit.form.CheckBox");
    Spring.addDecoration(new Spring.ElementDecoration({
      elementId : inputElement.id,
      widgetType : 'dijit.form.CheckBox',
      widgetAttrs : {
    	promptMessage : dojo.attr(inputElement, "alt"),
        "class" : inputElement.className
      }
    }));
    var widget = dijit.byId(inputElement.id);
    ams.registerWidget("boolean", widget);
    ams.registerWidget("form", widget);
  });

  dojo.query('select.spring-js-select').forEach(function(inputElement){
    dojo.require("dijit.form.FilteringSelect");
    Spring.addDecoration(new Spring.ElementDecoration({
      elementId : inputElement.id,
      widgetType : 'dijit.form.FilteringSelect',
      widgetAttrs : {
    	promptMessage : dojo.attr(inputElement, "alt"),
        "class" : inputElement.className
      }
    }));
    var widget = dijit.byId(inputElement.id);
    ams.registerWidget("select", widget);
    ams.registerWidget("form", widget);
  });

  dojo.query('select.spring-js-select-open').forEach(function(inputElement){
    dojo.require("dijit.form.ComboBox");
    Spring.addDecoration(new Spring.ElementDecoration({
      elementId : inputElement.id,
      widgetType : 'dijit.form.ComboBox',
      widgetAttrs : {
    	promptMessage : dojo.attr(inputElement, "alt"),
        "class" : inputElement.className
      }
    }));
    var widget = dijit.byId(inputElement.id);
    ams.registerWidget("select", widget);
    ams.registerWidget("form", widget);
  });

  dojo.query('textarea.spring-js-textarea').forEach(function(inputElement){
    dojo.require("dijit.form.Textarea");
    Spring.addDecoration(new Spring.ElementDecoration({
      elementId : inputElement.id,
      widgetType : 'dijit.form.Textarea',
      widgetAttrs : {
    	promptMessage : dojo.attr(inputElement, "alt"),
        "class" : inputElement.className
      }
    }));
    var widget = dijit.byId(inputElement.id);
    ams.registerWidget("textarea", widget);
    ams.registerWidget("form", widget);
  });
  
  dojo.query('.spring-js-submit').forEach(function(submitElement){
    if (!submitElement.id) {
      submitElement.id = 'spring-js-submit';
    }
    Spring.addDecoration(new Spring.ValidateAllDecoration({elementId: submitElement.id, event: 'onclick'}));
  });
});

function validateFileAs(fileType, fileSelectId) {
  var fileSelect = dojo.byId(fileSelectId);
  var fileExtention = fileSelect.value.substring(fileSelect.value.length - (fileType.length+1))
  if (fileExtention != '.' + fileType) {
    var fileSelectError = dojo.byId(fileSelectId + 'Error');
    if (!fileSelectError) {
      fileSelectError = document.createElement('div');
      dojo.attr(fileSelectError, 'id', fileSelectId + 'Error');
      dojo.attr(fileSelectError, 'class', 'error');
      dojo.html.set(fileSelectError, 'Please select an ' + fileType.toUpperCase() + ' file to upload');
      dojo.place(fileSelectError, fileSelect, 'before');
    }
    return false;
  }
}
