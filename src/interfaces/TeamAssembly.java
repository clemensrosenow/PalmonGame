package interfaces;

public interface TeamAssembly {
    enum Method {
        random, id, type
    }
    void assembleRandomly();
    void assembleById();
    void assembleByType();
}
