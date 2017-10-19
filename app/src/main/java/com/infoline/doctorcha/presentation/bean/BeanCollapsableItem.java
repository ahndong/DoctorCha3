package com.infoline.doctorcha.presentation.bean;

public class BeanCollapsableItem {
	public int originPos;
	public int groupType;
	public boolean isExpanded;

	public BeanCollapsableItem() {
	}

	public BeanCollapsableItem(final int originPos, final int groupType, final boolean isExpanded) {
		this.originPos = originPos;
		this.groupType = groupType;
		this.isExpanded = isExpanded;
	}
}
