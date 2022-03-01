package de.az.ware.match.ttt;

public class TicTacToe {

    public static class IllegalTurnException extends Exception {
        public IllegalTurnException(String message){
            super(message);
        }
    }

    private int height = 3;
    private int width = 3;

    private int[][] field = new int[height][width];
    private boolean player = false;

    private int lastPlayer = 0;

    public TicTacToe() {
        checkPlayerWin();
    }

    public void draw() {
        System.out.println("TTT Game: " + isMatchOver() + " " + checkPlayerWin() );
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                System.out.print(field[j][i]);
            }
            System.out.println(" ");
        }
    }

    public int checkPlayerWin() {
        int win = 0;

        for (int winCondition = 1; winCondition <= 2; winCondition++) {

            // vertical
            for (int i = 0; i < height; i++) {
                if (field[0][i] == winCondition && field[1][i] == winCondition && field[2][i] == winCondition) {
                    win = winCondition;
                }
            }
            // horizontal
            for (int i = 0; i < width; i++) {
                if (field[i][0] == winCondition && field[i][1] == winCondition && field[i][2] == winCondition) {
                    win = winCondition;
                }
            }
            // diagonal
            if (field[0][0] == winCondition && field[1][1] == winCondition && field[2][2] == winCondition) {
                win = winCondition;
            }
            if (field[0][2] == winCondition && field[1][1] == winCondition && field[2][0] == winCondition) {
                win = winCondition;
            }

        }
        if (win > 0) {
            System.out.println("Player "+ win +" won!");
        }

        return win-1;
    }

    public void setField(int player, int x, int y) throws IllegalTurnException{
        if(player != 0 && player != 1) throw new IllegalTurnException("Bad Player");
        if(x < 0 || x >= 3) throw  new IllegalTurnException("Bad X");
        if(y < 0 || y >= 3) throw  new IllegalTurnException("Bad Y");
        if(field[y][x] != 0) throw new IllegalTurnException("Field already taken!");
        if(lastPlayer == player) throw new IllegalTurnException("Player have to alternate");

        lastPlayer = player;
        field[y][x] = player;
    }

    public boolean isMatchOver(){
        for(int x = 0; x < field.length; x++){
            for(int y = 0; y < field[x].length; y++){
                if(field[x][y] == 0) return false;
            }
        }
        return true;
    }

    public int getNextPlayer(){
        return lastPlayer % 2;
    }



}