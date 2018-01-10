/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.common;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package:  businessLogic.common.</P>
 * <P>Thread with conditional loop for long run uses,
 * and stop option.</P>
 * <P>The thread can be reused.</P>
 *
 * @see Thread
 */

public abstract class LongRunStoppableThread extends Thread {

    private volatile boolean running;

    public LongRunStoppableThread() {
        super();
        running = false;
    }

    /**
     * @return true if the thread is running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Stop thread running when its possible.
     */
    public void stopRun() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        boolean internalRunningStatus = internalInitialize();
        while (running && internalRunningStatus) {
            internalRunningStatus = internalRun();
        }
        internalStop();
        running = false;
    }

    /**
     * Happen before the conditional loop starts.
     *
     * @return true if the initialization succeed.
     */
    @SuppressWarnings("SameReturnValue")
    protected boolean internalInitialize(){
        return true;
    }


    /**
     * Method happen after the conditional loop stop due to any reason.
     */
    @SuppressWarnings("EmptyMethod")
    protected void internalStop(){

    }

    /**
     * The conditional loop logic.
     *
     * @return true to continue to the next loop.
     */
    protected abstract boolean internalRun();

}
