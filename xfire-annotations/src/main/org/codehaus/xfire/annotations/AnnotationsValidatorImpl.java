package org.codehaus.xfire.annotations;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 * 
 */
public class AnnotationsValidatorImpl implements AnnotationsValidator {

	private WebAnnotations annotations;

	protected void validateClass(Class clazz) throws AnnotationException {
		int modz = clazz.getModifiers();
		if (!clazz.isInterface()
				&& (Modifier.isAbstract(modz) || Modifier.isFinal(modz))) {
			throwException("Webservice class can't be abstract or final",
					"4.1.1", "Class : " + clazz.getName());
		}

		WebServiceAnnotation webAnn = annotations
				.getWebServiceAnnotation(clazz);
		if (webAnn.getPortName() != null && webAnn.getPortName().length() > 0
				&& clazz.isInterface()) {
			throwException("Port name can't be defined on interface", "4.1.1",
					"Class : " + clazz.getName());
		}

		if (webAnn.getEndpointInterface() != null
				&& webAnn.getEndpointInterface().length() > 0
				&& clazz.isInterface()) {
			throwException("EndpointInterface can't be defined on interface",
					"4.1.1", " Class : " + clazz.getName());
		}

		if (annotations.hasHandlerChainAnnotation(clazz)) {
			throwException(
					"Annotations @HandlerChain is NOT supported. Use @InHandlers instread",
					"0.0", "Class : " + clazz.getName());

		}

		Method[] methods = clazz.getMethods();
		for (int m = 0; m < methods.length; m++) {
			Method method = methods[m];
			if (annotations.hasWebMethodAnnotation(method)) {
				validateMethod(method);
			}
		}

	}

	/**
	 * @param method
	 * @throws AnnotationException
	 */
	protected void validateOneWay(Method method) throws AnnotationException {

		if (method.getExceptionTypes().length > 0
				|| !method.getGenericReturnType().equals(void.class)) {
			throwException(
					"Method annotated with @OneWay annotation cannot  have return value or declarated exceptions",
					"4.3.1", "Method : " + method.getName());
		}
		for (int p = 0; p < method.getParameterTypes().length; p++) {
			WebParamAnnotation param = annotations.getWebParamAnnotation(
					method, p);
			if (param != null
					&& (param.getMode() == WebParamAnnotation.MODE_INOUT || param
							.getMode() == WebParamAnnotation.MODE_OUT)) {
				throwException(
						"Method annotated with @OneWay annotation cannot have INOUT/OUT parameters",
						"4.3.1", "Method : " + method.getName());
			}
		}

	}

	/**
	 * @param method
	 * @throws AnnotationException
	 */
	protected void validateMethod(Method method) throws AnnotationException {

		if (!Modifier.isPublic(method.getModifiers())) {
			throwException("Method annotated with @WebMethod must be public",
					"3.3", "Method : " + method.getName());
		}

		if (annotations.hasOnewayAnnotation(method)) {
			validateOneWay(method);
		}

		if (void.class.equals(method.getReturnType())
				&& annotations.hasWebResultAnnotation(method)) {
			throwException(
					"Method with result can't have @WebResult annotations",
					"0.0", "Method : " + method.getName());
		}
		/*
		 * for (int p = 0; p < method.getParameterTypes().length; p++) {
		 * WebParamAnnotation param = annotations.getWebParamAnnotation( method,
		 * p); if (param != null && (param.getMode() ==
		 * WebParamAnnotation.MODE_INOUT || param .getMode() ==
		 * WebParamAnnotation.MODE_OUT) && !param.isHeader()) { throwException(
		 * "Parameters with mode INOUT/OUT must be marked as header=true",
		 * "4.4.1","Method : "+ method.getName()); } }
		 */

	}

	public void validate(WebAnnotations annotations, Class clazz)
			throws AnnotationException {
		this.annotations = annotations;
		validateClass(clazz);

	}

	private void throwException(String message, String spec, String details)
			throws AnnotationException {
		throw new AnnotationException(message + ". JSR181 spec : " + spec
				+ ". " + details);
	}
}
