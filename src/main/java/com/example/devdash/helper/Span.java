package com.example.devdash.helper;

public class Span {
    public int col;
    public int row;
    public int colSpan;
    public int rowSpan;
    Span(int col, int row, int colSpan, int rowSpan) {
        this.col = col;
        this.row = row;
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
    }

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
