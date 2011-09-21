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
package net.firefly.client.gui.swing.other;

import javax.swing.JProgressBar;

public class LabeledProgressBar extends JProgressBar {

	private static final long serialVersionUID = 2734611631719126238L;

	private String additionalLabel;

	private boolean displayPercentage = true;

	public LabeledProgressBar(int min, int max, String additionalLabel, boolean displayPercentage) {
		super(min, max);
		this.additionalLabel = additionalLabel;
		this.displayPercentage = displayPercentage;
	}

	public LabeledProgressBar(int min, int max, String additionalLabel) {
		this(min, max, additionalLabel, true);
	}

	public LabeledProgressBar(int min, int max) {
		this(min, max, null, true);
	}

	public LabeledProgressBar(int min, int max, boolean displayPercentage) {
		this(min, max, null, displayPercentage);
	}

	public String getAdditionalLabel() {
		return additionalLabel;
	}

	public void setAdditionalLabel(String additionalLabel) {
		this.additionalLabel = additionalLabel;
	}

	public boolean isDisplayPercentage() {
		return displayPercentage;
	}

	public void setDisplayPercentage(boolean displayPercentage) {
		this.displayPercentage = displayPercentage;
	}

	public String getString() {
		StringBuffer result = new StringBuffer();
		if (this.additionalLabel != null) {
			result.append(additionalLabel).append(" ");
		}
		if (displayPercentage) {
			result.append((int) (getPercentComplete() * 100)).append(" %");
		}
		setString(result.toString());
		return super.getString();
	}
}
