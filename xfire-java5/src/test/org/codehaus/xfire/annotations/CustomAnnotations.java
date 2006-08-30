package org.codehaus.xfire.annotations;
@EnableMTOM
@ServiceProperty(key="singleKey",list={"aa","bb"} )
@ServiceProperties(properties={@ServiceProperty(key="key1",value="value1")})
public class CustomAnnotations {

}
