package com.huangyiming.disjob.register.repository;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Repository;

import com.huangyiming.disjob.common.Constants;
import com.huangyiming.disjob.common.exception.ZKNodeException;
import com.huangyiming.disjob.common.util.LoggerUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * 基于Curator的ZnodeApi接口实现
 * @author Disjob
 */
@Repository("znodeApi")
public class ZnodeApiCuratorImpl implements ZnodeApi {
	
	public ZnodeApiCuratorImpl(){
	}
	
	public boolean checkExists(CuratorFramework client, String znode) {
		try {
			return null != client.checkExists().forPath(znode);
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}
	
	public Stat getStat(CuratorFramework client, String znode){
		try {
			return client.checkExists().forPath(znode);
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}

	public String getData(CuratorFramework client, String znode) {
		try {
		    if (checkExists(client, znode)) {
			    return new String(client.getData().forPath(znode), Charset.forName("UTF-8"));
            } else {
                return null;
            }
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}
	public byte[] getByteData(CuratorFramework client, String znode) {
		try {
			if (checkExists(client, znode)) {
				return client.getData().forPath(znode);
			} else {
				return new byte[0];
			}
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}
	
	public <T> T getData(CuratorFramework client, String znode, Class<T> clazz) {
		try {
			if (checkExists(client, znode)) {
				String json = new String(client.getData().forPath(znode), Charset.forName("UTF-8"));
				try {
					return new Gson().fromJson(json, clazz);					
				} catch (JsonSyntaxException e) {
					throw new ZKNodeException("convert String[" + json + "] to Json[" + clazz.getName() + "] error" ,e);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}
	
	public byte [] getDataForBytes(CuratorFramework client, String znode) {
		try {
		    if (checkExists(client, znode)) {
			    return client.getData().forPath(znode);
            } else {
                return null;
            }
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}

	public List<String> getChildren(CuratorFramework client, String znode) {
		try {
			return client.getChildren().forPath(znode);
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}

	public void createPersistent(CuratorFramework client, String znode, Object data) {
		try {
			if(null == data){
				//client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(znode);
			    makeDirs(client, znode);
			}else{
				client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(znode, data.toString().getBytes(Charset.forName("UTF-8")));
			}
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}
	
	public void createPersistent(CuratorFramework client, String znode, List<ACL> aclList) {
		createPersistent(client, znode, new byte[0], aclList);
	}
	
	public void createPersistent(CuratorFramework client, String znode, byte[] data, List<ACL> aclList) {
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).withACL(aclList).forPath(znode, data);
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}
	
	public void createPersistent(CuratorFramework client, String znode, Object data, List<ACL> aclList) {
		String dataStr = data == null ? "" : data.toString();
		try {
			createPersistent(client, znode, dataStr.getBytes(Charset.forName("UTF-8")), aclList);
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}
	
	public CuratorTransactionFinal addCreateToTransaction(CuratorTransaction transaction, String path, String data, List<ACL> aclList) {
		String dataStr = data == null ? "" : data.toString();
		try {
			return transaction.create().withACL(aclList).forPath(path, dataStr.getBytes("UTF-8")).and();
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}
	
	public void createEphemeral(CuratorFramework client, String znode, Object data){
		try {
			if(null == data){
				client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(znode);
			}else{
				client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(znode, data.toString().getBytes(Charset.forName("UTF-8")));
			}
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}

	public void updateWithJson(CuratorFramework client, String znode, Object value) {
		String jsonData = new Gson().toJson(value);
		update(client, znode, jsonData);
	}
	
	public void update(CuratorFramework client, String znode, Object value) {
		try {
			if(null == value){
				client.setData().forPath(znode);
			}else{
				client.setData().forPath(znode, value.toString().getBytes(Charset.forName("UTF-8")));
			}
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}
	
	public void update(CuratorFramework client, int version, String znode, Object value) {
		try {
			if(null == value){
				client.setData().withVersion(version).forPath(znode);
			}else{
				client.setData().withVersion(version).forPath(znode, value.toString().getBytes(Charset.forName("UTF-8")));
			}
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}

	public void deleteByZnode(CuratorFramework client, String znode) {
		try {
			client.delete().forPath(znode);
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}

	public void deleteByVersion(CuratorFramework client, int version, String znode) {
		try {
			client.delete().withVersion(version).forPath(znode);
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}

	public void deleteByRecursion(CuratorFramework client, String znode) {
		try {
			client.delete().deletingChildrenIfNeeded().forPath(znode);
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}

	public void deleteGuaranteed(CuratorFramework client, String znode) {
		try {
			client.delete().deletingChildrenIfNeeded().forPath(znode);
		} catch (Exception e) {
			throw new ZKNodeException(e);
		}
	}
	
	@Override
	public String makePath(String parent, String firstChild, String... restChildren) {
		return ZKPaths.makePath(parent, firstChild, restChildren);
	}
	
	@Override
	public void makeDirs(CuratorFramework client, String path) throws ZKNodeException {
		try {
			ZooKeeper zkClient = client.getZookeeperClient().getZooKeeper();
			ZKPaths.mkdirs(zkClient, path);
		} catch (InterruptedException e) {
			throw new ZKNodeException(e);
		} catch (KeeperException e) {
			throw new ZKNodeException(e);
		} catch (Exception e) {
			LoggerUtil.debug("DisJob:DisJob server用client获取ZooKeeper对象出现异常:", e);
			throw new ZKNodeException(e);
		}
	}

	@Override
	public CuratorTransaction startTransaction(CuratorFramework client) {
		 return client.inTransaction();
	}

	@Override
	public CuratorTransactionFinal addCreateToTransaction(
			CuratorTransaction transaction, String path, String data) throws Exception {
		if(StringUtils.isEmpty(path) || StringUtils.isEmpty(data)){
			throw new Exception("create "+path  + " , data is " +data + " error" );
		}
 		  return transaction .create().forPath(path, data.getBytes("UTF-8")).and();
	}

	@Override
	public CuratorTransactionFinal addUpdateToTransaction(
			CuratorTransaction transaction, String path, String data)
			throws Exception {
		if(StringUtils.isEmpty(path) || StringUtils.isEmpty(data)){
			throw new Exception("update "+path  + " , data is " +data + " error" );
		}
  		return  transaction.setData().forPath(path, data.getBytes("UTF-8")).and();
		 
	}

	@Override
	public CuratorTransactionFinal addDeleteToTransaction(
			CuratorTransaction transaction, String path) throws Exception {
		if(StringUtils.isEmpty(path)  ){
			throw new Exception("delete "+path + " error" );
		}
         return transaction.delete().forPath(path).and();
	}

	@Override
	public Collection<CuratorTransactionResult>   commitTransaction(CuratorTransactionFinal transaction)
			throws Exception {
       return transaction.commit();
 	}

	@Override
	public void setACL(CuratorFramework client, String path, List<ACL> acllist) {
		create(client, path, null);
		try {
			client.setACL().withACL(acllist).forPath(path);
		} catch (Exception e) {
			LoggerUtil.error("节点授权异常", e); 
			throw new ZKNodeException(e);
		}
	}

	@Override
	public void create(CuratorFramework client, String znode, Object data) throws ZKNodeException {
		if(!this.checkExists(client, znode)){
			this.createPersistent(client, znode, data);
		}
	}

	@Override
	public String getJobConfig(CuratorFramework client, String group,String jobName) {
		String jobPath = makePath(Constants.ROOT,Constants.APP_JOB_NODE_ROOT, Constants.PATH_SEPARATOR+ group, Constants.PATH_SEPARATOR + jobName,Constants.APP_JOB_NODE_CONFIG);
		return getData(client, jobPath);
	}

	@Override
	public void createWithJson(CuratorFramework client, String znode, Object data) throws ZKNodeException {
		create(client, znode, new Gson().toJson(data));
	}
}
