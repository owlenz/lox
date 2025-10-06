package com.owlenz.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    private static Interpreter inter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    static int exitCode = 0;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("jlox");
            System.exit(64);
        } else if (args.length == 1) {
            readFile(args[0]);
        } else {
            repl();
        }
    }

    private static void readFile(String path) throws IOException {
        byte[] Bytes = Files.readAllBytes(Paths.get(path));
        run(new String(Bytes, Charset.defaultCharset()));

        if (hadError) 
            System.exit(65);
        if(hadRuntimeError)
            if (exitCode > 0)
                System.exit(exitCode);
            else
                System.exit(70);

    }

    private static void repl() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            run(line);
            hadError = false;
        }
    }

    private static void printAst(Expr exprAst) {
        AstPrinter printer = new AstPrinter();
        System.out.println(printer.print(exprAst));
    }

    private static void run(String source) throws IOException {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        if(hadError) return;

        // printAst(exprAst);

        inter.interpret(statements);
    }

    static void error(Token token, String message) {
        if(token.type != TokenType.EOF)
            report(token.line, " at '" + token.lexeme + "'", message);
        else
            report(token.line, "", message);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void runtimeError(RuntimeError err) {
        System.err.printf("%s\n[line %d]\n", err.getMessage(), err.token.line);
        exitCode = err.signal != null ? err.signal.getNumber() + 128 : 0;
        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        System.err.printf("%d: error: %s\n", line, message);
        hadError = true;
    }
}
