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
     * Visits the given service.
     *
     * @param serviceInfo the service.
     */
    void visit(ServiceInfo serviceInfo);

    /**
     * Visits the given operation.
     *
     * @param operationInfo the operation.
     */
    void visit(OperationInfo operationInfo);

    /**
     * Visits the given message.
     *
     * @param messageInfo the message.
     */
    void visit(MessageInfo messageInfo);

    /**
     * Visits the given fault.
     *
     * @param faultInfo the fault.
     */
    void visit(FaultInfo faultInfo);

    /**
     * Visits the given message part info.
     *
     * @param messagePartInfo the message part info.
     */
    void visit(MessagePartInfo messagePartInfo);
}
