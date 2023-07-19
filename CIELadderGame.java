import com.sun.codemodel.internal.JOp;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CIELadderGame implements ActionListener {
    class WordPanel extends JPanel {
        JLabel[] wordColumns = new JLabel[4];

        public WordPanel() {
            GridLayout gl = new GridLayout(1, 5);
            gl.setHgap(10);
            gl.setVgap(10);
            this.setLayout(gl);

            this.setBackground(Color.white);
            Border grayBord = BorderFactory.createLineBorder(Color.lightGray);
            for (int i = 0; i < 4; i++) {
                wordColumns[i] = new JLabel();
                wordColumns[i].setHorizontalAlignment(JLabel.CENTER);
                wordColumns[i].setOpaque(true);
                wordColumns[i].setBorder(grayBord);
                wordColumns[i].setFont(new Font("SansSerif Bold", Font.PLAIN, 50));
                wordColumns[i].setForeground(Color.black);
                wordColumns[i].setBackground(Color.white); // not going to work

                this.add(wordColumns[i]);
            }
        }

        public void orientPanel() {

        }

        public void setPanelText(String charValue, int index, Color c) {
            this.wordColumns[index].setText(charValue);
            this.wordColumns[index].setBackground(c);
        }
    }

    class UserPanel extends JPanel {
        private JTextField userInput;
        private JButton enter;

        public UserPanel() {
            this.setLayout(new GridLayout(1, 2));
            userInput = new JTextField();
            userInput.setFont(new Font("Sans Serif", Font.PLAIN, 20));
            this.add(userInput);
            enter = new JButton("Enter");
            // enter.setMnemonic(KeyEvent.VK_ENTER); doesn't do anything
            enter.setFont(new Font("Sans Serif", Font.PLAIN, 20));
            this.add(enter);
        }

        public JTextField getUserInput() {
            return userInput;
        }

        public JButton getEnter() {
            return enter;
        }
    }

    private JFrame gameFrame;
    private WordPanel startWordPanel;
    private WordPanel endWordPanel;
    private ArrayList <WordPanel> wordPanelAL;
    private WordPanel blankPanel;
    private String startWord;
    private String endWord;
    private String previousWord;
    private UserPanel up;
    private int attemptCount;
    private String wordleString;
    private ArrayList<String> fourLetterD;
    private String userWord;

    public CIELadderGame() {
        gameFrame = new JFrame("LadderGame");
        gameFrame.setSize(600, 700);
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setLayout(new GridLayout(10, 1));
        gameFrame.setVisible(true);
        gameFrame.setLocationRelativeTo(null);

        attemptCount = 1;
        wordPanelAL = new ArrayList<>();
        up = new UserPanel();
        startWordPanel = new WordPanel();
        startWord = this.generateWord().trim().toUpperCase(Locale.ROOT);
        startWord = "STAR";
        String[] startArray = startWord.split("");
        for (int i = 0; i < 4; i++) {
            startWordPanel.setPanelText(startArray[i], i, Color.lightGray);
        }
        endWordPanel = new WordPanel();
        endWord = this.generateWord().trim().toUpperCase(Locale.ROOT);
        endWord = "BEND";
        String[] endArray = endWord.split("");
        for (int i = 0; i < 4; i++) {
            if (startArray[i].equals(endArray[i])) {
                endWordPanel.setPanelText(endArray[i], i, Color.green.brighter());
            } else {
                endWordPanel.setPanelText(endArray[i], i, Color.lightGray);
            }
        }
        blankPanel = new WordPanel();
        gameFrame.add(up);
        up.getEnter().addActionListener(this);
        wordPanelAL.add(startWordPanel);
        wordPanelAL.add(blankPanel);
        wordPanelAL.add(endWordPanel);

        for (int i = 0; i<wordPanelAL.size(); i++){
            gameFrame.add(wordPanelAL.get(i));
        }



        gameFrame.revalidate();
    }


    private String generateWord() {
        makeFourLetterD();
        Random r = new Random();
        int rIndex = r.nextInt(3996);
        return fourLetterD.get(rIndex).trim().toUpperCase();
    }

    private void makeFourLetterD() {
        try {
            fourLetterD = new ArrayList<>();
            Scanner fromFile = new Scanner(new File("FourLetterList.txt"));
            for (int i = 0; i < 3997; i++) {
                fourLetterD.add(fromFile.nextLine());
            }

        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    private int compareWords() {
        int errCount = 0;
        if (attemptCount == 1) {
            previousWord = startWord.trim().toUpperCase(Locale.ROOT);
            ;
        }
        userWord = this.up.getUserInput().getText().trim().toUpperCase(Locale.ROOT);
        String[] previousArray = previousWord.split("");
        String[] userArray = userWord.split("");
        for (int i = 0; i < 4; i++) {
            if (!previousArray[i].equals(userArray[i])) {
                errCount++;
            }
        }
        previousWord = userWord;
        return errCount;
    }

    public static void main(String[] args) {
        new CIELadderGame();
    }

    private boolean isUserWordEqualTo(String userWord) {
        userWord = userWord.trim().toUpperCase(Locale.ROOT);

        String[] endWordArray = endWord.split("");
        String[] userWordsArray = userWord.split("");
        ArrayList<Boolean> wordMatchList = new ArrayList<Boolean>();
        for (int i = 0; i < 4; i++) {
            if (endWordArray[i].equals(userWordsArray[i])) {
                blankPanel.setPanelText(userWordsArray[i], i, Color.GREEN.brighter());
                endWordPanel.setPanelText(endWordArray[i], i, Color.GREEN.brighter());
                wordMatchList.add(true);

            } else {
                blankPanel.setPanelText(userWordsArray[i], i, Color.lightGray);
                endWordPanel.setPanelText(endWordArray[i], i, Color.lightGray);
                wordMatchList.add(false);
            }
        }

        return !wordMatchList.contains(false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        userWord = this.up.getUserInput().getText();

        if (fourLetterD.contains(userWord)) {
            if (!(compareWords() > 1)) {
                if (isUserWordEqualTo(userWord)) {
                    JOptionPane.showMessageDialog(null, "You Win!", "Congratulations! Attempts: " + attemptCount, JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else {
                    WordPanel temp = new WordPanel();
                    wordPanelAL.add(wordPanelAL.size()-1, temp);
                    for (int i = 0; i<wordPanelAL.size(); i++){
                        gameFrame.add(wordPanelAL.get(i));
                    }
                    blankPanel = temp;
                }
            }

            attemptCount++;
        }
    }
}
