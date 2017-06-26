# DisJob

DisJob 是一个基于Quartz、Netty、ZooKeeper的分布式rpc调度job框架，是目前业内极少使用tcp协议来做异步RPC调度的框架，因为基于tcp协议，所以支持多语言，多语言系统间定时调用非常方便。在整个集团有java版本和php版本，支撑着数十个团队，数万个job的运行。目前只开源java版本。容易要支持PHP或者python等版本，只需要根据协议写服务端代码即可。支持多语言<br/>
# 一、项目背景
disjob分布式任务调度概述<br/>
公司现有定时任务（job）有10w+，基于crontab实现分散于很多系统中，有时会发生故障，原因有多种，job执行过程中本身发生故障，系统进程挂 掉，系统所在服务器宕机；对于需要执行几个小时的大型job没有分片功能，或者不能非常方便的分片。因此disjob调度中心是为解决这些问题而设计，初步计 划要实现的功能如下：<br/>
        1、防单点故障：业务系统的单点故障以及调度中心本身的单点故障<br/>
                      应用服务器必须要两台及以上才能防单点故障<br/>
        2、job本身发生故障时可通过邮件/短信通知相关人员<br/>
        3、job执行进程僵死时，可通过job管理平台kill进程<br/>
        4、对于无状态任务，可提供并发执行功能，对于有状态任务，严格按照先后顺序执行，且不会重复执行<br/>
        5、提供web管理平台，将所有定时任务集中管理，可手动提交、停止任务、设置任务执行的频率<br/>
           停止任务、设置任务频率均要在下次任务执行时才能生效，也就是说正在执行的任务不能停止，设置的新的执行频率也不会即时生效<br/>
        6、提供任务监控功能，即时生成任务执行进度报告<br/>
           需要提供日志数据，对定时的任务的业务代码有一定侵入<br/>
        7、任务执行发生异常时的异常信息，且可提供重试的配置项<br/>
           需要任务发送异常信息到调度中心或抛出异常<br/>
        8、大型任务的分片功能，假设要在A库处理数百万条数据，可将任务分片到多个系统上同时执行<br/>
           要实现分片功能必须要多台服务器（已实现）<br/>
        你们需要做的:<br/>
	1、基于RPC（远程过程调用或者说远程方法调用）框架提供定时任务接口，框架和基础方法我们提供，方便快捷<br/>
        2、使用我们提供的网络API给调度中心发送日志消息，会一定程度侵入业务代码，只增加代码，不会改变原有代码的逻辑<br/>
目前该项目上线后支持数千个Job，数十个项目在线调度，还在不断的有项目接入。disjob客户端是java，目前有java服务端和php服务端，开源部分是java服务端和客户端。经过压测，三台双核云主机能支持几千个20秒内随机周期job并发运行，无延迟。
  
# 二、总体架构
  
  ![](https://github.com/huangyiminghappy/DisJob/blob/master/imgs/%E6%9E%B6%E6%9E%84%E5%9B%BE.png)

  
# 三、ZooKeeper数据存储模型

  ![](https://github.com/huangyiminghappy/DisJob/blob/master/imgs/zookeeper%E6%95%B0%E6%8D%AE%E6%A8%A1%E5%9E%8B.png)
  
  ![](https://github.com/huangyiminghappy/DisJob/blob/master/imgs/%E6%95%B0%E6%8D%AE%E6%A8%A1%E5%9E%8B%E5%9B%BE.png)
  
# 四、主要流程图  

  ![](https://github.com/huangyiminghappy/DisJob/blob/master/imgs/%E8%B0%83%E5%BA%A6%E4%B8%AD%E5%BF%83%E6%B5%81%E7%A8%8B%E5%9B%BE.png)<br/>
# 五、模块介绍<br/>
## Disjob-quence：<br/>
  一个事件通知模式的基础公共模块，在disjob的服务端和客户端代码中都有引入该模块。
## Disjob-event：
一个基于无阻塞队列的线程池顺序调度公共模块，线程池执行完队列中一个任务继续有序的执行下一个任务。在disjob 的服务端和客户端代码中都有引入该模块。
## Disjob-jar：
服务端发布job只需要引入该包，实现相应的接口，配置zk地址和节点信息，启动服务即可将job发布到zk上并启动netty服务接收客户端的定时调用。
## Common：
disjob客户端公共模块
## Monitor：
disjob客户端的监控模块，disjob服务端的进度信息以及任务接收、任务开始执行、执行结束时间传给monitor模块保存进db中
## Rpc：
disjob客户端的rpc模块，rpc通信以及任务调度是通过该模块执行的
## reister：
disjob客户端的注册模块，zk的监听，选主等都是在该模块中进行的
## Console：
disjob 后台管理模块，在后台对job进行管理、监控、登陆等都是通过该模块
## Disjob-java-web：
 服务端接入disjob的例子，实际步骤就是引入disjob-jar然后按照要求配置并实现接口接口
# 六、Quick Start
## maven 引入disjob-jar
          <dependency><br/>
			<groupId>com.disJob</groupId><br/>
			<artifactId>Disjob-jar</artifactId><br/>
			<version>1.0.0</version><br/>
         </dependency><br>
  我们先构造disjob的服务端（被调用方），先用最简单的普通java接入方式做例子：<br/>
## 普通的java app 应用接入步骤：<br/>
  
### 1.在指定的 **.properties 文件 配置ejob必要的一些参数：zookeeper 集群地址、服务监听的端口，以及job所在的package<br/>
  
  #please tall me where is the zookeeper server host<br/>
  zk.host=127.0.0.1:2181<br/>
  #start the server listener on the port,if the port has occupancy then change the port<br/>
  server.port=9501<br/>
  #where is the job class inside the packages<br/>
  job.packages=com.huangyiming.job.pack;<br/>
  
### 2.业务所在的类必须实现EJob 接口，所有的业务实现都在execute 方法中，同时加上暴露给我们EJOB 调度中心的注解
  
			@JobDec(group="alarm",jobName="alarmJob1",quartz="0/10 * * * * ?",fireNow=true)	
			public class AlarmJobAction implements EJob{
			@Override
			public void execute(SchedulerParam schedulerParam)throws TaskExecuteException {
			 DebugInfoPrintUtil.debug("D:/"+this.getClass().getSimpleName()+"_.log", this.getClass().getSimpleName()+ "       　			at:"+TimeUtils.getFormatNow());
			}	
			@Override　		
			public void beforeExecute(SchedulerParam schedulerParam) {
         
			}
			@Override
			public void executeSuccess(SchedulerParam schedulerParam) {
			}
			@Override
			public void executeFail(SchedulerParam schedulerParam) {
         
			}
		}
 
### 3.调用我们给定的api 即可注册 [注意：]configPath 必须给的是绝对路径

		public class FireNowMain {
			public static void main(String[] args) {
			   String path = "E:/workspace/disjob/EjobJavaApp/src/main/resources/META-INF/ejob.properties"; 
			   new EjobBootstrap().startUpEjob(EjobConstants.StartUpType.JAVA_APPLICATION, path);
			}
		}

### 4 导入sql和配置conf文件
#### 1、导入disjob.sql到db中
#### 2、解压disjob.zip文件放入pom指定环境的配置文件中
　　如pom的profiles标签中有三种环境可配置,根据实际环境在指定路径配置conf文件，如在 <Disjob-conf>D:/conf/Disjob</Disjob-conf>路径下放入 　　　　disjob.zip 中的jdbc.properties等文件。
### 5、编译打包
最后对DisJob的pom文件 所在路径下执行mvn clean package install -Ppublish -X -Dmaven.test.skip=true(这里是执行publish环境)
##### disjob后台效果:
   ![](https://raw.githubusercontent.com/huangyiminghappy/DisJob/master/imgs/job%E7%AE%A1%E7%90%86.png)
  
  
