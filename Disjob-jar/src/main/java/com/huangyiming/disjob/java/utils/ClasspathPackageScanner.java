package com.huangyiming.disjob.java.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * This scanner is used to find out all classes in a package.
 * Created by whf on 15-2-26.
 */
public class ClasspathPackageScanner implements PackageScanner {

    private String basePackage;
    private ClassLoader cl;

    /**
     * Construct an instance and specify the base package it should scan.
     * @param basePackage The base package to scan.
     */
    public ClasspathPackageScanner(String basePackage) {
        this.basePackage = basePackage;
        this.cl = getClass().getClassLoader();

    }

    /**
     * Construct an instance with base package and class loader.
     * @param basePackage The base package to scan.
     * @param cl Use this class load to locate the package.
     */
    public ClasspathPackageScanner(String basePackage, ClassLoader cl) {
        this.basePackage = basePackage;
        this.cl = cl;
    }

    /**
     * Get all fully qualified names located in the specified package
     * and its sub-package.
     *
     * @return A list of fully qualified names.
     * @throws IOException
     */
    public List<String> getClassNameList() throws IOException {
        Log.info("开始扫描包{}下的所有类:"+basePackage);

        return doScan(basePackage, new ArrayList<String>());
    }

    /**
     * Actually perform the scanning procedure.
     *
     * @param resource
     * @param nameList A list to contain the result.
     * @return A list of fully qualified names.
     *
     * @throws IOException
     */
    private List<String> doScan(String resource, List<String> nameList) throws IOException {
    	List<String> names = null; // contains the name of the class file. e.g., Apple.class will be stored as "Apple"
        if (FileUtils.isJarFile(resource)) {
            // jar file
            Log.debug("{} 是一个JAR包:"+resource);
            names = readFromJarFile(resource);
            List<String> className = new ArrayList<String>(names.size());
            for(String path : names){
            	className.add(toFullyQualifiedName(path));
            }
            
            return className;
        } else {
        	// replace dots with splashes
        	String splashPath = StringUtils.dotToSplash(resource);
        	
        	// get file path
        	URL url = cl.getResource(splashPath);
        	if(url == null){
        		return null ;
        	}
        	String filePath = StringUtils.getRootPath(url);
        	
        	// Get classes in that package.
        	// If the web server unzips the jar file, then the classes will exist in the form of
        	// normal file in the directory.
        	// If the web server does not unzip the jar file, then classes will exist in jar file.
            // directory
            Log.debug("{} 是一个目录:"+filePath);
            names = readFromDirectory(filePath);
        }

        for (String name : names) {
            if (isClassFile(name)) {
                //nameList.add(basePackage + "." + StringUtil.trimExtension(name));
                nameList.add(toFullyQualifiedName(name, resource));
            } else {
                // this is a directory
                // check this directory for more classes
                // do recursive invocation
                doScan(resource + "." + name, nameList);
            }
        }

        for (String n : nameList) {
        	Log.debug("找到{}"+n);
        }

        return nameList;
    }

    /**
     * Convert short class name to fully qualified name.
     * e.g., String -> java.lang.String
     */
    private String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(StringUtils.trimExtension(shortName));

        return sb.toString();
    }

    private String toFullyQualifiedName(String absPath) {
    	absPath = absPath.substring(0,absPath.indexOf(".class"));

        return absPath.replace("/", ".");
    }
    
    public List<String> readFromJarFile(String jarPath) throws IOException {
        Log.debug("从JAR包中读取类: {}:"+jarPath);
        @SuppressWarnings("resource")
		JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();

        List<String> nameList = new ArrayList<String>();
        while (null != entry) {
            String name = entry.getName();
            if (isClassFile(name)) {
                nameList.add(name);
            }

            entry = jarIn.getNextJarEntry();
        }

        return nameList;
    }

    private List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();

        if (null == names) {
            return null;
        }

        return Arrays.asList(names);
    }

    private boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

}
