package org.codehaus.xfire.gen.documentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.service.documentation.XMLDocumentationBuilder;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

/**
 * Generate configuration based on javadoc, used for inserting wsdl:documentation tags
 *
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a>
 *
 *
 */
public class DocumentationConfigGen {

	
    private static final Log LOG  = LogFactory.getLog(DocumentationConfigGen.class);

	private String[] srcFiles;

	private String outputFolder = ".";

	/**
	 * Generates documentation config file for given classes
	 */
	public void generate() {

		if (srcFiles == null || srcFiles.length == 0) {
			System.out.print("No files specified\n");
			return;
		}

        System.out.print("Files to document:");
        for( int i=0;i<srcFiles.length;i++){
            System.out.print(srcFiles[i]+"\n");
        }
        System.out.print("\n Output directory : "+ getOutputFolder() +"\n");
        
		try {
			for (int i = 0; i < srcFiles.length; i++) {
				createDocumentationConfiguration(srcFiles[i]);
			}
		} catch (NoClassDefFoundError e) {
			LOG.error(e);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			LOG.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			LOG.equals(e);
			e.printStackTrace();
		}

	}

	/**
	 * @param parent
	 * @param child
	 * @return
	 */
	private String addSubfolder(String parent, String child) {
		if (!parent.endsWith("/")) {
			parent = parent + "/";
		}
		return parent + child;
	}

	/**
	 * @param srcFile - jakis param
	 * @return jakis string
	 * @throws NoClassDefFoundError - wywalka
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void createDocumentationConfiguration(String srcFile) throws NoClassDefFoundError, FileNotFoundException,
			IOException {

		JavaDocBuilder builder = new JavaDocBuilder();
		File file = new File(srcFile);

		builder.addSource(file);
		JavaSource src = builder.getSources()[0];
		JavaClass clazz = src.getClasses()[0];
		// Build document with documention data
		Document doc = generateClassDocumentation(clazz);
		// Create path from package name
		String name = clazz.getFullyQualifiedName().replaceAll("\\.", "/");
		name = name + XMLDocumentationBuilder.CONFIG_SUFIX;
		name = addSubfolder(outputFolder, name);

		File targetFile = new File(name);
		// Create folders is neccesery
		createOutputFolders(clazz.getPackage());
		// Write config file
		XMLOutputter outputer = new XMLOutputter();
		FileWriter writer = new FileWriter(targetFile);
		outputer.setFormat(Format.getPrettyFormat());
		outputer.output(doc, writer);
		writer.close();
	}

	/**
	 * @param tag
	 * @param types
	 * @return
	 */
	private Element createExceptionElement(DocletTag tag, Type[] types) {

		String value = tag.getValue().trim();
		// Check if we have any comment on that expection
		int idx = value.indexOf(' ');
		if (idx < 0) {
			return null;
		}

		// parse tag value
		String exceptionName = value.substring(0, idx);
		String exceptionComment = value.substring(idx + 1);

		String exceptionFullName = getExcFullName(types, exceptionName);

		Element exc = new Element(XMLDocumentationBuilder.EXCEPTION_TAG);
		exc.setAttribute(XMLDocumentationBuilder.CLASS_ATTR, exceptionFullName);
		return exc.addContent(newDocElement(exceptionComment));

	}

	/**
	 * @param text
	 * @return
	 */
	private Element newDocElement(String text) {
		return new Element(XMLDocumentationBuilder.DOCUMENTATION_TAG).setText(text);
	}

	/**
	 * @param method
	 * @return
	 */
	private Element generateMethod(JavaMethod method) {
		// Document only public not static methods
		if (!method.isPublic() || method.isStatic()) {
			return null;
		}

		// create method element
		Element methodElement = new Element(XMLDocumentationBuilder.METHOD_TAG);
		methodElement.setAttribute(XMLDocumentationBuilder.NAME_ATTR, method.getName());
		methodElement.setAttribute(XMLDocumentationBuilder.ARGUMENTS_NUMBER_ATTR, String
				.valueOf(method.getParameters().length));

		// process method comment
		String comment = method.getComment();
		if (comment != null && !"".equals(comment)) {
			methodElement.addContent(newDocElement(comment));
		}

		// process paramters
		JavaParameter paramters[] = method.getParameters();
		DocletTag paramsTag[] = method.getTagsByName("param");
		if (paramsTag != null) {
			for (int p = 0; p < paramsTag.length; p++) {
				Element param = createParameterElement(paramsTag[p],paramters);
				if( param != null){
					methodElement.addContent(param);
				}
			}
		}



		// process return
		DocletTag returnTag = method.getTagByName("return");
		if (returnTag != null) {
			String returnStr = returnTag.getValue().trim();
			if (returnStr.length() > 0) {
				Element returnElement = new Element(XMLDocumentationBuilder.RETURN_TAG);
				returnElement.addContent(newDocElement(returnStr));
				methodElement.addContent(returnElement);
			}
		}

		// process throwns
		DocletTag throwsTag[] = method.getTagsByName("throws");
		Type[] types = method.getExceptions();
		if (throwsTag != null) {
			for (int t = 0; t < throwsTag.length; t++) {
				Element exceptionElment = createExceptionElement(throwsTag[t], types);
				if (exceptionElment != null) {
					methodElement.addContent(exceptionElment);
				}
			}
		}

		if( methodElement.getChildren().size() == 0 )
			return null;
		return methodElement;
	}

	/**
	 * @param tag
	 * @param paramters
	 * @return
	 */
	private Element createParameterElement(DocletTag tag, JavaParameter parameters[]) {
		String value = tag.getValue().trim();
		// Check if we have any comment
		int idx = value.indexOf(' ');
		if (idx < 0) {
			return null;
		}

		// parse tag value
		String paramName = value.substring(0, idx);
		String paramComment = value.substring(idx + 1);
		int paramIdx = -1;

		for (int i = 0; i < parameters.length; i++) {
			if (paramName.equals(parameters[i].getName())) {
				paramIdx = i;
				break;
			}
		}
		// param name from javadoc doesn't match any real paramter
		if( paramIdx < 0 ){
			return null;
		}

		Element param = new Element(XMLDocumentationBuilder.PARAMTER_TAG);
		param.setAttribute(XMLDocumentationBuilder.INDEX_ATTR, String.valueOf(paramIdx));
		return param.addContent(newDocElement(paramComment));
	}

	/**
	 *
	 * @param types
	 * @param localName - exception classname
	 * @return full name of exception ( package included )
	 */
	private String getExcFullName(Type types[], String localName) {
		for (int i = 0; i < types.length; i++) {
			Type type = types[i];
			String fullname = type.getValue();
			if (fullname.endsWith(localName)) {
				return fullname;
			}
		}
		return null;
	}

	/**
	 * Creates folder structure based on class package
	 * @param path
	 */
	private void createOutputFolders(String path) {
		StringTokenizer tokenizer = new StringTokenizer(path, ".");
		String pathLevel = getOutputFolder();

		while (tokenizer.hasMoreTokens()) {
			String folder = tokenizer.nextToken();
			pathLevel = addSubfolder(pathLevel, folder);
			File file = new File(pathLevel);
			if (!file.exists()) {
				file.mkdir();
			}
		}
	}

	/**
	 * Creates document with documentation data from class javadocs
	 * @param clazz
	 * @return
	 */
	private Document generateClassDocumentation(JavaClass clazz) {
		Document document = new Document();
		Element service = new Element("service");
		document.addContent(service);
		// process class commment
		if (!"".equals(clazz.getComment())) {
			service.addContent(newDocElement(clazz.getComment()));
		}
		// process methods
		JavaMethod methods[] = clazz.getMethods();
		for (int m = 0; m < methods.length; m++) {
			Element methodElem = generateMethod(methods[m]);
			if (methodElem != null) {
				service.addContent(methodElem);
			}
		}
		return document;

	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	public String[] getSrcFiles() {
		return srcFiles;
	}

	public void setSrcFiles(String[] srcFiles) {
		this.srcFiles = srcFiles;
	}

	public static void main(String[] args) throws Exception {
		DocumentationConfigGen gen = new DocumentationConfigGen();
		gen.setOutputFolder(".");
        gen.setSrcFiles(new String[]{"/home/tomeks/dev/xfire/examples/book/src/main/org/codehaus/xfire/demo/BookService.java"});
        gen.generate();
		//gen.createDocumentationConfiguration("/home/tomeks/dev/xfire/examples/book/src/main/org/codehaus/xfire/demo/BookService.java");

	}

}
