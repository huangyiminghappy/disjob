package com.huangyiming.disjob.register.repository;

import java.util.Collection;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import com.huangyiming.disjob.common.exception.ZKNodeException;

/**
 * 操作ZooKeeper数据节点的封装
 * @author Disjob
 */
public interface ZnodeApi {
	/**
	 * 根据path获取节点的stat
	 * Stat对象的属性：
	 * long czxid:即created ZXID，表示该数据节点被创建时的事务ID
	 * long mzxid:即modified ZXID，表示该节点最后一次被更新时的事务ID
     * long ctime:即created Time，表示节点被创建的时间
     * long mtime:即modified Time，表示该节点最后一次被更新的时间
     * int version:数据节点的版本号
     * int cversion:子节点的版本号
     * int aversion:节点的ACL版本号
     * long ephemeralOwner:创建该临时节点的回话的sessionID。如果该节点时持久节点，那么这个属性值为0
     * int dataLength:数据内容的长度
     * int numChildren:当前节点的子节点个数
     * long pzxid:表示该节点的子节点列表最后一次被修改时的事务ID
	 * @param znode
	 * @return
	 */
	public Stat getStat(CuratorFramework client, String znode) throws ZKNodeException;
	
	/**
	 * 检查节点是否存在
	 * @param znode
	 * @return
	 */
	boolean checkExists(CuratorFramework client, String znode) throws ZKNodeException;
	
	/**
	 * 根据path获取节点数据
	 * @param znode
	 * @return String
	 */
	String getData(CuratorFramework client, String znode) throws ZKNodeException;
	
	/**
	 * 获取某个job 的config 数据
	 * @param client
	 * @param znode
	 * @return
	 */
	public String getJobConfig(CuratorFramework client,String group,String name);
	
	public byte[] getByteData(CuratorFramework client, String znode);
	
	public <T> T getData(CuratorFramework client, String znode, Class<T> clazz);
	/**
	 * 根据path获取节点数据
	 * @param client
	 * @param znode
	 * @return byte []
	 * @throws ZKNodeException
	 */
	public byte [] getDataForBytes(CuratorFramework client, String znode) throws ZKNodeException;
	    
	
	List<String> getChildren(CuratorFramework client, String znode) throws ZKNodeException;
	
	/**
	 * 创建持久节点，可递归创建
	 * 若data为null，则创建空节点
	 * @param znode
	 * @param data
	 */
	void createPersistent(CuratorFramework client, String znode, Object data) throws ZKNodeException;
	void create(CuratorFramework client, String znode, Object data) throws ZKNodeException;
	
	public void createPersistent(CuratorFramework client, String znode, List<ACL> aclList);
	public void createPersistent(CuratorFramework client, String znode, byte[] data, List<ACL> aclList);
	public void createPersistent(CuratorFramework client, String znode, Object data, List<ACL> list);
	
	public CuratorTransactionFinal addCreateToTransaction(CuratorTransaction transaction, String path, String data, List<ACL> aclList);
	/**
	 * 创建临时节点，可递归创建
	 * 若data为null，则创建空节点
	 * @param znode
	 * @param data
	 */
	void createEphemeral(CuratorFramework client, String znode, Object data) throws ZKNodeException;
	    
	void updateWithJson(CuratorFramework client, String znode, Object value);
	/**
	 * 如果data为null，则此节点无数据
	 * @param znode
	 * @param value
	 */
	void update(CuratorFramework client, String znode, Object data) throws ZKNodeException;
	
	/**
	 * 根据版本号更新数据
	 * @param version
	 * @param znode
	 * @param data
	 */
	void update(CuratorFramework client, int version, String znode, Object data) throws ZKNodeException;
	
	/**
	 * 根据节点删除，只能删除叶子节点
	 * @param znode
	 */
	void deleteByZnode(CuratorFramework client, String znode) throws ZKNodeException;
	
	/**
	 * 根据版本删除
	 * @param version
	 */
	void deleteByVersion(CuratorFramework client, int version, String znode) throws ZKNodeException;
	
	/**
	 * 递归删除，及删除一个节点，并递归删除所有子节点
	 * @param zpath
	 */
	void deleteByRecursion(CuratorFramework client, String znode) throws ZKNodeException;
	
	/**
	 * 强制保证删除
	 * @param znode
	 */
	void deleteGuaranteed(CuratorFramework client, String znode) throws ZKNodeException;
	
	/**
	 * 传入多个节点路径，组成一个全路径
	 * @param parent
	 * @param firstChild
	 * @param restChildren
	 * @return
	 * @throws ZKNodeException
	 */
	String makePath(String parent, String firstChild, String... restChildren );
	
	/**
	 * 创建一个全路径的所有节点
	 * @param zooKeeper
	 * @param path
	 * @throws ZKNodeException
	 */
	void makeDirs(CuratorFramework client, String path) throws ZKNodeException;
	
	
	
	/**
	 * 事务开始
	 * @param client
	 * @return
	 */
    CuratorTransaction startTransaction(CuratorFramework client); 
	 
    
    /**
     * 事务中添加方法
     * @param transaction
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
	CuratorTransactionFinal addCreateToTransaction(CuratorTransaction transaction,String path,String data) throws Exception ;
 	
	/**
	 * 事务中修改方法
	 * @param transaction
	 * @param path
	 * @param data
	 * @return
	 * @throws Exception
	 */
	CuratorTransactionFinal addUpdateToTransaction(CuratorTransaction transaction,String path,String data) throws Exception ;

	/**
	 * 事务中删除方法
	 * @param transaction
	 * @param path
	 * @return
	 * @throws Exception
	 */
	CuratorTransactionFinal addDeleteToTransaction(CuratorTransaction transaction,String path) throws Exception ;
	   
	/**
	 * 事务中提交方法
	 * @param transaction
	 * @throws Exception
	 */
	Collection<CuratorTransactionResult>   commitTransaction(CuratorTransactionFinal transaction) throws Exception;
	   
	   
	   
	public void setACL(CuratorFramework client, String path, List<ACL> acllist);
	
	public void createWithJson(CuratorFramework client, String znode, Object data) throws ZKNodeException;
}
