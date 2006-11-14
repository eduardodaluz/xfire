package org.codehaus.xfire.aegis.inheritance.xfire704.response;

import org.codehaus.xfire.aegis.inheritance.xfire704.TestValue;

public class TestSubResponse extends TestBaseResponse {
    private TestValue testValue;

    public TestSubResponse() {
    }

    public TestSubResponse(TestValue testValue) {
        this.testValue = testValue;
    }

    public TestValue getTestValue() {
        return testValue;
    }

    public void setTestValue(TestValue testValue)
    {
        this.testValue = testValue;
    }
    
}