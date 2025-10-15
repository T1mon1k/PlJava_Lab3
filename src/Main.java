import controller.TurnstileController;
import service.Registry;
import view.TurnstileView;

public class Main {
    public static void main(String[] args) {
        Registry registry = new Registry();
        TurnstileController controller = new TurnstileController(registry);
        TurnstileView view = new TurnstileView(controller);
        view.run();
    }
}
