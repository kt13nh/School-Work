package com.example.tram.calculator;

import android.widget.TextView;
import java.util.*;

public class Operators {

    String MS="0";

    //this sets the global parameter "MS" to store the value.
    void setMemory(TextView t) {
        //variable to check when the string encounters an operator and record the index
        int index=0;
        for (int i = 0; i < t.getText().length(); i++){
            char operand = t.getText().toString().charAt(i);
            index++;
            //if encountered an operator, then store everything that came before the operator
            if (operand=='('||operand==')'||operand=='/'||operand=='*'||operand=='-'||operand=='+') {
                //get the substring before the operator, and break the loop because value is found
                //if there is not only an operator on the screen, then let it equal to the value
                if(!t.getText().toString().substring(0, index-1).equals(""))
                    this.MS = t.getText().toString().substring(0, index-1);
                //otherwise, if there is only an operator on the textview and nothing else,
                //then let the memory equal be unchanged, and break loop
                break;
            }
            else{
                //otherwise, store the value if there is no operator
                this.MS=t.getText().toString();
            }
        }
    }
    //reset the memory function to the value 0
    void clearMemory(TextView t){
        this.MS ="0";
    }

    //get the value stored by the global variable "MS" and display it onto the text view
    void getMemory(TextView t){
        t.setText(this.MS);
    }

    //reset the display to 0
    void allClear(TextView t){
        t.setText("");
        t.setText("0");
    }

    //add the operator + to the display
    void add(TextView t){
        t.append("+");
    }

    //open parenthesis will replace the starting number 0 if it is the only value
    void openBracket(TextView t){
        if(t.getText().toString().equals("0")){
            t.setText("(");
        }
        else
            t.append("(");
    }

    //display a closed bracket on the textview
    void closeBracket(TextView t){
        t.append(")");
    }

    //display a "-" on the textview
    void subtract(TextView t){
        t.append("-");
    }

    //Delete the current farthest indexed value
    void delete(TextView t){
        //variable to get the substring, after deleting the farthest indexed value
        String deletedResult;

        try {
            //get the substring after deleting a value
            deletedResult = t.getText().toString().substring(0, t.getText().length() - 1);
            //if there was only one value on the textview, then reset the display back to 0
            if(deletedResult.equals("")){
                t.setText("0");
            }
            //will clear the display if pressed to clear the invalid expression output
            else if(t.getText().toString().equals("Invalid expression!")){
                t.setText("0");
            }
            else
                t.setText(deletedResult);
        }
        catch(Exception ex){};
    }

    //add divide operator to the textview
    void divide(TextView t){
        t.append("/");
    }

    //append the asterisk to the textview for multiplication
    void multiply(TextView t){
        t.append("*");
    }

    //compute function for the "=" button
    void compute(TextView t){
        //get everything that is on the textview
        String textViewText = t.getText().toString();
        //string buffer to store the values in the textview
        StringBuffer str = new StringBuffer();
        //loop through until the end of the textview characters
        for(int i=0;i<textViewText.length();i++){
            //check if it is an operator
            if(Character.toString(textViewText.charAt(i)).equals("+")||
                    Character.toString(textViewText.charAt(i)).equals("-")||
                    Character.toString(textViewText.charAt(i)).equals("*")||
                    Character.toString(textViewText.charAt(i)).equals("(")||
                    Character.toString(textViewText.charAt(i)).equals(")")||
                    Character.toString(textViewText.charAt(i)).equals("/")){
                //seperate the string buffer between the number, if there is one, with a space
                str.append(" ");
                //append the next value
                str.append(Character.toString(textViewText.charAt(i)));
                //append another space to seperate after
                str.append(" ");
            }
            else{
                //otherwise just append the number to the stringbuffer
                str.append(Character.toString(textViewText.charAt(i)));
            }
        }
        //System.out.println(str.toString());
        //reference to the string buffer that stored our expression
        textViewText = str.toString();
        try {
            //try to evaluate the expression
            double result = evaluateExpression(textViewText);
            t.setText(""+result);
        }catch(Exception ex){
            //otherwise, let the user know the expression they entered was not valid
            t.setText("Invalid expression!");
        }
    }


    //method to complete the expression formula that is entered
    public double evaluateExpression(String expression) {
        //array to store the expression entered, and be able to be looped through
        char[] values = expression.toCharArray();
        // Stack to store numbers
        Stack<Double> numbers = new Stack<Double>();
        // Stack to store operators
        Stack<Character> ops = new Stack<Character>();
        //loop through the values in the expression
        for (int i = 0; i < values.length; i++) {
            // Current value is a whitespace, skip it
            if (values[i] == ' ') {
                continue;
            }
            // Current value is a number, push it to stack for numbers
            if (values[i] >= '0' && values[i] <= '9') {
                StringBuilder str = new StringBuilder();
                // There may be more than one digits in number
                while (i < values.length && values[i] >= '0' && values[i] <= '9')
                    str.append(values[i++]);
                numbers.push(Double.parseDouble(str.toString()));
            }
            // Current token is an opening brace, push it to operators stack
            else if (values[i] == '(') {
                ops.push(values[i]);
            }
                //Solve everything between the closing parenthesis and the opening once
                //closing parenthesis is encountered
            else if (values[i] == ')') {
                while (ops.peek() != '(') {
                    numbers.push(applyOperators(ops.pop(), numbers.pop(), numbers.pop()));
                }
                ops.pop();
            }

            //if the current value is an operator
            else if (values[i] == '+' || values[i] == '-' ||
                    values[i] == '*' || values[i] == '/') {
                // While top of operators stack has same or greater precedence to current
                // value, which is an operator, apply operator on top of operators stack to top two
                //elements in the values stack
                while (!ops.empty() && (checkPrecedence(ops.peek().toString()))>=checkPrecedence(Character.toString(values[i]))) {
                    numbers.push(applyOperators(ops.pop(), numbers.pop(), numbers.pop()));
                }
                // Push current value to operators stack
                ops.push(values[i]);
            }
        }

        //try to apply the remaining operators to the numbers
        while (!ops.empty()) {
            numbers.push(applyOperators(ops.pop(), numbers.pop(), numbers.pop()));
        }
        //return the top of the numbers stack which will contain the result
        return numbers.pop();
    }

    //function to apply the operators on values a and b, and then return
    public static double applyOperators(char op, double b, double a) {
        if(op=='+') {
            return a + b;
        }
        else if(op=='-') {
            return a - b;
        }
        else if(op=='*') {
            return a * b;
        }
        else if(op=='/') {
            if (b == 0) {
                throw new UnsupportedOperationException("Cannot divide by zero");
            }
            else
                return a / b;
        }
        return 0.0;
    }

    //check the precedence of each of the operators to perform the order of operations on the expression
    int checkPrecedence(String operator){
        if(operator.equals("*"))
            return 2;
        else if(operator.equals("/"))
            return 2;
        else if(operator.equals("+"))
            return 1;
        else if(operator.equals("-"))
            return 1;
        else
            return 0;
    }
    //set the text view's value to the number selected, and ensure to set display to the number
    //after an invalid expression was tried
    void numberVal(TextView t,String number){
        //set the text view to the number entered if already 0
        if(t.getText().toString().equals("0"))
            t.setText(number);
        //if invalid expression previously, then set the display to the number
        else if(t.getText().toString().equals("Invalid expression!")){
            t.setText(number);
        }
        //if it equalled 0 already, then change it to the number that was last entered
        else if(!(t.getText().toString().equals("0"))){
            t.append(number);
        }
    }
}
