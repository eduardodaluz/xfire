package org.codehaus.xfire.service;

/**
 * Defines the contract for classes that iterate over the <code>*Info</code> classes. Used to recurse into {@link
 * ServiceInfo}, {@link OperationInfo}, {@link MessageInfo}, etc.
 * <p/>
 * <strong>Note</strong> that implementations of this interface are not required to recurse themselves; instead, this is
 * handled by the various vistable implementations.
 *
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see Visitable
 */
public interface Visitor
{
    /**
     * Visits the given service info.
     *
     * @param serviceInfo the service info.
     */
    void visit(ServiceInfo serviceInfo);

    /**
     * Visits the given operation info.
     *
     * @param operationInfo the operation info.
     */
    void visit(OperationInfo operationInfo);

    /**
     * Visits the given message info.
     *
     * @param messageInfo the message info.
     */
    void visit(MessageInfo messageInfo);

    /**
     * Visits the given message part info.
     *
     * @param messagePartInfo the message part info.
     */
    void visit(MessagePartInfo messagePartInfo);
}
