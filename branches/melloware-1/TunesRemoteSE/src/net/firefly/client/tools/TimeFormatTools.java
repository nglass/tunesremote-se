/*
 * This file is part of FireflyClient.
 *
 * FireflyClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * FireflyClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FireflyClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2007 Vincent Cariven
 */
package net.firefly.client.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class TimeFormatTools {
	
	private static SimpleDateFormat SDF_MMSS = new SimpleDateFormat("m:ss");
	private static SimpleDateFormat SDF_HHMMSS = new SimpleDateFormat("H:mm:ss");
	
	private static final long HOUR_IN_MILLISECONDS = 3600000;
	
	static {
		SDF_MMSS.setTimeZone(TimeZone.getTimeZone("GMT"));
		SDF_HHMMSS.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public static synchronized String format(Date time){
		if (time.getTime() > HOUR_IN_MILLISECONDS){
			return SDF_HHMMSS.format(time);
		} else {
			return SDF_MMSS.format(time);
		}
	}
}