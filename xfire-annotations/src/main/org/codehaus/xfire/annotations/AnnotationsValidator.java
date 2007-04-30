package org.codehaus.xfire.annotations;


/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 */
public interface AnnotationsValidator {

	void validate(WebAnnotations annotations,Class clazz) throws AnnotationException;
	
	
}
