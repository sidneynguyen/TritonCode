import static spark.Spark.get;
import static spark.Spark.port;

public class TritonCodeServer {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        port(3000);
        get("/", (req, res) -> "Hello World");
    }
}
