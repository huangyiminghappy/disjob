package com.grobalegrow.disJob.quartz;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicInteger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException; 
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
public class MyExample_1 {
  
	AtomicInteger atomicInteger = new AtomicInteger(0);
	
	static Scheduler sched;
	
	static TriggerKey triggerKey;

	public static class MyJob implements Job {

		private String jobData;

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			System.out.println(this.getClass().getName() + " " + new Date() + " || " + getJobData());
		}

		public String getJobData() {
			return jobData;
		}

		public void setJobData(String jobData) {
			this.jobData = jobData;
		}

	}

	// MyTask
	public static void main(String[] args) throws SchedulerException {

		SchedulerFactory factory = new StdSchedulerFactory();
		sched = factory.getScheduler();
		triggerKey = new TriggerKey("myTriggerName", "myTriggerGroup");
		JobDetail jobDetail = 
				JobBuilder.newJob(MyJob.class).storeDurably().usingJobData("jobData", "hahahahaaa11").withIdentity("myJobName", "myJobGroup").build();
		
		Calendar Calendar = new GregorianCalendar();
		Calendar.add(Calendar.SECOND, 10);
		Trigger trigger = TriggerBuilder.newTrigger().startAt(Calendar.getTime()).build();
		
		Calendar Calendar2 = new GregorianCalendar();
		Calendar2.add(Calendar.SECOND, 16);
		Trigger trigger2 = TriggerBuilder.newTrigger().startAt(Calendar2.getTime()).build();
		
		sched.start();
		
//		sched.scheduleJob(jobDetail, Sets.newHashSet(trigger, trigger2), true);
	}
}
