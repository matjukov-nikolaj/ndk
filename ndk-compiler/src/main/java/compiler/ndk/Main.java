package compiler.ndk;

import compiler.ndk.codebuilder.Codelet;
import compiler.ndk.codebuilder.CodeletBuilder;

import java.io.File;

public class Main {
	
	public static void main(String[] args) throws Exception{

		File file = new File("D:\\arithmetic-compiler\\ndk\\prog.ndk");
		Codelet codelet = CodeletBuilder.newInstance(file);
		if (codelet != null) {
			codelet.execute();
		}
			
	}
	
}