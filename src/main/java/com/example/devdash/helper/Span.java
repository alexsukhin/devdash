package com.example.devdash.helper;

/**
 * Represents a grid span for layout positioning.
 *
 * Author: Alexander Sukhin
 * Version: 04/08/2025
 */
public class Span {

    public int col; // Column index of the span
    public int row; // Row index of the span
    public int colSpan; // Number of columns to span
    public int rowSpan; // Number of rows to span

    /**
     * Constructs a Span object with the specified grid parameters.
     *
     * @param col      Column index
     * @param row      Row index
     * @param colSpan  Number of columns to span
     * @param rowSpan  Number of rows to span
     */
    Span(int col, int row, int colSpan, int rowSpan) {
        this.col = col;
        this.row = row;
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
    }

    /**
     * Returns a Span configured for positioning cards in a grid layout.
     *
     * Layout logic:
     * - 1 card: spans 2x2 grid (full size)
     * - 2 cards: each spans 2 columns by 1 row
     * - 3 cards: first two cards each 1x1, third card spans 2 columns by 1 row at bottom
     * - 4 or more cards: split into 2 columns by 2 rows, each card 1x1 in this grid
     *
     * @param totalCards The total number of cards to layout
     * @param index      The index of the current card
     * @return A Span object representing position and size in the grid
     */
    public static Span forCard(int totalCards, int index) {
        switch (totalCards) {
            case 1:
                return new Span(0, 0, 2, 2);

            case 2:
                return new Span(0, index, 2, 1);

            case 3:
                if (index <= 1) return new Span(index, 0, 1, 1);
                else return new Span(0, 1, 2, 1);
            default:
                if (index <= 1) return new Span(0, index % 2, 1, 1);
                else return new Span(1, index % 2, 1, 1);
        }
    }
}
