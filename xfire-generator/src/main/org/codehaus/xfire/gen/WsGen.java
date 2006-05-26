package org.codehaus.xfire.gen;

import org.apache.tools.ant.BuildException;

/**
 * @author <a href="mailto:tsztelak@gmail.com">Tomasz Sztelak</a> Helper class
 *         which allows to run wsgen from command line.
 * 
 */
public class WsGen {

	private static void usage() {
		System.out
				.print("Usage: wsgen -wsdl wsdl.file -o outputDirectory [-p package] [-b binding] [-r profile] [-e externalBinging] [-u baseURI]\n");
	}

	private static void missingParam(String param) {
		System.out.print("Missing param : " + param + "\n");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String _package = null;
		String outputDirectory = null;
		String wsdl = null;
		String binding = null;
		String profile = null;
		String externalBindings = null;
		String baseURI = null;
		
		if (args.length < 3) {
			usage();
			return;
		}
		for (int i = 0; i < args.length; i += 2) {
			String param = args[i];
			String value = args[i + 1];
			param = param.toLowerCase().trim();
			value = value.trim();
			if ("-wsdl".equals(param)) {
				wsdl = value;
			}
			if ("-o".equals(param)) {
				outputDirectory = value;
			}
			if ("-p".equals(param)) {
				_package = value;
			}
			if ("-b".equals(param)) {
				binding = value;
			}
			if("-e".equals(param)){
				externalBindings = value;
			}
			if("-u".equals(param)){
				baseURI = value;
			}
			if("-r".equals(param)){
				profile = value;
			}
		}

		if (wsdl == null) {
			missingParam("wsdl");
			usage();
			return;
		}
		if (outputDirectory == null) {
			outputDirectory = ".";
			System.out
					.print("Output directory not specified. Using current.\n");
		}
		System.out.print("Running WsGen...\n");
		System.out.print("wsdl    : " + wsdl + "\n");
		System.out.print("package : " + _package + "\n");
		System.out.print("output  : " + outputDirectory + "\n");
		System.out.print("binding : " + (binding==null?"":binding) + "\n");
		System.out.print("externalBindings : " + (externalBindings == null?"" : externalBindings) + "\n" );
		System.out.print("baseURI : " + (baseURI == null?"" : baseURI)+ "\n");
		System.out.print("profile : " + (profile == null?"" : profile)+ "\n");
		
		

		Wsdl11Generator generator = new Wsdl11Generator();
		generator.setDestinationPackage(_package);
		generator.setOutputDirectory(outputDirectory);
		generator.setWsdl(wsdl);

		if (binding != null)
			generator.setBinding(binding);
		if (profile != null)
			generator.setProfile(profile);
		
		if( baseURI!= null )
			generator.setBaseURI(baseURI);
			
		if(externalBindings != null )
			generator.setExternalBindings(externalBindings);
			

		try {
			generator.generate();
			System.out.print("Done.\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

	}

}
