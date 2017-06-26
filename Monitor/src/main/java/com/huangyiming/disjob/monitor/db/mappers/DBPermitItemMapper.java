package com.huangyiming.disjob.monitor.db.mappers;

import java.util.List;

import com.huangyiming.disjob.monitor.db.domain.DBPermitItem;

public interface DBPermitItemMapper {

	public List<DBPermitItem> getAllPermitIterm();

	public DBPermitItem selectById(String id);

	public DBPermitItem selectByUri(String uri);

}
