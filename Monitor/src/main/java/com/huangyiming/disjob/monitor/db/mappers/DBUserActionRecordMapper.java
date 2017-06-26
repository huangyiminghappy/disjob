package com.huangyiming.disjob.monitor.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huangyiming.disjob.monitor.db.domain.DBUserActionRecord;

public interface DBUserActionRecordMapper {

	public void insert(DBUserActionRecord dbUserActionRecord);

	public List<DBUserActionRecord> selectUserActionRecordByPaging(@Param("limit")int limit,@Param("offset") int offset, @Param("search")String search);
	
	public int selectUserActionRecordCount(@Param("search")String search);
}
