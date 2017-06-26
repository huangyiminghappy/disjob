package com.huangyiming.disjob.java.dynamic;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.Date;
import java.util.List;
import com.huangyiming.disjob.java.ExecutorBuilder;
import com.huangyiming.disjob.java.utils.TimeUtils;
import com.huangyiming.disjob.quence.Command;
import com.huangyiming.disjob.quence.Log;

public class WatchKeyCommand extends Command {

	private WatchKey watchKey;
	private String listenerPath;

	public WatchKeyCommand(WatchKey watchKey, String listenerPath) {
		this.watchKey = watchKey;
		this.listenerPath = listenerPath;
	}

	@Override
	public void execute() {
		List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
		for (WatchEvent<?> event : watchEvents) {
			if(event.context()!=null){
				String fileName = event.context().toString();
				if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_MODIFY.name())) {
					// 文件更改
					ExecutorBuilder.getExecutor().enDefaultQueue(new FileModifyHandler(listenerPath, fileName));
				} else if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_DELETE.name())) {
					// 文件删除
					ExecutorBuilder.getExecutor().enDefaultQueue(new FileDeleteHandler(listenerPath, fileName));
				} else if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_CREATE.name())) {
					// 文件创建
					ExecutorBuilder.getExecutor().enDefaultQueue(new FileCreateHandler(listenerPath, fileName));
				}
			}
		}
		watchKey.reset();
	}

	@Override
	public void executeException(String execeptionMsg) {
		Log.error(listenerPath +" poll events error."+execeptionMsg+"; at time :"+TimeUtils.getFormat(new Date(), TimeUtils.YYYY_MM_DD_HH_MM_SS));
	}

	@Override
	public void executeSuccess() {
		Log.info(listenerPath+" pool events success.");
	}
}
