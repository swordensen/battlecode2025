package examplefuncsplayer;

import static org.junit.Assert.*;
import org.junit.Test;

public class RobotPlayerTest {

	@Test
	public void testSanity() {
		assertEquals(2, 1+1);
	}

	@Test
	public  void testConvertToGrid() {
		int number = 22365525; // Example decimal number

		// Convert to 5x5 grid
		int[][] grid = convertToGrid(number);

		// Print the grid
		for (int[] row : grid) {
			for (int cell : row) {
				System.out.print(cell + " ");
			}
			System.out.println();
		}
	}

	public static int[][] convertToGrid(int number) {
		int[][] grid = new int[5][5];

		// Iterate over the 25 bits
		for (int i = 0; i < 25; i++) {
			// Extract the ith bit
			int bit = (number >> i) & 1;

			// Map the bit to the grid
			int row = i / 5; // Row index
			int col = i % 5; // Column index
			grid[row][col] = bit;
		}

		return grid;
	}

}
