package org.codehaus.xfire.service.event;

import org.codehaus.xfire.service.Service;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface RegistrationEventListener
{
    public void onRegister(Service service);
    
    public void onUnregister(Service service);
}
