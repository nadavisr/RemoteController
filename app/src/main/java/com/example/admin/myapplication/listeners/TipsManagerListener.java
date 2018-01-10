/*
 * Created by Administrator on 27/12/2017
 * Last modified 09:55 27/12/17
 */

package com.example.admin.myapplication.listeners;

/**
 * Listener for the scheduler dialog
 */

public interface TipsManagerListener {

    /**
     * Show the next tip for this robot fragment
     */
    void onShowNextTip();


    /**
     * Force to implement the isLastTipToShow setter in order not to forget using it
     */
    void setIsLastTipToShow(boolean isLastTipToShow);

}
