/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package businessLogic.graph.filters;

import businessLogic.graph.filters.interfaces.IFilter;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: businessLogic.graph.filters.</P>
 * <P>An exception which contains the {@link IFilter} that throws the exception.</P>
 *
 * @see Exception
 */
public class FilterException extends Exception {

    private final IFilter m_filter;
    private final Exception m_exception;

    /**
     * @param filter The {@link IFilter} that throws the exception.
     * @param ex     The thrown {@link Exception}.
     */
    public FilterException(IFilter filter, Exception ex) {
        m_filter = filter;
        m_exception = ex;
    }

    /**
     * Getter of the thrown {@link Exception}.
     *
     * @return The thrown {@link Exception}.
     */
    public Exception getException() {
        return m_exception;
    }

    /**
     * Getter of {@link IFilter} that throws the exception.
     *
     * @return the {@link IFilter} that throws the exception.
     */
    public IFilter getFilter() {
        return m_filter;
    }

}
