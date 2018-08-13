package com.tarena.tetris;
import java.awt.Image;
/**
 * 是一个格子
 */
public class Cell {
  private int row;
  private int col;
  private Image image;//格子的贴图效果
  public Cell(){
	  
  }
//鼠标放在空白位置右键-->Source-->Generate Constructor using Fields  

public Cell(int row, int col, Image image) {
	super();
	this.row = row;
	this.col = col;
	this.image = image;
}
//鼠标放在空白位置右键-->Source-->Generate Getters and Setters 
public int getRow() {
	return row;
}
public void setRow(int row) {
	this.row = row;
}
public int getCol() {
	return col;
}
public void setCol(int col) {
	this.col = col;
}
public Image getImage() {
	return image;
}
public void setImage(Image image) {
	this.image = image;
}

public void drop(){
	row++;
}

public void moveLeft(){
	col--;
}

public void moveRight(){
	col++;
}

public  String toString(){
	return "["+row+","+col+"]";	
}
   
}
