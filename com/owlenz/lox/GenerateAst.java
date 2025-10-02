package com.owlenz.lox;

import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ArrayList;

public class GenerateAst {
    public static void main() throws IOException {
        List<String> rules = new ArrayList<String>(
                Arrays.asList(
                        "Literal : Object value",
                        "Unary   : Token operator, Expr exp",
                        "Binary  : Expr exp, Token op, Expr exp1",
                        "Paren   : Expr exp"));
        generateExpr(rules);
    };

    private static void generateExpr(List<String> rules) throws IOException {

        String path = "./com/owlenz/lox/Expr.java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.printf("package com.owlenz.lox;\n\n");

        // writer.printf("\n\n");



        writer.printf("abstract class Expr {\n\n");


        writer.printf("\tinterface Visitor<R> {\n");
        for (String rule : rules) {
            String ruleName = rule.split(":")[0].trim();
            writer.printf("\t\tR visit%s(%s %s);\n", ruleName, ruleName, ruleName.toLowerCase());
        }
        writer.printf("\t}\n\n");

        writer.printf("\tabstract <R> R accept(Visitor<R> visitor);\n\n");
        for (String rule : rules) {
            String ruleName = rule.split(":")[0].trim();
            String para = rule.split(":")[1].trim();
            String vars[] = para.split(",");

            writer.printf("\tstatic class %s extends Expr{\n", ruleName);

            for (String var : vars) {
                writer.printf("\t\tfinal %s;\n", var.trim());
            }
            writer.printf("\n");

            // Constructor start
            writer.printf("\t\t%s(%s){\n", ruleName, para);
            for (String var : vars) {
                String varName = var.trim().split(" ")[1];
                writer.printf("\t\t\tthis.%s = %s;\n", varName.trim(), varName.trim());
            }
            writer.printf("\t\t}\n\n");
            // end

            // accept start
            writer.printf("\t\t@Override\n");
            writer.printf("\t\t<R> R accept(Visitor<R> visitor) {\n");
            writer.printf("\t\t\treturn visitor.visit%s(this);\n", ruleName.trim(), ruleName.trim());
            writer.printf("\t\t}\n\n");
            // end

            writer.printf("\t}\n");
        }

        writer.printf("}\n");

        writer.close();

    }
}
