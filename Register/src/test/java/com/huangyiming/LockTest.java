package com.huangyiming;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

/**
 * <pre>
 * 
 *  File: LockTest.java
 * 
 *  Copyright (c) 2016, huangyiming.com All Rights Reserved.
 * 
 *  Description:
 *  TODO
 * 
 *  Revision History
 *  Date,					Who,					What;
 *  2016年5月20日				Disjob				Initial.
 *
 * </pre>
 */
public class LockTest
{
    @Test
    public void test3() throws Exception{
        final Lock lock=new ReentrantLock();
        lock.lock();
        Thread.sleep(1000);
        Thread t1=new Thread(new Runnable(){
            @Override
            public void run() {
                lock.lock();
                System.out.println(Thread.currentThread().getName()+" interrupted.");
            }
        });
        t1.start();
        Thread.sleep(1000);
        t1.interrupt();
        Thread.sleep(1000000);
    }
    
    @Test
    public void test4() throws Exception{
        final Lock lock=new ReentrantLock();
        lock.lock();
        Thread.sleep(3000);
        Thread t1=new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    lock.lockInterruptibly();
                } catch (InterruptedException e) {
                            System.out.println(Thread.currentThread().getName()+" interrupted.");
                }
            }
        });
        t1.start();
        Thread.sleep(5000);
        t1.interrupt();
       Thread.sleep(1000000);
    }
    
    @Test
    public void test() {
        final Lock lock=new ReentrantLock();
        Thread t = new Thread(  new Runnable()
        {
            public void run()
            {  lock.lock();
            System.out.println("33333"); 
              try
            {
                TimeUnit.SECONDS.sleep(5);
            }
            catch (InterruptedException e)
            {
                System.out.println("xixi");
                e.printStackTrace();
            }
              System.out.println("2222"); 
              lock.unlock();
               System.out.println("1111"); 
            }
        });
        t.start();
        try
        {
            TimeUnit.SECONDS.sleep(1);
        }
        catch (InterruptedException e)
        {
           System.out.println("hehe");
            e.printStackTrace();
        }
        t.interrupt();
    }
    
    @Test
    public void testCountDownLaw() throws InterruptedException{
        CountDownLatch count = new CountDownLatch(3);
        count.countDown();
        count.await();
    }
    
    @Test
    public void test1() throws Exception{
        CuratorFramework curatorClient = CuratorFrameworkFactory.builder()
                .connectString("10.40.6.100:2181")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curatorClient.start();
        curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/bbb");

    }

}

