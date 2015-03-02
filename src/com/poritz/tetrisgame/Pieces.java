package com.poritz.tetrisgame;
import java.awt.*; 

// Constants representing the pieces and their colors
// Pieces are represented by a grid of booleans (true = filled, false = empty)

public class Pieces { 

	public static final Color [] COLORS = { 
		new Color(0xFF00FF), // fuchsia
		new Color(0xDC143C), // crimson
		new Color(0x00CED1), // dark turquoise
		new Color(0xFFD700), // gold
		new Color(0x32CD32), // lime green
		new Color(0x008080), // teal
		new Color(0xFFA500), // orange
	};

	public static final boolean[][][] PIECES = {

		{
			{false, true, true}, 
			{ true,  true, false} 
		},


		{
			{true, true, false}, 
			{false, true, true}      
		},      


		{
			{true, true, true, true}
		},


		{
			{true, false, false},
			{true, true, true}
		},


		{
			{false, false, true}, 
			{true, true, true}
		},


		{
			{true, true},
			{true, true}
		},


		{
			{false, true, false},
			{true, true, true}
		},

	};

}