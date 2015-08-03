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

var ams = {
	
	_ids : {},
	
	getId : function(key) {
		return this._ids[key];
	},
	
	putId : function(key, id) {
		this._ids[key] = id;
	},
	
	getWidgetByKey : function(key) {
		return dijit.byId(this.getId(key));
	},
	
	_widgetRegistry : {
	},
	
	registerWidget : function(type, widget) {
		if (!this._widgetRegistry[type]) {
			this._widgetRegistry[type] = new dijit.WidgetSet();
		}
		this._widgetRegistry[type].add(widget);
	},
	
	getWidgetsByType : function(type) {
		if (this._widgetRegistry[type]) {
			return this._widgetRegistry[type];
		}
		else {
			return new dojo.NodeList();
		}
	},
	
	setReadOnly : function(widget, value) {
		if (dojo.isArray(widget)) {
			for (var i in widget) {
				this._setReadOnly(widget[i], value);
			}
		}
		else {
			this._setReadOnly(widget, value);
		}
	},
	
	_setReadOnly : function(widget, value) {
		widget.attr("readOnly", value);
		// force the css styles to update on the element, appears to be a dojo bug
		if (widget._setStateClass) {
			widget._setStateClass();
		}
		else {
			logger.info("widget._setStateClass() is not a function")
		}
	},
	
	openAllBlinds : function() {
		ams.getWidgetsByType("blind").forEach(function(widget) {
			if (!widget.attr("open")) {
				widget.toggle();
			}
		});
	},
	
	createMinMaxConstraint : function(minWidget, maxWidget) {
		minWidget.attr("intermediateChanges", true);
		maxWidget.attr("intermediateChanges", true);
		
		var initialMinWidgetMin =  minWidget.constraints.min;
		var initialMinWidgetMax =  minWidget.constraints.max;
		var initialMaxWidgetMin =  maxWidget.constraints.min;
		var initialMaxWidgetMax =  maxWidget.constraints.max;
		
		var minLabel;
		var maxLabel;
		
		dojo.query("label[for="+minWidget.attr("id")+"]").forEach(function(label) {
			minLabel = "";
			for (var i=0; i<label.childNodes.length; i++) {
				if (label.childNodes[i].nodeType == 3) {
					minLabel += label.childNodes[i].nodeValue;
				}
			}
		});
		dojo.query("label[for="+maxWidget.attr("id")+"]").forEach(function(label) {
			maxLabel = "";
			for (var i=0; i<label.childNodes.length; i++) {
				if (label.childNodes[i].nodeType == 3) {
					maxLabel += label.childNodes[i].nodeValue;
				}
			}
		});
		
		var minWidgetOnChange = function() {
			var min = parseInt(minWidget.getValue());
			if (min && min >= initialMaxWidgetMin && min <= initialMaxWidgetMax) {
				maxWidget.constraints.min = min;
				if (minLabel) {
					maxWidget.attr("rangeMessage", "Must be greater than " + minLabel);
				}
				else {
					maxWidget.attr("rangeMessage", "Must be greater than " + maxWidget.constraints.min);
				}
				maxWidget.validate();
			}
		};
		
		var maxWidgetOnChange = function() {
			var max = parseInt(maxWidget.getValue());
			if (max && max >= initialMinWidgetMin && max <= initialMinWidgetMax) {
				minWidget.constraints.max = max;
				if (maxLabel) {
					minWidget.attr("rangeMessage", "Must be between " + minWidget.constraints.min + " and " + maxLabel);
				}
				else {
					minWidget.attr("rangeMessage", "Must be between " + minWidget.constraints.min + " and " + minWidget.constraints.max);
				}
				minWidget.validate();
			}
		};
		
		minWidget.connect(minWidget, "onChange", minWidgetOnChange);
		maxWidget.connect(maxWidget, "onChange", maxWidgetOnChange);
		
		minWidgetOnChange();
		maxWidgetOnChange();
	},
	
	entityEncode : function(str) {
		return str.replace(/\&/g,'&'+'amp;')
		          .replace(/</g,'&'+'lt;')
		          .replace(/>/g,'&'+'gt;')
		          .replace(/\'/g,'&'+'apos;')
		          .replace(/\"/g,'&'+'quot;');
	}
	
}