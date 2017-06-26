package com.huangyiming.disjob.java;

public class LockTest {

	public static void main(String[] args) {
		new Thread(new Runnable() {
			
			public void run() {
				LockTest.lock1();
			}
		}).start();
		
		new Thread(new Runnable() {
			
			public void run() {
				LockTest.lock1();
			}
		}).start();	
	}
	
	public static void lock1(){
		synchronized (LockTest.class) {
			System.out.println("lock _1 ");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock2();
		}
	}
	
	public static void lock2(){
		synchronized (LockTest.class) {
			System.out.println("lock _2 ");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
