package org.codehaus.xfire.aegis.inheritance.xfire704;

import org.codehaus.xfire.aegis.inheritance.xfire704.response.TestSubResponse;

public class TestService {
    public TestService() {
    }

    public TestSubResponse getTestValue() {
        return new TestSubResponse(new TestValue());
    }
}