package edu.xmu.hwb.storage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Herrfe on 14-7-4.
 */
public class AutoMsgStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoMsgStorage.class);

    String SQL;

    String tablename;

    Class contextClass;

    Method vauleOf;

    // 消息缓冲队列长度
    private int batchCount;
    // 消息最大缓冲时长，为0表示不使用最大缓冲时长
    private int batchInterval;
    // 上次存储时间，用于缓冲最大时长计算
    private long lastBatchTime = System.currentTimeMillis();

    // 消息队列
    private ConcurrentLinkedQueue<Object> msgQueue = new ConcurrentLinkedQueue<Object>();

    private void generateSQL(String classname) throws ClassNotFoundException {
        contextClass = Class.forName(classname);
        Annotation annotation = contextClass.getAnnotation(TABLE.class);
        if (annotation != null) {
            TABLE table = (TABLE) annotation;
            StringBuilder sql = new StringBuilder("insert into ");
            sql.append(tablename = table.name());
            sql.append(" (");

            Field fields[] = contextClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                String name = fields[i].getName();
                sql.append(name).append(",");
            }
            sql.deleteCharAt(sql.length() - 1).append(" ) \n").append("values ").append("(");

            for (int i = 0; i < fields.length; i++) {
                sql.append("?,");
            }
            sql.deleteCharAt(sql.length() - 1).append(")");

            System.out.println(classname + " " + (SQL = sql.toString()));
        }

        annotation = contextClass.getAnnotation(PROC.class);
        if (annotation != null) {
            PROC proc = (PROC) annotation;
            StringBuilder sql = new StringBuilder("call ");
            sql.append(tablename = proc.name());
            sql.append(" (");

            Field fields[] = contextClass.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                sql.append("?,");
            }
            sql.deleteCharAt(sql.length() - 1).append(")");

            System.out.println(classname + " " + (SQL = sql.toString()));
        }

    }

    public AutoMsgStorage(String msgDataClassName) {
        try {
            generateSQL(msgDataClassName);
            /*
                做缓存提高效率
             */
            vauleOf = contextClass.getMethod("vauleOf", Object.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public AutoMsgStorage(String msgDataClassName,int batchCount, int batchInterval){
        try {
            generateSQL(msgDataClassName);
            /*
                做缓存提高效率
             */
            vauleOf = contextClass.getMethod("vauleOf", Object.class);
            this.batchCount = batchCount;
            this.batchInterval = batchInterval;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    boolean preprocess(Object msg){
        Object dbstruct = null;
        if( (dbstruct = this.process(msg))!=null)
            return msgQueue.add(dbstruct);
        else
            return false;
    };

    private   Object process(Object msg){
        try {
            Object invoke = vauleOf.invoke(this, msg);
            if (invoke != null) {
                return invoke;
            } else
                return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean store(Connection conn, LinkedList<Object> msgList) throws Exception {
        PreparedStatement prst = null;
        try {
            String strSql = SQL;
            prst = conn.prepareStatement(strSql);
            conn.setAutoCommit(false);
            int total = msgList.size();
            for (int i = 0; i < total; i++) {
                setPrstParam(prst, msgList.get(i));
                prst.addBatch();
            }
            int[] counter = prst.executeBatch();

            // 输出执行结果
            if (counter != null ) {
                int gtOneAff = 0, oneAff = 0, zeroAff = 0, sni = 0, failed = 0, other = 0;

                for (int i : counter) {
                    if (i > 1) {
                        gtOneAff++;
                    } else if (i == 1) {
                        oneAff++;
                    } else if (i == 0) {
                        zeroAff++;
                    } else if (i == Statement.SUCCESS_NO_INFO) {
                        sni++;
                    } else if (i == Statement.EXECUTE_FAILED) {
                        failed++;
                    } else {
                        other++;
                    }
                }
                LOGGER.debug(tablename + " ExecuteBatch [TOT] = " + msgList.size()
                        + " [>1] = " + gtOneAff + " [=1] = " + oneAff
                        + " [=0] = " + zeroAff + " [SNI] = " + sni
                        + " [EF] = " + failed + " [OTH] = " + other);
            }

            conn.commit();
            conn.setAutoCommit(true);

            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LOGGER.error(e1.toString());
            }

            LOGGER.error(e.toString());
            e.printStackTrace();

            throw e;
        } finally {
            if (prst != null) {
                try {
                    prst.close();
                } catch (SQLException ex) {
                    LOGGER.error(ex.toString());
                }
            }
        }
    }

    private void setPrstParam(PreparedStatement prst, Object track) {

        Class contextClass = track.getClass();

        Field fields[] = contextClass.getDeclaredFields();
        for (int j = 0; j < fields.length; j++) {
            String name = fields[j].getName();
            Class<?> type = fields[j].getType();

            try {
                switch (type.getName()) {
                    case "boolean":
                        fields[j].setAccessible(true);
                        prst.setBoolean(j + 1, fields[j].getBoolean(track));
                        fields[j].setAccessible(false);
                        break;
                    case "int":
                        fields[j].setAccessible(true);
                        prst.setInt(j + 1, fields[j].getInt(track));
                        fields[j].setAccessible(false);
                        break;
                    case "long":
                        fields[j].setAccessible(true);
                        prst.setLong(j + 1, fields[j].getLong(track));
                        fields[j].setAccessible(false);
                        break;
                    case "java.sql.Date":
                        fields[j].setAccessible(true);
                        prst.setTimestamp(j + 1, new Timestamp(((Date) fields[j].get(track)).getTime()));
                        fields[j].setAccessible(false);
                        //prst.setDate(j+1, (Date) fields[j].get(track));
                        break;
                    default:
                        fields[j].setAccessible(true);
                        prst.setObject(j + 1, fields[j].get(track));
                        fields[j].setAccessible(false);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ;

        }
    }

    public int getBatchCount() {
        return batchCount;
    }

    public int getBatchInterval() {
        return batchInterval;
    }

    public long getLastBatchTime() {
        return lastBatchTime;
    }

    LinkedList<Object> getTaskData(){
        final LinkedList<Object> dataList = new LinkedList<Object>();
        // 队列中有数据，且时间间隔大于消息则进行数据存储最大缓冲时长，
        if (msgQueue.size() > 0 && System.currentTimeMillis() - lastBatchTime > batchInterval){
            // 从列表中提取一批要存储的消息列表
            for (int i = 0; i < batchCount; i++){
                Object data = msgQueue.poll();
                if (data != null){
                    dataList.add(data);
                } else {
                    break;
                }
            }
            // 记录存储时间
            lastBatchTime = System.currentTimeMillis();
        }
        return  dataList;
    }
}
