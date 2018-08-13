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
 * 俄罗斯方块
 * JPanel 图形界面上能够显示的空白面板(空白矩形区域)
 * 扩展了面板为 俄罗斯方块，扩展出分数和正在下落的方块，
 * 以及下一个下落的方块。
 */
public class Tetris extends JPanel {
	public static final int ROWS = 20;
	public static final int COLS = 10;
	/**格子的绘制大小 */
	public static final int CELL_SIZE = 26;
    private int lines;
    private int score;
    /**墙*/
    private Cell[][] wall = new Cell[ROWS][COLS];
    /**正在下落的方块*/
	private Tetromino tetromino;
	/**下一个方块*/
	private Tetromino nextOne;
	
	/**
	 * 在Tetris类中添加  
	 * 利用静态代码块静态加载图片资源
	 * 将磁盘上图片文件，加载到内存中的图片对象
	 **/

	public  static Image I;
	public  static Image T;
	public  static Image S;
	public  static Image Z;
	public  static Image L;
	public  static Image J;
	public  static Image O;
	public  static Image background;//背景图片对象
	public  static Image gameOverImg;
	static{
		//Class 类提供了一个方法getResource()可以找到
		//package中的文件位置，利用这个位置可以读取
		//图片文件到内存中的对象
		//tetris.png 文件与 Tetris.class在同一个包中

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
	/** Tetris类中 重写JPanel类的paint()方法 
	 * paint:涂画，这个是JPanel的绘图方法
	 * 重写了以后就修改了 父类的paint方法,目的
	 * 是实现自定义绘制。
	 * Graphics理解为一个绑定到当前面板的画笔。
	 * */
	 public void paint(Graphics g){
		g.drawImage(background, 0, 0, null); 
		g.translate(15, 15);//坐标系平移
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
	 /** 绘制分数 */
	 private void paintScore(Graphics g) {
		 int x = 289;
		 int y = 160;
		 g.setColor(new Color(FONT_COLOR));
		 Font font = getFont();//获得系统字体(FONT)
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
	 
	 /** Tetris类上添加绘制正在下落方块的方法 */
	 private void paintTetromino(Graphics g){
		if(tetromino==null){//如果没有正在下落的方块，就不绘制
			return;
		}
		Cell[] cells = tetromino.cells;
		for(int i=0;i<cells.length;i++){
			Cell cell = cells[i];//cell代表每个小格子
			int x = cell.getCol() * CELL_SIZE;
			int y = cell.getRow() * CELL_SIZE;
			//System.out.println(x+","+y+","+cell.getImage());//跟踪语句排错用
			g.drawImage(cell.getImage(),x-1,y-1,null);
		}
	 }
	 private void paintNextOne(Graphics g){
		if(nextOne==null){//如果没有正在下落的方块，就不绘制
			return;
		}
		Cell[] cells = nextOne.cells;
		for(int i=0;i<cells.length;i++){
			Cell cell = cells[i];//cell代表每个小格子
			int x = (cell.getCol()+10) * CELL_SIZE;
			int y = (cell.getRow()+1) * CELL_SIZE;
			//System.out.println(x+","+y+","+cell.getImage());//跟踪语句排错用
			g.drawImage(cell.getImage(),x-1,y-1,null);
		}
	 }
 
	 /** 绘制墙，就是将wall数组的内容绘制到界面*/
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
					 g.setColor(new Color(0));//黑色
					 g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
				 }else{
					 g.drawImage(cell.getImage(), x-1,y-1, null);
				 }
			 }
		 }
	 }
	/** Tetris类中添加方法action启动软件 */
	public void action(){
		//wall[19][2] = new Cell(19,2,T);
		//tetromino = Tetromino.randomOne();
		//nextOne = Tetromino.randomOne();
		startAction();
		repaint();//JPanel中的重绘方法,会尽快调用paint	
	    //键盘按键监听器 对象
		KeyListener l = new KeyAdapter(){
			/** 如果有按键按下完成时候(Pressed)就会执行 */
            public void keyPressed(KeyEvent e) {
            	 //long time = e.getWhen();
                int key = e.getKeyCode();
                if(key==KeyEvent.VK_Q) {
                	System.exit(0);//结束Java进程
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
                	return;//提交结束方法，不在处理后续事件
                }
                //System.out.println(key);
                switch (key) {
				 case KeyEvent.VK_DOWN:
					    softDropAction();//ctrl+鼠标点到softDropAction()方法上就会自动跳转到方法的定义或实现					
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
		};//将键盘监听器对象添加到面板上.
		this.addKeyListener(l);//this代表当前俄罗斯方块面板
	    this.requestFocus();//Focus焦点
	}
	/**Tetris类的main方法 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("俄罗斯方块");//窗口框
		Tetris tetris = new Tetris();//Tetris继承了JPanel
		//Tetris也是面板,面板可以放到frame中显示
		tetris.setBackground(new Color(0x00ff00));
		frame.add(tetris);		
		frame.setSize(525, 550);//Size大小
		frame.setUndecorated(true);//去除窗口装饰
		frame.setLocationRelativeTo(null);//居中了！
		//Default:默认Close关闭Operation操作
		//设置窗口的默认关闭操作是退出程序
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);//显示窗口
		//frame在显示的时候会尽快的调用paint方法
		tetris.action();//开始动作
	}
	/** 在Tetris类中添加 下落流程控制方法 */
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
       //当能够下落时候就下落一步	
       while(canDrop()){
    		tetromino.softDrop();
    	}
       landToWall();//着陆到墙上
	   destroyLines();//消行
	   checkGameOver();//检查游戏结束
       tetromino = nextOne;//当前换成下一个
       nextOne = Tetromino.randomOne();
    }
    /**检查当前方块是否能够继续下落
     * 1）方块是否到达底部
     * 2）方块下方墙上是否有格子。
     */
    private boolean canDrop(){
    	Cell[] cells = tetromino.cells;
    	//检查当前方块是否到达底部
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
    /** 着陆到墙*/
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
    /** 在Tetris中添加方法 */
    private void moveLeftAction(){
    	tetromino.moveLeft();
    	if(outofBounds() || coincide() ){//出界检查方法
    		tetromino.moveRight();
    	}
    }
    private void moveRightAction(){
    	tetromino.moveRight();
    	if(outofBounds() || coincide() ){//出界检查方法
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
    /** 检查重合 */
    private boolean coincide(){
    	Cell[] cells = tetromino.cells;
    	for(Cell cell: cells){
    		int row = cell.getRow();
    		int col = cell.getCol();
    		//System.out.println(row+","+col);
    		//if(row>0 && row<ROWS && col>=0 && col<COLS && 
    		  if(row<0 || row>=ROWS || col<0 || col>=COLS || 
    				wall[row][col]!=null){
    			return true;//在墙上找到格子，就发生了重合
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
    private int interval = 800;//间隔时间
    /** 开始动作流程*/
    public void startAction() {
		pause = false;
		gameOver = false; 
		score = 0;
		lines = 0;
		//清空墙
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
		/** 暂停流程*/
		public void pauseAction(){
			timer.cancel();
			pause = true;		
       }
		/** 继续流程 */
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

