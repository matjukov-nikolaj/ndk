package compiler.ndk;

import compiler.ndk.codebuilder.Codelet;
import compiler.ndk.codebuilder.CodeletBuilder;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {


	public static void main(String[] args) throws Exception{
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		File file = new File(s + "\\prog.ndk");
		Codelet codelet = CodeletBuilder.newInstance(file);
		if (codelet != null) {
			codelet.execute();
		}

			
	}
	
}