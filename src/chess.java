import java.util.Random;
import java.util.Scanner;


public class chess {

    static int min(int a, int b) {
        int min;
        if (a > b) {
            min = a;
        } else {
            min = b;
        }
        return min;
    }

    //потом цвет фигур
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    //проверка ввода
    private static int userInputCheck(String message) {
        Scanner reader = new Scanner(System.in);
        int digit;
        while (true) {
            System.out.print(message);
            try {
                digit = Integer.parseInt(reader.next());
                if (digit >= 0 && digit <= 7) {
                    break;
                }
                System.out.println("Число вне диапазона (0-7)");
            } catch (NumberFormatException e) {
                System.out.println("Неверный ввод!");
            }
        }
        return digit;
    }

    public enum Player {
        PLAYER1('W'),
        PLAYER2('B'),
        INTIAL(' ');

        private char value;

        Player(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }
    }


    //стартовое расположение фигур игроков
    static void fillStartBoard(Cell[][] array, Player player) {
        int rowPawn;
        int rowOst;
        if (player == Player.PLAYER1) {
            rowPawn = 1;
            rowOst = 0;
        } else {
            rowPawn = 6;
            rowOst = 7;
        }
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (i == rowPawn) {
                    array[i][j] = Cell.PAWN;
                } else if (i == rowOst) {
                    switch (j) {
                        case 0:
                        case 7:
                            array[i][j] = Cell.ROOK;
                            break;
                        case 1:
                        case 6:
                            array[i][j] = Cell.KNIGHT;
                            break;
                        case 2:
                        case 5:
                            array[i][j] = Cell.BISHOP;
                            break;
                        case 3:
                            array[i][j] = Cell.QUENN;
                            break;
                        case 4:
                            array[i][j] = Cell.KING;
                            break;
                    }
                } else {
                    array[i][j] = Cell.EMPTY;
                }
            }
        }
    }

    //все поле
    static Cell[][] fillAndPrintFullBoard(Cell[][] arrayPlayer1, Cell[][] arrayPlayer2, int fieldSize) {
        Cell[][] board = new Cell[fieldSize][fieldSize];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (arrayPlayer1[i][j] != Cell.EMPTY) {
                    board[i][j] = arrayPlayer1[i][j];
                    System.out.print(ANSI_RED + board[i][j].getValue() + ANSI_RESET);
                } else if (arrayPlayer2[i][j] != Cell.EMPTY) {
                    board[i][j] = arrayPlayer2[i][j];
                    System.out.print(ANSI_GREEN + board[i][j].getValue() + ANSI_RESET);
                } else {
                    board[i][j] = Cell.EMPTY;
                    System.out.print(board[i][j].getValue());
                }
            }
            System.out.println();

        }
        return board;
    }


    //Ход игрока
    static void movePlayer(Cell[][] arrayActivePlayer, Cell[][] arrayEnemy, Player activePlayer) {
        System.out.println("Координаты фигуры: ");
        int coordRow, coordCol;
        int coordRowMove, coordColMove;
        do {
            coordRow = userInputCheck("Строка: ");
            coordCol = userInputCheck("Столбец: ");
        } while (arrayActivePlayer[coordRow][coordCol] == Cell.EMPTY && arrayEnemy[coordRow][coordCol] != Cell.EMPTY);
        Cell figure;
        figure = arrayActivePlayer[coordRow][coordCol];
        arrayActivePlayer[coordRow][coordCol] = Cell.EMPTY;
        //потом добавить логику хода!!!
        do {
            System.out.println("Координаты хода: ");
            coordRowMove = userInputCheck("Строка: ");
            coordColMove = userInputCheck("Столбец: ");
        } while (checkPossibleMove(figure, arrayEnemy, arrayActivePlayer, coordRow, coordCol, coordRowMove, coordColMove, activePlayer) == false);

        arrayActivePlayer[coordRowMove][coordColMove] = figure;
    }

    //проверка возможности походить
    static boolean checkPossibleMove(Cell fiqure, Cell[][] arrayEnemy, Cell[][] arrayActivePlayer, int x1, int y1, int x2, int y2, Player activePlayer) {
        boolean result = false;
        switch (fiqure) {
            case PAWN:
                int tempCoodrX1;
                int tempCoodrX2;
                if (activePlayer == Player.PLAYER2) {
                    tempCoodrX1 = x2;
                    tempCoodrX2 = x1;
                } else {
                    tempCoodrX1 = x1;
                    tempCoodrX2 = x2;
                }
                if (tempCoodrX2 - tempCoodrX1 == 1 && arrayEnemy[x2][y2] == Cell.EMPTY && arrayEnemy[x2][y2] == Cell.EMPTY) {   //пешка умеет ходить, но не умеет бить
                    result = true;
                } else {
                    System.out.println("Ход невозможен. Повторите попытку");
                }
                break;
            // остальные умеют бить, но и могут ходить через остальные фигуры=_=
            case ROOK:
                if (x1 == x2) {
                    for (int i = min(y1, y2); i < Math.abs(y1 - y2); i++) {
                        if (arrayEnemy[x2][i] != Cell.EMPTY || arrayActivePlayer[x2][i] != Cell.EMPTY) {
                            System.out.println("Ход невозможен. Повторите попытку");
                            break;
                        } else {
                            arrayEnemy[x2][y2] = Cell.EMPTY;
                            result = true;
                        }
                    }
                } else if (y1 == y2) {
                    for (int i = min(x1, x2); i < Math.abs(x1 - x2); i++) {
                        if (arrayEnemy[i][y2] != Cell.EMPTY || arrayActivePlayer[i][y2] != Cell.EMPTY) {
                            System.out.println("Ход невозможен. Повторите попытку");
                            break;
                        } else {
                            arrayEnemy[x2][y2] = Cell.EMPTY;
                            result = true;
                        }
                    }
                } else {
                    System.out.println("Ход невозможен. Повторите попытку");
                }
                break;
            case KNIGHT:
                if (Math.abs((x1 - x2) * (y1 - y2)) == 2) {
                    if (arrayEnemy[x2][y2] != Cell.EMPTY) {
                        arrayEnemy[x2][y2] = Cell.EMPTY;
                    }
                    result = true;
                } else {
                    System.out.println("Ход невозможен. Повторите попытку");
                }
                break;
            case BISHOP:
                if ((y1 - y2) * (y1 - y2) == (x1 - x2) * (x1 - x2)) {
                    if (arrayEnemy[x2][y2] != Cell.EMPTY) {
                        arrayEnemy[x2][y2] = Cell.EMPTY;
                    }
                    result = true;
                } else {
                    System.out.println("Ход невозможен. Повторите попытку");
                }
                break;
            case QUENN:
                if (x1 == x2 || y1 == y2 || (y1 - y2) * (y1 - y2) == (x1 - x2) * (x1 - x2)) {
                    if (arrayEnemy[x2][y2] != Cell.EMPTY) {
                        arrayEnemy[x2][y2] = Cell.EMPTY;
                    }
                    result = true;
                } else {
                    System.out.println("Ход невозможен. Повторите попытку");
                }
                break;
            case KING:
                if ((x1 - 1 <= x2 && x2 <= x1 + 1) && (y1 - 1 <= y2 && y2 <= y1 + 1)) {
                    if (arrayEnemy[x2][y2] != Cell.EMPTY) {
                        arrayEnemy[x2][y2] = Cell.EMPTY;
                    }
                    result = true;
                } else {
                    System.out.println("Ход невозможен. Повторите попытку");
                }
                break;
        }
        return result;
    }

    public enum Cell {
        PAWN('P'),
        ROOK('R'),
        KNIGHT('N'),
        BISHOP('B'),
        QUENN('Q'),
        KING('K'),
        EMPTY('.');

        private char value;

        Cell(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }
    }

    public static void main(String[] args) {

        int fieldSize = 8;
        Cell[][] board = new Cell[fieldSize][fieldSize];
        Cell[][] playerWhiteField = new Cell[fieldSize][fieldSize];
        Cell[][] playerBlackField = new Cell[fieldSize][fieldSize];
        boolean isPlay = true;
        Random randGenerator = new Random();
        Player activePlayer, winner = Player.INTIAL;

        activePlayer = Player.PLAYER1;

        fillStartBoard(playerWhiteField, Player.PLAYER1);
        System.out.println();
        fillStartBoard(playerBlackField, Player.PLAYER2);


        while (isPlay) {
            board = fillAndPrintFullBoard(playerWhiteField, playerBlackField, fieldSize);
            System.out.println();
            if (activePlayer == Player.PLAYER1) {
                movePlayer(playerWhiteField, playerBlackField, activePlayer);
                activePlayer = Player.PLAYER2;
            } else {
                movePlayer(playerBlackField, playerWhiteField, activePlayer);
                activePlayer = Player.PLAYER1;
            }
        }
    }
}
