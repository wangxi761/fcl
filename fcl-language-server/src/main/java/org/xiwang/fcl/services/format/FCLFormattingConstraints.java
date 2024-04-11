package org.xiwang.fcl.services.format;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.lemminx.services.format.XMLFormattingConstraints;
import org.xiwang.fcl.services.FCLType;

@Getter
@Setter
public class FCLFormattingConstraints extends XMLFormattingConstraints {
	
	private FCLType fclType;
	
	@Override
	public void copyConstraints(XMLFormattingConstraints constraints) {
		if (constraints instanceof FCLFormattingConstraints fclFormattingConstraints) {
			this.fclType = fclFormattingConstraints.fclType;
		}
		super.copyConstraints(constraints);
	}
}
