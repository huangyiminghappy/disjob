package com.huangyiming.disjob.java.dynamic;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.huangyiming.disjob.java.ExecutorBuilder;
import com.huangyiming.disjob.quence.Log;

/**
 * 这个action 是阻塞式操作，所以不能进队列
 * @author Disjob
 *
 */
public class WatchServiceReactor implements Runnable {

	private WatchService service;
	private String rootPath;
	private WatchKey watchKey;

	public static final int ONE_MIN = 1000 * 60;

	public static final int ONE_SECONDS = 1000;

	public WatchServiceReactor(String path) {
		try {
			this.rootPath = path;
			this.service = FileSystems.getDefault().newWatchService();
			Path p = Paths.get(path);
			p.register(this.service, StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.ENTRY_DELETE,StandardWatchEventKinds.ENTRY_CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			Log.info("开始监听：" + rootPath);
			while (true) {
				watchKey = service.take();// save the last watch key when timer
				// is update then handler the last // change
				ExecutorBuilder.getExecutor().execute(new WatchKeyCommand(watchKey, rootPath));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				service.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
}
