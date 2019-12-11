package compiler.ndk;

import compiler.ndk.codebuilder.CodeBuilder;
import compiler.ndk.codebuilder.CodeBuilderImpl;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        File file = new File(s + "\\prog.ndk");
        CodeBuilder codeBuilder = CodeBuilderImpl.newInstance(file);
        if (codeBuilder != null) {
            codeBuilder.main();
        }

    }

}