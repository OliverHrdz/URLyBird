package suncertify.db;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An asynchronous process for writing to the database file. Database write
 * operations are instances of {@link AsyncFileWriterTask} objects, and are
 * queued to this process to be executed in a separate thread.
 * 
 * @author Oliver Hernandez
 * 
 */
class AsyncFileWriter implements Runnable {

    private LinkedBlockingQueue<AsyncFileWriterTask> tasks;

    /**
     * Constructs an <code>AsyncFileWriter</code> process.
     */
    AsyncFileWriter() {
        this.tasks = new LinkedBlockingQueue<AsyncFileWriterTask>();
    }

    /**
     * Add a database write operation for asynchronous execution.
     * 
     * @param task
     *            the operation to enqueue.
     */
    void add(AsyncFileWriterTask task) {
        this.tasks.add(task);
    }

    /**
     * Execute database write operations queued to this process.
     */
    public void run() {
        AsyncFileWriterTask task;

        while (true) {
            try {
                task = this.tasks.take();
                task.execute();

                if (task instanceof EndFileWritingTask) {
                    this.tasks.clear(); // clear queue since database is closed
                    break;
                }
            } catch (InterruptedException e) {
                // ignore and try again
            } catch (IOException e) {
                // shutdown the server on any database file IO error.
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

}
