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

dojo.provide("com.springsource.hq.plugin.tcserver.serverconfig.dijit.form.UnitAwareNumberSpinner");

dojo.require("dijit.form.NumberSpinner");

dojo.declare(
"com.springsource.hq.plugin.tcserver.serverconfig.dijit.form.UnitAwareNumberSpinner",
[dijit.form.NumberSpinner],
{
	// summary:
	// extends NumberSpinner to provide unit support
	
	unit : null,
	
	regExpGen: function(/*dojo.number.__RegexpOptions?*/options){
		var re = dojo.number.regexp(options);
		if (this.unit) {
			re = "(?:" + re + "(\\s" +  this.unit + ")?)";
		}
		return re;
	},
	
	format: function(/*Number*/ value, /*dojo.number.__FormatOptions*/ constraints){
		//	summary: formats the value as a Number, according to constraints

		if(typeof value == "string") { return value; }
		if(isNaN(value)){ return ""; }
		if(this.editOptions && this._focused){
			constraints = dojo.mixin(dojo.mixin({}, this.editOptions), this.constraints);
		}
		var formattedStr = this._formatter(value, constraints);
		if (!constraints.pattern && formattedStr && formattedStr != "" && this.unit) {
			formattedStr = formattedStr + " " + this.unit;
		}
		return formattedStr;
	},
	
	_parser: dojo.number.parse,
	
	parse: function(/*String*/ value, /*dojo.number.__ParseOptions*/ constraints){
		if (this.unit && value) {
			var unitIndex = value.lastIndexOf(" " + this.unit);
			var lengthWithoutUnit = value.length - (this.unit.length + 1);
			if (unitIndex > 0 && unitIndex == lengthWithoutUnit) {
				value = value.substr(0, lengthWithoutUnit);
			}
		}
		return this._parser(value, constraints);
	}
	
});
