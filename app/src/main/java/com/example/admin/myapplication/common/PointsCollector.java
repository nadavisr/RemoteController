/*
 * Created by admin on 12/11/2017
 * Last modified 11:18 12/11/17
 */

package com.example.admin.myapplication.common;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.common.</P>
 * <P></P>
 */
public class PointsCollector {

    //region Fields

    private int[] m_sumOfPoints;

    private int[] m_lastPoint;

    private int m_count;

    private final Lock m_sumLock;

    private final Lock m_lastPointLock;

    private boolean m_beenFirstPoint;
    //endregion

    //region Constructors

    public PointsCollector() {
        m_sumLock = new ReentrantLock();
        m_lastPointLock = new ReentrantLock();
        m_sumOfPoints = new int[2];
        m_lastPoint = new int[2];
        m_lastPoint[0] = m_lastPoint[1] = m_sumOfPoints[0] = m_sumOfPoints[1] = m_count = 0;
        m_beenFirstPoint = false;
    }

    //endregion

    //region Methods

    /**
     * Synchronized update to last point.
     *
     * @param x X point .
     * @param y Y point.
     */
    public void addPoint(int x, int y) {
        m_sumLock.lock();
        m_sumOfPoints[0] += x;
        m_sumOfPoints[1] += y;
        m_count++;
        m_sumLock.unlock();

        m_lastPointLock.lock();
        m_lastPoint[0] = x;
        m_lastPoint[1] = y;
        m_beenFirstPoint = true;
        m_lastPointLock.unlock();

    }

    /**
     * Get the current average of the collected X and Y , and nullify the collection.
     *
     * @return The average x in place 0 and average y in place 1, returns null if there is no points to collect.
     */
    public int[] getAverage() {
        m_sumLock.lock();

        if (m_count == 0) {
            m_sumLock.unlock();
            return null;
        }

        int[] clonedArray = m_sumOfPoints.clone();
        int currentCount = m_count;
        m_sumOfPoints[0] = m_sumOfPoints[1] = m_count = 0;

        m_sumLock.unlock();

        clonedArray[0] = clonedArray[0] / currentCount;
        clonedArray[1] = clonedArray[1] / currentCount;

        return clonedArray;
    }


    /**
     * Get the current average of the collected X and Y, and continue the collection.
     *
     * @return The average x in place 0 and average y in place 1, returns null if there is no points to collect.
     */
    public int[] getIntermediateAverage() {
        m_sumLock.lock();

        if (m_count == 0) {
            m_sumLock.unlock();
            return null;
        }

        int[] clonedArray = m_sumOfPoints.clone();
        int currentCount = m_count;

        m_sumLock.unlock();

        clonedArray[0] = clonedArray[0] / currentCount;
        clonedArray[1] = clonedArray[1] / currentCount;

        return clonedArray;
    }

    /**
     * Get the last added point to the points collector.
     *
     * @return Last added point to the points collector, returns null if there is no points to collect.
     */
    public int[] getLastAddedPoint() {
        m_lastPointLock.lock();

        if (!m_beenFirstPoint) {
            m_lastPointLock.unlock();
            return null;
        }

        int[] clonedArray = m_lastPoint.clone();

        m_lastPointLock.unlock();

        return clonedArray;
    }

    //endregion
}
