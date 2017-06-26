package com.huangyiming.disjob.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.huangyiming.disjob.common.exception.JobException;

/**
 * 获取真实本机IP&主机名.
 * 
 * @author Disjob
 */
public class LocalHost {

	private static volatile String cachedIpAddress;

	/**
	 * 获取本机IP地址.
	 * 有限获取外网IP地址. 也有可能是链接着路由器的最终IP地址.
	 * @return 本机IP地址
	 */
	public String getIp() {
		if (null != cachedIpAddress) {
			return cachedIpAddress;
		}
		Enumeration<NetworkInterface> netInterfaces;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (final SocketException ex) {
			throw new JobException(ex);
		}
		String localIpAddress = null;
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = netInterfaces.nextElement();
			Enumeration<InetAddress> ipAddresses = netInterface.getInetAddresses();
			while (ipAddresses.hasMoreElements()) {
				InetAddress ipAddress = ipAddresses.nextElement();
				if (isPublicIpAddress(ipAddress)) {
					String publicIpAddress = ipAddress.getHostAddress();
					cachedIpAddress = publicIpAddress;
					return publicIpAddress;
				}
				if (isLocalIpAddress(ipAddress)) {
					localIpAddress = ipAddress.getHostAddress();
				}
			}
		}
		cachedIpAddress = localIpAddress;
		return localIpAddress;
	}
	
	/**
	 * 获取本机Host名称.
	 * 
	 * @return 本机Host名称
	 */
	public String getHostName() {
		return getLocalHost().getHostName();
	}

	private boolean isPublicIpAddress(final InetAddress ipAddress) {
		return !ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
	}

	private boolean isLocalIpAddress(final InetAddress ipAddress) {
		return ipAddress.isSiteLocalAddress() && !ipAddress.isLoopbackAddress() && !isV6IpAddress(ipAddress);
	}

	private boolean isV6IpAddress(final InetAddress ipAddress) {
		return ipAddress.getHostAddress().contains(":");
	}

	private static InetAddress getLocalHost() {
		InetAddress result;
		try {
			result = InetAddress.getLocalHost();
		} catch (final UnknownHostException ex) {
			throw new JobException(ex);
		}
		return result;
	}
}
