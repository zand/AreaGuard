package com.zand.areaguard.error.area;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Msg;

public class ErrorMsg implements Msg {
	final private Area area;
	final private String name;
	final private String msg;
	
	public ErrorMsg(Area area, String name, String msg) {
		this.area = area;
		this.name = name;
		this.msg = msg;
	}

	@Override
	public Area getArea() {
		return area;
	}

	@Override
	public String getMsg() {
		return msg;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean setMsg(String creator, String msg) {
		return false;
	}

}
