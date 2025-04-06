package uj.wmii.pwj.anns;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyTestEngine {

    private final String className;

    public static void main(String[] args) {
        displayAsciiArt();
        displayEngineAsciiArt();
        displayAsciiArt();
        if (args.length < 1) {
            System.out.println("Please specify test class name");
            System.exit(-1);
        }
        String className = args[0].trim();
        System.out.printf("Testing class: %s\n", className);
        System.out.println("=============================================================");
        MyTestEngine engine = new MyTestEngine(className);
        engine.runTests();
    }

    public MyTestEngine(String className) {
        this.className = className;
    }

    public void runTests() {
        final Object unit = getObject(className);
        List<Method> testMethods = getTestMethods(unit);
        int successCount = 0;
        int failCount = 0;
        int errorCount = 0;
        for (Method m : testMethods) {
            TestResult result = launchSingleMethod(m, unit);
            switch (result) {
                case SUCCESS -> successCount++;
                case FAIL -> failCount++;
                case ERROR -> errorCount++;
            }
        }
        System.out.printf("\033[34mEngine launched %d tests.\033[0m\n", testMethods.size());
        System.out.printf("\033[32m%d passed, \033[31m%d failed, \033[0m%d errors.\n", successCount, failCount, errorCount);
    }

    private TestResult launchSingleMethod(Method m, Object unit) {
        try {
            MyTest annotation = m.getAnnotation(MyTest.class);
            Object[] params = parseParameters(annotation);
            Object expected = parseExpectedResult(annotation);

            if (expected != null && m.getReturnType() == Void.TYPE) {
                handleTestError(m, "Method is void and is expected");
                return TestResult.ERROR;
            }
            if (expected == null && m.getReturnType() != Void.TYPE) {
                handleTestError(m, "Method is not void and expected is null");
                return TestResult.ERROR;
            }

            if (params.length != m.getParameterCount()) {
                handleTestError(m, "Expects different number of parameters");
                return TestResult.ERROR;
            }

            return executeTest(m, unit, params, expected);

        } catch (ReflectiveOperationException e) {
            if (e.getCause() instanceof AssertionError) {
                printTestResult(m, TestResult.FAIL);
                System.out.println(e.getCause().getMessage());
                System.out.println("--------------------------------------------------------------");
                return TestResult.FAIL;
            } else {
                handleTestError(m, "Exception");
                //e.printStackTrace();
                return TestResult.ERROR;
            }
        }
    }

    private TestResult executeTest(Method m, Object unit, Object[] params, Object expected) throws ReflectiveOperationException {
        Object result = m.invoke(unit, params);
        if (expected == null) {
            printTestResult(m, TestResult.SUCCESS);
            System.out.println("--------------------------------------------------------------");
            return TestResult.SUCCESS;
        }
        if (result.equals(expected)) {
            printTestResult(m, TestResult.SUCCESS);
            System.out.println("--------------------------------------------------------------");
            return TestResult.SUCCESS;
        } else {
            printTestResult(m, TestResult.FAIL);
            System.out.printf("[Params: %s] Expected: %s, Actual: %s\n",
                        Arrays.toString(params), expected, result);
            System.out.println("--------------------------------------------------------------");
            return TestResult.FAIL;
        }
    }

    private Object[] parseParameters(MyTest annotation) {
        if (annotation == null) return new Object[]{};

        Object[] intParams = Arrays.stream(annotation.intParams()).boxed().toArray();
        Object[] strParams = annotation.strParams();

        return combineArrays(intParams, strParams);
    }

    private Object parseExpectedResult(MyTest annotation) {
        if (annotation == null) return null;

        if (annotation.intExpected().length > 0) {
            return annotation.intExpected()[0];
        } else if (annotation.strExpected().length > 0) {
            return annotation.strExpected()[0];
        }
        return null;
    }

    private Object[] combineArrays(Object[]... arrays) {
        return Arrays.stream(arrays).flatMap(Arrays::stream).toArray();
    }

    private void handleTestError(Method m, String message) {
        printTestResult(m, TestResult.ERROR);
        System.out.println(message);
        System.out.println("--------------------------------------------------------------");
    }

    private static void printTestResult(Method testedMethod, TestResult result) {
        System.out.printf("Tested method: %s, result: %s\n", testedMethod.getName(), result);
    }

    private static List<Method> getTestMethods(Object unit) {
        Method[] methods = unit.getClass().getDeclaredMethods();
        return Arrays.stream(methods)
                .filter(m -> m.isAnnotationPresent(MyTest.class))
                .collect(Collectors.toList());
    }

    private static Object getObject(String className) {
        try {
            Class<?> unitClass = Class.forName(className);
            return unitClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return new Object();
        }
    }

    public static void displayEngineAsciiArt() {
        System.out.println("\033[35m" +
                " _______ ______  _____ _______   ______ _   _  _____ _____ _   _ ______ \n" +
                "|__   __|  ____|/ ____|__   __| |  ____| \\ | |/ ____|_   _| \\ | |  ____|\n" +
                "   | |  | |__  | (___    | |    | |__  |  \\| | |  __  | | |  \\| | |__   \n" +
                "   | |  |  __|  \\___ \\   | |    |  __| | . ` | | |_ | | | | . ` |  __|  \n" +
                "   | |  | |____ ____) |  | |    | |____| |\\  | |__| |_| |_| |\\  | |____ \n" +
                "   |_|  |______|_____/   |_|    |______|_| \\_|\\_____|_____|_| \\_|______| \n" +
                "\033[0m");
    }


    public static void displayAsciiArt() {
        System.out.println("\033[36m" +
                " ______ ______ ______ ______ ______ ______ ______ ______ ______ ______ ______ \n" +
                "|______|______|______|______|______|______|______|______|______|______|______|\n" +
                "                                                                                  " + "\033[0m");
    }
}
