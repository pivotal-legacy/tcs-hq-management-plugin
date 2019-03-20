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

/**
 * Based on the expressinstall.as class created by Geoff Stearns as part
 * of the FlashObject library.
 *
 * Use this file to invoke the Macromedia Flash Player Express Install functionality
 * This file is intended for use with the FlashObject embed script. You can download FlashObject 
 * and this file at the following URL: http://blog.deconcept.com/flashobject/
 *
 * Usage: 
 *          var ExpressInstall = new ExpressInstall();
 *          
 *          // test to see if install is needed:
 *          if (ExpressInstall.needsUpdate) { // returns true if update is needed
 *              ExpressInstall.init(); // starts the update
 *          }
 *
 *	NOTE: Your Flash movie must be at least 214px by 137px in order to use ExpressInstall.
 *
 */

class ExpressInstall{
	public var needsUpdate:Boolean;
	private var updater:MovieClip;
	private var hold:MovieClip;
	
	public function ExpressInstall(){
		// does the user need to update?
		this.needsUpdate = (_root.MMplayerType == undefined) ? false : true;	
	}

	public function init():Void{
		this.loadUpdater();
	}

	public function loadUpdater():Void{
		System.security.allowDomain("fpdownload.macromedia.com");

		// hope that nothing is at a depth of 10000000, you can change this depth if needed, but you want
		// it to be on top of your content if you have any stuff on the first frame
		this.updater = _root.createEmptyMovieClip("expressInstallHolder", 10000000);

		// register the callback so we know if they cancel or there is an error
		var _self = this;
		this.updater.installStatus = _self.onInstallStatus;
		this.hold = this.updater.createEmptyMovieClip("hold", 1);

		// can't use movieClipLoader because it has to work in 6.0.65
		this.updater.onEnterFrame = function():Void {
			if(typeof this.hold.startUpdate == 'function'){
				_self.initUpdater();
				this.onEnterFrame = null;
			}
		}

		var cacheBuster:Number = Math.random();

		this.hold.loadMovie("https://fpdownload.macromedia.com/pub/flashplayer/"
												+"update/current/swf/autoUpdater.swf?"+ cacheBuster);
	}

	private function initUpdater():Void{
		this.hold.redirectURL = _root.MMredirectURL;
		this.hold.MMplayerType = _root.MMplayerType;
		this.hold.MMdoctitle = _root.MMdoctitle;
		this.hold.startUpdate();
	}

	public function onInstallStatus(msg):Void{
		getURL("javascript:dojox.flash.install._onInstallStatus('"+msg+"')");
	}
}
