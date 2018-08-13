package com.tarena.test;

import com.tarena.tetris.Tetromino;

public class Test01 {
	public static void main(String[] args) {
		/**Tetromino.randomOne()四格方块的随机的某一个*/
		Tetromino t = Tetromino.randomOne();	
        System.out.println(t);
	}
}
