package com.huangyiming.disjob.slaver.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.huangyiming.disjob.register.domain.DisJobServerInfo;
import com.huangyiming.disjob.register.domain.SlaveNode;
import com.huangyiming.disjob.register.repository.ZnodeApiCuratorImpl;
import com.huangyiming.disjob.register.rpc.ConcurrentHashSet;

/**
 * 
 * 使用一致性hash模型来建立group-job跟slave节点的对应关系,尽可能保证增加或者减少机器的时候减少对job所属节点的影响,减少震荡
 * 
 * @author Disjob
 *
 * @param <Node>目前只是机器ip
 */
public class Shard<T extends SlaveNode> {  
	  
    static private TreeMap<Long, SlaveNode> nodes; // 虚拟节点到真实节点的映射  
    static private TreeMap<Long,SlaveNode> treeKey; //key到真实节点的映射  
    static private List<SlaveNode> shards = new ArrayList<SlaveNode>(); // 真实机器节点  
    private final int NODE_NUM = 2000; // 每个机器节点关联的虚拟节点个数  
    boolean flag = false;  
      
    public Shard(List<SlaveNode> shards) {  
        super();  
        this.shards = shards;  
        //this.shards.addAll(shards);
        init();  
    }  
    
    /**
     * 
     * 1.构造得到了当前可用slave节点的Shard对象
     * 2.把group,job放入shard虚拟圆环中
     * @param client
     * @return
     */
    public static Shard<SlaveNode> initSlaveIpInShard(CuratorFramework client){
    	ZnodeApiCuratorImpl znode = new ZnodeApiCuratorImpl();
    	Shard<SlaveNode> shard = null;
    	ConcurrentHashSet<DisJobServerInfo> availableIps =  com.huangyiming.disjob.slaver.utils.SlaveUtils.refreshSlaveIp(client);
    	//step1
    	if(CollectionUtils.isNotEmpty(availableIps)){
    		List<SlaveNode> shards = new ArrayList<SlaveNode>();
    		for(DisJobServerInfo info : availableIps){
    			shards.add(new SlaveNode(info.getIp()));
    		}
    		shard = new Shard<SlaveNode>(shards);
     		
    		//step 2
     		String jobNode = Constants.ROOT +Constants.APP_JOB_NODE_ROOT;
				List<String> groupList = znode.getChildren(client, jobNode);
				if(CollectionUtils.isNotEmpty(groupList)){
					for(String groupName :groupList ){
						String groupNode = jobNode + "/" + groupName;
						 List<String> jobAll = znode.getChildren(client, groupNode) ;
						 if(CollectionUtils.isNotEmpty(jobAll)){
 							for(String jobName:jobAll){
 								
								shard.keyToNode(groupName+jobName);
 								
						        String jobPath = znode.makePath(Constants.ROOT, Constants.APP_JOB_NODE_ROOT,Constants.PATH_SEPARATOR+groupName,Constants.PATH_SEPARATOR+jobName, Constants.APP_JOB_NODE_CONFIG);

 								//String data = znode.getData(client, "/disJob/job/"+groupName+"/"+jobName+"/config");
 								String data = znode.getData(client, jobPath);

 								if(!StringUtils.isEmpty(data)){
  								}else{
 									LoggerUtil.error(" =====groupname:"+groupName+ " ,jobname:"+jobName);
  								}
							}
						}
						 else{
								LoggerUtil.error("no value is  groupname:"+groupName);
						 }
					}
				}
     		 
    	}
    	return shard;
    }

  
 
    public static void printKeyTree(){  
        for(Iterator<Long> it = treeKey.keySet().iterator();it.hasNext();){  
            Long lo = it.next();  
        }  
          
    }  
      /**
       * 初始化一致性hash环  
       */
    private void init() { 
        nodes = new TreeMap<Long, SlaveNode>();  
        treeKey = new TreeMap<Long, SlaveNode>();  
       // 每个真实机器节点都需要关联虚拟节点  
        for (int i = 0; i != shards.size(); ++i) { 
              SlaveNode shardInfo = shards.get(i);  
  
            for (int n = 0; n < NODE_NUM; n++)  
                // 一个真实机器节点关联NODE_NUM个虚拟节点  
                nodes.put(hash("SHARD-" + shardInfo.getIp() + "-NODE-" + n), shardInfo);  
        }  
    }  
     /**
     * 增加一个主机
     * @param s
     */
    private void addSlaveNode(SlaveNode s) {  
        for (int n = 0; n < NODE_NUM; n++)  
            addHashSlave(hash("SHARD-" + s.getIp() + "-NODE-" + n), s);  
  
    }  
      
     /**
     * 添加一个虚拟节点进环形结构,lg为虚拟节点的hash值  
     * @param lg  
     * @param s
     */
    public void addHashSlave(Long lg,SlaveNode s){  
        SortedMap<Long, SlaveNode> tail = nodes.tailMap(lg);  
        SortedMap<Long,SlaveNode>  head = nodes.headMap(lg);  
        Long begin = 0L;  
        Long end = 0L;  
        SortedMap<Long, SlaveNode> between;  
        if(head.size()==0){  
            between = treeKey.tailMap(nodes.lastKey());  
            flag = true;  
        }else{  
            begin = head.lastKey();  
            between = treeKey.subMap(begin, lg);  
            flag = false;  
        }  
        nodes.put(lg, s);  
        for(Iterator<Long> it=between.keySet().iterator();it.hasNext();){  
            Long lo = it.next();  
            if(flag){  
                treeKey.put(lo, nodes.get(lg));  
            }else{  
                treeKey.put(lo, nodes.get(lg));  
            }  
        }  
    }  
      
     /**
     * 删除真实节点是s  
     * @param s
     */
    public void deleteSlaveNode(SlaveNode s){  
        if(s==null){  
            return;  
        }  
        for(int i=0;i<NODE_NUM;i++){  
            //定位s节点的第i的虚拟节点的位置  
            SortedMap<Long, SlaveNode> tail = nodes.tailMap(hash("SHARD-" + s.getIp() + "-NODE-" + i));  
            SortedMap<Long,SlaveNode>  head = nodes.headMap(hash("SHARD-" + s.getIp() + "-NODE-" + i));  
            Long begin = 0L;  
            Long end = 0L;  
              
            SortedMap<Long, SlaveNode> between;  
            if(head.size()==0){  
                between = treeKey.tailMap(nodes.lastKey());  
                end = tail.firstKey();  
                tail.remove(tail.firstKey());  
                nodes.remove(tail.firstKey());//从nodes中删除s节点的第i个虚拟节点  
                flag = true;  
            }else{  
                begin = head.lastKey();  
                end = tail.firstKey();  
                tail.remove(tail.firstKey());  
                between = treeKey.subMap(begin, end);//在s节点的第i个虚拟节点的所有key的集合  
                flag = false;  
            }  
            for(Iterator<Long> it = between.keySet().iterator();it.hasNext();){  
                Long lo  = it.next();  
                if(flag){  
                    treeKey.put(lo, tail.get(tail.firstKey()));  
                }else{  
                    treeKey.put(lo, tail.get(tail.firstKey()));  
                }  
            }  
        }  
          
    }  
  
     /**
     * 映射key到真实节点  
     * @param key
     */
    public void keyToNode(String key){  
        SortedMap<Long, SlaveNode> tail = nodes.tailMap(hash(key)); // 沿环的顺时针找到一个虚拟节点  
        if (tail.size() == 0) {  
            return;  
        }  
        treeKey.put(hash(key), tail.get(tail.firstKey()));  
        LoggerUtil.debug(key+"（hash："+hash(key)+"）连接到主机->"+tail.get(tail.firstKey()));  
    }  
    
    /**
     * 根据group+job查询对应的机器ip
     * @return
     */
    public String getIpByKey(String groupJobName){
    	long hashKey = hash(groupJobName);
    	SlaveNode node = treeKey.get(hashKey);
     	return node !=null ?node.getIp() :null;
    }
      
    /** 
     *  MurMurHash算法，是非加密HASH算法，性能很高， 
     *  比传统的CRC32,MD5，SHA-1（这两个算法都是加密HASH算法，复杂度本身就很高，带来的性能上的损害也不可避免） 
     *  等HASH算法要快很多，而且据说这个算法的碰撞率很低. 
      */  
    private static Long hash(String key) {  
          
        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());  
        int seed = 0x1234ABCD;  
          
        ByteOrder byteOrder = buf.order();  
        buf.order(ByteOrder.LITTLE_ENDIAN);  
  
        long m = 0xc6a4a7935bd1e995L;  
        int r = 47;  
  
        long h = seed ^ (buf.remaining() * m);  
  
        long k;  
        while (buf.remaining() >= 8) {  
            k = buf.getLong();  
  
            k *= m;  
            k ^= k >>> r;  
            k *= m;  
  
            h ^= k;  
            h *= m;  
        }  
  
        if (buf.remaining() > 0) {  
            ByteBuffer finish = ByteBuffer.allocate(8).order(  
                    ByteOrder.LITTLE_ENDIAN);  
            // for big-endian version, do this first:  
            // finish.position(8-buf.remaining());  
            finish.put(buf).rewind();  
            h ^= finish.getLong();  
            h *= m;  
        }  
  
        h ^= h >>> r;  
        h *= m;  
        h ^= h >>> r;  
  
        buf.order(byteOrder);  
        return h;  
    }  
      
    
 /*   static class SlaveNode{  
        String name="";  
        String ip;  
        public SlaveNode(String name,String ip) {  
            this.name = name;  
            this.ip = ip;  
        }  
        public SlaveNode(String ip) {  
             this.ip = ip;  
        } 
        @Override  
        public String toString() {  
            return this.name+"-"+this.ip;  
        }  
    } */ 
    
     public static void main(String[] args) {  
        SlaveNode s1 = new SlaveNode("s1", "192.168.1.1");  
        SlaveNode s2 = new SlaveNode("s2", "192.168.1.2");  
        SlaveNode s3 = new SlaveNode("s3", "192.168.1.3");  
        SlaveNode s4 = new SlaveNode("s4", "192.168.1.4");  
        SlaveNode s5 = new SlaveNode("s5","192.168.1.5");  
        shards.add(s1);  
        shards.add(s2);  
        shards.add(s3);  
        shards.add(s4);  
        Shard<SlaveNode> sh = new Shard<SlaveNode>(shards);  
        System.out.println("添加客户端，一开始有4个主机，分别为s1,s2,s3,s4,每个主机有100个虚拟主机：");  
        sh.keyToNode("101客户端");  
        sh.keyToNode("102客户端");  
        sh.keyToNode("103客户端");  
        sh.keyToNode("104客户端");  
        sh.keyToNode("105客户端");  
        sh.keyToNode("106客户端");  
        sh.keyToNode("107客户端");  
        sh.keyToNode("108客户端");  
        sh.keyToNode("109客户端");  
          
        sh.deleteSlaveNode(s2);  
          
          
        sh.addSlaveNode(s5);  
          
        System.out.println("最后的客户端到主机的映射为：");  
        printKeyTree();  
        
        
        System.out.println("ip:"+treeKey.get(hash("109客户端")));
        
		
		
    }  
  
}  

