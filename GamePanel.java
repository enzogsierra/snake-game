package snake;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;


public final class GamePanel extends JPanel implements ActionListener 
{
    static final int SCREEN_X = 600; // screen width
    static final int SCREEN_Y = 600; // screen heigth
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_X * SCREEN_Y) / UNIT_SIZE;
    static final int DELAY = 100; // Timer delay in miliseconds
    
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 3;
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random = new Random();
    
    GamePanel()
    {
        this.setPreferredSize(new Dimension(SCREEN_X, SCREEN_Y));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        
        startGame();
    }
    
    public void startGame()
    {
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        
        newApple();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g)
    {
        if(running)
        {
            // Draw grid
            for(int i = 0; i < SCREEN_X / UNIT_SIZE; i++)
            {
                g.drawLine(0, i * UNIT_SIZE, SCREEN_X, i * UNIT_SIZE);
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_Y);
            }

            // Draw snake
            for(int i = 0; i < bodyParts; i++)
            {
                // Set a green color for head
                // Set a darken green color for rest of body
                Color color = (i == 0) ? (Color.green) : (new Color(45, 180, 0));
                g.setColor(color);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            
            // Draw apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            
            // Draw score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));

            String text = "Score: " + applesEaten;
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString(text, (SCREEN_X - metrics.stringWidth(text)) / 2, g.getFont().getSize());
        }
        else
        {
            gameOver(g); // Draw game over screen
        }
    }
    
    public void newApple()
    {
        appleX = random.nextInt((int) (SCREEN_X / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_Y / UNIT_SIZE)) * UNIT_SIZE;
    }
    
    public void move()
    {
        for(int i = bodyParts; i > 0; i--)
        {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        
        // Move snake to the next direction
        switch(direction)
        {
            case 'U': // Up
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D': // Down
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L': // Left
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R': // Right
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }
    
    public void checkApple()
    {
        // Check if head collides with apple
        if(x[0] == appleX && y[0] == appleY)
        {
            newApple();
            bodyParts++;
            applesEaten++;
        }
    }
    
    public void checkCollisions()
    {
        // Check if head collides with body
        for(int i = 1; i < bodyParts; i++)
        {
            if(x[0] == x[i] && y[0] == y[i])
            {
                running = false;
            }
        }
        
        // Check if head touches any border
        if(x[0] < 0) running = false; // Left border
        if(x[0] >= SCREEN_X) running = false; // Right border
        if(y[0] < 0) running = false; // Top border 
        if(y[0] > SCREEN_Y) running = false; // Bottom border
        
        
        // Check if collided
        if(!running)
        {
            timer.stop();
        }
    }
    
    /*
    * Game over screen
    */
    public void gameOver(Graphics g)
    {
        String text;
        
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        
        // Draw score
        text = "Score: " + applesEaten;
        g.drawString(text, (SCREEN_X - metrics.stringWidth(text)) / 2, g.getFont().getSize());
        
        // Draw "Game Over" text
        text = "Game over";
        g.drawString(text, (SCREEN_X - metrics.stringWidth(text)) / 2, SCREEN_Y / 2);
        
        // Draw restart text
        text = "Press 'R' to restart";
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        metrics = getFontMetrics(g.getFont());
        g.drawString(text, (SCREEN_X - metrics.stringWidth(text)) / 2, (SCREEN_Y / 2) + metrics.getHeight());
    }
    
    /*
    * Every time "timer" is called
    */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(running) // Game's running
        {
            move();
            checkApple();
            checkCollisions();
        }
        
        repaint();
    }
    
    public class MyKeyAdapter extends KeyAdapter
    {
        @Override
        public void keyPressed(KeyEvent e)
        {
            if(running)
            {
                switch(e.getKeyCode())
                {
                    case KeyEvent.VK_UP:
                        if(direction != 'D') direction = 'U'; // Avoid snake turning 180°
                        break;
                    case KeyEvent.VK_DOWN:
                        if(direction != 'U') direction = 'D'; // ...
                        break;
                    case KeyEvent.VK_LEFT:
                        if(direction != 'R') direction = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(direction != 'L') direction = 'R';
                        break;
                }
            }
            else
            {
                if(e.getKeyCode() == KeyEvent.VK_R) // Restart button
                {
                    // Reset values
                    for(int i = 0; i < x.length; x[i] = 0, i++) { }
                    for(int i = 0; i < y.length; y[i] = 0, i++) { }
                   
                    bodyParts = 3;
                    applesEaten = 0;
                    direction = 'R';
                    
                    // Start game
                    startGame();
                }
            }
        }
    }
}
