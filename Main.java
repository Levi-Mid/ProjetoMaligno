import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        try {
            Turismo t = new Turismo(50);

            String[] cidades = {
                "Miami", "Fort Lauderdale", "Orlando", "Atlanta", "Nova Orleans",
                "Nashville", "Memphis", "Little Rock", "Dallas", "Houston",
                "San Antonio", "Kansas City", "St Louis", "Indianapolis", "Chicago",
                "Milwaukee", "Minneapolis", "Omaha", "Detroit", "Richmond",
                "Washington", "Nova York", "Albany", "Boston", "Denver",
                "Vail", "Aspen", "Colorado Springs", "Yellowstone", "Salt Lake City",
                "Phoenix", "Grand Canyon", "Las Vegas", "Palm Springs", "San Diego",
                "Los Angeles", "San Francisco", "Lake Tahoe", "Boise", "Portland", "Seattle"
            };
            for (int i = 0; i < cidades.length; i++) t.adicionarCidade(cidades[i]);

            String[][] estradas = {
                {"Seattle", "Portland", "17"}, {"Seattle", "Boise", "50"}, {"Portland", "Boise", "42"},
                {"Portland", "San Francisco", "64"}, {"Portland", "Lake Tahoe", "58"}, {"San Francisco", "Lake Tahoe", "30"},
                {"San Francisco", "Los Angeles", "38"}, {"Lake Tahoe", "Boise", "42"}, {"Lake Tahoe", "Salt Lake City", "52"},
                {"Lake Tahoe", "Palm Springs", "78"}, {"Los Angeles", "San Diego", "12"}, {"Los Angeles", "Palm Springs", "16"},
                {"Los Angeles", "Las Vegas", "27"}, {"San Diego", "Palm Springs", "14"}, {"San Diego", "Phoenix", "35"},
                {"Palm Springs", "Las Vegas", "29"}, {"Palm Springs", "Grand Canyon", "44"}, {"Las Vegas", "Grand Canyon", "47"},
                {"Las Vegas", "Salt Lake City", "42"}, {"Grand Canyon", "Phoenix", "36"}, {"Grand Canyon", "Salt Lake City", "66"},
                {"Boise", "Yellowstone", "67"}, {"Yellowstone", "Denver", "93"}, {"Yellowstone", "Minneapolis", "134"},
                {"Salt Lake City", "Denver", "53"}, {"Denver", "Vail", "16"}, {"Denver", "Aspen", "17"},
                {"Denver", "Colorado Springs", "12"}, {"Vail", "Aspen", "8"}, {"Aspen", "Colorado Springs", "18"},
                {"Denver", "Omaha", "54"}, {"Denver", "Dallas", "73"}, {"Phoenix", "Dallas", "107"},
                {"Phoenix", "San Antonio", "99"}, {"Minneapolis", "Omaha", "38"}, {"Minneapolis", "Milwaukee", "34"},
                {"Minneapolis", "Chicago", "41"}, {"Milwaukee", "Chicago", "9"}, {"Omaha", "Chicago", "47"},
                {"Omaha", "Kansas City", "19"}, {"Kansas City", "St Louis", "25"}, {"Kansas City", "Dallas", "55"},
                {"Chicago", "St Louis", "30"}, {"Chicago", "Indianapolis", "18"}, {"Chicago", "Detroit", "28"},
                {"Chicago", "Washington", "70"}, {"St Louis", "Indianapolis", "25"}, {"St Louis", "Little Rock", "40"},
                {"St Louis", "Memphis", "29"}, {"Indianapolis", "Washington", "59"}, {"Indianapolis", "Nashville", "29"},
                {"Detroit", "Albany", "65"}, {"Detroit", "Nova York", "64"}, {"Detroit", "Washington", "53"},
                {"Albany", "Boston", "17"}, {"Albany", "Nova York", "15"}, {"Boston", "Nova York", "21"},
                {"Nova York", "Washington", "24"}, {"Washington", "Richmond", "20"}, {"Richmond", "Nashville", "71"},
                {"Richmond", "Atlanta", "56"}, {"Little Rock", "Memphis", "14"}, {"Little Rock", "Dallas", "32"},
                {"Memphis", "Nashville", "21"}, {"Memphis", "Atlanta", "39"}, {"Nashville", "Atlanta", "24"},
                {"Dallas", "Atlanta", "79"}, {"Dallas", "San Antonio", "28"}, {"Dallas", "Houston", "25"},
                {"San Antonio", "Houston", "31"}, {"Houston", "Nova Orleans", "53"}, {"Nova Orleans", "Atlanta", "47"},
                {"Nova Orleans", "Orlando", "64"}, {"Nova Orleans", "Miami", "86"}, {"Atlanta", "Orlando", "44"},
                {"Orlando", "Fort Lauderdale", "18"}, {"Orlando", "Miami", "23"}, {"Fort Lauderdale", "Miami", "3"}
            };
            for (int i = 0; i < estradas.length; i++)
                t.adicionarEstrada(estradas[i][0], estradas[i][1], Integer.parseInt(estradas[i][2]));

            System.out.println(t.percursos("Miami", null, 1));                                  
            String[] escolhidas = {"Dallas", "Atlanta", "Nashville", "Houston"};
            System.out.println(t.percursos("Miami", escolhidas, 3));                             
            t.setDias("Miami", 2);
            t.setDias("Dallas", 3);
            System.out.println(t.datas("Miami", escolhidas, LocalDate.of(2026, 12, 1)) + "\n"); 
            System.out.println(t.menorCaminho("Seattle", "Boston"));                             
        } catch (Exception erro) {
            System.out.println(erro.getMessage());
        }
    }
}
