package proj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MediumCalc extends JFrame {
    private JButton compute = new JButton("=");
    private JButton number0Button = new JButton("0");
    private JButton addButton = new JButton("+");
    private JButton subtractButton = new JButton("-");
    private JButton multiplyButton = new JButton("*");
    private JButton divideButton = new JButton("/");
    private JButton commaButton = new JButton(",");
    private JButton clearButton = new JButton("C");
    private JButton[] equationButtons;
    private JButton[][] numberButtons;
    private JTextField resultField = new JTextField();
    private boolean commaAdded = false;

    public MediumCalc(String title) throws HeadlessException {
        super(title);
        initFrame();
        initComponents();
        initActionListeners();
        pack(); // dopasowanie rozmiaru do zawartości
    }

    private void initFrame() { // setup okna
        setLocationRelativeTo(null); // pozycja centralnie względem okna
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // krzyżyk powoduje zamknięcie
        setResizable(false); // nie można zmieniać rozmiaru
        setVisible(true); // okno widoczne
    }

    private void initComponents() {
        Container panel = getContentPane();
        GridBagLayout gridBagLayout = new GridBagLayout(); // layout ustawiający w jednym rzędzie
        panel.setLayout(gridBagLayout);
        initEquationButtonsArray();

        initNumberButtons();
        initComponentsPosition(panel);

        disableCompute();
        resultField.setEditable(false); // nie można edytować okna wyniku
        resultField.setHorizontalAlignment(JTextField.CENTER); // centruję wynik
    }

    private void initEquationButtonsArray() {
        equationButtons = new JButton[4];
        equationButtons[0] = divideButton;
        equationButtons[1] = multiplyButton;
        equationButtons[2] = subtractButton;
        equationButtons[3] = addButton;
    }

    private void initComponentsPosition(Container panel) {
        GridBagConstraints constraints = new GridBagConstraints();
        initNumbersPosition(panel, constraints);
        initOtherComponentsPosition(panel, constraints);
    }

    private void initOtherComponentsPosition(Container panel, GridBagConstraints constraints) {
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 4;
        panel.add(resultField, constraints);
        constraints.gridwidth = 1;
        constraints.gridx = 3;
        constraints.gridy = 1;
        panel.add(divideButton, constraints);
        constraints.gridy = 2;
        panel.add(multiplyButton, constraints);
        constraints.gridy = 3;
        panel.add(subtractButton, constraints);
        constraints.gridy = 4;
        panel.add(addButton, constraints);
        constraints.gridx = 1;
        panel.add(number0Button, constraints);
        constraints.gridx = 2;
        panel.add(commaButton, constraints);
        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridwidth = 4;
        panel.add(compute, constraints);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        constraints.fill = GridBagConstraints.VERTICAL;
        panel.add(clearButton, constraints);
    }

    private void initNumbersPosition(Container panel, GridBagConstraints constraints) {
        for (int row = 0; row < numberButtons.length; row++) {
            for (int col = 0; col < numberButtons[row].length; col++) {
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.gridx = col;
                constraints.gridy = row + 1;
                panel.add(numberButtons[row][col], constraints);
            }
        }
    }

    private void initNumberButtons() {
        numberButtons = new JButton[3][3];
        int buttonNr = 1;
        for (int row = numberButtons.length - 1; row >= 0; row--) {
            for (int col = 0; col < numberButtons.length; col++) {
                numberButtons[row][col] = new JButton(buttonNr++ + "");
            }
        }
    }

    private void initActionListeners() {
        initNumbersActionListeners();
        initClearActionListener();
        initCommaActionListener();
        addEquationSymbolActionListeners();
        addComputeActionListener();

    }

    private void addEquationSymbolActionListeners() {
        for (JButton equationButton : equationButtons) {
            equationButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    resultField.setText(resultField.getText() + " " + button.getText() + " ");
                    equationSymbolDisable();
                    enableCompute();
                    enableComma();
                }
            });
        }
    }

    private void equationSymbolDisable() {
        for (JButton equationButton : equationButtons) {
            equationButton.setEnabled(false);
        }
    }

    private void enableEquationSymbols() {
        for (JButton equationButton : equationButtons) {
            equationButton.setEnabled(true);
        }
    }

    private void initClearActionListener() {
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultField.setText("");
                enableEquationSymbols();
                enableComma();
                disableCompute();
            }
        });
    }

    private void initCommaActionListener() {
        commaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (commaAdded) {
                    return;
                }
                resultField.setText(resultField.getText() + commaButton.getText());
                disableComma();

            }
        });
    }


    private void initNumbersActionListeners() {
        for (JButton[] buttonsRow : numberButtons) {
            for (JButton numberButton : buttonsRow) {
                numberButton.addActionListener(getNumberButtonAction());
            }
        }
        number0Button.addActionListener(getNumberButtonAction());
    }

    private ActionListener getNumberButtonAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton button = (JButton) e.getSource();
                resultField.setText(resultField.getText() + button.getText());
            }
        };
    }

    private void addComputeActionListener() { // definuje działanie przycisku compute
        compute.addActionListener(getComputeActionListener());
    }

    private ActionListener getComputeActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String equationString = resultField.getText();
                equationString = equationString.replaceAll(commaButton.getText(), ".");
                String[] parsedEquation = equationString.split(" ");
                double number1 = Double.parseDouble(parsedEquation[0]);
                if (parsedEquation.length < 3) {
                    setResultField(number1);
                    return;
                }
                double number2 = Double.parseDouble(parsedEquation[2]);
                double result = computeResult(number1, number2, parsedEquation[1]);
                setResultField(result);
            }
        };
    }

    private double computeResult(double number1, double number2, String equationSymbol) {
        switch (equationSymbol) {
            case "+":
                return number1 + number2;
            case "-":
                return number1 - number2;
            case "/":
                return number1 / number2;
            case "*":
                return number1 * number2;
        }
        return -1;
    }

    private void setResultField(double number) {
        String numberString = String.valueOf(number);
        if (number == (int) number) {
            numberString = numberString.substring(0, numberString.indexOf('.'));
            enableComma();
        } else {
            disableComma();
        }
        resultField.setText(numberString.replace(".",commaButton.getText()));
        enableEquationSymbols();
    }

    private void enableComma() {
        commaButton.setEnabled(true);
    }

    private void disableComma() {
        commaButton.setEnabled(false);
    }

    private void enableCompute() {
        compute.setEnabled(true);
    }

    private void disableCompute() {
        compute.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {  // na osobnym watku
            @Override
            public void run() {
                new MediumCalc("Medium Calc"); // tworze instancję okna
            }
        });
    }
}
