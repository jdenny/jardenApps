package jarden.gui;

import sales.SalesUnit;

public interface SalesTeamIF extends SalesUnitIF {
	void addMember(SalesUnit salesPerson);
	int getMemberCt();
	SalesUnit getMember(int index);
}
