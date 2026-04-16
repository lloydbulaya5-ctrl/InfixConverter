import java.util.Stack;
import java.util.Scanner;

/**
 * InfixConverter
 * Converts an infix expression to both Postfix (RPN) and Prefix notation.
 *
 * Supported operators : + - * / ^
 * Supported operands  : single letters (A-Z, a-z) and single digits (0-9)
 * Parentheses         : ( )
 */
public class InfixConverter {

    // ---------------------------------------------------------------
    // Returns operator precedence (higher = binds tighter)
    // ---------------------------------------------------------------
    private static int precedence(char op) {
        switch (op) {
            case '^': return 3;
            case '*':
            case '/': return 2;
            case '+':
            case '-': return 1;
            default:  return 0;
        }
    }

    // ---------------------------------------------------------------
    // Returns true if the character is an operand (letter or digit)
    // ---------------------------------------------------------------
    private static boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);
    }

    // ---------------------------------------------------------------
    // Returns true if the character is a recognised operator
    // ---------------------------------------------------------------
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    // ---------------------------------------------------------------
    // Converts an infix string to POSTFIX using Shunting-Yard algorithm
    // ---------------------------------------------------------------
    public static String toPostfix(String infix) {
        StringBuilder output = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < infix.length(); i++) {
            char token = infix.charAt(i);

            // Skip spaces
            if (token == ' ') continue;

            if (isOperand(token)) {
                // Operands go straight to output
                output.append(token);

            } else if (token == '(') {
                // Left parenthesis: push onto stack
                stack.push(token);

            } else if (token == ')') {
                // Right parenthesis: pop until matching '('
                while (!stack.isEmpty() && stack.peek() != '(') {
                    output.append(stack.pop());
                }
                if (!stack.isEmpty()) {
                    stack.pop(); // remove the '('
                }

            } else if (isOperator(token)) {
                // Operator: pop operators with higher/equal precedence first
                // Note: '^' is right-associative, so use strict '>' for it
                while (!stack.isEmpty()
                        && stack.peek() != '('
                        && isOperator(stack.peek())
                        && (token == '^'
                        ? precedence(stack.peek()) > precedence(token)
                        : precedence(stack.peek()) >= precedence(token))) {
                    output.append(stack.pop());
                }
                stack.push(token);
            }
        }

        // Pop all remaining operators from the stack
        while (!stack.isEmpty()) {
            output.append(stack.pop());
        }

        return output.toString();
    }

    // ---------------------------------------------------------------
    // Converts an infix string to PREFIX
    // Strategy:
    //   1. Reverse the infix expression
    //   2. Swap '(' <-> ')' in the reversed string
    //   3. Apply toPostfix on the modified string
    //   4. Reverse the postfix result → prefix
    // ---------------------------------------------------------------
    public static String toPrefix(String infix) {
        // Step 1 & 2: reverse and swap brackets
        StringBuilder reversed = new StringBuilder();
        for (int i = infix.length() - 1; i >= 0; i--) {
            char c = infix.charAt(i);
            if (c == '(')       reversed.append(')');
            else if (c == ')')  reversed.append('(');
            else                reversed.append(c);
        }

        // Step 3: apply postfix algorithm on reversed expression
        String postfixOfReversed = toPostfix(reversed.toString());

        // Step 4: reverse the result to get prefix
        return new StringBuilder(postfixOfReversed).reverse().toString();
    }

    // ---------------------------------------------------------------
    // Main: drives the program interactively
    // ---------------------------------------------------------------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("==============================================");
        System.out.println("   Infix  →  Postfix  &  Prefix  Converter   ");
        System.out.println("==============================================");
        System.out.println("Supported operators : + - * / ^");
        System.out.println("Operands            : letters (A-Z) or digits");
        System.out.println("Type 'exit' to quit.\n");

        // Built-in demo examples
        String[] demos = {
                "A+B*C",
                "(A+B)*C",
                "A+B*C-D/E",
                "A^B^C",
                "(A+B)*(C-D)",
                "A*(B+C)/D-E"
        };

        System.out.println("--- Demo expressions ---");
        System.out.printf("%-25s %-20s %-20s%n", "Infix", "Postfix", "Prefix");
        System.out.println("-".repeat(65));
        for (String expr : demos) {
            System.out.printf("%-25s %-20s %-20s%n",
                    expr, toPostfix(expr), toPrefix(expr));
        }

        System.out.println("\n--- Enter your own expressions ---");
        while (true) {
            System.out.print("\nInfix expression: ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (input.isEmpty()) {
                System.out.println("Please enter a non-empty expression.");
                continue;
            }

            try {
                String postfix = toPostfix(input);
                String prefix  = toPrefix(input);

                System.out.println("  Postfix : " + postfix);
                System.out.println("  Prefix  : " + prefix);
            } catch (Exception e) {
                System.out.println("  Error: " + e.getMessage() +
                        " — check that your expression is well-formed.");
            }
        }

        sc.close();
    }
}