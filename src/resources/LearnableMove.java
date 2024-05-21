package resources;

/**
 * Encapsulated and immutable data structure to represent a move that a Palmon can learn at a specific level.
 *
 * @param moveID the unique identifier of the move
 * @param learnedOnLevel the level at which the move is learned
 */
public record LearnableMove(int moveID, int learnedOnLevel) {
}
