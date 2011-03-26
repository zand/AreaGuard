package com.zand.areaguard.area.error;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Msg;

public class ErrorMsg extends Msg {
	final private String msg;
	
	public ErrorMsg(Area area, String name, String msg) {
		super(area, name);
		this.msg = msg;
	}

	@Override
	public String getMsg() {
		return msg;
	}

	@Override
	public boolean setMsg(String creator, String msg) {
		return false;
	}

	@Override
	public boolean exsists() {
		return false;
	}

}
