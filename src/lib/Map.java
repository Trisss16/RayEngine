package lib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.*;
import java.util.ArrayList;
import static lib.Engine.TILE_SIZE;

public class Map {
    
    public final int[][] map;
    
    public final int m;
    public final int n;
    
    
    public Map(String path) {
        map = openMapFile(path);
        m = map.length;
        n = map[0].length;
        
        System.out.println("imprimiendo mapa");
        for (int[] i: map) {
            for (int j: i) {
                System.out.printf("%d ", j);
            }
            System.out.println();
        }
    }
    
    protected final int[][] openMapFile(String path) {
        int[][] result = new int[1][1];
        //intenta abrir el archivo del mapa
        try{
            InputStream in = getClass().getResourceAsStream(path);
            
            if (in == null) {
                System.out.println("No se encontró el archivo de mapa: " + path);
                System.exit(0);
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            
            result = readMapFile(reader);
            
            //readMapFile regresa matrices de 1x1 cuando hay casos invalidos, por lo que termina el programa si el mapa no es valido.
            if (result.length == 1 && result[0].length == 1) {
                System.out.println("Mapa invalido.");
                System.exit(0);
            }
        }
        catch(Exception e) {
            System.out.println("No se encontró el archivo de mapa.\nError: " + e);
            System.exit(0);
        }
        
        //convertir el archivo a un mapa
        return result;
    }
    
    protected final int[][] readMapFile(BufferedReader reader) {
        //lee la primera linea
        String line;
        try {
            line = reader.readLine();
        } catch (IOException ex) {
            return new int[1][1];
        }
        if (line == null) return new int[1][1];
        
        int n1 = line.split(" ").length;//columnas
        ArrayList<String> lines = new ArrayList<>();
        
        //continua leyendo el resto, si alguna linea tiene un no de elementos que la primera regresa
        while(line != null) {
            if (line.split(" ").length != n1) {
                System.out.println("Lineas de diferentes longitudes.");
                return new int[1][1];
            }
            
            lines.add(line);
            try {
                line = reader.readLine();
            } catch (IOException ex) {
                return new int[1][1];
            }
        }
        
        
        //ahora con la lista empieza a convertirla a ints
        int[][] result = new int[lines.size()][n1];
        
        for (int i = 0; i < result.length; i++) {
            String[] lineStr = lines.get(i).split("\\s+");
            
            for (int j = 0; j < lineStr.length; j++) {
                try {
                    result[i][j] = Integer.parseInt(lineStr[j]);
                } catch(NumberFormatException E) {
                    return new int[1][1];
                }
            }
        }
        
        return result;
    }
    
    //dibuja el mapa
    public void renderMap(Graphics2D g) {
    int[][] data = map; // matriz del mapa

        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                int value = data[i][j];
                
                //blanco para paredes, negro para espacio vacio
                if (value == 0) {
                    g.setColor(Color.black);
                } else {
                    g.setColor(Color.white);
                }

                //dibuja el cuadrado de la casilla un poco más pequeño y centrado para que el color de fondo de la apariencia de un contorno
                g.fillRect(
                    j * TILE_SIZE + 2, //x
                    i * TILE_SIZE + 2, //y
                    TILE_SIZE - 2,
                    TILE_SIZE -2
                );
                
                //dibuja el mapa sin el contorno
                /*g.fillRect(
                    j * TILE_SIZE, //x
                    i * TILE_SIZE, //y
                    TILE_SIZE,
                    TILE_SIZE
                );*/
            }
        }
    }
    
    //regesa un objeto position indicando la casilla del mapa dentro de la que las coordenadas recibidas se encuentran
    public static Position getTile(double xPos, double yPos) {
        return new Position((int)(yPos / Engine.TILE_SIZE), (int)(xPos / Engine.TILE_SIZE));
    }
}
