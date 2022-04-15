import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window {

    JFrame frame;

    public Window(int width, int height, String title, Game game) {

        frame = new JFrame(title);
        frame.setFocusable(true);
        frame.setPreferredSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));

        JButton quitButton = new JButton("Quit");
        quitButton.setBounds(570, 300, 150, 50);
        quitButton.setForeground(Color.BLACK);
        quitButton.setFont(new Font("Arial", Font.BOLD, 30));
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

        JButton playButton = new JButton("Play");
        playButton.setBounds(570, 200, 150, 50);
        playButton.setForeground(Color.BLACK);
        playButton.setFont(new Font("Ariel", Font.BOLD, 30));
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game.state = Game.State.GAME;
                System.out.println("Playing");
                quitButton.setVisible(false);
                playButton.setVisible(false);
            }
        });

        frame.add(quitButton);
        frame.add(playButton);
        frame.add(game);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}