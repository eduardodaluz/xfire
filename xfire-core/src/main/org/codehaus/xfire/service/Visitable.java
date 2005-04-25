package org.codehaus.xfire.service;

/**
 * Indicates that a class may be visited by a {@link Visitor}.
 * <p/>
 * Used to recurse into {@link Service}, {@link OperationInfo}, {@link MessageInfo}, etc.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see Visitor
 */
public interface Visitable
{
    /**
     * Acceps the given visitor. Subclasses are required to call {@link Visitor#visit visit(this)}, iterate over their
     * members, and call {@link #accept} for each of them.
     *
     * @param visitor the visitor.
     */
    void accept(Visitor visitor);
}
