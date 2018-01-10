/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.graph.executors.interfaces;

import businessLogic.common.interfaces.IBackgroundOperation;
import businessLogic.common.interfaces.IExceptionDistributor;
import businessLogic.graph.filters.interfaces.IFilter;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.executors.interfaces.</P>
 * <P>Defines an executor that is contained in the {@link IFilter}</P>
 *
 * @see IBackgroundOperation
 * @see IExceptionDistributor
 */

public interface IExecutor extends IBackgroundOperation, IExceptionDistributor<Exception> {

}
