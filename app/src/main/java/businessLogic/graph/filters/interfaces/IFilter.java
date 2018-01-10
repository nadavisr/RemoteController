/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.graph.filters.interfaces;

import businessLogic.common.interfaces.IBackgroundOperation;
import businessLogic.common.interfaces.IExceptionDistributor;
import businessLogic.graph.executors.interfaces.IExecutor;
import businessLogic.graph.filters.FilterException;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.filters.interfaces.</P>
 * <P>Defines a filter that contains a {@link IExecutor} and part of 'Graph' pattern.</P>
 *
 * @see IBackgroundOperation
 * @see IExceptionDistributor
 */

public interface IFilter extends IBackgroundOperation,IExceptionDistributor<FilterException> {

}
