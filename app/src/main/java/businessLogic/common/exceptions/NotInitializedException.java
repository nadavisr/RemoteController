/*
 * Created by admin on 22/10/2017
 * Last modified 16:39 03/10/17
 */

package businessLogic.common.exceptions;

import businessLogic.common.interfaces.IInitializable;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.</P>
 * <P>An exception to throw when performing a prohibited method before initialization,
 * in {@link IInitializable} class.</P>
 */

public class NotInitializedException extends RuntimeException {

    public NotInitializedException(Class<? extends IInitializable> type) {
        super("The Class: \"" + type.getName() + "\" used before initialization.");
    }

}
