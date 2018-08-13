package com.tarena.tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

/**
 * ����˹����
 * JPanel ͼ�ν������ܹ���ʾ�Ŀհ����(�հ׾�������)
 * ��չ�����Ϊ ����˹���飬��չ����������������ķ��飬
 * �Լ���һ������ķ��顣
 */
public class Tetris extends JPanel {
	public static final int ROWS = 20;
	public static final int COLS = 10;
	/**���ӵĻ��ƴ�С */
	public static final int CELL_SIZE = 26;
    private int lines;
    private int score;
    /**ǽ*/
    private Cell[][] wall = new Cell[ROWS][COLS];
    /**��������ķ���*/
	private Tetromino tetromino;
	/**��һ������*/
	private Tetromino nextOne;
	
	/**
	 * ��Tetris�������  
	 * ���þ�̬����龲̬����ͼƬ��Դ
	 * ��������ͼƬ�ļ������ص��ڴ��е�ͼƬ����
	 **/

	public  static Image I;
	public  static Image T;
	public  static Image S;
	public  static Image Z;
	public  static Image L;
	public  static Image J;
	public  static Image O;
	public  static Image background;//����ͼƬ����
	public  static Image gameOverImg;
	static{
		//Class ���ṩ��һ������getResource()�����ҵ�
		//package�е��ļ�λ�ã��������λ�ÿ��Զ�ȡ
		//ͼƬ�ļ����ڴ��еĶ���
		//tetris.png �ļ��� Tetris.class��ͬһ������

		try {
			Class cls = Tetris.class;
		    I=ImageIO.read(cls.getResource("I.png"));
		    T=ImageIO.read(cls.getResource("T.png"));
		    S=ImageIO.read(cls.getResource("S.png"));
		    Z=ImageIO.read(cls.getResource("Z.png"));
		    L=ImageIO.read(cls.getResource("L.png"));
		    J=ImageIO.read(cls.getResource("J.png"));
		    O=ImageIO.read(cls.getResource("O.png"));
			background=ImageIO.read(cls.getResource("tetris.png"));
		    gameOverImg=ImageIO.read(cls.getResource("game-over.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** Tetris���� ��дJPanel���paint()���� 
	 * paint:Ϳ���������JPanel�Ļ�ͼ����
	 * ��д���Ժ���޸��� �����paint����,Ŀ��
	 * ��ʵ���Զ�����ơ�
	 * Graphics���Ϊһ���󶨵���ǰ���Ļ��ʡ�
	 * */
	 public void paint(Graphics g){
		g.drawImage(background, 0, 0, null); 
		g.translate(15, 15);//����ϵƽ��
		paintWall(g);
		paintTetromino(g);
		paintNextOne(g);
		paintScore(g);
		g.translate(-15, -15);
		if(gameOver) {
			g.drawImage(gameOverImg, 0, 0, null);
		}
	 }
	 public static final int FONT_COLOR=0x667799;
	 public static final int FONT_SIZE=30;	 
	 /** ���Ʒ��� */
	 private void paintScore(Graphics g) {
		 int x = 289;
		 int y = 160;
		 g.setColor(new Color(FONT_COLOR));
		 Font font = getFont();//���ϵͳ����(FONT)
		 font = new Font(font.getName(), Font.BOLD, FONT_SIZE);
		 g.setFont(font);
		 String str = "SCORE:"+score;
		 g.drawString(str, x, y);	 
		 y+=56;
		 str = "LINES:"+lines;
		 g.drawString(str, x, y);
		 y+=56;
		 str = "[P]Pause";
		 if(pause) {
			 str = "[C]Continue";
		 }
		 if(gameOver) {
			 str = "[S]Start";
		 }
		 g.drawString(str, x, y);
		 
	 }
	 
	 /** Tetris������ӻ����������䷽��ķ��� */
	 private void paintTetromino(Graphics g){
		if(tetromino==null){//���û����������ķ��飬�Ͳ�����
			return;
		}
		Cell[] cells = tetromino.cells;
		for(int i=0;i<cells.length;i++){
			Cell cell = cells[i];//cell����ÿ��С����
			int x = cell.getCol() * CELL_SIZE;
			int y = cell.getRow() * CELL_SIZE;
			//System.out.println(x+","+y+","+cell.getImage());//��������Ŵ���
			g.drawImage(cell.getImage(),x-1,y-1,null);
		}
	 }
	 private void paintNextOne(Graphics g){
		if(nextOne==null){//���û����������ķ��飬�Ͳ�����
			return;
		}
		Cell[] cells = nextOne.cells;
		for(int i=0;i<cells.length;i++){
			Cell cell = cells[i];//cell����ÿ��С����
			int x = (cell.getCol()+10) * CELL_SIZE;
			int y = (cell.getRow()+1) * CELL_SIZE;
			//System.out.println(x+","+y+","+cell.getImage());//��������Ŵ���
			g.drawImage(cell.getImage(),x-1,y-1,null);
		}
	 }
 
	 /** ����ǽ�����ǽ�wall��������ݻ��Ƶ�����*/
	 private void paintWall(Graphics g){
		 for(int row=0; row<wall.length; row++){
			 //row = 0 1 2 ... 19 = 1
			 Cell[] line = wall[row];
			 for(int col=0; col<line.length; col++){
				 //col = 0 1 2 ... 9 = 2
				 Cell cell = line[col];
				 //cell = wall[1][2]
				 int x = col*CELL_SIZE;
				 int y = row*CELL_SIZE;
				 if(cell==null){
					 g.setColor(new Color(0));//��ɫ
					 g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
				 }else{
					 g.drawImage(cell.getImage(), x-1,y-1, null);
				 }
			 }
		 }
	 }
	/** Tetris������ӷ���action������� */
	public void action(){
		//wall[19][2] = new Cell(19,2,T);
		//tetromino = Tetromino.randomOne();
		//nextOne = Tetromino.randomOne();
		startAction();
		repaint();//JPanel�е��ػ淽��,�ᾡ�����paint	
	    //���̰��������� ����
		KeyListener l = new KeyAdapter(){
			/** ����а����������ʱ��(Pressed)�ͻ�ִ�� */
            public void keyPressed(KeyEvent e) {
            	 //long time = e.getWhen();
                int key = e.getKeyCode();
                if(key==KeyEvent.VK_Q) {
                	System.exit(0);//����Java����
                }
                if(gameOver) {
                	if(key==KeyEvent.VK_S) {
                		startAction();
                		repaint();
                	}
                	return;
                }
                if(pause){
                	if(key==KeyEvent.VK_C) {
                		continueAction();
                		repaint();
                	}
                	return;//�ύ�������������ڴ�������¼�
                }
                //System.out.println(key);
                switch (key) {
				 case KeyEvent.VK_DOWN:
					    softDropAction();//ctrl+���㵽softDropAction()�����Ͼͻ��Զ���ת�������Ķ����ʵ��					
					    break;
				 case KeyEvent.VK_RIGHT:
						moveRightAction();					
						break;
				 case KeyEvent.VK_LEFT:
					    moveLeftAction();					
						break;
                 case KeyEvent.VK_UP:
                	    rotateRightAction();
                	    break;                    
	             case KeyEvent.VK_SPACE:
		        	    hardDropAction();
		        	    break;
	             case KeyEvent.VK_P:
		        	    pauseAction();
		        	    break;
	            }
                 repaint();
            }
		};//�����̼�����������ӵ������.
		this.addKeyListener(l);//this����ǰ����˹�������
	    this.requestFocus();//Focus����
	}
	/**Tetris���main���� */
	public static void main(String[] args) {
		JFrame frame = new JFrame("����˹����");//���ڿ�
		Tetris tetris = new Tetris();//Tetris�̳���JPanel
		//TetrisҲ�����,�����Էŵ�frame����ʾ
		tetris.setBackground(new Color(0x00ff00));
		frame.add(tetris);		
		frame.setSize(525, 550);//Size��С
		frame.setUndecorated(true);//ȥ������װ��
		frame.setLocationRelativeTo(null);//�����ˣ�
		//Default:Ĭ��Close�ر�Operation����
		//���ô��ڵ�Ĭ�Ϲرղ������˳�����
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);//��ʾ����
		//frame����ʾ��ʱ��ᾡ��ĵ���paint����
		tetris.action();//��ʼ����
	}
	/** ��Tetris������� �������̿��Ʒ��� */
    public void softDropAction(){
    	   //System.out.println("Call softDrop");    
    		
    	   if(canDrop()){
    		   //System.out.println("can drop");
    	       tetromino.softDrop();
    	   }else{
    		   //System.out.println("can not drop");
    	       landToWall();
    		   destroyLines();
    		   checkGameOver();
    	       tetromino = nextOne;
    	       nextOne = Tetromino.randomOne();
        }   	   
    }
    
    public void hardDropAction() {
       //���ܹ�����ʱ�������һ��	
       while(canDrop()){
    		tetromino.softDrop();
    	}
       landToWall();//��½��ǽ��
	   destroyLines();//����
	   checkGameOver();//�����Ϸ����
       tetromino = nextOne;//��ǰ������һ��
       nextOne = Tetromino.randomOne();
    }
    /**��鵱ǰ�����Ƿ��ܹ���������
     * 1�������Ƿ񵽴�ײ�
     * 2�������·�ǽ���Ƿ��и��ӡ�
     */
    private boolean canDrop(){
    	Cell[] cells = tetromino.cells;
    	//��鵱ǰ�����Ƿ񵽴�ײ�
    	for(Cell cell : cells){
    		int row = cell.getRow();
    		int col = cell.getCol();
    		if(row==ROWS-1){
    			return false;
    		}
    	}
    	for(Cell cell : cells){
    		int row = cell.getRow();
    		int col = cell.getCol();
    		if(wall[row+1][col]!=null){
    			return false;
    		}
    	}
    	return true;
    }
    /** ��½��ǽ*/
    private void landToWall(){
       Cell[] cells = tetromino.cells;      
       for(Cell cell : cells){
    		int row = cell.getRow();
    		int col = cell.getCol();
    		wall[row][col] = cell;
        }
    }
    private int[] scoreTable = {0, 1, 10, 50, 200};
    //                            0  1   2   3   4
    private void destroyLines(){
    	int lines = 0;
    	for(int row=0; row<wall.length; row++){
    		if(fullCells(row)){
    			deleteLine(row);
    			lines++;
    		}
    	}
    	this.lines += lines;
    	this.score += scoreTable[lines];
    }
    private void deleteLine(int row){
    	for(int i=row; i>=1; i--){
    		//wall[i-1]->wall[i]
    		System.arraycopy(wall[i-1], 0, wall[i], 0, COLS);
    	}
    	Arrays.fill(wall[0], null);
    }
    private boolean fullCells(int row){
    	Cell[] line = wall[row];
    	for(Cell cell : line){
    		if(cell==null){
    			return false;
    		}
    	}
    	return true;
    }
    private void checkGameOver(){
    	if(wall[0][4]!=null){
    		gameOver = true;
    		timer.cancel();
    		repaint();
    	}
    }
    /** ��Tetris����ӷ��� */
    private void moveLeftAction(){
    	tetromino.moveLeft();
    	if(outofBounds() || coincide() ){//�����鷽��
    		tetromino.moveRight();
    	}
    }
    private void moveRightAction(){
    	tetromino.moveRight();
    	if(outofBounds() || coincide() ){//�����鷽��
    		tetromino.moveLeft();
    	}
    }
    private boolean outofBounds(){
    	Cell[] cells = tetromino.cells;
    	for(Cell cell: cells){
    		int col = cell.getCol();
    		if(col<0 || col>=COLS){
    			return true;
    		}
    	}
    	return false;
    }
    /** ����غ� */
    private boolean coincide(){
    	Cell[] cells = tetromino.cells;
    	for(Cell cell: cells){
    		int row = cell.getRow();
    		int col = cell.getCol();
    		//System.out.println(row+","+col);
    		//if(row>0 && row<ROWS && col>=0 && col<COLS && 
    		  if(row<0 || row>=ROWS || col<0 || col>=COLS || 
    				wall[row][col]!=null){
    			return true;//��ǽ���ҵ����ӣ��ͷ������غ�
    		}
    	}
    	return false;
    }
    
    private void rotateRightAction(){
    	//System.out.println(Arrays.toString(tetromino.cells));
    	tetromino.rotateRight();
    	if(outofBounds() || coincide()){
    		tetromino.rotateLeft();
    	}
    	//System.out.println(Arrays.toString(tetromino.cells));
    }    
    private boolean pause;
    private boolean gameOver;
    private Timer timer;
    private int interval = 800;//���ʱ��
    /** ��ʼ��������*/
    public void startAction() {
		pause = false;
		gameOver = false; 
		score = 0;
		lines = 0;
		//���ǽ
		for(Cell[] line: wall) {
			Arrays.fill(line, null);
		}
		tetromino = Tetromino.randomOne();
		nextOne = Tetromino.randomOne();
		TimerTask task = new TimerTask(){
			public void run() {
				softDropAction();
				repaint();
			}
		};
		timer = new Timer();
		timer.schedule(task, interval, interval);
       }
		/** ��ͣ����*/
		public void pauseAction(){
			timer.cancel();
			pause = true;		
       }
		/** �������� */
		public void continueAction(){
		    pause = false;
			timer = new Timer();
			timer.schedule(new TimerTask(){
				public void run(){
					softDropAction();
					repaint();
				}
			}, interval, interval);
		}
		
}

