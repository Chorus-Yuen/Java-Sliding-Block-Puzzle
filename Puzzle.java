import java.lang.Math;


class Tile
{
    private int id;

    public Tile(int num)
    {
        id = num;
    }

    public int get_id()
    {
        return id;
    }
}


public class Puzzle
{
    private int size = 3;
    private Tile[][] tiles = new Tile[size][size];
    private boolean display = false;


    // Miscellaneous
    private void my_print(Object text, boolean... line)
    {
        if (line.length == 0) {System.out.println(text);}
        else {System.out.print(text);}
    }

    private void print(Object text, boolean... line)
    {
        if (display) {my_print(text, line);}
    }

    private void my_display_tiles()
    {
        System.out.println("-------");
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                System.out.print(" " + tiles[j][i].get_id());
            }
            System.out.print("\n");
        }
        System.out.println("-------");
    }

    private void display_tiles(boolean... confirm)
    {
        boolean check;
        if (confirm.length == 0) {check = true;}
        else {check = confirm[0];}

        if (check && display)
        {
            my_display_tiles();
        }
    }

    private int[] make_coord(int x, int y)
    {
        int[] coord = new int[2];
        coord[0] = x;
        coord[1] = y;
        return coord;
    }

    private int[] find_tile(int id)
    {
        boolean found = false;
        int j = 0, i = 0;

        while (j < size && !found)
        {
            i = 0;
            while (i < size && !found)
            {
                if (id == tiles[j][i].get_id())
                {
                    found = true;
                }
                i++;
            }
            j++;
        }
        return make_coord(i - 1, j - 1);
    }

    private boolean at_coord(int id, int x, int y)
    {
        int[] coord = find_tile(id);
        return coord[0] == x && coord[1] == y;
    }

    private void swap(int[] coord_1, int[] coord_2, boolean... display)
    {
        Tile temp = tiles[coord_1[1]][coord_1[0]];
        tiles[coord_1[1]][coord_1[0]] = tiles[coord_2[1]][coord_2[0]];
        tiles[coord_2[1]][coord_2[0]] = temp;

        if (display.length == 0) {display_tiles();}
        else {display_tiles(display[0]);}
    }

    private Tile[] square_to_list(int[] coord)
    {
        Tile[] loop = new Tile[4];
        int x = coord[0], y = coord[1];
        loop[0] = tiles[y][x];
        loop[1] = tiles[y][x + 1];
        loop[2] = tiles[y + 1][x + 1];
        loop[3] = tiles[y + 1][x];

        return loop;
    }

    private void update_square(Tile[] loop, int[] coord)
    {
        tiles[coord[1]][coord[0]] = loop[0];
        tiles[coord[1]][coord[0] + 1] = loop[1];
        tiles[coord[1] + 1][coord[0] + 1] = loop[2];
        tiles[coord[1] + 1][coord[0]] = loop[3];
        display_tiles();
    }

    private void cycle(int[] coord, int num)
    {
        Tile[] loop = square_to_list(coord);

        int inc, counter = 0, ind1, ind2;
        Tile temp;
        if (coord[1] == find_tile(0)[1]) {ind1 = find_tile(0)[0] - coord[0];}
        else {ind1 = coord[0] - find_tile(0)[0] + 3;}
        if (num > 0) {inc = 1;}
        else {inc = -1;}

        while (counter != num)
        {
            ind2 = ind1 + inc;
            if (ind2 == -1) {ind2 = 3;}
            else if (ind2 == 4) {ind2 = 0;}

            temp = loop[ind1];
            loop[ind1] = loop[ind2];
            loop[ind2] = temp;

            update_square(loop, coord);
            ind1 = ind2;
            counter += inc;
        }
    }

    private void cycle_while(int id, int x, int y, int[] cycle_coord)
    {
        while (!at_coord(id, x, y))
        {
            cycle(cycle_coord, 1);
        }
    }


    // Startup
    private Tile create_tile(int size, int i, int j)
    {
        int id = size * j + i + 1;
        if (id == size * size) {id = 0;}
        return new Tile(id);
    }

    private void create_puzzle()
    {
        for (int j = 0; j < size; j++)
        {
            for (int i = 0; i < size; i++)
            {
                tiles[j][i] = create_tile(size, i, j);
            }
        }
    }


    // Shuffle

    private int[] random_neighbour(int[] coord_0, int[] coord_x)
    {
        while (coord_0[0] == coord_x[0] && coord_0[1] == coord_x[1])
        {
            double rand = Math.random();
            if (rand > 0.75)
            {
                if (coord_x[0] != 2) {coord_x[0] += 1;}
            }
            else if (rand > 0.5)
            {
                if (coord_x[0] != 0) {coord_x[0] -= 1;}
            }
            else if (rand > 0.25)
            {
                if (coord_x[1] != 2) {coord_x[1] += 1;}
            }
            else
            {
                if (coord_x[1] != 0) {coord_x[1] -= 1;}
            }
        }

        return coord_x;
    }

    private void shuffle()
    {
        int[] coord_0 = find_tile(0), coord_x = new int[2];
        coord_x[0] = coord_0[0];
        coord_x[1] = coord_0[1];

        int count = 0;
        while (count < size * 30 || !at_coord(0, 2, 2) || at_coord(1, 0, 0))
        {
            coord_x = random_neighbour(coord_0, coord_x);
            swap(coord_0, coord_x, false);
            coord_0[0] = coord_x[0];
            coord_0[1] = coord_x[1];
            count++;
        }
        print("Shuffled");
    }


    // Algorithm
    private int[] CW_u(int x, int y)
    {
        if (x == 2 && y != 2) {y++;}
        else if (y == 2 && x != 0) {x--;}
        else {y--;}

        return make_coord(x, y);
    }

    private int[] antiCW_u(int x, int y)
    {
        if (x == 0 && y != 2) {y++;}
        else if (y == 2 && x != 2) {x++;}
        else if (x == 2 && y != 0) {y--;}
        else {x--;}

        return make_coord(x, y);
    }

    private void zero_big_u(int step, boolean CW) // Edgy to (1, 0)
    {
        int x, y;
        if (CW) {x = 2; y = 0;}
        else {x = 0; y = 1;}
        int[] coord;

        print("Crawling...");
        for (int i = 0; i < step; i++)
        {
            if (CW) {coord = CW_u(x, y);}
            else {coord = antiCW_u(x, y);}

            if (tiles[y][x].get_id() == 0)
            {
                swap(make_coord(x, y), make_coord(coord[0], coord[1]));
            }

            x = coord[0];
            y = coord[1];
        }
        print("Finished crawling");
    }

    private void zero_centre()
    {
        int[] zero = find_tile(0);
        if (!at_coord(0, 1, 1))
            if (zero[0] == 0)
            {
                swap(make_coord(0, 1), make_coord(1, 1));
            }
            else
            {
                swap(make_coord(1, 0), make_coord(1, 1));
            }
    }

    private void three_centre()
    {
        int[] three = find_tile(3);
        int x = 1, y = 0;
        if (three[0] > 0 && three[1] > 0) {y = 1;}
        else if (three[0] == 0) {x = 0; y = 1;}
        cycle_while(3, 1, 1, make_coord(x, y));
        while (!at_coord(3, 1, 1))
        {
            cycle(make_coord(x, y), 1);
        }
    }

    private void two_centre()
    {
        int x = 0;
        if (find_tile(2)[0] != 0) {x = 1;}
        cycle_while(2, 1, 1, make_coord(x, 1));
    }

    private void step_1()
    {
        print("Step 1:");

        int[] one = find_tile(1);
        if (one[0] < 2 && one[1] < 2)
        {
            if (!at_coord(1, 1, 1)) {cycle(make_coord(1, 1), 2);}
            else {
                zero_big_u(6, false);}
        }
        else if (one[0] == 1 || one[1] == 1)
        {
            if (one[0] == 2)
            {
                cycle(make_coord(1, 1), 3);
                cycle(make_coord(1, 0), -2);
            }
            else
            {
                cycle(make_coord(1, 1), -3);
                cycle(make_coord(0, 1), 2);
            }
        }
        else
        {
            cycle(make_coord(1, 1), 2);
            if (one[0] == 2) {cycle(make_coord(1, 0), 4);}
            else {cycle(make_coord(0, 1), -4);}
        }
        cycle_while(1, 0, 0, make_coord(0, 0));
    }

    private void step_2()
    {
        print("Step 2:");

        zero_centre();
        three_centre();
        zero_big_u(6, false);
        cycle(make_coord(1, 0), -3);
    }

    private void special_case_1()
    {
        print("Special case 1:");

        cycle(make_coord(1, 0), -2);
        cycle(make_coord(1, 1), 4);
        cycle(make_coord(1, 0), 2);

        print("Special solved");
    }

    private void step_3()
    {
        print("Step 3:");

        if (at_coord(2, 2, 1))
        {
            special_case_1();
        }

        cycle(make_coord(1, 0), 2);
        two_centre();
        zero_big_u(5, false);
        cycle(make_coord(1, 0), -2);
    }

    private void step_4()
    {
        print("Step 4:");

        int[] four = find_tile(4);
        if (four[0] != 0)
        {
            cycle_while(4, 1, 1, make_coord(1, 1));
            if (find_tile(0)[0] == 2)
            {
                cycle(make_coord(1, 1), 2);
            }
        }

        cycle_while(4, 0, 1, make_coord(0, 1));
    }

    private void special_case_2()
    {
        print("Special case 2:");

        cycle(make_coord(0, 1), 4);
        cycle(make_coord(1, 1), -4);
        cycle(make_coord(0, 1), -3);

        print("Special solved");
    }

    private void step_5()
    {
        print("Step 5:");

        if (at_coord(5, 0, 2))
        {
            special_case_2();
        }

        cycle_while(5, 1, 1, make_coord(1, 1));
        cycle_while(0, 1, 2, make_coord(1, 1));
    }

    private void step_6()
    {
        cycle(make_coord(0, 1), 3);
        cycle_while(7, 1, 2, make_coord(1, 1));
        cycle(make_coord(0, 1), -3);
        cycle(make_coord(1, 1), -1);
    }

    private void steps()
    {
        step_1();
        step_2();
        step_3();
        step_4();
        step_5();
        step_6();

        print("  End");
    }


    // Run
    private void run()
    {
        create_puzzle();
        display_tiles();
        shuffle();
        display_tiles();
        steps();
    }


    // Test
    private boolean test_run()
    {
        run();

        boolean pass = true;
        for (int j = 0; j < size; j++)
        {
            for (int i = 0; i < size; i++)
            {
                if (tiles[j][i].get_id() != i + 1 + j * size && i + j != (size - 1) * 2)
                {
                    pass = false;
                    break;
                }
            }
        }
        //my_display_tiles();

        return pass;
    }

    private void test()
    {
        boolean pass = true;
        int num_of_tests = 100;
        for (int i = 0; i < num_of_tests; i++)
        {
            if (!(new Puzzle().test_run())) {pass = false;}
        }
        my_print(num_of_tests + " tests: ", false);
        my_print(pass);
    }


    // Main
    public static void main(String[] args)
    {
        new Puzzle().test();
    }
}