package com.huangyiming.disjob.monitor.db.mappers;

import com.huangyiming.disjob.monitor.db.domain.DBUserPermit;

public interface DBUserPermitMapper {

	void insert(DBUserPermit userPermit);

	void delete(DBUserPermit permit);
	
	DBUserPermit select(DBUserPermit permit);
}
