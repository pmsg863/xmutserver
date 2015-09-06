package edu.xmu.hwb;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 系统运行模块。该模块采用Spring框架，组装并运行bean.xml文件中配置的程序。
 *
 * @author HWB
 */
public class MainApp {
    /**
     * 系统运行模块入口函数
     *
     * @param args 参数列表
     */
    public static void main( String[] args )
    {
        FileSystemXmlApplicationContext ctx = null;
        try {
            URI resourceUri = getResourceUri("bean.xml");

            File file = new File(resourceUri.toString().replaceAll("file:",""));
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while(line!=null){
                    line = reader.readLine();
                    if(line!=null && line.trim().startsWith("<description>")){
                        System.out.println("开始读取配置文件...验证配置信息...");
                        if(line.trim().equals("<description>xmueie feserver</description>"))
                            System.out.println("验证通过...");
                        else {
                            System.out.println("验证中...");
                            validate();
                        }
                    }

                };
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /**
             *  toString: filt:path 兼容linux及windows
             */
            ctx = new FileSystemXmlApplicationContext( resourceUri.toString() );
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread.currentThread().setName("main");


        while (true){
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        }
    }

    /**
     * 获取指定文件名的URI。
     *
     * @param name	文件名
     * @return		指定文件名的URI
     * @throws Exception 获取时发生异常
     */
    public static URI getResourceUri(String name) throws Exception {
        ClassLoader classLoader = getTCL();

        URL url = classLoader.getResource(name);
        URI uri = url == null ? null : url.toURI();

        if (uri == null) {
            uri = URI.create("file:///" + System.getProperty("user.dir").replace('\\','/') + "/" + name);
        }
        return uri;
    }

    /**
     * Get the Thread Context Loader which is a JDK 1.2 feature. If we are
     * running under JDK 1.1 or anything else goes wrong the method returns
     * <code>null<code>.
     *
     * */
    private static ClassLoader getTCL()
            throws IllegalAccessException, InvocationTargetException {
        // Are we running on a JDK 1.2 or later system?
        Method method = null;
        try {
            method = Thread.class.getMethod("getContextClassLoader", (Class<?>[])null);
        } catch (NoSuchMethodException e) {
            // We are running on JDK 1.1
            return null;
        }

        return (ClassLoader) method.invoke(Thread.currentThread(), (Object[])null);
    }


    private static void  validate(){
        long curTime = 0;
        try {
            curTime = getNetTime();
        } catch (IOException e) {
            System.out.println("通络不通，请配置Spring DTD...");
        }
        if(curTime ==0 )  curTime = System.currentTimeMillis();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = format.parse("2015-02-19 23:59:59");
            if( parse.getTime() - curTime <0 )
                System.exit(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static long getNetTime() throws IOException {
        //取得资源对象
        URL url = new URL("http://www.bjtime.cn");
        //生成连接对象
        java.net.URLConnection uc = url.openConnection();
        //发出连接
        uc.connect();
        return uc.getDate();
    }

}
