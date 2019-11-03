package compiler.ndk;

import compiler.ndk.codebuilder.CodeBuilder;
import compiler.ndk.codebuilder.CodeBuilderImpl;

import java.io.File;

public class Main {
	
	public static void main(String[] args) throws Exception{

		File file = new File("D:\\arithmetic-compiler\\ndk\\ndk-compiler\\prog.ndk");
		CodeBuilder codeBuilder = CodeBuilderImpl.newInstance(file);
		if (codeBuilder != null) {
			codeBuilder.execute();
		}

			
	}
	
}