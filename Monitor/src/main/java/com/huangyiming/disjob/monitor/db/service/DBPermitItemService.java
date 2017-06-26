package com.huangyiming.disjob.monitor.db.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huangyiming.disjob.monitor.db.mappers.DBPermitItemMapper;
import com.huangyiming.disjob.monitor.db.domain.DBPermitItem;

@Service("dbPermitItemService")
public class DBPermitItemService {

	@Autowired
	private DBPermitItemMapper permitItemMapper;
	
	public List<DBPermitItem> getAllPermit() {
		return permitItemMapper.getAllPermitIterm();
	}
	
	public DBPermitItem getPermitById(String id){
		return permitItemMapper.selectById(id);
	}
	
	public DBPermitItem getPermitByUri(String uri){
		return permitItemMapper.selectByUri(uri);
	}

}
