/*
 * Created by admin on 27/09/2017
 * Last modified 18:42 27/09/17
 */

package services.common;


import android.support.annotation.NonNull;

import businessLogic.common.BaseBgOperation;
import businessLogic.common.interfaces.IBackgroundOperation;
import businessLogic.common.interfaces.ILog;
import services.logging.LogManager;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.common.</P>
 * <P>Extends the base class for {@link IBackgroundOperation},
 * which implements the {@link ILog} dependencies.</P>
 *
 * @see BaseBgOperation
 */

public abstract class ServicesBaseBgOperation extends BaseBgOperation {

    private ServicesBaseBgOperation(@NonNull ILog logger, String id) {
        super(logger, id);
    }

    public ServicesBaseBgOperation(String id){
        this(LogManager.getLogger(),id);
    }

    public ServicesBaseBgOperation(){
        this(LogManager.getLogger(),"");
    }

}
