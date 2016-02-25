import java.io.*;
import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.List;

public class Wrapper {
    private static String src;
    private static Class<?> source;
    private static Method[] methods;
    private static Constructor[] constructors;
    private static Class<?>[] interfaces;

    private File newClass;
    private Writer writer;
    private StringBuilder output = new StringBuilder();
    private List<String> newmethods;

    private String[] types = {"String", "Integer", "boolean", "void"};
    private String[] userTypes = {"Key", "Value"};
    private boolean hasGenerics;


    public Wrapper(String name) throws ClassNotFoundException, SecurityException, UnsupportedClassVersionError {
        src = name;
        newClass = new File(name + "Wrapped.java");
        newmethods = new LinkedList<String>();

        try {
            source = Class.forName(src);
            methods = source.getDeclaredMethods();
            constructors = source.getConstructors();
            interfaces = source.getInterfaces();
        } catch (ClassNotFoundException exception) {
            throw new ClassNotFoundException("Brak klasy");
        } catch (SecurityException exception) {
            throw new SecurityException("Brak dostepu");
        }

        if(userTypes.length == 0)
            hasGenerics = false;
        else
            hasGenerics = true;

        if(interfaces.length == 0)
            throw new UnsupportedClassVersionError("Brak dostepnych interfejsow");
    }


    public void writeToFile() throws IOException, NullPointerException {
        //Imports
        output.append("import java.util.*;\n");
        output.append("import java.io.*;\n");
        output.append("import java.util.logging.*;\n\n");

        //Header
        if (hasGenerics) {
            if (interfaces.length == 0)
                output.append("public class " + src + "Wrapped" + "<" + userTypes[0] + ", " + userTypes[1] + "> {\n");
            else {
                output.append("public class " + src + "Wrapped" + "<" + userTypes[0] + ", " + userTypes[1] + "> implements ");
                for (Class<?> iface : interfaces) {
                    output.append(iface.toString().substring(10) + "<" + userTypes[0] + ", " + userTypes[1] + ">, ");
                }
                output.delete(output.length() - 2, output.length());
                output.append(" {\n");
            }
        } else {
            if (interfaces.length == 0)
                output.append("public class " + src + "Wrapped {\n");
            else {
                output.append("public class " + src + "Wrapped implements ");
                for (Class<?> iface : interfaces) {
                    output.append(iface.toString().substring(10) + ", ");
                }
                output.delete(output.length() - 2, output.length());
                output.append(" {\n");
            }
        }

        //Proxy & logger
        if(hasGenerics)
            output.append("\tprivate " + interfaces[0].toString().substring(10) + "<" + userTypes[0] + ", " + userTypes[1] + "> proxied;\n");
        else
            output.append("\tprivate " + interfaces[0].toString().substring(10) + " proxied;\n");
        output.append("\tprivate static Logger logger;\n");
        output.append("\tprivate static FileHandler fileHandler;\n\n");

        //Static block
        output.append("\tstatic {\n");
        output.append("\t\tlogger = Logger.getLogger(" + src + ".class.getName());\n");
        output.append("\t\ttry {\n");
        output.append("\t\t\tfileHandler = new FileHandler(\"log.log\", false);\n");
        output.append("\t\t} catch (SecurityException exception) {\n");
        output.append("\t\t\texception.printStackTrace();\n");
        output.append("\t\t} catch (IOException exception) {\n");
        output.append("\t\t\texception.printStackTrace();\n");
        output.append("\t\t}\n\n");
        output.append("\t\tfileHandler.setFormatter(new SimpleFormatter());\n");
        output.append("\t\tlogger.addHandler(fileHandler);\n");
        output.append("\t\tlogger.setLevel(Level.CONFIG);\n");
        output.append("\t}\n\n");

        //Constructor
        output.append("\tpublic " + src + "Wrapped(" + interfaces[0].toString().substring(10) + "<" + userTypes[0] + ", " + userTypes[1] + "> proxied) {\n");
        output.append("\t\tthis.proxied = proxied;\n");
        output.append("\t}\n\n");

        //Methods
        boolean check1, check2;
        StringBuilder methodheader = new StringBuilder();
        String methodname = new String();
        String returnedtype = new String();
        for (Method method : methods) {
            if (method.toString().contains("private") || method.toString().contains("main"))
                continue;

            check1 = false;
            for (String existing : newmethods) {
                if (method.toString().contains(existing))
                    check1 = true;
            }
            if(check1)
                continue;

            check2 = true;
            for (String type : types) {
                if (method.getReturnType().toString().contains(type)) {
                    methodheader.append("\tpublic " + type + " ");
                    check2 = false;
                    returnedtype = method.getReturnType().getSimpleName().toString();
                    break;
                }
            }
            if (check2) {
                if(hasGenerics) {
                    if (method.toString().contains("remove") || (method.toString().contains("put")) || (method.toString().contains("get"))) {
                        methodheader.append("\tpublic " + userTypes[1] + " ");
                        returnedtype = userTypes[1];
                    } else {
                        methodheader.append("\tpublic " + userTypes[0] + " ");
                        returnedtype = userTypes[0];
                    }
                }
                else {
                    methodheader.append("\tpublic " + method.getReturnType().getSimpleName().toString() + " ");
                    returnedtype = method.getReturnType().getSimpleName().toString();
                }
            }
            methodheader.append(method.toString().substring(method.toString().indexOf(src.charAt(src.length() - 1), 10) + 2, method.toString().indexOf("(")) + "(");
            methodname = method.toString().substring(method.toString().indexOf(src.charAt(src.length() - 1), 10) + 1, method.toString().indexOf("("));
            newmethods.add(methodname);

            check1 = false;
            for (int i = 0; i < method.getParameterCount(); i++) {
                if (i == 0) {
                    methodheader.append(userTypes[i] + " arg" + i + ", ");
                } else {
                    if (i == 1)
                        methodheader.append(userTypes[i] + " arg" + i + ", ");
                    else
                        methodheader.append("Object arg" + i + ", ");
                }
                check1 = true;
            }
            if (check1)
                methodheader.delete(methodheader.length() - 2, methodheader.length());
            methodheader.append(") throws RuntimeException {\n");
            output.append(methodheader);

            //Method's body
            output.append("\t\t" + returnedtype + " result;\n");
            output.append("\t\ttry {\n");
            output.append("\t\t\tlogger.log(Level.INFO, \"" + method.getName() + "\");\n");
            output.append("\t\t\tlong startT = System.nanoTime();\n");
            output.append("\t\t\tresult = " + " proxied" + methodname + "(");

            check1 = false;
            for (int i = 0; i < method.getParameterCount(); i++) {
                output.append("arg" + i + ", ");
                check1 = true;
            }
            if (check1)
                output.delete(output.length() - 2, output.length());

            output.append(");\n");
            output.append("\t\t\tlong endT = System.nanoTime();\n");
            output.append("\t\t\tlong executionT = endT - startT;\n");
            output.append("\t\t\tlogger.log(Level.INFO, executionT + \" ns\");\n");
            output.append("\t\t} catch (Exception exception) {\n");
            output.append("\t\t\texception.printStackTrace();\n");
            output.append("\t\t\tthrow new RuntimeException(\"Niespodziewany wyjatek: \" + exception.getMessage());\n");
            output.append("\t\t}\n\n");
            output.append("\t\treturn result;\n");

            output.append("\t}\n\n");
            methodheader.setLength(0);
        }

        output.delete(output.length() - 1, output.length());
        output.append("}");


        try {
            writer = new FileWriter(newClass);
            writer.write(output.toString());
            writer.flush();
        } catch (IOException exception) {
            throw new IOException("Blad przy zapisie do pliku");
        } finally {
            try {
                writer.close();
            } catch (IOException exception) {
                throw new IOException("Blad przy zamykaniu pliku");
            }
        }
    }

    public static void main(String[] args) {
        Wrapper wrappedclass = null;
        boolean check = false;

        try {
            wrappedclass = new Wrapper("skiplistv2");
            wrappedclass.writeToFile();
        } catch (ClassNotFoundException exception) {
            System.out.println(exception);
            check = true;
        } catch (SecurityException exception) {
            System.out.println(exception);
            check = true;
        } catch (UnsupportedClassVersionError exception) {
            System.out.println(exception);
            check = true;
        } catch (IOException exception) {
            System.out.println(exception);
            check = true;
        } finally {
            if (check)
                System.exit(0);
        }
    }
}
