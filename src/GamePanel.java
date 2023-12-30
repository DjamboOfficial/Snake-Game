import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    // Costanti
    private static final int SCREEN_WIDTH = 1300;
    private static final int SCREEN_HEIGHT = 750;
    private static final int UNIT_SIZE = 50;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 175;

    // Variabili
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodySegments = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private final Timer timer;
    private final Random random;

    // Costruttore
    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter()); // Inizializza KeyAdapter per l'input utente
        startGame();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    // Metodo per avviare il gioco
    public void startGame() {
        newApple();
        running = true;
    }

    // Override del metodo per dipingere i componenti sul pannello
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Metodo per disegnare i componenti del gioco
    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE); // Disegna la mela

            for (int i = 0; i < bodySegments; i++) {
                if (i == 0) {
                    g.setColor(Color.green); // Testa del serpente
                } else {
                    g.setColor(new Color(45, 180, 0)); // Segmenti del corpo
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE); // Disegna i segmenti del serpente
            }

            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Punteggio: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Punteggio: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g); // Mostra la schermata di game over
        }
    }

    // Metodo per generare una nuova mela in una posizione casuale
    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    // Metodo per muovere il serpente
    public void move() {
        for (int i = bodySegments; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE; // Muovi verso l'alto
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE; // Muovi verso il basso
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE; // Muovi a sinistra
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE; // Muovi a destra
                break;
        }
    }

    // Metodo per verificare se il serpente ha mangiato una mela
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodySegments++;
            applesEaten++;
            newApple();
        }
    }

    // Metodo per verificare collisioni con pareti o con il proprio corpo
    public void checkCollisions() {
        for (int i = bodySegments; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false; // Il serpente ha colpito se stesso
            }
        }

        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false; // Il serpente ha colpito le pareti
        }

        if (!running) {
            timer.stop(); // Ferma il timer del gioco
        }
    }

    // Metodo per visualizzare la schermata di game over
    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Punteggio: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Punteggio: " + applesEaten)) / 2, g.getFont().getSize());

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    // Classe interna per gestire gli eventi dei tasti
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }

    // Metodo chiamato dal timer per gli aggiornamenti del gioco
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint(); // Ridisegna il pannello di gioco
    }

    // Metodo principale per avviare il gioco
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
