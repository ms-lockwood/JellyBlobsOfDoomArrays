//Basic Game Application
//Version 2
// Basic Object, Image, Movement
// Astronaut moves to the right.
// Threaded

//K. Chun 8/2018

//*******************************************************************************
//Import Section
//Add Java libraries needed for the game
//import java.awt.Canvas;

//Graphics Libraries
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.File;
import java.awt.Font;
import java.io.IOException;
import java.awt.FontMetrics;


//*******************************************************************************
// Class Definition Section

public class BasicGameApp implements Runnable, KeyListener {

	//Sets the width and height of the program window
	final int WIDTH = 900;
	final int HEIGHT = 700;

	//Declare the variables needed for the graphics
	public JFrame frame;
	public Canvas canvas;
	public JPanel panel;
	private static Font font;
	private static Font fontTextSmall;
	private static Font fontTextLarge;
	public Rectangle rect;

	public boolean gamePlaying = false;
	public int counter = 0;
	public boolean isPaused = false;
	public boolean gameOver = false;

	public BufferStrategy bufferStrategy;
	public Image pressSpacebarPic1;
	public Image pressSpacebarPic2;
	public Image backgroundPic;
	public Image gameOverPic;

	public Image dinoPicR;
	public Image dinoPicL;

	public Image blobPicPink;
	public Image blobPicRed;
	public Image blobPicOrange;
	public Image blobPicYellow;
	public Image blobPicBlue;

	//Declare the objects used in the program
	//These are things that are made up of more than one variable type
	public Dinosaur dino;
	public Blob[] blob;
	public int horizVert;
	public int blobX;
	public int blobY;
	public int blobdx;
	public int blobdy;
	public int blobWidth;
	public int blobHeight;

	// experimenting with holding down keys for motion
	public boolean leftIsPressed = false;
	public boolean upIsPressed = false;
	public boolean rightIsPressed = false;
	public boolean downIsPressed = false;

	public double threshhold = .9997;
	public int maxSpeed = 7;
	public double ddx = .15;
	public double ddy = .15;
	public int dinoMaxWidth = 210;


	// Main method definition
	// This is the code that runs first and automatically
	public static void main(String[] args) {
		BasicGameApp ex = new BasicGameApp();
		new Thread(ex).start();

		try {
			GraphicsEnvironment ge =
					GraphicsEnvironment.getLocalGraphicsEnvironment();
//			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("alba.super.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("magical-mystery-tour.outline-shadow.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("Cocon-Regular-Font.ttf")));

		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
		font = new Font("Magical Mystery Tour Outline Shadow", Font.PLAIN, 30);
		fontTextSmall = new Font("Cocon", Font.PLAIN, 30);
		fontTextLarge = new Font("Cocon", Font.PLAIN, 50);

	}


	// Constructor Method
	// This has the same name as the class
	// This section is the setup portion of the program
	// Initialize your variables and construct your program objects here.
	public BasicGameApp() {
		font = new Font("TimesRoman", Font.PLAIN, 50);
		fontTextSmall = new Font("TimesRoman", Font.PLAIN, 50);
		fontTextLarge = new Font("TimesRoman", Font.PLAIN, 50);

		setUpGraphics();

		pressSpacebarPic1 = Toolkit.getDefaultToolkit().getImage("pressSpacebar1.png");
		pressSpacebarPic2 = Toolkit.getDefaultToolkit().getImage("pressSpacebar2.png");

		backgroundPic = Toolkit.getDefaultToolkit().getImage("jellyBackground.png");
		gameOverPic = Toolkit.getDefaultToolkit().getImage("gameOverStillBlank.png");

		dinoPicR = Toolkit.getDefaultToolkit().getImage("dinoRight.png");
		dinoPicL = Toolkit.getDefaultToolkit().getImage("dinoLeft.png");

		blobPicPink = Toolkit.getDefaultToolkit().getImage("blobPink.png");
		blobPicRed = Toolkit.getDefaultToolkit().getImage("blobRed.png");
		blobPicOrange = Toolkit.getDefaultToolkit().getImage("blobOrange.png");
		blobPicYellow = Toolkit.getDefaultToolkit().getImage("blobYellow.png");
		blobPicBlue = Toolkit.getDefaultToolkit().getImage("blobBlue.png");

		dino = new Dinosaur(WIDTH/2,HEIGHT/2);

		blob = new Blob[100];

		for (int i = 0; i < blob.length; i++) {
			getBlobStats();
			blob[i] = new Blob(blobX, blobY, blobdx, blobdy,
					blobWidth, blobHeight);
			if (blob[i].width < 30) {
				blob[i].pic = blobPicPink;
			} else if (blob[i].width >= 30 && blob[i].width < 100) {
				blob[i].pic = blobPicRed;
			} else if (blob[i].width >= 100 && blob[i].width < 150) {
				blob[i].pic = blobPicOrange;
			} else if (blob[i].width >= 150 && blob[i].width < 200) {
				blob[i].pic = blobPicYellow;
			} else if (blob[i].width >= 200) {
				blob[i].pic = blobPicBlue;
			}

			if (Math.random() > threshhold) {
				blob[i].isAlive = true;
			}
		} // construct all the blobs

	}// BasicGameApp()


//*******************************************************************************
//User Method Section


	public void run() {

		while (true) {

			checkKeys();
			if (gamePlaying && !isPaused) {
				moveThings();
			}
			checkIntersections();
			makeBlobs();
			render();

			pause(10);
		}
	}

	public void getBlobStats() {

		blobWidth = (int)(Math.random()*150+10);
		blobHeight = (int)(blobWidth / 1.5);

		horizVert = (int)(Math.random()+.5);
		if (horizVert == 0) { // horizontal
			if (Math.random() > .5) { // left to right
				blobX = 0 - blobWidth;
			} else { // right to left
				blobX = WIDTH;
			}
			blobY = (int)(Math.random() * HEIGHT);
			if (blobX <= 0) { // left to right
				blobdx = (int)(Math.random() * 3 + 2);
			} else { // right to left
				blobdx = (int)(Math.random() * -3 - 2);
			}
			blobdy = 0;
		} // blob horizontal motion
		else if (horizVert == 1) { // vertical
			blobX = (int)(Math.random() * WIDTH);
			if (Math.random() > .5) { // downward motion
				blobY = 0 - blobHeight;
			} else { // upward motion
				blobY = HEIGHT;
			}

			blobdx = 0;
			if (blobY == 0) { // downward motion
				blobdy = (int)(Math.random() * 3 + 2);
			} else { // upward motion
				blobdy = (int)(Math.random() * -3 - 2);
			}

		} // blob vertical motion
		else {
			System.out.println("WHY ISN'T THE THING ZERO OR ONE");
		} // blob motion broken
	}

	public void checkKeys() {
		if (leftIsPressed) {
			if (dino.dx > -maxSpeed) {
				dino.dx -= ddx;
			}
		} else if (rightIsPressed) {
			if (dino.dx < maxSpeed) {
				dino.dx += ddx;
			}
		} else {
			dino.dx *= .98;
		}
		if (upIsPressed) {
			if (dino.dy > -maxSpeed) {
				dino.dy -= ddy;
			}
		} else if (downIsPressed) {
			if (dino.dy < maxSpeed) {
				dino.dy += ddy;
			}
		} else {
			dino.dy *= .98;
		}
	}

	public void moveThings() {
		if (dino.isAlive) {
			dino.move();
		}
//		else {
//			dino.xpos = blob.xpos + blob.width/2 - dino.width/2;
//			dino.ypos = blob.ypos + blob.height/2 - dino.height/2;
//		}
		for (int i = 0; i < blob.length; i++) {
			if (dino.isAlive == false && blob[i].killedDino == true) {
				dino.xpos = blob[i].xpos + blob[i].width/2 - dino.width/2;
				dino.ypos = blob[i].ypos + blob[i].height/2 - dino.height/2;
			}
			blob[i].move();
		}
	}

	public void checkIntersections() {

		if (dino.width >= dinoMaxWidth) {
			gameOver = true;
		}

		for (int i = 0; i < blob.length; i++) {

			if (blob[i].ell.intersects(dino.rec) && blob[i].isAlive && !gameOver) {
				blob[i].killedDino = true;

				if (blob[i].width > dino.width) {
					dino.isAlive = false;
					gameOver = true;
					dino.xpos = blob[i].xpos + blob[i].width/2 - dino.width/2;
					dino.ypos = blob[i].ypos + blob[i].height/2 - dino.height/2;

				} else {
					blob[i].isAlive = false;
					dino.width += blob[i].width / 12;
					dino.height = (int)(dino.width*.7);

					dino.points += blob[i].width / 4;
				}
			}
		}
	}

	public void makeBlobs() {
		for (int i = 0; i < blob.length; i++) {
			if (blob[i].isAlive == false && !isPaused && gamePlaying) {
				getBlobStats();
				blob[i] = new Blob(blobX, blobY, blobdx, blobdy,
						blobWidth, (int) (blobWidth / 1.5));
				if (blob[i].width < 30) {
					blob[i].pic = blobPicPink;
				} else if (blob[i].width >= 30 && blob[i].width < 100) {
					blob[i].pic = blobPicRed;
				} else if (blob[i].width >= 100 && blob[i].width < 150) {
					blob[i].pic = blobPicOrange;
				} else if (blob[i].width >= 150 && blob[i].width < 250) {
					blob[i].pic = blobPicYellow;
				} else if (blob[i].width >= 300) {
					blob[i].pic = blobPicBlue;
				}

				if (Math.random() > threshhold) {
					blob[i].isAlive = true;
				}
			}
		}
	}

	public void pause(int time) {
		//sleep
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {

		}
	}

	private void setUpGraphics() {
		frame = new JFrame("Application Template");   //Create the program window or frame.  Names it.

		panel = (JPanel) frame.getContentPane();  //sets up a JPanel which is what goes in the frame
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));  //sizes the JPanel
		panel.setLayout(null);   //set the layout

		// creates a canvas which is a blank rectangular area of the screen onto which the application can draw
		// and trap input events (Mouse and Keyboard events)
		canvas = new Canvas();
		canvas.setBounds(0, 0, WIDTH, HEIGHT);
		canvas.setIgnoreRepaint(true);

		panel.add(canvas);  // adds the canvas to the panel.

		// frame operations
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //makes the frame close and exit nicely
		frame.pack();  //adjusts the frame and its contents so the sizes are at their default or larger
		frame.setResizable(false);   //makes it so the frame cannot be resized
		frame.setVisible(true);      //IMPORTANT!!!  if the frame is not set to visible it will not appear on the screen!

		// sets up things so the screen displays images nicely.
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.addKeyListener(this);

		canvas.requestFocus();
		System.out.println("DONE graphic setup");

	}

	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		g.clearRect(0, 0, WIDTH, HEIGHT);

		if (!gamePlaying) {
			g.drawImage(backgroundPic, 0, 0, WIDTH, HEIGHT, null);
			if (counter > 15) {
				g.drawImage(pressSpacebarPic1, 0, 0, 900, 700, null);
			} else {
				g.drawImage(pressSpacebarPic2, 0, 0, 900, 700, null);
			}

			g.setColor(Color.white);
			rect = new Rectangle(0, 350, 900, 50);
			String text = "Press Spacebar to Begin";
			g.setFont(fontTextLarge);
			FontMetrics metrics = g.getFontMetrics(fontTextLarge);
			int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
			int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
			g.drawString(text, x, y);
			counter++;
			if (counter >= 30) {
				counter = 0;
			}
		} // start screen
		else if (gamePlaying && !gameOver) {
			g.drawImage(backgroundPic, 0, 0, WIDTH, HEIGHT, null);

			if (dino.dx >= 0) {
				g.drawImage(dinoPicR, (int) dino.xpos, (int) dino.ypos,
						dino.width, dino.height, null);
			} else {
				g.drawImage(dinoPicL, (int) dino.xpos, (int) dino.ypos,
						dino.width, dino.height, null);
			}

			for (int i = 0; i < blob.length; i++) {
				if (blob[i].isAlive) {
					g.drawImage(blob[i].pic, blob[i].xpos, blob[i].ypos, blob[i].width, blob[i].height, null);
//				g.draw(new Ellipse2D.Double(blob[i].xpos, blob[i].ypos + blob[i].height * .2,
//						blob[i].width, blob[i].height - blob[i].height * .2));
				}
			}

//			g.setFont(new Font("TimesRoman", Font.PLAIN, 50));

//			System.out.println(font.getSize());
//			g.setFont(font);

			rect = new Rectangle(800, 15, 100, 50);
			FontMetrics metrics = g.getFontMetrics(font);
			int x = rect.x + (rect.width - metrics.stringWidth(String.valueOf(dino.points))) / 2;
			int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
			g.setFont(font);
			g.setColor(new Color(99, 16, 123));
			g.drawString(String.valueOf(dino.points), x, y);

			if (isPaused) {
				rect = new Rectangle(0, 300, 900, 50);
				String text = "Press p to Resume";
				x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
				y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
				g.setFont(fontTextSmall);
				g.drawString(text, x, y);
			}
		} // playing game
		else {
			g.drawImage(gameOverPic, 0, 0, 900, 700, null);
			if (dino.width >= dinoMaxWidth) {
				g.setFont(fontTextSmall);
				g.drawString("You ate all the jelly in", 100, 100);
				g.drawString("Jelly World!", 100, 150);
				g.drawString("Congratulations!", 100, 200);
				g.drawString("Your score is " + dino.points, 100, 250);
			} // you win
			else {
				g.setFont(fontTextSmall);
				g.drawString("Oh no! You've been", 100, 100);
				g.drawString("devoured by a jelly", 100, 150);
				g.drawString("blob! Your score is " + dino.points, 100, 200);
			} // you lose

			g.setFont(fontTextSmall);
			g.drawString("Press Spacebar", 550, 550);
			g.drawString("to try again", 575, 600);
		} // game over

		g.dispose();

		bufferStrategy.show();
	}

//	public int[] centerString(String text, int rectX, int rectY) {
//		System.out.println(text);
//		FontMetrics metrics = g.getFontMetrics(font);
//
//		rect = new Rectangle(0, 300, 900, 50);
//		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
//		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
//		g.setFont(font);
//		g.drawString(text, x, y);
//
//		int[] coords = new int[2];
//		coords[0] = 100; // x
//		coords[1] = 100; // y
//
//		return coords;
//	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

		char key = e.getKeyChar();     //gets the character of the key pressed
		int keyCode = e.getKeyCode();  //gets the keyCode (an integer) of the key pressed
//		System.out.println("Key Pressed: " + key + "  Code: " + keyCode);

		if (!isPaused) { // if the game is not paused
			if (keyCode == 37) {
				leftIsPressed = true;
			}
			if (keyCode == 38) {
				upIsPressed = true;
			}
			if (keyCode == 39) {
				rightIsPressed = true;
			}
			if (keyCode == 40) {
				downIsPressed = true;
			}
		}

		if (!gameOver) {
			if (!gamePlaying) { // if the game hasn't started yet
				if (keyCode == 32) {
					gamePlaying = true;
				}
			}
		}
		else {
			if (keyCode == 32) {
				gamePlaying = false;
				gameOver = false;
				dino = new Dinosaur(WIDTH/2,HEIGHT/2);
				for (int i = 0; i < blob.length; i++) {
					blob[i].isAlive = false;
				}
			}

		}

		if (keyCode == 80) {
			if (isPaused) {
				isPaused = false;
			} else {
				isPaused = true;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		char key = e.getKeyChar();     //gets the character of the key pressed
		int keyCode = e.getKeyCode();  //gets the keyCode (an integer) of the key pressed
//		System.out.println("Key Released: " + key + "  Code: " + keyCode);

		if (keyCode == 37) { // left
			leftIsPressed = false;
		}
		if (keyCode == 38) { // up
			upIsPressed = false;
		}
		if (keyCode == 39) { // right
			rightIsPressed = false;
		}
		if (keyCode == 40) { // down
			downIsPressed = false;
		}
	}

}