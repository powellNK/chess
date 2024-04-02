import java.util.Random;
import java.util.Scanner;


public class chess {

    static int min(int a, int b) {
        int min;
        if (a > b) {
            min = b;
        } else {
            min = a;
        }
        return min;
    }

    //потом цвет фигур
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    //проверка ввода в диапазоне доски
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
        PLAYER1('R'),
        PLAYER2('G'),
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
    static void movePlayer(Cell[][] arrayActivePlayer, Cell[][] arrayEnemy) {
        System.out.println("Координаты фигуры: ");
        int coordRow, coordCol;
        int coordRowMove, coordColMove;
        boolean checkMove;
        do {
            coordRow = userInputCheck("Строка: ");
            coordCol = userInputCheck("Столбец: ");
        } while (arrayActivePlayer[coordRow][coordCol] == Cell.EMPTY && arrayEnemy[coordRow][coordCol] != Cell.EMPTY);
        Cell figure;
        figure = arrayActivePlayer[coordRow][coordCol];
        arrayActivePlayer[coordRow][coordCol] = Cell.EMPTY;
        do {
            System.out.println("Координаты хода: ");
            coordRowMove = userInputCheck("Строка: ");
            coordColMove = userInputCheck("Столбец: ");
            checkMove = checkPossibleMove(figure, arrayEnemy, arrayActivePlayer, coordRow, coordCol, coordRowMove, coordColMove);
        } while (checkMove == false);
        if (checkMove == true) {
            arrayActivePlayer[coordRowMove][coordColMove] = figure;
        }
    }

    //проверка возможности походить
    static boolean checkPossibleMove(Cell fiqure, Cell[][] arrayEnemy, Cell[][] arrayActivePlayer, int x1, int y1, int x2, int y2) {
        boolean result = false;
        boolean Check = true;
        switch (fiqure) {
            case PAWN:
                if (Math.abs(x2 - x1) == 1 && arrayEnemy[x2][y2] == Cell.EMPTY && arrayEnemy[x2][y2] == Cell.EMPTY) {   //умеет ходить, но не умеет бить
                    result = true;
                } else {
                    System.out.println("Ход невозможен. Повторите попытку");
                }
                break;
            // остальные умеют бить, но и могут ходить через остальные фигуры=_=
            case ROOK: //если некуда пойти, то зависнет!!
                if (checkRook(arrayEnemy, arrayActivePlayer, x1, y1, x2, y2)) {
                    arrayEnemy[x2][y2] = Cell.EMPTY;
                    result = true;
                } else {
                    System.out.println("Ход невозможен. Повторите попытку");
                }
                break;
            case KNIGHT: // конь
                if (Math.abs((x1 - x2) * (y1 - y2)) == 2 && arrayActivePlayer[x2][y2] == Cell.EMPTY) { //логика хода и проверка на то, что там нет своих фигур
                    if (arrayEnemy[x2][y2] != Cell.EMPTY) {
                        arrayEnemy[x2][y2] = Cell.EMPTY;
                    }
                    result = true;
                } else {
                    System.out.println("Ход невозможен. Повторите попытку");
                }
                break;
            case BISHOP: // слон
                if (checkBishop(arrayEnemy, arrayActivePlayer, x1, y1, x2, y2)) {
                    arrayEnemy[x2][y2] = Cell.EMPTY;
                    result = true;
                } else {
                    System.out.println("Ход невозможен. Повторите попытку");
                }
                break;
            case QUENN:
                // добавить проверки слона и ладьи на препятствие!!! возможно в метод их добавить


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
                if ((x1 - 1 <= x2 && x2 <= x1 + 1) && (y1 - 1 <= y2 && y2 <= y1 + 1) && arrayActivePlayer[x2][y2] == Cell.EMPTY) {
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

    static boolean checkRook(Cell[][] arrayEnemy, Cell[][] arrayActivePlayer, int x1, int y1, int x2, int y2) {
        boolean Check = true;
        if (x1 == x2 && arrayActivePlayer[x2][y2] == Cell.EMPTY) {   // логика хода и проверка на то, что там нет своих фигур
            for (int i = min(y1, y2) + 1; i < Math.abs(y1 - y2); i++) {
                if (arrayEnemy[x2][i] != Cell.EMPTY || arrayActivePlayer[x2][i] != Cell.EMPTY) {  // проверка препятствий по пути к конечным координатам
                    Check = false;
                    break;
                }
            }
        } else if (y1 == y2 && arrayActivePlayer[x2][y2] == Cell.EMPTY) {   // логика хода и проверка на то, что там нет своих фигур
            for (int i = min(x1, x2) + 1; i < Math.abs(x1 - x2); i++) {
                if (arrayEnemy[i][y2] != Cell.EMPTY || arrayActivePlayer[i][y2] != Cell.EMPTY) { // проверка препятствий по пути к конечным координатам
                    Check = false;
                    break;
                }
            }

        } else {
            Check = false;
        }
        return Check;
    }

    static boolean checkBishop(Cell[][] arrayEnemy, Cell[][] arrayActivePlayer, int x1, int y1, int x2, int y2) {
        boolean Check = true;
        if (Math.abs(y1 - y2) == Math.abs(x1 - x2) && arrayActivePlayer[x2][y2] == Cell.EMPTY) {   // логика хода и проверка на то, что там нет своих фигур
            int xShift = x1 < x2 ? 1 : -1;
            int yShift = y1 < y2 ? 1 : -1;
            for (int i = x1 + xShift, j = y1 + yShift; i != x2 && j != y2; i += xShift, j += yShift) {
                if (arrayEnemy[i][j] != Cell.EMPTY || arrayActivePlayer[i][j] != Cell.EMPTY) {    // проверка препятствий по пути к конечным координатам
                    Check = false;
                    break;
                }
            }
        } else {
            Check = false;
        }
        return Check;
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
                movePlayer(playerWhiteField, playerBlackField);
                activePlayer = Player.PLAYER2;
            } else {
                movePlayer(playerBlackField, playerWhiteField);
                activePlayer = Player.PLAYER1;
            }
        }
    }
}
