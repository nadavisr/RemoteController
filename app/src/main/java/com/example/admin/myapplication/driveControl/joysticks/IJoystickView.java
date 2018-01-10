/*
 * Created by admin on 24/12/2017
 * Last modified 15:59 24/12/17
 */

package com.example.admin.myapplication.driveControl.joysticks;

import android.support.annotation.IntRange;

import businessLogic.common.interfaces.IDisposable;


/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.driveControl.joysticks.</P>
 * <P></P>
 */

public interface IJoystickView extends IDisposable{

    //region Interfaces

    /**
     * Interface definition for a callback to be invoked when a JoystickView's button is moved,
     * this is generic interface so please do not use this interface ,
     * use instead {@link IJoystickView.IOnMoveListenerGetAnglePower} or {@link IJoystickView.IOnMoveListenerGetXY}
     */
    interface IOnMoveListener {

        /**
         * Called when a JoystickView's button has been moved.
         *
         * @param firstArgument  The first argument which received from the joystick move.
         * @param secondArgument The second argument which received from the joystick move.
         */
        void onMove(int firstArgument, int secondArgument);

    }

    /**
     * Interface definition for a callback to be invoked when a JoystickView's button is moved,
     * returns the angle and the power of the move.
     */
    interface IOnMoveListenerGetAnglePower extends IJoystickView.IOnMoveListener {

        /**
         * {@inheritDoc}
         *
         * @param angle The angle of the button , angle following the 360° counter-clock protractor rules.
         * @param power The power of the button, power is the distance from the center in percent [0,100].
         */
        void onMove(int angle, @IntRange(from = 0, to = 100) int power);

    }

    /**
     * Interface definition for a callback to be invoked when a JoystickView's button is moved
     * returns the x point and y point of the move in Cartesian axis..
     */
    interface IOnMoveListenerGetXY extends IJoystickView.IOnMoveListener{

        /**
         * {@inheritDoc}
         *
         * @param x The angle of the button , angle following the 360° counter-clock protractor rules.
         * @param y The power of the button, power is the distance from the center in percent [0,100].
         */
        void onMove(int x, int y);
    }

    //endregion Interfaces

    //region Constants

    int DEFAULT_EVENT_FREQUENCY_IN_MILLISECONDS = 33; // ~ 23 per sec

    //endregion

    //region Setters

    /**
     * Register a callback to be invoked when this JoystickView's button is moved, probably should
     * use the default value of event frequency: {@value DEFAULT_EVENT_FREQUENCY_IN_MILLISECONDS} milliseconds.
     *
     * @param onMoveListener The callback that will run
     */
    void setOnMoveListener(IJoystickView.IOnMoveListener onMoveListener);

    /**
     * Register a callback to be invoked when this JoystickView's button is moved.
     *
     * @param onMoveListener               The callback that will run
     * @param eventFrequencyInMilliseconds Refresh rate to be invoked in milliseconds
     */
    void setOnMoveListener(IJoystickView.IOnMoveListener onMoveListener, @IntRange(from = 1) int eventFrequencyInMilliseconds);

    //endregion

    //region Methods

    /**
     * The function checks if a point in the joystick dead zone.
     *
     * @param x X point in Cartesian axis.
     * @param y Y point in Cartesian axis.
     * @return X (place 0) and Y (place 1) as received if not in the dead zone and 0 in one of the (or both) if in the dead zone..
     */
    int[] checkDeadZones(int x, int y);

    /**
     * The function calculate the power of the received point at a scale of 0 to 100.
     *
     * @param x X point in Cartesian axis.
     * @param y Y point in Cartesian axis.
     * @return The power of the received point at a scale of 0 to 100.
     */
    int calculatePower(int x, int y);

    /**
     * The function calculate the power of the received point at degrees.
     *
     * @param x X point in Cartesian axis.
     * @param y Y point in Cartesian axis.
     * @return The angle of the received point at degrees.
     */
    int calculateAngleInDegrees(int x, int y);

    /**
     * The function calculate the power of the received point at radians.
     *
     * @param x X point in Cartesian axis.
     * @param y Y point in Cartesian axis.
     * @return The angle of the received point at radians.
     */
    int calculateAngleInRadians(int x, int y);

    //endregion
}
