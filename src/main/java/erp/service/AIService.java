package erp.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Clasa care face legatura cu Inteligenta Artificiala (OpenAI)
 * Rolul ei este sa actioneze ca un traducator: ia un text dezordonat
 * (o comanda scrisa in limbaj natural) si il transforma in date structurate (JSON)
 * pe care aplicatia le poate intelege si salva in baza de date.
 */
public class AIService {

    /** Cheia de acces la API */
    private static final String API_KEY = "key";

    /** Adresa serverului unde trimitem intrebarile */
    private static final String API_URL = "url";

    /**
     * Metoda principala care trimite textul comenzii catre AI si primeste rezultatul interpretat.
     * * @param fileContent Textul brut citit din fisierul incarcat de utilizator (txt/csv).
     * @param listaProduseDisponibile Lista noastra de produse (Cod - Nume) trimisa ca context,
     * astfel incat AI-ul sa stie sa faca potrivirea exacta.
     * @return Un String in format JSON care contine clientul identificat si lista de produse.
     */
    public String extractOrderData(String fileContent, String listaProduseDisponibile) {
        try {
            // Se construieste prompt-ul pentru AI
            String prompt = "Ești un asistent ERP inteligent. Analizează textul de mai jos și extrage datele comenzii primite.\n\n"
                    + "TEXT COMANDĂ:\n" + fileContent + "\n\n"
                    + "LISTA PRODUSE DIN SISTEM (Cod - Nume - Aroma):\n" + listaProduseDisponibile + "\n\n"
                    + "INSTRUCȚIUNI SPECIALE:\n"
                    + "1. CLIENTUL: Identifică numele companiei (ex: SRL, SA, PFA, Magazin, Restaurant). "
                    + "Dacă textul conține 'Suntem firma X', extrage X. "
                    + "Dacă nu există firmă, extrage numele persoanei care trimite comanda. "
                    + "Ignoră formulele de salut sau numele angajaților care semnează, dacă există o firmă menționată.\n"
                    + "2. PRODUSELE: Compară textul cu lista mea de produse. Returnează DOAR codul exact din lista mea.\n\n"
                    + "FORMAT RĂSPUNS (JSON Valid): \n"
                    + "{\n"
                    + "  \"client\": \"Numele Exact Identificat\",\n"
                    + "  \"produse\": [\n"
                    + "    {\"cod\": \"COD_DIN_LISTA_MEA\", \"cantitate\": 10}\n"
                    + "  ]\n"
                    + "}";

            // Pregatim structura JSON pentru cererea HTTP (Request Body)
            // OpenAI cere un format specific: o lista de mesaje, unde noi suntem "user".
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);

            JSONArray messages = new JSONArray();
            messages.put(message);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-5-nano"); // Specificam ce model de inteligenta folosim
            requestBody.put("messages", messages);
            requestBody.put("temperature", 1);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY) // Aici atasam cheia secreta
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Afisare in consola pt situatii de debug
            System.out.println("STATUS CODE: " + response.statusCode());
            System.out.println("RASPUNS SERVER: " + response.body());

            // Procesare raspuns
            JSONObject jsonResponse = new JSONObject(response.body());

            // Verificare eroare la openAI
            if (jsonResponse.has("error")) {
                JSONObject errorObj = jsonResponse.getJSONObject("error");
                System.err.println("EROARE OPENAI: " + errorObj.getString("message"));
                return null;
            }

            if (!jsonResponse.has("choices")) {
                System.err.println("JSON-ul nu conține câmpul 'choices'!");
                return null;
            }

            // OpenAI returneaza raspunsul intr-un array numit "choices".
            // Luam primul element (si singurul) si extragem textul generat.
            jsonResponse = new JSONObject(response.body());
            String content = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return content; // Returnam JSON-ul curat generat de AI

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}