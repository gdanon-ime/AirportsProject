//IMPORTANTE: Devemos inserir pelo menos uma linha na tabela historic no banco de dados para nao haver erros

package src;

import java.sql.*;
import java.util.*;

public class mainCode {

    //Para inserir os dados necessarios precisamos primeiro definir uma funcao para calcular distancia entre dois pontos do globo dadas latitudes e longitudes
    static double findDistance(double LatX, double LongX, double LatY, double LongY){
        double RAIO = 6371.01;
        double DistLat = Math.toRadians(LatY - LatX);
        double DistLong = Math.toRadians(LongY - LongX);

        double aux = Math.sin(DistLat / 2) * Math.sin(DistLat / 2) + Math.cos(Math.toRadians(LatX)) * Math.cos(Math.toRadians(LatY)) * Math.sin(DistLong / 2) * Math.sin(DistLong / 2);
        double angle = 2 * Math.atan2(Math.sqrt(aux), Math.sqrt(1 - aux));
        double outb = RAIO * angle * 1000;

        return Math.min(Math.sqrt(outb), 2 * Math.PI * RAIO - Math.sqrt(outb));
    }
    public static void main(String[] args) {

        try {

            //Definimos a conexao local e os dados de acesso, assim como a query do sql que iremos rodar para extrair os dados
            //Nesse caso definimos no MySQL o schema airports para os dados importados em csv e o schema historic para armazenar cada acesso
            String HISTORIC_URL_MYSQL = "jdbc:mysql://localhost:3306/historic";
            String DATA_URL_MYSQL = "jdbc:mysql://localhost:3306/airports";
            String USER_MYSQL = "root";
            String PASSWORD_MYSQL = "Mysql123";
            String SQL_queryAll = "SELECT * FROM airports.dadosaeroportos";
            
            Connection connectionHistoric = DriverManager.getConnection(HISTORIC_URL_MYSQL, USER_MYSQL, PASSWORD_MYSQL);
            Connection connectionData = DriverManager.getConnection(DATA_URL_MYSQL, USER_MYSQL, PASSWORD_MYSQL);
            Statement statement = connectionData.createStatement();
            ResultSet queryAll = statement.executeQuery(SQL_queryAll);

            //Aqui vamos tratar os dados do arquivo de aeroportos e relacionar cada um deles a um int em um map e salvar suas coordenadas
            Map<String, Integer> mapAirports = new HashMap<String, Integer>();

            //Criamos um vetor com coordenadas
            Vector<Double> latitudes = new Vector<>();
            Vector<Double> longitudes = new Vector<>();
            Vector<String> infoAirports = new Vector<>();
            
            int iterat = 0;
            while(queryAll.next()){
                String airportName = queryAll.getString("initials");
                mapAirports.put(airportName, iterat);
                airportName = airportName + " - " + queryAll.getString("city") + " / " + queryAll.getString("state");
                infoAirports.add(airportName);

                latitudes.add(queryAll.getDouble("latitude"));
                longitudes.add(queryAll.getDouble("longitude"));
                iterat++;
            }

            //Vamos agora fazer um loop para criar uma matrix de adjacencia, usada no algoritmo de dijkstra
            //Para isso, veja que o iterat (numeros de iteracoes acima) eh o tamanho da query
            //Alem disso usaremos 3e7 como valor maximo de distancia (que eh maior que metade do comprimento da Terra)
            double adjMatrix[][] = new double[iterat][iterat];
            for(int i = 0; i < iterat; i++){
                for(int j = 0; j < iterat; j++){
                    if(i == j){
                        adjMatrix[i][j] = 3e7;
                    }
                    else{
                        adjMatrix[i][j] = findDistance(latitudes.get(i), longitudes.get(i), latitudes.get(j), longitudes.get(j));
                    }
                }
            }
            
            //Criamos o objeto com o qual trabalharemos o dijkstra
            DijkstraClass airportObject = new DijkstraClass(adjMatrix);

            //Agora comecaremos a interface com o usuario
            boolean resp = true;
            while(resp){
                Scanner input = new Scanner(System.in);
                System.out.print("Se deseja ver os aeroportos digite 1: ");
                if(input.nextInt() == 1){
                    for(int i = 0; i < iterat; i++){
                        System.out.println(infoAirports.get(i));
                    }
                }

                System.out.print("Insira o aeroporto de origem: ");
                String source = input.nextLine();
                System.out.print("Insira o aeroporto de destino: ");
                String goal = input.nextLine();

                try{
                    double shortPath = airportObject.ShortestPath(mapAirports.get(source), mapAirports.get(goal));
                    System.out.println(shortPath);

                    //Agora inserimos no banco de dados do MySQL os resultados
                    //
                    Statement statementHistoric = connectionHistoric.createStatement();
                    Integer maxID = statementHistoric.executeQuery("SELECT row FROM historic ORDER BY id DESC LIMIT 1").getInt("id");
                    String SQL_INSERT = "INSERT INTO historic VALUES(" + mapAirports.get(source) + ", " + mapAirports.get(goal) + ", " + shortPath + ", " + maxID + ")";
                    PreparedStatement preparedStmt = connectionHistoric.prepareStatement(SQL_INSERT);
                    preparedStmt.execute();

                } catch(Exception err1){
                    err1.printStackTrace();
                }

                System.out.print("Se deseja fazer outra operacao digite 1: ");
                resp = (input.nextInt() == 1);
                input.close();
            }

            statement.close();
            connectionData.close();
            connectionHistoric.close();

        } catch(Exception err){
            err.printStackTrace();
        }

    }
}
