package edu.xmu.hwb.storage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Herrfe on 14-8-25.
 */
public class StorageManagerFilter {

    /**
     * 线程池管理器
     */
    private ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {
        AtomicInteger threadID = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("storagePool" + threadID.getAndIncrement());
            return t;
        }
    });

    private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("storageTimer");
            return t;
        }
    });

    private int taskInterval = 180;
    /**
     * 数据库连接池管理器
     */
    private DataSource databasePoolManager;

    private Map<Object, AutoMsgStorage> storageProvide = new HashMap();

    public void setTaskInterval(int taskInterval) {
        this.taskInterval = taskInterval;
    }

    public void setDatabasePoolManager(DataSource databasePoolManager) {
        this.databasePoolManager = databasePoolManager;
    }

    public void setStorageProvide(Map<Object, AutoMsgStorage> storageProvide) {
        this.storageProvide = storageProvide;
    }

    public void store(Object key, Object msg) {
        AutoMsgStorage autoMsgStorage = storageProvide.get(key);
        if (autoMsgStorage != null)
            autoMsgStorage.preprocess(msg);
    }

    public void start() {
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                tick();
            }
        }, taskInterval, taskInterval, TimeUnit.SECONDS);
    }

    private void tick() {
        for (final AutoMsgStorage provide : storageProvide.values()) {
            final LinkedList<Object> taskData = provide.getTaskData();
            if (taskData.size() != 0) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Connection con = null;
                        try {
                            provide.store(con = databasePoolManager.getConnection(), taskData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (con != null) try {
                                con.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }
}
