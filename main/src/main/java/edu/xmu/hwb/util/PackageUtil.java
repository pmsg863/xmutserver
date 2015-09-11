/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.xmu.hwb.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 根据文件名反射加载协议处理过程到协议管理器中
 *
 * @author Herrfe
 */
public class PackageUtil {

    /**
     * Reload all process.
     *
     * @return the boolean
     */
    @SuppressWarnings("rawtypes")
	public static boolean reloadAllProcess() {
        List<String> cls = getClassInPackage("com.xmgps.hwb");
        for (String s : cls) {
            Object process = null;
            try {
                Class classType = Class.forName(s);
                //Class classType = Class.forName(s,false,ClassLoader.getSystemClassLoader());
                process = classType.newInstance();
                Class<?>[] interfaces = process.getClass().getInterfaces();
                for(Class antionInterface:interfaces) {
                    //TODO
                    /*if(antionInterface.equals(ProcessorAntion.class)){
                        ProtocolProcessManager.setProcessorAntion((ProcessorAntion) process);
                        System.out.println("");
                        System.out.println("ANTION IS "+s);
                        return true;
                    }*/
                }

            } catch (InstantiationException ex) {
                System.out.print(".");//Logger.getLogger(PackageUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                System.out.print("o");//Logger.getLogger(PackageUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                System.out.print("0");//Logger.getLogger(PackageUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("");
        return true;
    }

    /**
     * Gets class in package.
     *
     * @param pkgName the pkg name
     * @return the class in package
     */
    public static List<String> getClassInPackage(String pkgName) {
        List<String> ret = new ArrayList<String>();
        String rPath = pkgName.replace('.', '/');
        try {
            for (File classPath : CLASS_PATH_ARRAY) {
                if (!classPath.exists()) {
                    continue;
                }
                if (classPath.isDirectory()) {
                    File dir = new File(classPath, rPath);
                    if (!dir.exists()) {
                        continue;
                    }
                    retAdd(ret,dir,pkgName);
                } else {
                    FileInputStream fis = new FileInputStream(classPath);
                    JarInputStream jis = new JarInputStream(fis, false);
                    JarEntry e = null;
                    while ((e = jis.getNextJarEntry()) != null) {
                        String eName = e.getName();
                        if (eName.startsWith(rPath) && !eName.endsWith("/ ")) {
                            ret.add(eName.replace('/', '.').substring(0, eName.length() - 6));
                        }
                        jis.closeEntry();
                    }
                    jis.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

    private static void retAdd(List<String> ret,File dir,String pkgName) {
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                String clsName = file.getName();
                clsName = pkgName + "." + clsName.substring(0, clsName.length() - 6);
                ret.add(clsName);
            }  else{
                retAdd(ret,file,pkgName+"."+file.getName());
            }
        }
    }

    /**
     * The CLASS _ pATH _ pROP.
     */
    private static String[] CLASS_PATH_PROP = {"java.class.path", "java.ext.dirs", "sun.boot.class.path"};

    /**
     * The CLASS _ pATH _ aRRAY.
     */
    private static List<File> CLASS_PATH_ARRAY = getClassPath();

    /**
     * Gets class path.
     *
     * @return the class path
     */
    private static List<File> getClassPath() {
        List<File> ret = new ArrayList<File>();
        String delim = ":";
        if (System.getProperty("os.name").indexOf("Windows") != -1) {
            delim = ";";
        }
        for (String pro : CLASS_PATH_PROP) {
            String[] pathes = System.getProperty(pro).split(delim);
            for (String path : pathes) {
                ret.add(new File(path));
            }
        }
        ret.add(new File("D:\\Herrfe\\Documents\\IdeaProjects\\CommonFes\\plugin\\fes-plugin-1.0-HWB-SNAPSHOT.jar"));
        return ret;
    }
}
